package core.artifactFactory.typeFactories;

import core.fieldValues.common.Serializer;

public interface IFieldTypeFactory<T extends Serializer> {

	/**
	 * this method expects the jsonData of the type t
	 * as a Map<String, Object>.
	 * 
	 * @param object
	 * @return T
	 */
	public T createFieldType(Object object);
	
}
