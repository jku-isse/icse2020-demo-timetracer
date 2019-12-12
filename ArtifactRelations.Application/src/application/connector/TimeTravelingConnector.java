package application.connector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

import artifactFactory.factories.JiraArtifactFactory;
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

	public TimeTravelingConnector() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		//initializing the services
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 	
		Neo4JServiceFactory.init(n4jm);		
		
		JiraArtifactService jiraArtifactService = new JiraArtifactService();
		JiraServiceFactory.init(jiraArtifactService);		
		
		
	/*	String file ="UAV-1006.json";
      
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		reader.lines().forEach( l -> sb.append(l));
		reader.close();
				
	    ObjectMapper mapper = new ObjectMapper();
		HashMap<String, Object> map = mapper.readValue(sb.toString(), new TypeReference<Map<String, Object>>(){});
		Map<String, Object> schema = (HashMap<String, Object>) map.get("schema"), names = (HashMap<String, Object>) map.get("names");
			
	*/
		
		artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
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
