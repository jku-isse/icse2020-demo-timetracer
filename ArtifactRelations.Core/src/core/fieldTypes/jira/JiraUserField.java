package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.User;

public class JiraUserField extends FieldType<User> {

	public JiraUserField(String self, String id, User value) {
		super(self, id, value);
	}

}
