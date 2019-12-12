package core.jira;

import java.sql.Timestamp;

public interface IJiraRestClient {

	public String getIssue(final String issueKey);
	
	public String fetchUpdatedIssuesSince(Timestamp startFrom, int startAt, int maxResults);
	
	public String getIssues(int startAt, int maxResults);
	
	public String getNamesAndScheme();
	
	public String getStatus(String id);
	
	public String getUser(String key);
	
	public String getProject(String id);
	
	public String getIssueType(String id);
	
	public String getVersion(String id);
	
	public String getPriority(String id);
	
	public String getOption(String id);		
	
	public String getIssueTypes();

}
