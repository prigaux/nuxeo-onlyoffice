package fr.edu.lyon.nuxeo.onlyoffice.service;

import java.io.IOException;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface OnlyofficeService
{
	/**
	 * Configuration OnlyOffice côté client
	 * @return
	 */
	OnlyofficeConfig getConfig();

	/**
	 * Déclenchement d'événements en fonction du statut envoyé par OnlyOffice
	 * @param session connexion au référentiel
	 * @param docId document de référence
	 * @param jsonEvent datas transmises par OnlyOffice
	 * @param coEdition vrai si le mode coédition est activé
	 * @param creation vrai si on est le cadre d'une création
	 * @return code d'erreur
	 */
	String trackDocument(CoreSession session, String docId, String jsonEvent, boolean coEdition, boolean creation);

	/**
	 * Blob (contenu du fichier) associé à un document donné
	 * @param session connexion au référentiel
	 * @param doc  document
	 * @return
	 * @throws IOException
	 */
	byte[] getBlob(CoreSession session, DocumentModel doc) throws IOException;

	/**
	 * Blob (contenu du fichier) associé à un document en création
	 * @param session connexion au référentiel
	 * @param filetype type de fichier (docx, xlx ou pptx)
	 * @return
	 * @throws IOException
	 */
	byte[] getBlobFromTemplate(CoreSession session, String filetype) throws IOException;
}