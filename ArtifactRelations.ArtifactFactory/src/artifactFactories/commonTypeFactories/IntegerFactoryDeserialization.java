package artifactFactories.commonTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.IntegerFieldValue;

public class IntegerFactoryDeserialization implements IFieldTypeFactory<IntegerFieldValue> {

	@SuppressWarnings("unchecked")
	@Override
	public IntegerFieldValue createFieldType(Object object) {

		if(object == null) return null;		
		
		IntegerFieldValue integerFieldValue = new IntegerFieldValue();		

		if(object instanceof String) {
			integerFieldValue.setValue((Integer) object);
			return integerFieldValue;
		}
		
		Map<String, Object> map = (Map<String, Object>) object;		
		integerFieldValue.setValue((Integer) map.get("value"));	
		return integerFieldValue;
		
	}


}

