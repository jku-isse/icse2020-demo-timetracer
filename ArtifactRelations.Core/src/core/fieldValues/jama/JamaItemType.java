package core.fieldValues.jama;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class JamaItemType extends Serializer{

protected Map<String,Object> data;

	public Optional<Integer> getId() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Integer) data.get("id"));	
	}
	
	public void setId(Integer id) {
		if(data!=null) {
			data.put("id", id);
		}	
	}	

	public Optional<Integer> getTypeKey() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Integer) data.get("typeKey"));	
	}
	
	public void setTypeKey(String typeKey) {
		if(data!=null) {
			data.put("typeKey", typeKey);
		}	
	}	
	
	public Optional<Boolean> getSystem() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Boolean) data.get("system"));	
	}
	
	public void setSystem(String system) {
		if(data!=null) {
			data.put("system", system);
		}	
	}	
	
	public Optional<String> getDescription() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("description"));	
	}
	
	public void setDescription(String description) {
		if(data!=null) {
			data.put("description", description);
		}	
	}	
	
	public Optional<String> getCategory() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("category"));	
	}
	
	public void setCategory(String category) {
		if(data!=null) {
			data.put("category", category);
		}	
	}	
	
	public Optional<String> getDisplayPlural() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("displayPlural"));	
	}
	
	public void setDisplayPlural(String displayPlural) {
		if(data!=null) {
			data.put("displayPlural", displayPlural);
		}	
	}

	@Override
	public Map<String, Object> serialize(){
		data = NullTerminator.terminate(data);
		return data;	
	}
	
}
