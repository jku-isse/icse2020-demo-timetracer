package core.fieldValues.jama;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class JamaProject extends Serializer {

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

	public Optional<Integer> getParent() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Integer) data.get("parent"));	
	}
	
	public void setParent(Integer parentId) {
		if(data!=null) {
			data.put("parent", parentId);
		}	
	}	
	
	public Optional<String> getProjectKey() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("projectKey"));	
	}
	
	public void setProjectKey(String projectKey) {
		if(data!=null) {
			data.put("projectKey", projectKey);
		}	
	}	
	
	public Optional<Boolean> isFolder() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Boolean) data.get("isFolder"));	
	}
	
	public void setIsFolder(Boolean isFolder) {
		if(data!=null) {
			data.put("isFolder", isFolder);
		}	
	}	
	
	public Optional<String> getCreatedDate() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("createdDate"));	
	}
	
	public void setCreatedDate(String createdDate) {
		if(data!=null) {
			data.put("createdDate", createdDate);
		}	
	}	
	
	public Optional<String> getCreatedBy() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("createdBy"));	
	}
	
	public void setCreatedBy(String createdBy) {
		if(data!=null) {
			data.put("createdBy", createdBy);
		}	
	}	
	
	public Optional<Boolean> getModifiedDate() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Boolean) data.get("modifiedDate"));	
	}
	
	public void setModifiedDate(Boolean modifiedDate) {
		if(data!=null) {
			data.put("modifiedDate", modifiedDate);
		}	
	}	
	
	public Optional<String> getmodifiedBy() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("modifiedBy"));	
	}
	
	public void setModifiedBy(String modifiedBy) {
		if(data!=null) {
			data.put("modifiedBy", modifiedBy);
		}	
	}

	@Override
	public Map<String, Object> serialize(){
		data = NullTerminator.terminate(data);
		return data;	
	}

	
}

