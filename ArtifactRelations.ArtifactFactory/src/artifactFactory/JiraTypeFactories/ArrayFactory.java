package artifactFactory.JiraTypeFactories;

import java.util.ArrayList;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.Array;
import core.fieldValues.common.Serializer;



public class ArrayFactory<T extends Serializer> implements IFieldTypeFactory<Array<T>> {

	IFieldTypeFactory<T> factory;
	
	public ArrayFactory(IFieldTypeFactory<T> factory) {
		this.factory = factory;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Array<T> createFieldType(Object object) {

		if(object==null)  return null;
		
		Array<T> array = new Array();
		ArrayList<Object> list;		
		list = (ArrayList<Object>) object;
								
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
