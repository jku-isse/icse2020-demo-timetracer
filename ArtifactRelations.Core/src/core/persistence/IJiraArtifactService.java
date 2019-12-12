package core.persistence;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IJiraArtifactService {

	/**
	 * queries the database for an issue with the given id,
	 * and the returns the json-data as map.
	 * if not item with the given id was found null is returned.
	 * 
	 * @param id
	 * @return Map<String, Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Map<String, Object> getArtifact(String id) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns a list containing maps for all issues 
	 * currently stored in the database.
	 * 
	 * @return ArrayList<Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public ArrayList<Object> getAllArtifacts() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns only those artifacts, which were updated after the given timestamp.
	 * 
	 * @param timestamp
	 * @return ArrayList<Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public ArrayList<Object> getAllUpdatedArtifacts(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the statusType with the given id.
	 * 
	 * @param statusId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getStatus(String statusId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the issueType with the given id.
	 * 
	 * @param issueTypeId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getIssueType(String issueTypeId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the projectType with the given id.
	 * 
	 * @param projectId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getProject(String projectId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the userType with the given id.
	 * 
	 * @param userKey
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getUser(String userKey) throws JsonParseException, JsonMappingException, IOException;
			
	/**
	 * returns the JSON-Data of the optionType with the given id.
	 * 
	 * @param optionId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getOption(String optionId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the priorityType with the given id.
	 * 
	 * @param priorityId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getPriority(String priorityId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the versionType with the given id.
	 * 
	 * @param artifactId
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getVersion(String versionId) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the JSON-Data of the linkType with the given id.
	 * 
	 * @return Map<String, Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Map<String, Object> getLinkTypes() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the namesSchema for the whole database structures,
	 * which contains a map for mapping from fieldName to fieldId.
	 * 
	 * @return Map<String, Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Map<String, Object> getNames() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * returns the schema for the whole database structures,
	 * which contains a map for mapping from fieldId to fieldType. 
	 * 
	 * @return Map<String, Object>
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public Map<String, Object> getSchema() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * fetches the corresponding id for the given key.
	 * 
	 * @param key
	 * @return String
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public String getArtifactIdFromKey(String key) throws JsonParseException, JsonMappingException, IOException;

}
