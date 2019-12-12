package core.fieldValues.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StringFieldValue extends Serializer{

	protected Map<String,Object> data;
	
	public StringFieldValue() {
		data = new HashMap<String, Object>();
	}
	
	public Optional<String> getValue() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((String) data.get("value"));	
	}

	public void setValue(String value) {
		if(data!=null) {
			data.put("value", value);
		}
	}
	
	@Override
	public Map<String, Object> serialize() {		
		return data;
	}

}
