package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.StatusCategory;

public class StatusCategoryField extends FieldType<StatusCategory> {

	public StatusCategoryField(String self, String id, StatusCategory value) {
		super(self, id, value);
	}

}
