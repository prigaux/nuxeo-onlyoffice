package fr.edu.lyon.nuxeo.onlyoffice.operation;

import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;

import fr.edu.lyon.nuxeo.onlyoffice.util.OnlyOfficeUserHelper;
import net.sf.json.JSONObject;

@Operation(id = CanUseOnlyOfficeOperation.ID, category = Constants.CAT_USERS_GROUPS, label = "Permissions OnlyOffice", description = "Teste si un utilisateur peut utiliser OnlyOffice")
public class CanUseOnlyOfficeOperation
{
	public static final String ID = "User.CanUseOnlyOffice";

	@Context private CoreSession session;

	@OperationMethod
	public Blob run()
	{
		JSONObject result=new JSONObject();
		result.accumulate("onlyoffice", OnlyOfficeUserHelper.canUseOnlyOffice(session));

		return new StringBlob(result.toString(), "application/json");
	}
}
