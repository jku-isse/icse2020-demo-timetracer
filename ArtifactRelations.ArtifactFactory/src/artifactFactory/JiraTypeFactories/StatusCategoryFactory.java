package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.StatusCategory;

public class StatusCategoryFactory implements IFieldTypeFactory<StatusCategory> {

	@SuppressWarnings("unchecked")
	@Override
	public StatusCategory createFieldType(Object object) {
		
		if(object==null)  return null;
		
		StatusCategory statusCategory = new StatusCategory();
		
		Map<String, Object> map = (Map<String, Object>) object;

		statusCategory.setData(map);
		
		return statusCategory;
	}

}
