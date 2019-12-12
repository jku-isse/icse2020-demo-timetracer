package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Progress;

public class ProgressField extends FieldType<Progress> {

	public ProgressField(String name, String id, Progress value) {
		super(name, id, value);
	}

}
