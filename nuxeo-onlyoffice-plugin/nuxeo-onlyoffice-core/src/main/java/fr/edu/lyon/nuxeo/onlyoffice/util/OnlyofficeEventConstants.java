package fr.edu.lyon.nuxeo.onlyoffice.util;

/**
 * Constantes associées aux événements de type OnlyOffice
 * 
 * @author ftorchet
 *
 */
public class OnlyofficeEventConstants
{
	private OnlyofficeEventConstants()
	{
		super();
	}

	public static final String ONLYOFFICE_BEFORE_EDITION_EVENT = "onlyofficeBeforeEdition";
	public static final String ONLYOFFICE_AFTER_EDITION_EVENT = "onlyofficeAfterEdition";

	public static final String ONLYOFFICE_JSON_EVENT_CONTENT = "onlyofficeJsonEvent";
	public static final String COEDITION_EVENT_CONTENT = "coedition";
	public static final String CREATION_EVENT_CONTENT = "creation";
	public static final String ONLYOFFICE_STATUS = "status";
	public static final String FILENAME_EVENT_CONTENT = "filename";
	public static final String FILETYPE_EVENT_CONTENT = "filetype";
}
