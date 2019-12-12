package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Priority;

public class PriorityFactory implements IFieldTypeFactory<Priority> {

	@SuppressWarnings("unchecked")
	@Override
	public Priority createFieldType(Object object) {

		if(object==null) return null;
		
		Priority priority = new Priority();

		Map<String, Object> map = (Map<String, Object>) object;
		
		priority.setData(map);
	
		return priority;
	}

}
