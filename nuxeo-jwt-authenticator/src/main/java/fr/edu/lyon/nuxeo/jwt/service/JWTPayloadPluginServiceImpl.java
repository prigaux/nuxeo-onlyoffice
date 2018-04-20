package fr.edu.lyon.nuxeo.jwt.service;

import java.security.Principal;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class JWTPayloadPluginServiceImpl extends DefaultComponent implements JWTPayloadPluginService
{
	private static final String PLUGIN_EP = "plugin";
	private static final String JWT_SIGN_EP = "jwt-sign";

	private static Log log=LogFactory.getLog(JWTPayloadPluginServiceImpl.class);

	protected PayloadPluginRegistry pluginRegistry;
	protected JWTSignRegistry jwtSignRegistry;

	@Override
	public void activate(ComponentContext context)
	{
		super.activate(context);
		pluginRegistry = new PayloadPluginRegistry();
		jwtSignRegistry=new JWTSignRegistry();
	}

	@Override
	public void deactivate(ComponentContext context)
	{
		pluginRegistry = null;
		jwtSignRegistry=null;
		super.deactivate(context);
	}

	@Override
	public void registerContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
	{

		if (extensionPoint.equals(PLUGIN_EP)) {
			PayloadPluginDescriptor desc = (PayloadPluginDescriptor) contribution;
			pluginRegistry.addContribution(desc);
		}else if(JWT_SIGN_EP.equals(extensionPoint))
		{
			JWTSignDescriptor desc=(JWTSignDescriptor) contribution;
			jwtSignRegistry.addContribution(desc);
		}else {
			log.warn("Unknown extension point: " + extensionPoint);
			return;
		}
	}

	@Override
	public void unregisterContribution(Object contribution,
			String extensionPoint, ComponentInstance contributor)
	{
		if (extensionPoint.equals(PLUGIN_EP)) {
			PayloadPluginDescriptor desc = (PayloadPluginDescriptor) contribution;
			pluginRegistry.removeContribution(desc);
		}else if(JWT_SIGN_EP.equals(extensionPoint))
		{
			JWTSignDescriptor desc=(JWTSignDescriptor) contribution;
			jwtSignRegistry.removeContribution(desc);
		}else {
			log.warn("Unknown extension point: " + extensionPoint);
			return;
		}

	}

	@Override
	public PayloadResolver getPayloadResolver(Map<String, Object> payload)
	{
		PayloadPluginDescriptor pluginDescriptor=pluginRegistry.getPluginDescriptor(payload);
		if (pluginDescriptor==null)
		{
			return null;
		}else
		{
			try
			{
				return pluginDescriptor.getPayloadResolver().newInstance();
			} catch (InstantiationException | IllegalAccessException e)
			{
				throw new NuxeoException(e);
			}
		}
	}

	private JWTSignDescriptor getDescriptor(String algorithmId)
	{
		JWTSignDescriptor descriptor=jwtSignRegistry.getJwtSignDescriptor(algorithmId);
		if (descriptor==null)
		{
			log.error("Impossible de trouver l'algorithme JWT associé à l'id " + algorithmId);
		}

		return descriptor;
	}

	@Override
	public Map<String, Object> getPayload(String token, String algorithmId)
	{
		JWTSignDescriptor descriptor=getDescriptor(algorithmId);

		return descriptor==null ? null:descriptor.getPayloadFromToken(token);
	}

	@Override
	public String getSignedToken(String payloadObject, String algorithmId)
	{
		JWTSignDescriptor descriptor=getDescriptor(algorithmId);

		return descriptor==null ? null:descriptor.getSignedToken(payloadObject);
	}

	@Override
	public String getSessionToken(Principal principal, String algorithmId)
	{
		JWTSignDescriptor descriptor=getDescriptor(algorithmId);

		return descriptor==null ? null:descriptor.getSessionToken(principal);
	}
}
