package artifactFactory.mappers;

import java.util.Map;
import java.util.logging.Level;

import artifactFactories.commonTypeFactories.IntegerFactoryDeserialization;
import artifactFactories.commonTypeFactories.ObjectFactory;
import artifactFactories.commonTypeFactories.StringFactoryDeserialization;
import core.artifactFactory.mappers.AbstractIdFactoryMapper;
import core.artifactFactory.mappers.IdValueMapper;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.services.ErrorLoggerServiceFactory;

public class JamaIdFactoryMapper extends AbstractIdFactoryMapper{

	@SuppressWarnings("rawtypes")
	public JamaIdFactoryMapper(IdValueMapper idTypeMapper, Map<String, IFieldTypeFactory> typeToFactory, boolean deserialize) {
		super(idTypeMapper, typeToFactory, deserialize);
		
		//some things are stored differently when serialized 
		//from the original json-File fetched from Jira
		if(deserialize) {
			typeToFactory.put("STRING", new StringFactoryDeserialization());
			typeToFactory.put("INTEGER", new IntegerFactoryDeserialization());
		}
		
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IFieldTypeFactory map(String id) {
		
		resultFactory = null;
		String type = null;
				
		//find the corresponding type					
		//return a factory for creating an instance of that object
		type = (String) idTypeMapper.map(id);
						
		//if the current type is an array, we have to check for the itemType
		if(type!=null) {	
			resultFactory = typeToFactory.get(type);
		}
		
		//in case the current type is a one line string
		if(resultFactory==null) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.CONFIG, "IdFactoryMapper: map(String ig): No specific information about type therfore using ObjectFactory(type: " + id + ")");
			resultFactory = new ObjectFactory();	
		}
	
		return resultFactory;
	}

}
