package fr.edu.lyon.nuxeo.jwt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject("payload")
public class PayloadPluginDescriptor
{
	@XNode("@id")
	private String id;

	@XNode("@enabled")
	private boolean enabled=true;

	@XNode("@class")
	private Class<PayloadResolver> klass;

	@XNodeList(componentType=String.class, type=ArrayList.class, value="claim")
	private List<String> claims;

	public PayloadPluginDescriptor()
	{
		super();
	}

	public PayloadPluginDescriptor(PayloadPluginDescriptor orig)
	{
		this.id=orig.id;
		this.enabled=orig.enabled;
		this.klass=orig.klass;
		this.claims=new ArrayList<>(orig.getClaims());
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public Class<PayloadResolver> getPayloadResolver()
	{
		return klass;
	}

	public void setPayloadResolver(Class<PayloadResolver> klass)
	{
		this.klass = klass;
	}

	public List<String> getClaims()
	{
		if (claims==null)
		{
			claims=Collections.emptyList();
		}
		return claims;
	}

	public void setClaims(List<String> claims)
	{
		this.claims = claims;
	}
}
