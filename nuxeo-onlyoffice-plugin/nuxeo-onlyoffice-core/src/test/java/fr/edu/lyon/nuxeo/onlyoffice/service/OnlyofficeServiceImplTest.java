package fr.edu.lyon.nuxeo.onlyoffice.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.impl.blob.InputStreamBlob;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyofficeEventConstants;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy(value = { "fr.edu.lyon.nuxeo.onlyoffice" })
@LocalDeploy(value = { "fr.edu.lyon.nuxeo.onlyoffice:event-producer-test.xml" })
public class OnlyofficeServiceImplTest
{
	@Inject
	private CoreSession session;
	@Inject
	private OnlyofficeService onlyofficeService;

	public static String eventFired;

	private Blob getFileBlob()
	{
		return new InputStreamBlob(getClass().getResourceAsStream("/file.txt"));
	}

	private Blob getTemplateBlob()
	{
		return new InputStreamBlob(getClass().getResourceAsStream("/templates/empty.txt"));
	}

	@Before
	public void init()
	{
		DocumentModel file = session.createDocumentModel("/", "doc", "File");
		BlobHolder bh = file.getAdapter(BlobHolder.class);
		bh.setBlob(getFileBlob());
		session.createDocument(file);

		eventFired=null;
	}

	@Test
	public void testGetBlob() throws Exception
	{
		DocumentModel file = session.getDocument(new PathRef("/doc"));
		byte[] blob = onlyofficeService.getBlob(session, file);
		Blob fileBlob=getFileBlob();

		String expected=new String(fileBlob.getByteArray());
		String tested=new String(blob);

		assertThat(tested, notNullValue());
		assertThat(expected, is(tested));
	}

	@Test
	public void testGetBlobFromTemplate() throws Exception
	{
		byte[] template=onlyofficeService.getBlobFromTemplate(session, "txt");
		Blob fileBlob=getTemplateBlob();

		String expected=new String(fileBlob.getByteArray());
		String tested=new String(template);

		assertThat(tested, notNullValue());
		assertThat(expected, is(tested));
	}

	@Test
	public void testTrackDocument() throws Exception
	{
		DocumentModel file = session.getDocument(new PathRef("/doc"));
		String jsonEvent="{'status':1}";
		onlyofficeService.trackDocument(session, file.getId(), jsonEvent, false, false);

		assertThat(eventFired, is(OnlyofficeEventConstants.ONLYOFFICE_BEFORE_EDITION_EVENT));
	}

}
