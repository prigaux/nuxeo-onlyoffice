package fr.edu.lyon.nuxeo.jwt.service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.NuxeoException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.impl.PublicClaims;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import net.sf.json.JSONObject;

@XObject("algorithm")
public class JWTSignDescriptor
{
	@XNode("@id")
	private String id;

	@XNode("@name")
	private String name;

	@XNode("@key")
	private String key;

	private JWTVerifier verifier;
	private Algorithm algorithm;

	public JWTSignDescriptor()
	{
		super();
	}

	public JWTSignDescriptor(JWTSignDescriptor orig)
	{
		this.id = orig.id;
		this.name = orig.name;
		this.key = orig.key;
	}

	public String getId()
	{
		return id;
	}

	private Algorithm getAlgorithm()
	{
		if (algorithm == null)
		{
			try
			{
				switch (name)
				{
				case "HS256":
					algorithm = Algorithm.HMAC256(key);
					break;
				case "HS384":
					algorithm = Algorithm.HMAC384(key);
					break;
				case "HS512":
					algorithm = Algorithm.HMAC512(key);
					break;
				default:
					throw new NuxeoException("aucun algorithme correspondant à " + name);
				}
			} catch (IllegalArgumentException | UnsupportedEncodingException e)
			{
				throw new NuxeoException(e);
			}
		}

		return algorithm;
	}

	private JWTVerifier getJWTVerifier()
	{
		if (verifier == null)
		{
			verifier = JWT.require(getAlgorithm()).build();
		}

		return verifier;
	}

	public Map<String, Object> getPayloadFromToken(String token)
	{
		DecodedJWT jwt=getJWTVerifier().verify(token);
		Map<String, Claim> claims=jwt.getClaims();
		Map<String, Object> payload=new HashMap<>();

		for(Map.Entry<String, Claim> entry:claims.entrySet())
		{
			Claim claim=entry.getValue();
			String clkey=entry.getKey();
			if (!claim.isNull() && claim.asMap()!=null)
			{
				payload.put(clkey, claim.asMap());
			}else if (!claim.isNull())
			{
				payload.put(clkey, claim.as(Object.class));
			}
		}

		return payload;
	}

	public String getSignedToken(String payloadObject)
	{
		Algorithm algo = getAlgorithm();

		Map<String, String> headerClaims = new LinkedHashMap<>();
		headerClaims.put(PublicClaims.ALGORITHM, algo.getName());
		headerClaims.put(PublicClaims.TYPE, "JWT");

		String headerJson = JSONObject.fromObject(headerClaims).toString();

		String header = Base64.encodeBase64URLSafeString(headerJson.getBytes(StandardCharsets.UTF_8));
		String payload = Base64.encodeBase64URLSafeString(payloadObject.getBytes(StandardCharsets.UTF_8));
		String content = String.format("%s.%s", header, payload);

		byte[] signatureBytes = algo.sign(content.getBytes(StandardCharsets.UTF_8));
		String signature = Base64.encodeBase64URLSafeString((signatureBytes));

		return String.format("%s.%s", content, signature);
	}

	public String getSessionToken(Principal principal)
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.add(Calendar.MINUTE, 5);
		return JWT.create().withIssuedAt(new Date()).withExpiresAt(cal.getTime()).withClaim("userId", principal.getName()).sign(getAlgorithm());
	}
}
