package jiraconnector.connector;

import java.io.*;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.logging.Level;

import com.atlassian.httpclient.api.HttpClient;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import core.persistence.IJiraArtifactService;
import core.services.ErrorLoggerServiceFactory;

public class JiraArtifactService implements IJiraArtifactService {
	
	
	private static final int ISSUES_PER_ACCESS=50;

	private Map<String, Object> artifactMap;
	private AsynchronousIssueRestClientRawExtension rawExtension;
		
	public JiraArtifactService() throws IOException {
		Properties props = new Properties(); 
		props.load(new FileInputStream("local-app.properties"));
		
		String uri =  props.getProperty("jiraServerURI");
		String username =  props.getProperty("jiraConnectorUsername");
		String pw =  props.getProperty("jiraConnectorPassword");
		
		URI baseUri = URI.create(uri);
		HttpClientFactory.init(uri, username, pw);
		HttpClient client = HttpClientFactory.createHttpClient();
		
		rawExtension = new AsynchronousIssueRestClientRawExtension(client, baseUri);
		artifactMap = new HashMap<String, Object>();
	}
	
	@Override
	public Map<String, Object> getArtifact(String key) throws IOException {
		
		//fetching the issue from the database via REST-API
		String json = rawExtension.getIssue(key);		
		Map<String, Object> issue =  jsonToMap(json);
			
		return issue;
	}
	
	@Override
	public String getArtifactIdFromKey(String key) throws IOException {
		
		/*//fetching the issue from the database via REST-API
		String json = rawExtension.getIssue(key);		
		Map<String, Object> issue =  jsonToMap(json);			
		return (String) issue.get("id");*/

		return (String) ((Map<String, Object>) artifactMap.get(key)).get("id");
		
	}

	private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<Object> getAllArtifacts() throws IOException {


	/*	String json = rawExtension.getIssues(0, ISSUES_PER_ACCESS);
		Map<String, Object> jsonMap = jsonToMap(json);
		ArrayList<Object> issues = (ArrayList<Object>) jsonMap.get("issues");
		
		int totalIssues = (Integer) jsonMap.get("total");
		int fetchedIssues = ISSUES_PER_ACCESS;
		
		while(fetchedIssues<totalIssues) {
			json = rawExtension.getIssues(fetchedIssues, ISSUES_PER_ACCESS);
			FileOutputStream fileOutputStream = new FileOutputStream("safety.txt");
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
			bw.write(json +"," + "\n");
			jsonMap = jsonToMap(json);
			issues.addAll((ArrayList<Object>) jsonMap.get("issues"));
			fetchedIssues = fetchedIssues + ISSUES_PER_ACCESS;
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "Fetsched " + fetchedIssues + " from " + "total issuemaps");
		}
	*/

		/**
		 * Dronology Raw Data
		 */
		Map<String, Object> map;
		StringBuilder sb = new StringBuilder();
		String line;

		BufferedReader br = new BufferedReader (new FileReader("Dronology_items.json"));
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		map = jsonToMap(sb.toString());
		ArrayList<Object> issues = (ArrayList<Object>) map.get("issues");

		sb = new StringBuilder();
		br = new BufferedReader (new FileReader("Dronology_items_1.json"));
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		map = jsonToMap(sb.toString());
		issues.addAll((ArrayList<Object>) map.get("issues"));

		issues.forEach( issue -> {
			artifactMap.put(((String) ((Map<String, Object>) issue).get("key")), issue);
		});

		return issues;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getNames() throws  IOException{
		String json = rawExtension.getNamesAndScheme();
		Map<String, Object> names = (Map<String, Object>) jsonToMap(json).get("names");
		
		//TO-DO: look for a more generic solution like String.contains()
		//because Jira only return the value Fix Version's
		//which is then never used instead of the two below
		names.put("fixVersions", "Fix Version");
		
		return names;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getSchema() throws JsonParseException, JsonMappingException, IOException{
		String json = rawExtension.getNamesAndScheme();
		Map<String, Object> schema = (Map<String, Object>) jsonToMap(json).get("schema");
		return schema;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getLinkTypes() throws JsonParseException, JsonMappingException, IOException {
		String json = rawExtension.getIssueTypes();
		ArrayList<Object> linkTypes = (ArrayList<Object>) jsonToMap(json).get("issueLinkTypes");
		Map<String, Object> result = new HashMap<String, Object>();

		linkTypes.forEach(linkType -> {
			result.put((String) ((Map<String, Object>) linkType).get("id"), linkType);
		});
		
		return result;
	}

	@Override
	public Map<String, Object> getStatus(String statusId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getStatus(statusId));
	}

	@Override
	public Map<String, Object> getIssueType(String issueTypeId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getIssueType(issueTypeId));
	}

	@Override
	public Map<String, Object> getProject(String projectId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getProject(projectId));
	}

	@Override
	public Map<String, Object> getUser(String userKey) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getUser(userKey));
	}

	@Override
	public Map<String, Object> getPriority(String priorityId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getPriority(priorityId));
	}

	@Override
	public Map<String, Object> getVersion(String versionId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getVersion(versionId));
	}

	@Override
	public Map<String, Object> getOption(String optionId) throws JsonParseException, JsonMappingException, IOException {
		return jsonToMap(rawExtension.getOption(optionId));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<Object> getAllUpdatedArtifacts(Timestamp timestamp)
			throws JsonParseException, JsonMappingException, IOException {
		
		String json = rawExtension.fetchUpdatedIssuesSince(timestamp, 0, ISSUES_PER_ACCESS);
		Map<String, Object> jsonMap = jsonToMap(json);
		ArrayList<Object> issues = (ArrayList<Object>) jsonMap.get("issues");
		
		int totalIssues = (Integer) jsonMap.get("total");
		int fetchedIssues = ISSUES_PER_ACCESS;
		
		while(fetchedIssues<totalIssues) {
			json = rawExtension.getIssues(fetchedIssues, ISSUES_PER_ACCESS);
			jsonMap = jsonToMap(json);
			issues.addAll((ArrayList<Object>) jsonMap.get("issues"));
			fetchedIssues = fetchedIssues + ISSUES_PER_ACCESS;
		}
		
		return issues;
	}

	
}
