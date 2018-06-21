package fr.edu.lyon.nuxeo.onlyoffice.webengine;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentSecurityException;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.webengine.jaxrs.session.SessionFactory;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import fr.edu.lyon.nuxeo.onlyoffice.service.OnlyofficeConfig;
import fr.edu.lyon.nuxeo.onlyoffice.service.OnlyofficeService;
import fr.edu.lyon.onlyoffice.api.FileUtility;

@Path("/onlyoffice")
@Produces("text/html;charset=UTF-8")
@WebObject(type = "OnlyOffice")
public class OnlyOfficeWebservice extends ModuleRoot
{

	protected final Log			log	= LogFactory.getLog(OnlyOfficeWebservice.class);

	private OnlyofficeService	onlyofficeService;

	private OnlyofficeService getOnlyOfficeService()
	{
		if (onlyofficeService == null)
		{
			onlyofficeService = Framework.getService(OnlyofficeService.class);
		}

		return onlyofficeService;
	}

	private OnlyofficeConfig getOnlyOfficeConfig()
	{
		return getOnlyOfficeService().getConfig();
	}

	private DocumentModel getDocument(String docId)
	{
		return SessionFactory.getSession().getDocument(new IdRef(docId));
	}

	private void checkSecurid(DocumentModel document)
	{        
	}

	/**
	 * Génération de la vue à partir de la template index.ftl
	 * @param docId identifiant du document (ou père en cas de création)
	 * @param mode edit ou vue
	 * @param coedit coédition activée
	 * @param creation mode création ?
	 * @return
	 */
	private Object getTemplate(DocumentModel document, boolean mode, boolean coedit, Map<String, Serializable> creationMap)
	{
		boolean creation=creationMap!=null && !creationMap.isEmpty();

		Map<String, Serializable> options = new HashMap<>();
		options.put("mode", mode);
		options.put("autosave", coedit);
		if (creation)
		{
			options.put("createMode", true);
			options.putAll(creationMap);
		}

		String json = getOnlyOfficeConfig().getConfig(SessionFactory.getSession(), request, document, options);
		String config = json.substring(1, json.length() - 1);

		return getView("index").arg("documentServer", getOnlyOfficeConfig().getDocumentServerUrl()).arg("config", config);
	}

	private Object getSecuridErrorView()
	{
		return null;
	}

	@Path("coedit/{docId}")
	@GET
	public Object coEditDocument(@PathParam("docId") final String docId)
	{
        try {
            DocumentModel document=getDocument(docId);
            return getTemplate(document, true, true,null);
        } catch (DocumentSecurityException e) {
            return getView("DocumentSecurityException");
        }
}

	@Path("create/{docId}")
	@GET
	public Object createDocument(@PathParam("docId") final String docId)
	{
		String[] decodedParts=FileUtility.getDecoded(docId);
		Map<String, Serializable> creationMap=new HashMap<>();
		creationMap.put("filename", decodedParts[1]);
		creationMap.put("filetype", decodedParts[2]);

		try
		{
			DocumentModel doc=getDocument(decodedParts[0]);
			checkSecurid(doc);
			return getTemplate(doc, true, false, creationMap);
		}catch(Exception e)
		{
			return getSecuridErrorView();
		}
	}

	@Path("edit/{docId}")
	@GET
	public Object editDocument(@PathParam("docId") final String docId)
	{
		try
		{
			DocumentModel doc=getDocument(docId);
			checkSecurid(doc);
			return getTemplate(doc, true, false,null);
		}catch(Exception e)
		{
			return getSecuridErrorView();
		}

	}

	@Path("view/{docId}")
	@GET
	public Object viewDocument(@PathParam("docId") final String docId)
	{
		try
		{
			DocumentModel doc=getDocument(docId);
			checkSecurid(doc);
			return getTemplate(doc, false, false,null);
		}catch(Exception e)
		{
			return getSecuridErrorView();
		}
	}

	@Path("download/{docId}")
	@GET
	public Object downloadDoc(@PathParam("docId") final String docId) throws IOException
	{
		if (log.isDebugEnabled())
		{
			log.debug("OnlyOffice getDocument:" + docId);
		}

		try
		{
			DocumentModel doc=getDocument(docId);
			checkSecurid(doc);
			return getOnlyOfficeService().getBlob(SessionFactory.getSession(), doc);
		}catch(Exception e)
		{
			return getSecuridErrorView();
		}
	}

	@Path("template/{filetype}")
	@GET
	public Object createDocumentFromTemplate(@PathParam("filetype") final String filetype) throws IOException
	{
		return getOnlyOfficeService().getBlobFromTemplate(SessionFactory.getSession(), filetype);
	}

	private String getBody()
	{
		try (Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A"))
		{
			return scanner.hasNext() ? scanner.next() : "";
		} catch (Exception ex)
		{
			log.error(ex);
			return "";
		}
	}

	@Path("callbackEdit/{docId}")
	@POST
	public Object callbackEdit(@PathParam("docId") final String docId)
	{
		return getOnlyOfficeService().trackDocument(SessionFactory.getSession(), docId, getBody(), false, false);
	}

	@Path("callbackCoEdit/{docId}")
	@POST
	public Object callbackCoEdit(@PathParam("docId") final String docId)
	{
		return getOnlyOfficeService().trackDocument(SessionFactory.getSession(), docId, getBody(), true, false);
	}

	@Path("callbackCreate/{docId}")
	@POST
	public Object callbackCreate(@PathParam("docId") final String docId)
	{
		return getOnlyOfficeService().trackDocument(SessionFactory.getSession(), docId, getBody(), false, true);
	}
}