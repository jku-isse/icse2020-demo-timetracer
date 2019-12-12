package core.utility;

import java.util.Map;

public class NullTerminator {

	public static Map<String, Object> terminate(Map<String, Object> map) {
		
		map.forEach((x,y) -> {
			if(y==null) map.remove(y);
		});
		
		return map;
	}
	
}
