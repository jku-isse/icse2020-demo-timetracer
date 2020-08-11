package artifactFactory.JiraTypeFactories;

import java.util.HashMap;
import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.fieldValues.jira.User;

public class UserFactory implements IFieldTypeFactory<User> {

	@SuppressWarnings("unchecked")
	@Override
	public User createFieldType(Object object) {
	
//		if(object==null)  return null;
		
		User user = new User();
		Map<String, Object> map = null;			
		if(object==null || object.toString().equals("null"))  {
			map = new HashMap<String, Object>();
		} else {
			map = (Map<String, Object>) object;
		}
		user.setData(map);
		
		return user;
		
	}

}
