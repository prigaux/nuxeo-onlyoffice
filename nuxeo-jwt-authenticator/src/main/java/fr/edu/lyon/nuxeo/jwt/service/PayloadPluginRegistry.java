package fr.edu.lyon.nuxeo.jwt.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ContributionFragmentRegistry;

public final class PayloadPluginRegistry extends ContributionFragmentRegistry<PayloadPluginDescriptor>
{
	private static Log										log			= LogFactory.getLog(PayloadPluginRegistry.class);

	protected final Map<String, PayloadPluginDescriptor>	descriptors	= new LinkedHashMap<>();

	@Override
	public String getContributionId(PayloadPluginDescriptor contrib)
	{
		return contrib.getId();
	}

	@Override
	public void contributionUpdated(String id, PayloadPluginDescriptor contrib, PayloadPluginDescriptor newOrigContrib)
	{
		descriptors.remove(id);

		Map<String, PayloadPluginDescriptor> map = new LinkedHashMap<>(descriptors);
		descriptors.clear();
		descriptors.put(id, contrib);
		descriptors.putAll(map);

		log.debug("PayloadPluginDescriptor mis à jour : " + contrib);
	}

	@Override
	public void contributionRemoved(String id, PayloadPluginDescriptor contrib)
	{
		descriptors.remove(id);
		log.debug("PayloadPluginDescriptor supprimé : " + contrib);
	}

	@Override
	public PayloadPluginDescriptor clone(PayloadPluginDescriptor orig)
	{
		return new PayloadPluginDescriptor(orig);
	}

	@Override
	public void merge(PayloadPluginDescriptor src, PayloadPluginDescriptor dst)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isSupportingMerge()
	{
		return false;
	}

	protected Iterable<PayloadPluginDescriptor> descriptorIterator()
	{
		return descriptors.values();
	}

	@SuppressWarnings("unchecked")
	private Object getObjectLevel(Object object, String key)
	{
		if (object != null)
		{
			if (object instanceof Map)
			{
				return ((Map<String, Object>) object).get(key);
			} else if (object instanceof List)
			{
				List<Object> list = (List<Object>) object;
				for (int j = 0; j < list.size(); j++)
				{
					Object o = list.get(j);

					Object r;
					if ((r = getObjectLevel(o, key)) != null)
					{
						return r;
					}
				}

				return null;
			} else
			{
				/*
				 *  si l'object courant n'est ni une map ni une liste,
				 *  c'est que la clé à tester n'existe pas
				 */
				return null;
			}
		}

		return null;
	}

	private boolean checkClaims(Map<String, Object> payload, List<String> claims)
	{
		if (claims == null || claims.isEmpty())
		{
			// si aucun claim n'est fournit, le descripteur est réputé valide !
			return true;
		}

		if (payload==null)
		{
			// dans le cas d'une vérification des claims, le claim payload est
			// requis.
			return false;
		}

		for (String claim : claims)
		{
			String[] parts = claim.split("\\.");

			Object map = payload;
			for (int i = 0; i < parts.length; i++)
			{
				String part = parts[i];

				map = getObjectLevel(map, part);
				if (map == null)
				{
					return false;
				}
			}
		}

		return true;
	}

	public PayloadPluginDescriptor getPluginDescriptor(Map<String, Object> payload)
	{
		for (Map.Entry<String, PayloadPluginDescriptor> entry : descriptors.entrySet())
		{
			PayloadPluginDescriptor descriptor = entry.getValue();
			if (descriptor.isEnabled())
			{
				if (descriptor.getClaims().isEmpty())
				{
					return descriptor;
				} else
				{
					if (checkClaims(payload, descriptor.getClaims()))
					{
						return descriptor;
					}
				}
			}
		}

		return null;
	}
}
