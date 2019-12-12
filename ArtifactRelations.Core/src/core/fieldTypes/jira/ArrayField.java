package core.fieldTypes.jira;

import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.Array;
import core.fieldValues.common.Serializer;


public class ArrayField<T extends Serializer> extends FieldType<Array<T>>{

	public ArrayField(String name, String id, Array<T> value) {
		super(name, id, value);
	}

}
