package core.base;

import java.io.IOException;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface IReplayableArtifact {

	/**
	 * updates this instance of ReplayableArtifact with the given changeLogItem,
	 * a cache containing all artifacts required to create a future status is expected,
	 * if this list does not contain the necessary items placeholders will be created.
	 * 
	 * @param changeLogItem
	 * @param cache
	 * @return ReplayableArtifact
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public ReplayableArtifact updateArtifact(ChangeLogItem changeLogItem, HashMap<String, ReplayableArtifact> cache) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * undoes the change of the given changeLogItem for this instance of ReplayableArtifact,
	 * a cache containing all artifacts required to create a past status is expected,
	 * if this list does not contain the necessary items placeholders will be created.
	 * 
	 * @param changeLogItem
	 * @param cache
	 * @return ReplayableArtifact
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public ReplayableArtifact undoUpdate(ChangeLogItem changeLogItem, HashMap<String, ReplayableArtifact> cache) throws JsonParseException, JsonMappingException, IOException;
	
}
