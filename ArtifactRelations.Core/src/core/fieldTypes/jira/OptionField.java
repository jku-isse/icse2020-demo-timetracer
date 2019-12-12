package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Option;

public class OptionField extends FieldType<Option>{

	public OptionField(String name, String id, Option value) {
		super(name, id, value);
	}

}
