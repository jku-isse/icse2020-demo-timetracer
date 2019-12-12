package artifactFactories.commonTypeFactories;


import artifactFactory.JiraTypeFactories.SerializableObject;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.common.Serializer;

public class ObjectFactory implements IFieldTypeFactory<Serializer>{

	@Override
	public Serializer createFieldType(Object object) {
		return new SerializableObject(object);
	}

}
