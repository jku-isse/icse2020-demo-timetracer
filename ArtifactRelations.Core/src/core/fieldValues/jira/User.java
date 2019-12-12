package core.fieldValues.jira;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class User extends Serializer {

	protected Map<String,Object> data;
		
	@SuppressWarnings("unused")
	private Optional<String> getSelf() {
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
	
	public Optional<String> getKey() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("key"));
	}

	public void setKey(String key) {
		if(data!=null) {
			data.put("key", key);
		}
	}

	public Optional<String> getEmailAddress() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("emailAddress"));
	}

	public void setEmailAddress(String emailAddress) {
		if(data!=null) {
			data.put("emailAddress", emailAddress);
		}
	}

	public Optional<String> getDisplayName() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("displayName"));
	}

	public void setDisplayName(String displayName) {
		if(data!=null) {
			data.put("displayName", displayName);
		}
	}

	public Optional<String> getTimeZone() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("timeZone"));
	}

	public void setTimeZone(String timeZone) {
		if(data!=null) {
			data.put("timeZone", timeZone);
		}
	}

	public Optional<Boolean> isActive() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((Boolean) data.get("total"));	
	}

	public void setActive(boolean active) {
		if(data!=null) {
			data.put("active", active);
		}
	}

	public Optional<String> getName() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((String) data.get("name"));	}

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
		if(data!=null) {
			this.data = data;
		}
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
