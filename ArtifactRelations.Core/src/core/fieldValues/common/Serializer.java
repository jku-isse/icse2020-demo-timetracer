package core.fieldValues.common;

import java.util.Map;

public abstract class Serializer implements IFieldValue {

	public abstract Map<String, Object> serialize();
	
}
