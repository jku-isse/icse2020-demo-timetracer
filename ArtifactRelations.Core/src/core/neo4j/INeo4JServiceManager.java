package core.neo4j;

import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.RelationMemoryService;
import core.persistence.BasicServices.ReplayableArtifactService;
import core.persistence.BasicServices.StatusService;

public interface INeo4JServiceManager {
				
	/**
	 * delete all NodeEntities from the database, 
	 * concerns all types of nodes.
	 */
	public void deleteEverything();

	/**
	 * returns the artifactService this implementation is wrapped around.
	 * 
	 * @return ArtifactService
	 */
	public ArtifactService getArtifactService();

	/**
	 * returns the changeLogItemService this implementation is wrapped around.
	 * 
	 * @return ChangeLogItemService
	 */
	public ChangeLogItemService getChangeLogItemService();

	/**
	 * returns the RelationMemoryService this implementation is wrapped around.
	 * 
	 * @return RelationMemoryService
	 */
	public RelationMemoryService getRelationMemoryService();
	
	/**
	 * returns the ReplayableArtifactService this implementation is wrapped around.
	 * 
	 * @return
	 */
	public ReplayableArtifactService getReplayableArtifactService();

	/**
	 * returns the StatusService this implementation is wrapped around.
	 * 
	 * @return StatusService
	 */
	public StatusService getStatusService();
	
}
