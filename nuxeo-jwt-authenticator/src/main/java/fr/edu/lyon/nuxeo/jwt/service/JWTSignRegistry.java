package fr.edu.lyon.nuxeo.jwt.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;

public class JWTSignRegistry extends ContributionFragmentRegistry<JWTSignDescriptor>
{
	private static Log										log			= LogFactory.getLog(PayloadPluginRegistry.class);

	protected final Map<String, JWTSignDescriptor>	descriptors	= new LinkedHashMap<>();

	@Override
	public String getContributionId(JWTSignDescriptor contrib)
	{
		return contrib.getId();
	}

	@Override
	public void contributionUpdated(String id, JWTSignDescriptor contrib, JWTSignDescriptor newOrigContrib)
	{
		descriptors.put(id, contrib);
		log.debug("JWTSignDescriptor mis à jour : " + contrib);
	}

	@Override
	public void contributionRemoved(String id, JWTSignDescriptor contrib)
	{
		descriptors.remove(id);
		log.debug("JWTSignDescriptor supprimé : " + contrib);
	}

	@Override
	public JWTSignDescriptor clone(JWTSignDescriptor orig)
	{
		return new JWTSignDescriptor(orig);
	}

	@Override
	public void merge(JWTSignDescriptor src, JWTSignDescriptor dst)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSupportingMerge()
	{
		return false;
	}

	public JWTSignDescriptor getJwtSignDescriptor(String algorithmId)
	{
		return descriptors.get(algorithmId);
	}

}
