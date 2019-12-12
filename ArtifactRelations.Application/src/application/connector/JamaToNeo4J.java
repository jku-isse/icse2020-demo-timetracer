package application.connector;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import artifactFactory.factories.JamaArtifactFactory;
import core.application.userInterface.IServiceToNeo4J;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.base.ReplayableArtifact;
import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.StatusService;
import core.services.ErrorLoggerServiceFactory;
import core.services.Neo4JServiceFactory;
import neo4j.connector.Neo4JServiceManager;

public class JamaToNeo4J implements IServiceToNeo4J {

	private ArtifactService artifactService;
	private ChangeLogItemService changeLogItemService;
	private StatusService statusService;
	private JamaArtifactFactory jamaArtifactFactory;
	
	public JamaToNeo4J() throws IOException {
		
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 		
		
		Neo4JServiceFactory.init(n4jm);
		artifactService = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService();
		changeLogItemService = Neo4JServiceFactory.getNeo4JServiceManager().getChangeLogItemService();
		statusService = Neo4JServiceFactory.getNeo4JServiceManager().getStatusService();
		
		ErrorLoggerServiceFactory.init(new ErrorLogger());	
		
	}
	
	@Override
	public Artifact issueToNeo4J(String id)
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		
		
		return null;
	}

	@Override
	public void fetchCompleteServiceDatabase()
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fetchDatabaseDelta()
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void purgeNeo4Database() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Artifact issueToNeo4J(ReplayableArtifact ra)
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().addArtifact(ra);
		
		return null;
	}

	
	
}
