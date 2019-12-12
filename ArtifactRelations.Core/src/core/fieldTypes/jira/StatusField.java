package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Status;

public class StatusField extends FieldType<Status>{

	public StatusField(String self, String id, Status value) {
		super(self, id, value);
	}

}
