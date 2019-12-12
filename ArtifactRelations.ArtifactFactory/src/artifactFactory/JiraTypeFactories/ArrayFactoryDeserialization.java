package artifactFactory.JiraTypeFactories;

import java.util.ArrayList;
import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.Array;
import core.fieldValues.common.Serializer;

public class ArrayFactoryDeserialization<T extends Serializer> implements IFieldTypeFactory<Array<T>> {

	IFieldTypeFactory<T> factory;
	
	public ArrayFactoryDeserialization(IFieldTypeFactory<T> factory) {
		this.factory = factory;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Array<T> createFieldType(Object object) {

		if(object==null)  return null;
				
		Array<T> array = new Array();
		ArrayList<Object> list = new ArrayList<Object>();			
				
		((Map<String, Object>) object).forEach((x,y) -> list.add(Integer.parseInt(x),y));
		
		T[] items = (T[]) new Serializer[list.size()];
		
		if(factory!=null) {
			for(int i=0; i<list.size(); i++) {
				items[i] = factory.createFieldType(list.get(i));
			}
		}
		
		array.setItems(items);
				
		return array;
				
	}

}