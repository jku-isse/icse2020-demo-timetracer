package core.fieldValues.jira;

import java.util.Map;
import java.util.Optional;

import core.fieldValues.common.Serializer;
import core.utility.NullTerminator;

public class Progress extends Serializer {
	
	protected Map<String,Object> data;
	
	public Optional<Integer> getProgress() {		
		if(data==null) {
			return Optional.empty();
		}
			
		return  Optional.ofNullable((Integer) data.get("progress"));		
	}
	
	public void setProgress(Integer progress) {
		if(data!=null) {
			data.put("progress", progress);
		}
	}
	
	public Optional<Integer> getTotal() {
		if(data==null) {
			return Optional.empty();
		}
		
		return Optional.ofNullable((Integer) data.get("total"));
	}
	
	public void setTotal(Integer total) {
		if(data!=null) {
			data.put("total", total);
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

	@Override
	public String toString() {
		
		return data.toString();

	}

	@Override
	public Map<String, Object> serialize() {
		data = NullTerminator.terminate(data);
		return data;
	}

	
}
