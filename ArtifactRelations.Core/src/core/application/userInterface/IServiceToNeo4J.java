package core.application.userInterface;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.base.Artifact;
import core.base.ReplayableArtifact;

public interface IServiceToNeo4J {
		
	public Artifact issueToNeo4J(String id) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
		
	/**
	 * fetches the item with the given id and all it's history from the service
	 * and creates an instance for the item(Artifact) and instances(ChangeLogItem) of the history,
	 * which are then persisted in Neo4J
	 * 
	 * In elements already exists in Neo4J an update is executed
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException	
	 */	
	public Artifact issueToNeo4J(ReplayableArtifact ra) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
	/**
	 * creates instances of type Artifact for every issue fetched from the service 
	 * as well as instances of ChangeLogItem for their histories and persists them in Neo4J
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException;	
	 */	
	public void fetchCompleteServiceDatabase() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

	/**
	 * only fetches the changes since the last Update from the serviceDatabase
	 * and creates or updates the artifacts accordingly
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException;	
	 */	
	public void fetchDatabaseDelta() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
		
	/**
	 * deletes everything stored in the Neo4J-Database(artifacts of all services)
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException;	
	 */	
	public void purgeNeo4Database();
	
}
