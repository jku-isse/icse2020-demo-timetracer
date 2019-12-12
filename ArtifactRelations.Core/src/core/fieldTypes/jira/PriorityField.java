package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Priority;

public class PriorityField extends FieldType<Priority>{

	public PriorityField(String name, String id, Priority value) {
		super(name, id, value);	
	}

}
