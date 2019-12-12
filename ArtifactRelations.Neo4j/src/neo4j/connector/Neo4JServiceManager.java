package neo4j.connector;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.neo4j.INeo4JServiceManager;
import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.RelationMemoryService;
import core.persistence.BasicServices.ReplayableArtifactService;
import core.persistence.BasicServices.StatusService;
import core.services.Neo4JServiceFactory;
import neo4j.config.Neo4JConnectorSetupConfig;


public class Neo4JServiceManager implements INeo4JServiceManager{

	private Injector injector;	
	private Session sessionLive;
	
	private ArtifactService artifactService;
	private ChangeLogItemService changeLogItemService;
	private RelationMemoryService relationMemoryService;
	private ReplayableArtifactService replayableArtifactService;
	private StatusService statusService;
	
	/**
	 * 
	 * creates services for managing the different kind of NodeEntities 
	 * and makes them available.
	 * 
	 * ruleEvaluation should be true if the Manager is created for 
	 * doing constraint Checks, since check-results are persisted
	 * 
	 * @param ruleEvaluation
	 */
	public Neo4JServiceManager() {
		
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
		
		SessionFactory nsf = injector.getInstance(SessionFactory.class);
		sessionLive = nsf.openSession(); 
				
		changeLogItemService = injector.getInstance(ChangeLogItemService.class);
		artifactService = injector.getInstance(ArtifactService.class);
		relationMemoryService = injector.getInstance(RelationMemoryService.class);
		replayableArtifactService = injector.getInstance(ReplayableArtifactService.class);
		statusService = injector.getInstance(StatusService.class);
		
		//in case the client accesses the relations of a queried artifact
		//this artifact needs to lazyLoad it's relations
		//and for that it uses the static ServiceFactory
		Neo4JServiceFactory.init(this);
		
	}

	public void addArtifact(Artifact artifact) {
		if(artifact!=null) {					
			artifactService.addArtifact(artifact);			
		}
	}
	
	public IdentifiableArtifact getArtifact(String id) {
		return artifactService.find(id);
	}
	
	public void deleteArtifact(String id) {
		artifactService.delete(id);
	}
	
	public void addChangeLogItem(ChangeLogItem changeLogItem) {
		changeLogItemService.addChangeLogItem(changeLogItem);
	}
	
	public ChangeLogItem getChangeLogItem(String id) {
		return changeLogItemService.getChangeLogItem(id);
	}
	
	public void deleteChangeLogItem(String id) {
		changeLogItemService.deleteChangeLogItem(id);
	}
	
	public void deleteEverything() {
		sessionLive.purgeDatabase();
	}

	public ArtifactService getArtifactService() {
		return artifactService;
	}

	public ChangeLogItemService getChangeLogItemService() {
		return changeLogItemService;
	}

	public RelationMemoryService getRelationMemoryService() {
		return relationMemoryService;
	}
	
	public ReplayableArtifactService getReplayableArtifactService() {
		return replayableArtifactService;
	}

	public StatusService getStatusService() {
		return statusService;
	}

	public void setStatusService(StatusService statusService) {
		this.statusService = statusService;
	}
	
}
