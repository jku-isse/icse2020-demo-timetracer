package core.connector;

import java.util.List;
import java.util.Optional;

import core.base.Artifact;

public interface IConnector {

	/**
	 * if the artifact with the given id exists 
	 * in the database it is deserialized and returned,
	 * if not null is returned.
	 * 
	 * @param artifactKey
	 * @return Artifact
	 */
	public Optional<Artifact> fetchAndMonitor(String artifactKey);
	
	
	/**
	 * collects all items in the current database and returns them
	 * 
	 * @return ArrayList<Artifact> 
	 */
	public Optional<List<Artifact>> fetchDatabase();
	

}
