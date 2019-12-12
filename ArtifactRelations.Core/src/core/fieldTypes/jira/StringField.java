package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.StringFieldValue;

public class StringField extends FieldType<StringFieldValue>{

	public StringField(String name, String id, StringFieldValue value) {
		super(name, id, value);
	}


}