package fr.edu.lyon.test.onlyoffice.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import fr.edu.lyon.onlyoffice.api.FileType;
import fr.edu.lyon.onlyoffice.api.FileUtility;

public class FileUtilityTest
{

	@Test
	public void testGetFileType() throws Exception
	{
		FileType fileType=FileUtility.getFileType("essai.doc");
		assertThat(fileType, is(FileType.TEXT));

		fileType=FileUtility.getFileType("essai.docx");
		assertThat(fileType, is(FileType.TEXT));

		fileType=FileUtility.getFileType("essai.odt");
		assertThat(fileType, is(FileType.TEXT));

		fileType=FileUtility.getFileType("essai.xls");
		assertThat(fileType, is(FileType.SPREADSHEET));

		fileType=FileUtility.getFileType("essai.xlsx");
		assertThat(fileType, is(FileType.SPREADSHEET));

		fileType=FileUtility.getFileType("essai.ods");
		assertThat(fileType, is(FileType.SPREADSHEET));

		fileType=FileUtility.getFileType("essai.ppt");
		assertThat(fileType, is(FileType.PRESENTATION));

		fileType=FileUtility.getFileType("essai.pptx");
		assertThat(fileType, is(FileType.PRESENTATION));

		fileType=FileUtility.getFileType("essai.odp");
		assertThat(fileType, is(FileType.PRESENTATION));
	}

	@Test
	public void testIsManaged() throws Exception
	{
		assertThat(FileUtility.isManaged("essai.doc"), is(true));
		assertThat(FileUtility.isManaged("essai.docx"), is(true));
		assertThat(FileUtility.isManaged("essai.xls"), is(true));
		assertThat(FileUtility.isManaged("essai.xlsx"), is(true));
		assertThat(FileUtility.isManaged("essai.ppt"), is(true));
		assertThat(FileUtility.isManaged("essai.pptx"), is(true));
		assertThat(FileUtility.isManaged("essai.odt"), is(true));
		assertThat(FileUtility.isManaged("essai.ods"), is(true));
		assertThat(FileUtility.isManaged("essai.odp"), is(true));

		assertThat(FileUtility.isManaged("essai.avi"), is(false));
		assertThat(FileUtility.isManaged("essai.wav"), is(false));
	}

	@Test
	public void testIsValidMimetype() throws Exception
	{
		Blob blob=new StringBlob("",FileUtility.DOCX_MIME_TYPE,"UTF-8");
		blob.setFilename("essai.docx");
		assertThat(FileUtility.isValidMimetype(blob), is(true));

		blob=new StringBlob("",FileUtility.XLSX_MIME_TYPE,"UTF-8");
		blob.setFilename("essai.xlsx");
		assertThat(FileUtility.isValidMimetype(blob), is(true));

		blob=new StringBlob("",FileUtility.PPTX_MIME_TYPE,"UTF-8");
		blob.setFilename("essai.pptx");
		assertThat(FileUtility.isValidMimetype(blob), is(true));

		blob=new StringBlob("","application/octet-stream","UTF-8");
		blob.setFilename("essai.doc");
		assertThat(FileUtility.isValidMimetype(blob), is(false));
	}

	@Test
	public void testGetOnlyofficeMimeType() throws Exception
	{
		assertThat(FileUtility.getOnlyofficeMimeType("essai.doc"), is(FileUtility.DOCX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.docx"), is(FileUtility.DOCX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.txt"), is(FileUtility.DOCX_MIME_TYPE));

		assertThat(FileUtility.getOnlyofficeMimeType("essai.xls"), is(FileUtility.XLSX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.xlsx"), is(FileUtility.XLSX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.ods"), is(FileUtility.XLSX_MIME_TYPE));

		assertThat(FileUtility.getOnlyofficeMimeType("essai.ppt"), is(FileUtility.PPTX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.pptx"), is(FileUtility.PPTX_MIME_TYPE));
		assertThat(FileUtility.getOnlyofficeMimeType("essai.odp"), is(FileUtility.PPTX_MIME_TYPE));
	}

	@Test
	public void testGetOnlyOfficeExtension() throws Exception
	{
		assertThat(FileUtility.getOnlyOfficeExtension("essai.doc"), is(FileUtility.TEXT_EXTENSION));
		assertThat(FileUtility.getOnlyOfficeExtension("essai.xlsx"), is(FileUtility.SPREADSHEET_EXTENSION));
		assertThat(FileUtility.getOnlyOfficeExtension("essai.odp"), is(FileUtility.PRESENTATION_EXTENSION));
	}

	@Test
	public void testGetFileName() throws Exception
	{
		assertThat(FileUtility.getFileName("c:/ici/monfichier.fr.xls"), is("monfichier.fr.xls"));
	}

	@Test
	public void testGetFileNameWithoutExtension() throws Exception
	{
		assertThat(FileUtility.getFileNameWithoutExtension("c:/ici/monfichier.fr.xls"), is("monfichier.fr"));
	}

	@Test
	public void testGetFileExtension() throws Exception
	{
		assertThat(FileUtility.getFileExtension("c:/ici/monfichier.fr.xls"), is(".xls"));
	}

	@Test
	public void testGetEncoded() throws Exception
	{
		String encoded = FileUtility.getEncoded("c1","c2","c.3");
		String[] decoded=FileUtility.getDecoded(encoded);

		assertThat(decoded[0], is("c1"));
		assertThat(decoded[1], is("c2"));
		assertThat(decoded[2], is("c 3"));
	}
}
