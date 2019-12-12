package artifactFactories.commonTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.IntegerFieldValue;

public class IntegerFactory implements IFieldTypeFactory<IntegerFieldValue> {

	@SuppressWarnings("unchecked")
	@Override
	public IntegerFieldValue createFieldType(Object object) {

		if(object == null) return null;		
		
		IntegerFieldValue integerFieldValue = new IntegerFieldValue();		
		
		try{
			integerFieldValue.setValue((Integer) object);
		} catch(ClassCastException e) {
			integerFieldValue.setValue((int) (long) ((Map<String, Object>) object).get("value"));
		}
			
		return integerFieldValue;
	}
	
}
