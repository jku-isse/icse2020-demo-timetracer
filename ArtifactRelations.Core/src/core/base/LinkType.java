package core.base;

import java.util.Map;
import java.util.Optional;

public class LinkType {
	
	protected Map<String, Object> data;
	
	public LinkType(Map<String, Object> data) {
		this.data = data;
	}
	
	public Optional<String> getId() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((String) data.get("id"));	
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
			
		return  Optional.ofNullable((String) data.get("name"));	
	}
	
	public void setName(String name) {
		if(data!=null) {
			data.put("name", name);
		}
	}
	
	public Optional<String> getInward() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((String) data.get("inward"));	
	}
	
	public void setInward(String inward) {
		if(data!=null) {
			data.put("inward", inward);
		}
	}
	
	public Optional<String> getOutward() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((String) data.get("outward"));	
	}
	
	public void setOutward(String outward) {
		if(data!=null) {
			data.put("outward", outward);
		}
	}
	
	public Optional<String> getSelf() {
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((String) data.get("self"));	
	}
	
	public void setSelf(String self) {
		if(data!=null) {
			data.put("self", self);
		}
	}
	
}
