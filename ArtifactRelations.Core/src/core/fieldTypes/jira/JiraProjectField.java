package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Project;

public class JiraProjectField extends FieldType<Project>{

	public JiraProjectField(String self, String id, Project value) {
		super(self, id, value);
	}


}
