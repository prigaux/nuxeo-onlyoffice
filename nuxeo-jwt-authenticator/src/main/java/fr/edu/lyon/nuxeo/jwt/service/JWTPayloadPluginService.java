package fr.edu.lyon.nuxeo.jwt.service;

import java.security.Principal;
import java.util.Map;

public interface JWTPayloadPluginService
{

	PayloadResolver getPayloadResolver(Map<String, Object> payload);

	Map<String, Object> getPayload(String token, String algorithmId);

	String getSignedToken(String payloadObject, String algorithmId);

	String getSessionToken(Principal principal, String algorithmId);

}