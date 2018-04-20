package fr.edu.lyon.nuxeo.onlyoffice.listener;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.VersioningOption;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.ByteArrayBlob;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;
import fr.edu.lyon.onlyoffice.api.FileUtility;
import net.sf.json.JSONObject;

/**
 * Persistence d'un document en intégrant les modification du contenu
 * de son fichier principal à partir d'OnlyOffice
 * @author ftorchet
 *
 */
public class OnlyofficeSaveDocumentListener implements EventListener
{
	private static Log log = LogFactory.getLog(OnlyofficeSaveDocumentListener.class);

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
			throw new ClientException(e);
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

			CoreSession session = docCtx.getCoreSession();
			DocumentModel doc = docCtx.getSourceDocument();

			JSONObject jsonObj = JSONObject.fromObject(jsonEvent);
			if (status == 2 && !creation)
			{
				try
				{
					BlobHolder bh = doc.getAdapter(BlobHolder.class);
					Blob originalBlob = bh.getBlob();
					String originalFilename = originalBlob.getFilename();
					String originalExt = FileUtility.getFileExtension(originalFilename);
					String onlyofficeExt = FileUtility.getOnlyOfficeExtension(originalFilename);

					String updatedFilename = new StringBuilder(FileUtility.getFileNameWithoutExtension(originalFilename)).append(onlyofficeExt).toString();

					Blob blob=getOnlyofficeBlob(jsonObj);
					blob.setFilename(updatedFilename);
					blob.setMimeType(FileUtility.getOnlyofficeMimeType(originalFilename));

					bh.setBlob(blob);

					if (!originalExt.equalsIgnoreCase(onlyofficeExt) || session.isCheckedOut(doc.getRef()))
					{
						session.checkIn(doc.getRef(), VersioningOption.NONE, "historisation avant modification onlyoffice");
					}

					if (!originalExt.equalsIgnoreCase(onlyofficeExt))
					{
						doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
					}else
					{
						doc.putContextData(VersioningService.VERSIONING_OPTION, VersioningOption.MINOR);
					}

					session.saveDocument(doc);
				} catch (Exception e)
				{
					log.error(e);
				}
			}
		}
	}
}
