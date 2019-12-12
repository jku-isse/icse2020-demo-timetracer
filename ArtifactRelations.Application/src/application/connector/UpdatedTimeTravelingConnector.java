package application.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import artifactFactory.factories.JiraArtifactFactory;
import core.ReplayableSession.IReplayableSession;
import core.base.ErrorLogger;
import core.connector.IUpdatedTimeTravelingConnector;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;
import replayableSession.session.ReplayableSession;

public class UpdatedTimeTravelingConnector implements IUpdatedTimeTravelingConnector{
	
	public UpdatedTimeTravelingConnector() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		//initializing the services
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 	
		Neo4JServiceFactory.init(n4jm);		

		JiraArtifactService jiraArtifactService = new JiraArtifactService();
		JiraServiceFactory.init(jiraArtifactService);		
		
		JiraArtifactFactory artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
		JiraArtifactFactoryServiceFactory.init(artifactFactory);

		ErrorLoggerServiceFactory.init(new ErrorLogger());
		
	}

	@Override
	public IReplayableSession getSessionForEntireDatabase() {
		return new ReplayableSession();
	}

	@Override
	public IReplayableSession getSession(int depth, String... artifactKeys) {
		try {
			return new ReplayableSession(depth, artifactKeys);
		} catch (Exception e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "Requested Session could not be created!");
			return null;
		}
	}
	
	
	
}
