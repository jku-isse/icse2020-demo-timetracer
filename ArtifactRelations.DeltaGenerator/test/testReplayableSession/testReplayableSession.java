package testReplayableSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import artifactFactory.factories.JiraArtifactFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.base.Artifact;
import core.base.ControlLog;
import core.base.ErrorLogger;
import core.base.ReplayableArtifact;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;
import org.junit.Test;
import replayableSession.session.ReplayableSession;

public class testReplayableSession {
	
	private JiraArtifactFactory factory;
	private JiraArtifactService jiraArtifactService;
	
	public void initialize() throws IOException, NoSuchMethodException, SecurityException  {
		
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 
		JiraServiceFactory.init(jiraArtifactService);
		Neo4JServiceFactory.init(n4jm);

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map;
		StringBuilder sb = new StringBuilder();
		String line;

		BufferedReader br = new BufferedReader (new FileReader("Dronology_Schema.json"));
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		map = mapper.readValue(sb.toString(), new TypeReference<Map<String, Object>>(){});

		factory = new JiraArtifactFactory(map.get("schema"), map.get("names"));
		ErrorLoggerServiceFactory.init(new ErrorLogger());
		JiraArtifactFactoryServiceFactory.init(factory);
		
	}
	
	@Test
	public void testCreateReplayableSession() throws Exception {
		
		initialize();
		
		ReplayableSession replayableSession = new ReplayableSession(2, "11800");
		
		replayableSession.getArtifactCache().forEach((x,y) -> {
			if(!y.doesRequireLazyLoad()) System.out.println(x);
		});
		
		System.out.println(replayableSession.getArtifactCache());
		System.out.println(replayableSession.getHistory());
		
	}
	
	@Test
	public void testCreateReplayableSessionForEntireDatabase() throws Exception {
		
		initialize();
		
		long startTime = System.currentTimeMillis();
		System.out.println((System.currentTimeMillis()-startTime)/1000);
		
	}
	
	@Test
	public void testSpecificAttributesAfterTimeTravel() throws Exception {
		
		initialize();
		Object oldValue, newValue;
		ReplayableSession replayableSession = new ReplayableSession(2, "11800");	
		Artifact a = replayableSession.getReplayableArtifact("11800").get();
	
		assertNotEquals(null, a);		
		
		oldValue = a.getProperties().get("customfield_10000");
		replayableSession.backward();	
		newValue = a.getProperties().get("customfield_10000");
		
		assertNotEquals(oldValue, newValue);
		
	}
	
	@Test
	public void testRelationCount() throws Exception {

		initialize();
		
		ReplayableSession replayableSession = new ReplayableSession(10, "10143");
		
		HashMap<String, Integer> relationCountsBeforeReplay = new HashMap<String, Integer>();
		replayableSession.getArtifactCache().forEach((id, ra) -> { relationCountsBeforeReplay.put(id, ra.getRealRelationCount());});
				
		while(!replayableSession.areAllUpdatesUndone()) {
			replayableSession.backward();	
		}	
		
		while(!replayableSession.isFullyUpdated()) {
			replayableSession.forward();	
		}
		
		HashMap<String, Integer> relationCountsAfterReplay = new HashMap<String, Integer>();
		replayableSession.getArtifactCache().forEach((id, ra) -> { relationCountsAfterReplay.put(id, ra.getRealRelationCount());});


		relationCountsAfterReplay.forEach((id, newCount) -> {
			Integer oldCount = relationCountsBeforeReplay.get(id);
			if(oldCount!=null) {
				if(oldCount!=newCount) System.out.println("Error: artifactId: " + id + ", " + "expected: " + oldCount + ", counted: " + newCount);
				assertEquals(oldCount, newCount);
			}
		});
		
	}
	
	@Test
	public void testSingleUpdates() throws Exception {
		
		initialize();
		
		ControlLog log = new ControlLog("testReplayableSession");
		ReplayableSession replayableSession = new ReplayableSession(3,"11800");
		System.out.println(replayableSession.getArtifactCache());
		System.out.println(replayableSession.getHistory());
		ReplayableArtifact artifact;
		
		artifact = replayableSession.getLastArtifactChanged().get();
		log.addLog("Initial 			" + artifact.getId());
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
		
		replayableSession.backward();
		artifact = replayableSession.getLastArtifactChanged().get();
		log.addLog("Undone Update of 	" + artifact.getId());
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
		
		replayableSession.backward();
		artifact = replayableSession.getLastArtifactChanged().get();
		log.addLog("Undone Update of 	" + artifact.getId());
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");

		replayableSession.backward();
		artifact = replayableSession.getLastArtifactChanged().get();
		log.addLog("Undone Update of 	" + artifact.getId());
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
	
		
		replayableSession.backward();
		artifact = replayableSession.getLastArtifactChanged().get();
		log.addLog("Undone Update of 	" + artifact.getId());
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
	}
	
	
	@Test
	public void testCompleteRollbackAndRestore() throws Exception {
		
		//Results are stored in controllLog.txt		
		initialize();
		
		ControlLog log = new ControlLog("testReplayableSession");
		ReplayableSession replayableSession = new ReplayableSession(1, "10136");
		
		ReplayableArtifact artifact;
		
		log.setSubTopic("Undone Update");
		/*artifact = replayableSession.getLastArtifactChanged().get();
		
		log.addLog("Init:");
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");*/
		
		while(!replayableSession.areAllUpdatesUndone()) {
			replayableSession.backward();	
			artifact = replayableSession.getLastArtifactChanged().get();
			
			log.addLog("Undone Update of 	" + artifact.getId());
			if(replayableSession.getCurrentChangeLogItem()!=null&&replayableSession.getCurrentChangeLogItem().getId()!=null) {
				log.addLog(replayableSession.getCurrentChangeLogItem().getId());
			}
						
			log.addLog("Properties:			" + artifact.getProperties().toString());
			log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
			log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
			log.addLog("\n");
		}

		
		log.setSubTopic("Update");
		while(!replayableSession.isFullyUpdated()) {
			replayableSession.forward();	
			artifact = replayableSession.getLastArtifactChanged().get();
			
			log.addLog("Update of 	" + artifact.getId());
			if(replayableSession.getCurrentChangeLogItem()!=null&&replayableSession.getCurrentChangeLogItem().getId()!=null) {
				log.addLog(replayableSession.getCurrentChangeLogItem().getId());
			}
			log.addLog("Properties:			" + artifact.getProperties().toString());
			log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
			log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
			log.addLog("\n");
		}
				
		long time = System.currentTimeMillis();
		replayableSession.getAllArtifactsInSession();
		System.out.println((System.currentTimeMillis()-time)/1000);
		
		log.writeToFile();
						
	}
	
	
	@Test
	public void testRollbackUntilCertainPointInTimeAndRestore() throws Exception {
		
		//Results are stored in controllLog.txt		
		initialize();
		
		ControlLog log = new ControlLog("testReplayableSession");
		ReplayableSession replayableSession = new ReplayableSession(1, "11800", "10132");
	
		Timestamp ts = new Timestamp(1564908248000l);
		
		ReplayableArtifact artifact;
		
		log.setSubTopic("Undone Update");

		replayableSession.backward(ts);
		artifact = replayableSession.getLastArtifactChanged().get();
		
		log.addLog("Undone Update of 	" + artifact.getId());
		if(replayableSession.getCurrentChangeLogItem()!=null&&replayableSession.getCurrentChangeLogItem().getId()!=null) {
			log.addLog(replayableSession.getCurrentChangeLogItem().getId());
		}
					
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
		

		
		log.setSubTopic("Update");
		replayableSession.latest();
		artifact = replayableSession.getLastArtifactChanged().get();
		
		log.addLog("Update of 	" + artifact.getId());
		if(replayableSession.getCurrentChangeLogItem()!=null&&replayableSession.getCurrentChangeLogItem().getId()!=null) {
			log.addLog(replayableSession.getCurrentChangeLogItem().getId());
		}
		log.addLog("Properties:			" + artifact.getProperties().toString());
		log.addLog("LinksIncoming:		" + artifact.getRelationsIncoming());
		log.addLog("LinksOutgoing:		" + artifact.getRelationsOutgoing());
		log.addLog("\n");
	
				
		log.writeToFile();
						
	}
	
	
}
