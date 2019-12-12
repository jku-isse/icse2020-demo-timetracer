package core.fieldValues.common;


import java.util.HashMap;
import java.util.Map;


public class Array<T extends Serializer> extends Serializer{

	protected T[] items;

	public Array() {

	}

	public T[] getItems() {
		return items;
	}

	public void setItems(T[] items) {
		this.items = items;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		
		for(T item : items) {
			sb.append(item + ",");
		}
		
		sb.append("]");
		
		return sb.toString();
	}

	@Override
	public Map<String, Object> serialize() {

		Map<String, Object> array = new HashMap<String, Object>();		
        			
		for(int i=0; i<items.length; i++) {
			if(items[i]!=null) array.put(Integer.toString(i), items[i].serialize());
		}
	        
		return array;
	}

}
