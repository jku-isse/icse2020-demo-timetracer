package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.IssueType;

public class IssueTypeField extends FieldType<IssueType>{

	public IssueTypeField(String name, String id, IssueType value) {
		super(name, id, value);
	}

}
