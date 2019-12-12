package core.fieldValues.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IntegerFieldValue extends Serializer{

	protected Map<String,Object> data;
	
	public IntegerFieldValue() {
		data = new HashMap<String, Object>();
	}
	
	public Optional<Integer> getValue() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((Integer) data.get("value"));	
	}

	public void setValue(Integer value) {
		if(data!=null) {
			data.put("value", value);
		}
	}
	
	@Override
	public Map<String, Object> serialize() {		
		return data;
	}

}
