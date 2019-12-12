package application.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import artifactFactory.factories.JiraArtifactFactory;
import core.application.userInterface.IServiceToNeo4J;
import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.ErrorLogger;
import core.base.IdentifiableRelationMemory;
import core.base.IdentifiableStatus;
import core.base.ReplayableArtifact;
import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.RelationMemoryService;
import core.persistence.BasicServices.StatusService;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;

//this is a helper class for getting the data from your jiraServer to your Neo4JDatabase
public class JiraToNeo4J implements IServiceToNeo4J {
	
	private JiraArtifactFactory artifactFactory;
	
	private ArtifactService artifactService;
	private ChangeLogItemService changeLogItemService;
	private StatusService statusService;
	
	
	/**
	 * Establishes a connection to the service as well as Neo4J
	 * and initializes the helperClasses for 
	 * communicating with both service and database
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */	
	public JiraToNeo4J() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 		
		
		JiraArtifactService jiraArtifactService = new JiraArtifactService();
		JiraServiceFactory.init(jiraArtifactService);		
		
		Neo4JServiceFactory.init(n4jm);
		artifactService = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService();
		changeLogItemService = Neo4JServiceFactory.getNeo4JServiceManager().getChangeLogItemService();
		statusService = Neo4JServiceFactory.getNeo4JServiceManager().getStatusService();
		
		updateRelationMemory();
		ErrorLoggerServiceFactory.init(new ErrorLogger());	
		artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
		JiraArtifactFactoryServiceFactory.init(artifactFactory);
		
	}
	
	
	
	
	//this method fetches all data and overwrites the stored artifact
	@Override
	public Artifact issueToNeo4J(String issueKey) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				
		//first we check if this item is already a part of the database
		String id = JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(issueKey);
		ReplayableArtifact artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);
		
		//secondly the artifactData is fetched from the server 
		Object artifactData = JiraServiceFactory.getJiraArtifactService().getArtifact(issueKey);	
		updateRelationMemory();
		
		transportArtifact(artifact, artifactData);				
	
		return artifact;
		
	}
	
	
	
	
	//this method fetches all data and overwrites the stored artifact
	@SuppressWarnings("unchecked")
	@Override
	public void fetchCompleteServiceDatabase() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		//this method can be used to do an initial fetch, were everything is loaded without
		//consideration of already
		ArrayList<Object> issues = JiraServiceFactory.getJiraArtifactService().getAllArtifacts();
		String id;
		ReplayableArtifact artifact;
		
		//in order to be able to keep the database updated we have to store 
		//a status object, which contains a timeStamp holding the last updateTime
		IdentifiableStatus status = new IdentifiableStatus();
		status.setLastUpdate(System.currentTimeMillis());
		statusService.push(status);
		
				
		for(int i=0; i<issues.size(); i++) {
				
			id = (String) ((Map<String, Object>)issues.get(i)).get("id");
			artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);			
			transportArtifact(artifact, issues.get(i));								
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, (i+1) + " of " + issues.size() + " issues have been fetched from Jira and pushed to Neo4J! ");
		}
				
	}


	@SuppressWarnings("unchecked")
	@Override
	public void fetchDatabaseDelta()
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
				
		String id;
		Timestamp lastUpdated = new Timestamp(statusService.fetchStatus().getLastUpdate());
		ArrayList<Object> issues = JiraServiceFactory.getJiraArtifactService().getAllUpdatedArtifacts(lastUpdated);					
		ReplayableArtifact artifact;
		
		IdentifiableStatus status = new IdentifiableStatus();
		status.setLastUpdate(System.currentTimeMillis());
		statusService.push(status);
		
				
		for(int i=0; i<issues.size(); i++) {				
		
			id = (String) ((Map<String, Object>)issues.get(i)).get("id");
			artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);			
			transportArtifact(artifact, issues.get(i));						
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, (i+1) + " of " + issues.size() + " issues were updated! ");
			
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public ReplayableArtifact transportArtifact(ReplayableArtifact ra, Object artifactData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
	
		if(ra==null) {
			ra = artifactFactory.createArtifact(artifactData);
		} else {
			ra = artifactFactory.updateArtifact(artifactData, ra);
		}
		
		ArrayList<ChangeLogItem> changeLogItems = artifactFactory.buildChangeLog((Map<String, Object>) artifactData);						
		artifactService.addArtifact(ra);					
		changeLogItems.forEach(item -> 	{changeLogItemService.addChangeLogItem(item);});		
		
		return ra;
	}
	
	public void updateRelationMemory() throws JsonParseException, JsonMappingException, IOException {
		
		RelationMemoryService relationMemoryService = Neo4JServiceFactory.getNeo4JServiceManager().getRelationMemoryService();
		IdentifiableRelationMemory relationMemory = relationMemoryService.fetchRelationMemory();
		relationMemory.setRelationMemory(JiraServiceFactory.getJiraArtifactService().getLinkTypes());
		relationMemoryService.push(relationMemory);
		
	}
	
	public void purgeNeo4Database() {
		artifactService.deleteEverything();
	}




	@Override
	public Artifact issueToNeo4J(ReplayableArtifact ra)
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
