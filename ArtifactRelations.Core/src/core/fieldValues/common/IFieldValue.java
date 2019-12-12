package core.fieldValues.common;


public interface IFieldValue{
	
	/**
	 * every field value is be converted to a map,
	 * that contains the basic json-data as values needed to 
	 * restore a deserialized object of the instance.
	 * 
	 * @return Object(Map<String, String)
	 */
	public Object serialize();
	
}
