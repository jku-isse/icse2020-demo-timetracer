package application.connector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.logging.Level;

import artifactFactory.factories.JiraArtifactFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.connector.ITimeTravelingConnector;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;
import replayableSession.session.ReplayableSession;

public class TimeTravelingConnector implements ITimeTravelingConnector{

	
	private JiraArtifactFactory artifactFactory;
	private ReplayableSession replayableSession;

	public static void main(String[] args) {

		try {
			TimeTravelingConnector t = new TimeTravelingConnector();
			t.travelTo(Timestamp.from(Instant.MIN));
			Optional<List<Artifact>> list = t.fetchDatabase();

			if(list.isPresent()) {
				System.out.println(list);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	public TimeTravelingConnector() throws IOException, NoSuchMethodException, SecurityException {
		
		//initializing the services
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 	
		Neo4JServiceFactory.init(n4jm);		

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map;
		StringBuilder sb = new StringBuilder();
		String line;

		BufferedReader br = new BufferedReader (new FileReader("Dronology_Schema.json"));
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		map = mapper.readValue(sb.toString(), new TypeReference<Map<String, Object>>(){});


		artifactFactory = new JiraArtifactFactory(map.get("schema"), map.get("names"));
		JiraArtifactFactoryServiceFactory.init(artifactFactory);

		ErrorLoggerServiceFactory.init(new ErrorLogger());

		replayableSession = new ReplayableSession();
		
	}

	@Override
	public Optional<Artifact> fetchAndMonitor(String artifactKey) {	
		
		Optional<Artifact> opt;

		//check if artifact is in the database
		//the assumption is that the Neo4J-database 
		//contains all information
		opt = replayableSession.getReplayableArtifactWithKey(artifactKey);
		
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
		
		replayableSession.getAllArtifactsInSession().forEach((id, a) -> {
			artifacts.add(a);
		});
		
		return Optional.of(artifacts);
		
	}
	
	
	@Override
	public void travelTo(Timestamp ts) {
		try {
			replayableSession.jumpTo(ts);
		} catch (IOException e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.SEVERE, "Connector: travelTo(Timestamp)" + e.getMessage());
		}
	}

	@Override
	public void travelToNextChange(boolean backward, String...artifactKeys) {
		
		try {
			
			boolean givenKeysAreValid = true;
			List<String> keys = Arrays.asList(artifactKeys);
			
			for(String key : keys) {
				if(replayableSession.getReplayableArtifactWithKey(key).isEmpty()) {
					givenKeysAreValid = false;
					break;
				}
			}
			
			if(givenKeysAreValid) {
				if(backward) {		
					replayableSession.backward();
					while(!replayableSession.areAllUpdatesUndone() && !keys.contains(replayableSession.getLastArtifactChanged().get().getIdInSource())) {
						replayableSession.backward();
					}				
				} else {				
					replayableSession.forward();
					while(!replayableSession.isFullyUpdated() && !keys.contains(replayableSession.getLastArtifactChanged().get().getIdInSource())) {
						replayableSession.forward();
					}				
				}
			} else {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "Connector: travelToNextChange(Timestamp): Input contains invalid key");
			}
			
		} catch (IOException e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.SEVERE, "Connector: travelToNextChange(Timestamp)" + e.getMessage());
		}
		
	}

	@Override
	public Timestamp getCurrentTime() {
		return replayableSession.getCurrentTime();
	}
	
}
