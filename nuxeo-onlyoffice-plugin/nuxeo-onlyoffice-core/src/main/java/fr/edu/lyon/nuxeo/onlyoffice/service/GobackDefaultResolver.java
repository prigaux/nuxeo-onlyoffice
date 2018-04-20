package fr.edu.lyon.nuxeo.onlyoffice.service;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.url.DocumentViewImpl;
import org.nuxeo.ecm.platform.url.api.DocumentView;
import org.nuxeo.ecm.platform.url.api.DocumentViewCodecManager;
import org.nuxeo.runtime.api.Framework;

/**
 * Gestion de l'adresse goback pour onlyOffice.
 * <p>Retour vers le document père dans le cas d'une création</p>
 * @author ftorchet
 *
 */
public class GobackDefaultResolver implements GobackResolver
{

	@Override
	public String getGobackUrl(OnlyOfficeContext context, DocumentModel doc)
	{
		DocumentViewCodecManager codecManager=Framework.getService(DocumentViewCodecManager.class);
		DocumentView docView=new DocumentViewImpl(doc);
		return codecManager.getUrlFromDocumentView(codecManager.getDefaultCodecName(), docView, true, context.getGobackBaseUrl());
	}

}
