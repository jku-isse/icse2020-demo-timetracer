package core.connector;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Optional;

import core.ReplayableSession.IReplayableSession;
import core.base.Artifact;

public interface IReplayableConnector extends IConnector {
	
	/**
	 * creates a snapshot of the database for the given time, by creating
	 * an instance of ReplayableSession, which undoes all updates made since then.
	 * returns a map with all artifacts in the database mapped to their keys.
	 * 
	 * already deleted artifacts may be created, those artifacts are not stored
	 * with their id, but with their old key, since the id may not be recovered.
	 * 
	 * @param timestamp
	 * @return Optional<HashMap<String, Artifact>>
	 */
	public Optional<HashMap<String, Artifact>> fetchDatabasePast(Timestamp timestamp);
	
	
	/**
	 * creates a recent version of the artifact and it's neighbors, depth determines 
	 * what neighbors are to be replayed too. 
	 * 
	 * when iterating through the graph, exceptions will be thrown on a lower level
	 * in case the given depth is exceeded
	 * 
	 * @param timestamp
	 * @param depth
	 * @param artifactId
	 * @return Optional<Artifact>
	 */
	public Optional<Artifact> fetchArtifactPast(Timestamp timestamp, int depth, String artifactId);
	
	/**
	 * creates a recent version of the artifacts and their neighbors, depth determines 
	 * what neighbors are to be replayed too. 
	 * 
	 * when iterating through the graph, exceptions will be thrown on a lower level
	 * in case the given depth is exceeded
	 * 
	 * @param timestamp
	 * @param depth
	 * @param artifactId
	 * @return Optional<Artifact[]>
	 */
	public Optional<Artifact[]> fetchArtifactsPast(Timestamp timestamp, int depth, String...artifactIds);
		
	/**
	 * returns a replayableSession with the given artifacts and depth
	 * 
	 * @param depth
	 * @param artifactIds
	 * @return Optional<IReplayableSession>
	 */
	public Optional<IReplayableSession> fetchReplayableSession(int depth, String...artifactIds);
	
	/**
	 * returns a replayableSession for the entire database
	 *  
	 * @return Optional<IReplayableSession>
	 */
	public Optional<IReplayableSession> fetchReplayableDatabase();
	
}
