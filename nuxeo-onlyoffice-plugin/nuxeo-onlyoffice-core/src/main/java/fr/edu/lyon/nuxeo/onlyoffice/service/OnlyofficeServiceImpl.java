package fr.edu.lyon.nuxeo.onlyoffice.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.Blobs;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;
import fr.edu.lyon.onlyoffice.api.FileUtility;
import net.sf.json.JSONObject;

public class OnlyofficeServiceImpl extends DefaultComponent implements OnlyofficeService
{
	private static final String	CONFIG_EP	= "config";

	private static Log			log			= LogFactory.getLog(OnlyofficeServiceImpl.class);

	protected OnlyofficeConfig	config;
	protected EventProducer		eventProducer;

	protected EventProducer getEventProducer()
	{
		if (eventProducer == null)
		{
			eventProducer = Framework.getService(EventProducer.class);
		}

		return eventProducer;
	}

	@Override
	public void deactivate(ComponentContext context)
	{
		config = null;
		super.deactivate(context);
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
	{

		if (extensionPoint.equals(CONFIG_EP))
		{
			if (config != null)
			{
				log.info("configuration onlyoffice mise à jour");
			}
			config = (OnlyofficeConfig) contribution;
		} else
		{
			log.warn("Unknown extension point: " + extensionPoint);
			return;
		}
	}

	@Override
	public OnlyofficeConfig getConfig()
	{
		if (config == null)
		{
			throw new NuxeoException("Aucune configuration onlyoffice définie");
		}
		return config;
	}

	@Override
	public byte[] getBlob(CoreSession session, DocumentModel document) throws IOException
	{
		BlobHolder bh = document.getAdapter(BlobHolder.class);
		Blob blob = bh.getBlob();

		return blob.getByteArray();
	}

	@Override
	public byte[] getBlobFromTemplate(CoreSession session, String filetype) throws IOException
	{
		String template = "/templates/empty." + filetype;
		Blob blob = Blobs.createBlob(getClass().getResourceAsStream(template));

		return blob.getByteArray();
	}

	private int getStatus(String jsonEvent)
	{
		JSONObject jsonObj = JSONObject.fromObject(jsonEvent);
		return jsonObj.getInt("status");
	}

	@Override
	public String trackDocument(CoreSession session, String docId, String jsonEvent, boolean coEdition, boolean creation)
	{
		DocumentModel documentModel;
		Map<String, Serializable> properties = new HashMap<>();

		if (creation)
		{
			String[] docIdParts=FileUtility.getDecoded(docId);
			documentModel= session.getDocument(new IdRef(docIdParts[0]));
			properties.put(OnlyofficeEventConstants.FILENAME_EVENT_CONTENT, docIdParts[1]);
			properties.put(OnlyofficeEventConstants.FILETYPE_EVENT_CONTENT, docIdParts[2]);
		}else
		{
			documentModel= session.getDocument(new IdRef(docId));
		}

		DocumentEventContext eventCtx = new DocumentEventContext(session, session.getPrincipal(), documentModel);

		eventCtx.setProperties(properties);

		int status=getStatus(jsonEvent);
		properties.put(OnlyofficeEventConstants.ONLYOFFICE_JSON_EVENT_CONTENT, jsonEvent);
		properties.put(OnlyofficeEventConstants.COEDITION_EVENT_CONTENT, coEdition);
		properties.put(OnlyofficeEventConstants.CREATION_EVENT_CONTENT, creation);
		properties.put(OnlyofficeEventConstants.ONLYOFFICE_STATUS, status);

		String eventName;
		switch (status)
		{
		case 1:
			eventName = OnlyofficeEventConstants.ONLYOFFICE_BEFORE_EDITION_EVENT;
			break;
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			eventName = OnlyofficeEventConstants.ONLYOFFICE_AFTER_EDITION_EVENT;
			break;
		default:
			eventName = null;
		}

		if (eventName!=null)
		{
			getEventProducer().fireEvent(eventCtx.newEvent(eventName));
		}

		return "{\"error\":0}";
	}
}
