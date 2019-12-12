package core.artifactFactory.mappers;

import java.util.Map;

public class IdValueMapper implements IidValueMapper {

	Map<String, Object> map;
	private String searcher;
	
	public IdValueMapper(Map<String, Object> map) {
		this.map = (Map<String,Object>) map;
	}
	
	@Override
	public Object map(String id) {		
		if(id==null) return null;
		return map.get(id);
	}
	
	@Override
	public String reverseMap(String value) {			
		
		if(value==null) return null;
		searcher = null;
		
		map.forEach((x,y) -> {
			if(y.equals(value)) searcher = x;
		});
		
		return searcher;
	}

}
