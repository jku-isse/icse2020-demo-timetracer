package artifactFactories.commonTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.StringFieldValue;

public class StringFactoryDeserialization implements IFieldTypeFactory<StringFieldValue> {

	@SuppressWarnings("unchecked")
	@Override
	public StringFieldValue createFieldType(Object object) {

		if(object == null) return null;		
		
		StringFieldValue stringFieldValue = new StringFieldValue();		

		if(object instanceof String) {
			stringFieldValue.setValue((String) object);
			return stringFieldValue;
		}
		
		Map<String, Object> map = (Map<String, Object>) object;		
		stringFieldValue.setValue((String) map.get("value"));	
		return stringFieldValue;
	}


}


