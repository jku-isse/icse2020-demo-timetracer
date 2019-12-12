package artifactFactories.commonTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.StringFieldValue;

public class StringFactory implements IFieldTypeFactory<StringFieldValue> {

	@SuppressWarnings("unchecked")
	@Override
	public StringFieldValue createFieldType(Object object) {

		if(object == null) return null;		
		
		StringFieldValue stringFieldValue = new StringFieldValue();		
		
		//in some cases the StringFactory is also called with a HashMap
		//that holds the string with the key value
		try{
			stringFieldValue.setValue((String) object);
		} catch(ClassCastException e) {
			stringFieldValue.setValue((String) ((Map<String, Object>) object).get("value"));
		}
			
		return stringFieldValue;
	}


}
