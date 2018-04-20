package fr.edu.lyon.nuxeo.onlyoffice.listener;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;

/**
 * Définition d'un verrou lors de l'ouverture d'un document dans OnlyOffice
 * <p>Uniquement si le document n'est ouvert ni en co-édition ni en création</p>
 * @author ftorchet
 *
 */
public class OnlyofficeLockListener implements EventListener
{

	@Override
	public void handleEvent(Event event)
	{
		EventContext ctx = event.getContext();
		if (ctx instanceof DocumentEventContext)
		{
			DocumentEventContext docCtx=(DocumentEventContext) ctx;

			boolean coedit=Boolean.TRUE.equals(docCtx.getProperty(OnlyofficeEventConstants.COEDITION_EVENT_CONTENT));
			boolean creation=Boolean.TRUE.equals(docCtx.getProperty(OnlyofficeEventConstants.CREATION_EVENT_CONTENT));
			if (!coedit && !creation)
			{
				CoreSession session=docCtx.getCoreSession();
				DocumentModel doc=docCtx.getSourceDocument();
				session.setLock(doc.getRef());
			}
		}

	}

}
