package artifactFactory.mappers;

import java.util.HashMap;
import java.util.Map;

import artifactFactories.commonTypeFactories.IntegerFactory;
import artifactFactories.commonTypeFactories.StringFactory;
import artifactFactory.JiraTypeFactories.IssueTypeFactory;
import artifactFactory.JiraTypeFactories.OptionFactory;
import artifactFactory.JiraTypeFactories.PriorityFactory;
import artifactFactory.JiraTypeFactories.ProgressFactory;
import artifactFactory.JiraTypeFactories.ProjectFactory;
import artifactFactory.JiraTypeFactories.StatusFactory;
import artifactFactory.JiraTypeFactories.UserFactory;
import artifactFactory.JiraTypeFactories.VersionFactory;
import core.artifactFactory.typeFactories.IFieldTypeFactory;

public enum TypeFactories {

	@SuppressWarnings({ "serial", "rawtypes" })
	Jira(
		
		new HashMap<String, IFieldTypeFactory>(){{
			put("project", new ProjectFactory());
			put("user", new UserFactory());	
			put("status", new StatusFactory());	
			put("priority", new PriorityFactory());	
			put("progress", new ProgressFactory());	
			put("issuetype", new IssueTypeFactory());
			put("string", new StringFactory());
			put("option", new OptionFactory());
			put("version", new VersionFactory());
		}}
		
	),	
	@SuppressWarnings({ "serial", "rawtypes" })
	Jama(
			
		//TO-DO:
		//until now the jama-data always only linked with the 
		//id of the corresponding user, or Lookup etc.
		//therefore IntegerFactory and StringFactory suffice???
		new HashMap<String, IFieldTypeFactory>(){{
			put("STRING", new StringFactory());
			put("DATE", new StringFactory());
			put("TEXT", new StringFactory());
			put("INTEGER", new IntegerFactory());
			put("LOOKUP", new IntegerFactory());
			put("USER", new IntegerFactory());

		}}
		
	);
	
	@SuppressWarnings("rawtypes")
	Map<String, IFieldTypeFactory> value;
	
	@SuppressWarnings("rawtypes")
	TypeFactories(Map<String, IFieldTypeFactory> mapping){
		value = mapping;
	}
	
	@SuppressWarnings("rawtypes")
	public Map<String, IFieldTypeFactory> getValue() {
		return value;
	}
	
}
