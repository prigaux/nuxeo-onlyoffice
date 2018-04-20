package fr.edu.lyon.nuxeo.onlyoffice.service;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Objet destiné à fournir une méthode pour calculer l'adresse
 * de goback dans la configuration OlyOffice
 * @author ftorchet
 *
 */
public interface GobackResolver
{
	String getGobackUrl(OnlyOfficeContext context, DocumentModel doc);
}
