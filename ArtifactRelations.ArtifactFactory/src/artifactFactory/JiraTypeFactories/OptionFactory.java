package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Option;

public class OptionFactory implements IFieldTypeFactory<Option>{

	@SuppressWarnings("unchecked")
	@Override
	public Option createFieldType(Object object) {
		
		if(object==null)  return null;
		
		Option option = new Option();
		
		Map<String, Object> map = (Map<String, Object>) object;
	
		option.setData(map);
						
		return option;
	}


}
