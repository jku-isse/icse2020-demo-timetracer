package core.artifactFactory.factories;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;

public interface IArtifactFactory {

	/**
	 * when given the json-data of a file, this method deserializes the data into an Artifact.
	 * 
	 * Every service has to use the correct instance of ArtifactFactory
	 * 
	 * @throws Exception
	 */	
	public Artifact createArtifact(Object object) throws Exception;
	
	/**
	 * when given the json-data of a file, this method updates the already existing instance of Artifact
	 * 
	 * Every service has to use the correct instance of ArtifactFactory
	 * 
	 * @throws Exception
	 */	
	public Artifact updateArtifact(Object data, Artifact artifact) throws Exception;
	
	/**
	 * given an instance of iArtifact, fetched from the cache,
	 * this method returns an instance of Artifact(fully deserialized)
	 *
	 * Every service has to use the correct instance of ArtifactFactory
	 *
	 * @throws Exception
	 */	
	public Artifact deserialize(IdentifiableArtifact iartifact) throws Exception;

	/**
	 * given the historyData as a map, this method creates a list
	 * of ChangeLogItems(history), which may be used to replay ReplayableArtifacts
	 * 
	 * Every service has to use the correct instance of ArtifactFactory
	 * @throws Exception
	 */	
	public ArrayList<ChangeLogItem> buildChangeLog(Map<String, Object> historyData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;
	
}
