package fr.edu.lyon.nuxeo.onlyoffice.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;
import fr.edu.lyon.onlyoffice.api.FileUtility;
import net.sf.json.JSONObject;

/**
 * Persistence d'un nouveau document à partir d'un blob émis par OnlyOffice
 * @author ftorchet
 *
 */
public class OnlyofficeCreateDocumentListener implements EventListener
{
	private static Log log = LogFactory.getLog(OnlyofficeCreateDocumentListener.class);

	protected Blob getOnlyofficeBlob(JSONObject jsonObj)
	{
		HttpURLConnection connection = null;
		InputStream stream = null;
		try
		{
			String downloadUri = jsonObj.getString("url");
			URL url = new URL(downloadUri);
			connection = (HttpURLConnection) url.openConnection();
			stream = connection.getInputStream();
			byte[] bytes = IOUtils.toByteArray(stream);
			return new ByteArrayBlob(bytes);
		} catch (IOException e)
		{
			throw new NuxeoException(e);
		} finally
		{
			if (stream!=null)
			{
				try
				{
					stream.close();
				} catch (IOException e)
				{
					log.debug("impossible de clôturer le flux associé à " + jsonObj, e);
				}
			}

			if (connection!=null)
			{
				connection.disconnect();
			}
		}

	}

	@Override
	public void handleEvent(Event event)
	{
		EventContext ctx = event.getContext();
		if (ctx instanceof DocumentEventContext)
		{
			DocumentEventContext docCtx = (DocumentEventContext) ctx;

			String jsonEvent = (String) docCtx.getProperty(OnlyofficeEventConstants.ONLYOFFICE_JSON_EVENT_CONTENT);
			int status = (int) docCtx.getProperty(OnlyofficeEventConstants.ONLYOFFICE_STATUS);
			boolean creation=Boolean.TRUE.equals(docCtx.getProperty(OnlyofficeEventConstants.CREATION_EVENT_CONTENT));
			String filename=(String) docCtx.getProperty(OnlyofficeEventConstants.FILENAME_EVENT_CONTENT);
			String filetype=(String) docCtx.getProperty(OnlyofficeEventConstants.FILETYPE_EVENT_CONTENT);

			filename += "." + filetype;

			CoreSession session = docCtx.getCoreSession();
			DocumentModel parent = docCtx.getSourceDocument();

			JSONObject jsonObj = JSONObject.fromObject(jsonEvent);
			if (status == 2 && creation)
			{
				try
				{
					Blob blob=getOnlyofficeBlob(jsonObj);
					blob.setFilename(filename);
					blob.setMimeType(FileUtility.getOnlyofficeMimeType(filename));

					DocumentModel documentModel=session.createDocumentModel(parent.getPathAsString(), filename, "File");
                    documentModel.setPropertyValue("dc:title", filename);
					BlobHolder bh=documentModel.getAdapter(BlobHolder.class);
					bh.setBlob(blob);

					session.createDocument(documentModel);
				} catch (Exception e)
				{
					log.error(e);
				}
			}
		}
	}
}
