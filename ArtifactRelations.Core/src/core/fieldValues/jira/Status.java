package core.fieldValues.jira;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class Status extends Serializer {

	protected Map<String,Object> data;

	
	public Status() {	
		super();
	}
	

	public Optional<String> getDescription() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("decription"));	}

	
	public void setDescription(String description) {
		if(data!=null) {
			data.put("description", description);
		}	
	}

	public Optional<String> getIconUrl() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("iconUrl"));	}

	public void setIconUrl(String iconUrl) {
		if(data!=null) {
			data.put("iconUrl", iconUrl);
		}	
	}

	@SuppressWarnings("unchecked")
	public Optional<StatusCategory> getStatusCategory() {
		if(data==null) {
			return Optional.empty();
		}
		
		StatusCategory statusCategory = new StatusCategory();
		statusCategory.setData((Map<String,Object>)data.get("statusCategory"));
		
		return Optional.ofNullable(statusCategory);	
	}

	public void setStatusCategory(StatusCategory statusCategory) {
		if(data!=null) {
			data.put("statusCategory", statusCategory.serialize());
		}
	}
	
	public Optional<String> getSelf() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("self"));	
	}


	public void setSelf(String self) {
		if(data!=null) {
			data.put("self", self);
		}	
	}


	public Optional<String> getId() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("id"));	
	}


	public void setId(String id) {
		if(data!=null) {
			data.put("id", id);
		}	
	}


	public Optional<String> getName() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("name"));	
	}


	public void setName(String name) {
		if(data!=null) {
			data.put("name", name);
		}	
	}
	
	public Optional<Map<String, Object>> getData() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable(data);
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	
	public Map<String, Object> serialize(){
		data = NullTerminator.terminate(data);
		return data;	
	}

	@Override
	public String toString() {
		
		return data.toString();

	}
	
}
