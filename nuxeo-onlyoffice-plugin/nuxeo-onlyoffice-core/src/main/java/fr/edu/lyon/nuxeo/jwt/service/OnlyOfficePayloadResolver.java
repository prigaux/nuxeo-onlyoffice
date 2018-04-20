package fr.edu.lyon.nuxeo.jwt.service;

import java.util.List;
import java.util.Map;

import fr.edu.lyon.nuxeo.jwt.service.PayloadResolver;

/**
 * Accès au user dans un token JWT formé par OnlyOffice
 * @author ftorchet
 *
 */
public class OnlyOfficePayloadResolver implements PayloadResolver
{

	@SuppressWarnings("unchecked")
	@Override
	public String getUserId(Map<String, Object> payload)
	{
		return (String) ((Map<String, Object>)((List<Object>) ((Map<String,Object>)payload.get("payload")).get("actions")).get(0)).get("userid");
	}

}
