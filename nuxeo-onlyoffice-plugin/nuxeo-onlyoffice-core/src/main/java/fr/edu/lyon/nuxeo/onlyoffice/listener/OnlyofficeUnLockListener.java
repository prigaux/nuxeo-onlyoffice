package fr.edu.lyon.nuxeo.onlyoffice.listener;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Lock;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;

/**
 * Suppression du verrour posé sur un document à l'isssue
 * d'une session d'édition dans OnlyOffice
 * @author ftorchet
 *
 */
public class OnlyofficeUnLockListener implements EventListener
{


	@Override
	public void handleEvent(Event event)
	{
		EventContext ctx = event.getContext();
		if (ctx instanceof DocumentEventContext)
		{
			DocumentEventContext docCtx = (DocumentEventContext) ctx;

			boolean creation=Boolean.TRUE.equals(docCtx.getProperty(OnlyofficeEventConstants.CREATION_EVENT_CONTENT));

			CoreSession session = docCtx.getCoreSession();
			DocumentModel doc = docCtx.getSourceDocument();

			if (!creation)
			{
				Lock lock = session.getLockInfo(doc.getRef());
				if (lock!=null && lock.getOwner().equals(session.getPrincipal().getName()))
				{
					session.removeLock(doc.getRef());
				}
			}
		}
	}

}
