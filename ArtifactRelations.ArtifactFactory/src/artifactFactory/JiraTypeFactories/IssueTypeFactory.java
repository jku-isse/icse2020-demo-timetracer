package artifactFactory.JiraTypeFactories;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.IssueType;

public class IssueTypeFactory implements IFieldTypeFactory<IssueType> {

	@SuppressWarnings("unchecked")
	@Override
	public IssueType createFieldType(Object object) {

		if(object==null) return null;
		
		IssueType issueType = new IssueType();
		
		Map<String, Object> map = (Map<String, Object>) object;
		
		issueType.setData(map);
		
		return issueType;
	}

}
