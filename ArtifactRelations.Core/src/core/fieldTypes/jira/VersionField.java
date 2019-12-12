package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jira.Version;

public class VersionField extends FieldType<Version>{

	public VersionField(String name, String id, Version value) {
		super(name, id, value);
	}


}
