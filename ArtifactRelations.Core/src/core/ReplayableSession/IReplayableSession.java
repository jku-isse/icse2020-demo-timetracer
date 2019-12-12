package core.ReplayableSession;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.base.Artifact;
import core.base.ReplayableArtifact;
import core.connector.IConnector;

public interface IReplayableSession extends IConnector{

	/**
	 * applies the changes of the next changeLogItem to it's corresponding artifact
	 * in the session and updates the timeStamp of the session with the changeLog's timeStamp.
	 * no change is made when there are no more updates
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void forward() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * applies the changes of the next past changeLogItem to it's corresponding artifact
	 * in the session and updates the timeStamp of the session with the changeLog's timeStamp.
	 * no change is made when there are no more updates to undo
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void backward() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * applies the method forward() until the the replayableSession
	 * is consistent with the given point in time
	 * 
	 * @param timestamp
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void forward(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * applies the method backward() until the the replayableSession
	 * is consistent with the given point in time
	 * 
	 * @param timestamp
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void backward(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * applies the method backward() or forward until the the replayableSession
	 * is consistent with the given point in time
	 * 
	 * @param timestamp
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void jumpTo(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * applies all updates
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void latest() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * undo all updates
	 * 
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void oldest() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * 
	 * travels from current time of the database to
	 * to the future or past in which a change of one of the specified artifact's
	 * did occur. 
	 * 
	 * In case no more things are left to do or undo, 
	 * the database will be completely updated or 
	 * the exact opposite will be the case.
	 * 
	 * @param ts
	 * @param backInTime
	 */
	public void travelToNextChange(boolean backward, String...artifactKeys);
	
	
	/**
	 * returns the artifact of the session with the given id
	 * 
	 * @param artifactId
	 * @return Optional<Artifact>
	 */
	public Optional<Artifact> getReplayableArtifact(String artifactId);
	
	/**
	 * returns the artifact of the session with the given key
	 * 
	 * @param artifactKey
	 * @return Optional<Artifact>
	 */
	public Optional<Artifact> getReplayableArtifactWithKey(String artifactKey);
	
	/**
	 * returns an array of root-artifacts, those artifacts that were
	 * handed over via the constructor of the session 
	 * 
	 * @return Optional<Artifact[]>
	 */
	public Optional<Artifact[]> getRootElements();
	
	/**
	 * adds an artifact to the session, in which all of it's history
	 * will be fetched and stored to 
	 * 
	 * @param replayableArtifact
	 */
	public void checkInArtifact(ReplayableArtifact replayableArtifact);
	
	/**
	 * returns a map<String id, Artifact artifact) of all artifacts in this session.
	 * the map is an image caught in time from the state in which the session is currently in.
	 * 
	 * @return HashMap<String, Artifact>
	 */
	public HashMap<String, Artifact> getAllArtifactsInSession();
	
	/**
	 * returns the time to which this session is currently replayed
	 * 
	 * @return
	 */
	public Timestamp getCurrentTime();
	
}
