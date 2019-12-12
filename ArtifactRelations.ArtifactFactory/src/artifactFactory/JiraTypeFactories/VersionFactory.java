package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.Version;

public class VersionFactory implements IFieldTypeFactory<Version> {

	@SuppressWarnings("unchecked")
	@Override
	public Version createFieldType(Object object) {

		if(object==null)  return null;

		Version version = new Version();
		
		Map<String, Object> map = (Map<String,Object>) object;
		
		version.setData(map);
				
		return version;
	}

}
