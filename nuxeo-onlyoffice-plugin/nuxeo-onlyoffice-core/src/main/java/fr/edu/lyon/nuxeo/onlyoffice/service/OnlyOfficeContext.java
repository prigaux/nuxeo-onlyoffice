package fr.edu.lyon.nuxeo.onlyoffice.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

/**
 * Contexte de données associées à l'ouverture d'un environnement OnlyOffice
 * @author ftorchet
 *
 */
public class OnlyOfficeContext extends HashMap<String, Serializable>
{
	private static final long	serialVersionUID			= 4604922178952305074L;

	public static final String	REQUEST_PARAMETER_PREFIX	= "http_";
	public static final String	CALLBACK_BASE_URL			= "callbackBaseUrl";
	public static final String	GOBACK_BASE_URL				= "bobackBaseUrl";

	public OnlyOfficeContext(Map<String, Serializable> options)
	{
		super(options);
	}

	public void addRequestParameters(ServletRequest request)
	{
		for (String key : request.getParameterMap().keySet())
		{
			this.put(REQUEST_PARAMETER_PREFIX + key, request.getParameter(key));
		}
	}

	public String getRequestParameter(String key)
	{
		return (String) this.get(REQUEST_PARAMETER_PREFIX + key);
	}

	public void setCallbackBaseUrl(String value)
	{
		this.put(CALLBACK_BASE_URL, value);
	}

	public void setGobackBaseUrl(String value)
	{
		this.put(GOBACK_BASE_URL, value);
	}

	public String getGobackBaseUrl()
	{
		return (String) this.get(GOBACK_BASE_URL);
	}

	public String getCallbackBaseUrl()
	{
		return (String) this.get(CALLBACK_BASE_URL);
	}
}
