package fr.edu.lyon.nuxeo.jwt.service;

import java.util.Map;

public class SessionTokenPayloadResolver implements PayloadResolver
{

	@Override
	public String getUserId(Map<String, Object> payload)
	{
		return (String) payload.get("userId");
	}

}
