package artifactFactory.mappers;

import java.util.Map;
import java.util.logging.Level;

import artifactFactories.commonTypeFactories.ObjectFactory;
import artifactFactories.commonTypeFactories.StringFactoryDeserialization;
import artifactFactory.JiraTypeFactories.ArrayFactory;
import artifactFactory.JiraTypeFactories.ArrayFactoryDeserialization;
import core.artifactFactory.mappers.AbstractIdFactoryMapper;
import core.artifactFactory.mappers.IdValueMapper;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.services.ErrorLoggerServiceFactory;

public class JiraIdFactoryMapper extends AbstractIdFactoryMapper{

	@SuppressWarnings("rawtypes")
	public JiraIdFactoryMapper(IdValueMapper idTypeMapper, Map<String, IFieldTypeFactory> typeToFactory, boolean deserialize) {				
		super(idTypeMapper, typeToFactory, deserialize);	
		
		//some things are stored differently when serialized 
		//from the original json-File fetched from Jira
		if(deserialize) {
			typeToFactory.put("string", new StringFactoryDeserialization());
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public IFieldTypeFactory map(String id) {
		
		if(id==null) return null;
		
		resultFactory = null;
		String type = null;
				
		//find the corresponding schema						
		//return a factory for creating an instance of that object
		Map<String, String> map = (Map<String, String>) idTypeMapper.map(id);
		if(map!=null) type = map.get("type");	
						
		//if the current type is an array, we have to check for the itemType
		if(type!=null) {	
			if(type.equals("array")) resultFactory = getArrayTypeFactory(idTypeMapper.map(id));
			else resultFactory = typeToFactory.get(type);
		}
		
		//in case the current type is a one line string
		if(resultFactory==null) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.FINEST, "JiraIdFactoryMapper: map(String id): No specific information about type therfore using ObjectFactory(type: " + id + ")");
			resultFactory = new ObjectFactory();	
		}
	
		return resultFactory;
		
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IFieldTypeFactory getArrayTypeFactory(Object y) {		
		
		IFieldTypeFactory factory = typeToFactory.get(((Map<String, String>) y).get("items"));
		
		if(deserialize) {
			return new ArrayFactoryDeserialization(factory);
		} else {
			return new ArrayFactory(factory);
		}
		
	}
		
	
}
