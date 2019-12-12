package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Status;

public class StatusFactory implements IFieldTypeFactory<Status> {

	@SuppressWarnings("unchecked")
	@Override
	public Status createFieldType(Object object) {
		
		if(object==null)  return null;
		
		Status status = new Status();
		
		Map<String, Object> map = (Map<String, Object>) object;
		
		status.setData(map);
		
		return status;
		
	}

}
