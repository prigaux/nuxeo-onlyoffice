package fr.edu.lyon.nuxeo.onlyoffice.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.api.Framework;

public class OnlyOfficeUserHelper
{
	private OnlyOfficeUserHelper()
	{
		super();
	}

	/**
	 * L'utilisateur associé à la session passée en paramètre est-il
	 * autorisé à utiliser OnlyOffice ?
	 * @param session
	 * @return
	 */
	public static boolean canUseOnlyOffice(CoreSession session)
	{
		DirectoryService directoryService=Framework.getService(DirectoryService.class);
		Session dirSession=null;

		NuxeoPrincipal principal=(NuxeoPrincipal) session.getPrincipal();
		List<String> groups = principal.getAllGroups();
		Set<String> lowerGroups=new HashSet<>(groups.size());
		for(String group:groups)
		{
			lowerGroups.add(group.toLowerCase());
		}

		try
		{
			dirSession= directoryService.open("onlyoffice_users");

			Map<String, Serializable> filter=Collections.emptyMap();
			DocumentModelList onlyofficeUsers = dirSession.query(filter);

			if (onlyofficeUsers.isEmpty())
			{
				return true;
			}

			for (DocumentModel onlyOfficeUser:onlyofficeUsers)
			{
				String groupId=onlyOfficeUser.getId().toLowerCase();
				if (lowerGroups.contains(groupId) || principal.getName().equalsIgnoreCase(groupId))
				{
					return true;
				}
			}

		}finally {
			if (dirSession!=null)
			{
				dirSession.close();
			}
		}

		return false;
	}
}
