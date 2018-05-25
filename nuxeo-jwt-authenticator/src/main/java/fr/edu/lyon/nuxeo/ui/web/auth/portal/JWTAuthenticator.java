package fr.edu.lyon.nuxeo.ui.web.auth.portal;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.api.login.UserIdentificationInfo;
import org.nuxeo.ecm.platform.ui.web.auth.interfaces.NuxeoAuthenticationPlugin;
import org.nuxeo.runtime.api.Framework;

import fr.edu.lyon.nuxeo.jwt.service.JWTPayloadPluginService;
import fr.edu.lyon.nuxeo.jwt.service.PayloadResolver;

public class JWTAuthenticator implements NuxeoAuthenticationPlugin
{
	private static Log			log						= LogFactory.getLog(JWTAuthenticator.class);
	private static final String	AUTHORIZATION_HEADER	= "Authorization";
	private static final String	ALGORITHM_KEY_NAME			= "algorithm";
	private static final String	PREFIX_KEY_NAME			= "prefix";

	private String				algorithmId;
	private String				prefix					= "Bearer";

	private JWTPayloadPluginService payloadPluginService;

	protected JWTPayloadPluginService getPayloadPluginService()
	{
		if (payloadPluginService==null)
		{
			payloadPluginService=Framework.getService(JWTPayloadPluginService.class);
		}

		return payloadPluginService;
	}

	public List<String> getUnAuthenticatedURLPrefix()
	{
		return Collections.emptyList();
	}

	public Boolean handleLoginPrompt(HttpServletRequest httpRequest, HttpServletResponse httpResponse, String baseURL)
	{
		return false;
	}

	public String getJWTToken(String authorizationString)
	{
		if (!StringUtils.startsWith(authorizationString, prefix))
		{
			return null;
		}

		return authorizationString.substring(prefix.length()).trim();
	}



	public UserIdentificationInfo handleRetrieveIdentity(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
	{
        String token = getJWTToken(httpRequest.getHeader(AUTHORIZATION_HEADER));
        String userId = token2userId(token);
        if (userId == null) {
            token = httpRequest.getParameter("sessionToken");
            userId = token2userId(token);
        }
        return userId == null ? null : new UserIdentificationInfo(userId, userId);
    }

    private String token2userId(String token) {
        if (token == null) return null;
		try
		{
			Map<String, Object> payload = getPayloadPluginService().getPayload(token, algorithmId);

			PayloadResolver payloadResolver=getPayloadPluginService().getPayloadResolver(payload);
            if (payloadResolver==null) return null;
            
            return payloadResolver.getUserId(payload);
		} catch (Exception e)
		{
			log.warn("Impossible de valider le token JWT", e);
			return null;
		}

	}

	public void initPlugin(Map<String, String> parameters)
	{
		if (parameters.containsKey(ALGORITHM_KEY_NAME))
		{
			algorithmId = parameters.get(ALGORITHM_KEY_NAME);
		}else
		{
			throw new NuxeoException("un id d'algorithm JWT est requis");
		}
		if (parameters.containsKey(PREFIX_KEY_NAME))
		{
			prefix = parameters.get(PREFIX_KEY_NAME);
		}
	}

	public Boolean needLoginPrompt(HttpServletRequest httpRequest)
	{
		return false;
	}
}
