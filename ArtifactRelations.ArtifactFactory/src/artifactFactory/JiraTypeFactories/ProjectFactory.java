package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Project;

public class ProjectFactory implements IFieldTypeFactory<Project> {

	@SuppressWarnings("unchecked")
	public Project createFieldType(Object object) {

		if(object==null)  return null;
		
		Project project = new Project();
		
		Map<String, Object> map = (Map<String, Object>) object;
		
		project.setData(map);
				
		return project;
	}

	
}
