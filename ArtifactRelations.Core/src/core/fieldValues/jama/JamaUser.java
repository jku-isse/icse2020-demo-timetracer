package core.fieldValues.jama;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class JamaUser extends Serializer{

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

	public Optional<String> getFirstName() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("firstName"));	
	}
	
	public void setFirstName(String firstName) {
		if(data!=null) {
			data.put("firstName", firstName);
		}	
	}	
	
	public Optional<String> getLastName() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("lastName"));	
	}
	
	public void setLastName(String lastName) {
		if(data!=null) {
			data.put("lastName", lastName);
		}	
	}	
	
	public Optional<String> getLicenseType() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("licenseType"));	
	}
	
	public void setLicenseType(String licenseType) {
		if(data!=null) {
			data.put("licenseType", licenseType);
		}	
	}	
	
	public Optional<String> getPhone() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("phone"));	
	}
	
	public void setPhone(String phone) {
		if(data!=null) {
			data.put("phone", phone);
		}	
	}	
	
	public Optional<String> getAvatarUrl() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("avatarUrl"));	
	}
	
	public void setAvatarUrl(String avatarUrl) {
		if(data!=null) {
			data.put("avatarUrl", avatarUrl);
		}	
	}	
	
	public Optional<Boolean> getActive() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((Boolean) data.get("active"));	
	}
	
	public void setAvatarUrl(Boolean active) {
		if(data!=null) {
			data.put("active", active);
		}	
	}	
	
	public Optional<String> getLocation() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("location"));	
	}
	
	public void setLocation(String location) {
		if(data!=null) {
			data.put("location", location);
		}	
	}	
	
	public Optional<String> getTitle() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("title"));	
	}
	
	public void setTitle(String title) {
		if(data!=null) {
			data.put("title", title);
		}	
	}	
	

	public Optional<String> getEMail() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("email"));	
	}
	
	public void setEmail(String email) {
		if(data!=null) {
			data.put("email", email);
		}	
	}	
	
	public Optional<String> getUsername() {
		if(data==null) {
			return Optional.empty();
		}			
		return  Optional.ofNullable((String) data.get("username"));	
	}
	
	public void setUsername(String username) {
		if(data!=null) {
			data.put("username", username);
		}	
	}

	@Override
	public Map<String, Object> serialize(){
		data = NullTerminator.terminate(data);
		return data;	
	}

}
