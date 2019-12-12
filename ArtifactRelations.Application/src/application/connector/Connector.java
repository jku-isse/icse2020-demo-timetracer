package application.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import artifactFactory.factories.JiraArtifactFactory;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.base.IdentifiableArtifact;
import core.connector.IConnector;
import core.persistence.BasicServices.ArtifactService;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;

public class Connector implements IConnector{

	private JiraArtifactFactory artifactFactory;
	private ArtifactService artifactService;

	public Connector() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		//initializing the services
		//initialize the Neo4JService with the LiveDatabase
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 	
		Neo4JServiceFactory.init(n4jm);		
		artifactService = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService();
		
		JiraArtifactService jiraArtifactService = new JiraArtifactService();
		JiraServiceFactory.init(jiraArtifactService);		
		
		artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
		JiraArtifactFactoryServiceFactory.init(artifactFactory);

		ErrorLoggerServiceFactory.init(new ErrorLogger());
		
	}

	@Override
	public Optional<Artifact> fetchAndMonitor(String artifactId) {	
		
		IdentifiableArtifact iArtifact;

		//check if artifact is in the database
		//the assumption is that the Neo4J-database 
		//contains all information
		iArtifact = artifactService.getArtifact(artifactId);
		
		//the artifact is not in the database
		if(iArtifact==null) {
			return Optional.empty();
		}
		
		return Optional.of(artifactFactory.deserialize(iArtifact));			

	}
	
	@Override
	public Optional<List<Artifact>> fetchDatabase() {
		
		ArrayList<Artifact> artifacts = new ArrayList<Artifact>();
		
		artifactService.getAllArtifacts().forEach( ia -> {		
			artifacts.add(artifactFactory.deserialize(ia));			
		});
		
		return Optional.of(artifacts);
		
	}

}
