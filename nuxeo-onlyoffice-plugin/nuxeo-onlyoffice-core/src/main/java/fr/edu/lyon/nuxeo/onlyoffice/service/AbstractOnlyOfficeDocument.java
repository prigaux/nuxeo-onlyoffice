package fr.edu.lyon.nuxeo.onlyoffice.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;

public abstract class AbstractOnlyOfficeDocument implements OnlyOfficeDocument
{
	protected static final char			KEY_SEPARATOR				= '.';
	protected static final DateFormat	df							= new SimpleDateFormat("dd/MM/yyyy hh:mm");

	private static final String			EDIT_OPTION_MODE			= "mode";
	private static final String			AUTOSAVE_OPTION				= "autosave";

	protected CoreSession				session;
	protected OnlyOfficeContext			context;

	public AbstractOnlyOfficeDocument(CoreSession session, OnlyOfficeContext context)
	{
		this.session = session;
		this.context = context;
	}

	@Override
	public boolean isEditModeEnabled()
	{
		return Boolean.TRUE.equals(context.get(EDIT_OPTION_MODE));
	}

	@Override
	public boolean isAutosaveEnabled()
	{
		return Boolean.TRUE.equals(context.get(AUTOSAVE_OPTION));
	}

	@Override
	public NuxeoPrincipal getSessionPrincipal()
	{
		return (NuxeoPrincipal) session.getPrincipal();
	}

	public static NuxeoPrincipal getPrincipal(String userid)
	{
		if (StringUtils.isNotBlank(userid))
		{
			UserManager userManager = Framework.getService(UserManager.class);
			return userManager.getPrincipal(userid);
		}

		return null;
	}

	public static String getRealName(NuxeoPrincipal principal)
	{
		return principal != null ? String.format("%s %s", principal.getFirstName(), principal.getLastName()) : "Inconnu";
	}
}
