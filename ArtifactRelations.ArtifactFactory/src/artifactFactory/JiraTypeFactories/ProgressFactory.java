package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Progress;

public class ProgressFactory implements IFieldTypeFactory<Progress> {

	@SuppressWarnings("unchecked")
	@Override
	public Progress createFieldType(Object object) {

		if(object==null) return null;
		
		Progress progress = new Progress();
		
		Map<String,Object> map =  (Map<String,Object>) object;
		
		progress.setData(map);
		
		return progress;
	}

}
