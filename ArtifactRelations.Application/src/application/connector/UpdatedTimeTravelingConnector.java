package application.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import artifactFactory.factories.JiraArtifactFactory;
import core.ReplayableSession.IReplayableSession;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.connector.IConnector;
import core.connector.IUpdatedTimeTravelingConnector;
import core.persistence.IJiraArtifactService;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;
import replayableSession.session.ReplayableSession;

public class UpdatedTimeTravelingConnector implements IUpdatedTimeTravelingConnector{
	
	private ReplayableSession rs = null;
	private JiraArtifactFactory artifactFactory;
	
	public UpdatedTimeTravelingConnector(IJiraArtifactService jiraArtifactService) throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		//initializing the services
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 	
		Neo4JServiceFactory.init(n4jm);		

		JiraServiceFactory.init(jiraArtifactService);		
		
		artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
		JiraArtifactFactoryServiceFactory.init(artifactFactory);

		ErrorLoggerServiceFactory.init(new ErrorLogger());
		
	}

	@Override
	public IReplayableSession getSessionForEntireDatabase() {
		rs = new ReplayableSession();		
		return rs;
	}

	@Override
	public IReplayableSession getSession(int depth, String... artifactKeys) {
		try {			
			rs = new ReplayableSession(depth, artifactKeys);			
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "Requested Session could not be created!");
			return null;
		}
	}
	
	@Override
	public Optional<Artifact> fetchAndMonitor(String artifactKey) {	
		
		Optional<Artifact> opt;

		//check if artifact is in the database
		//the assumption is that the Neo4J-database 
		//contains all information
		opt = rs.getReplayableArtifactWithKey(artifactKey);
		
		//the artifact is not in the database
		if(opt.isEmpty()) {
			return Optional.empty();
		}
		
		opt.get().getRelationsIncoming().forEach(r -> r.setSource(artifactFactory.deserialize(r.getSource())));
		opt.get().getRelationsOutgoing().forEach(r -> r.setSource(artifactFactory.deserialize(r.getDestination())));
		return Optional.of(artifactFactory.deserialize(opt.get()));			

	}

	
	@Override
	public Optional<List<Artifact>> fetchDatabase() {
		
		ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
		
		rs.getAllArtifactsInSession().forEach((id, a) -> {
			artifacts.add(a);
		});
		
		return Optional.of(artifacts);
		
	}
	
}
