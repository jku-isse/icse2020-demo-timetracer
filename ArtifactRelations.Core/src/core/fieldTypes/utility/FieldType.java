package core.fieldTypes.utility;

import core.fieldValues.common.Serializer;

public class FieldType<T extends Serializer> {
	
	protected String name;
	protected String id;
	protected T value;
	
	public FieldType(String name, String id, T value) {
		this.name = name;
		this.id = id;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}	
	
	public Object serialize(){
		return value.serialize();
	}
	
		
}
