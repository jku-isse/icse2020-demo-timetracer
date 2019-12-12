package core.fieldTypes.jama;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.jama.JamaUser;

public class JamaUserField extends FieldType<JamaUser> {

	public JamaUserField(String name, String id, JamaUser value) {
		super(name, id, value);
	}

}
