package replayableSession.session;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.ReplayableSession.ChangeLogItemComparator;
import core.ReplayableSession.IReplayableCacheCompleter;
import core.ReplayableSession.IReplayableSession;
import core.artifactFactory.factories.IArtifactFactory;
import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.Neo4JServiceFactory;
import core.services.ReplayableSessionServiceFactory;
import replayableSession.replayable.ReplayableCacheCompleter;

public class ReplayableSession implements IReplayableSession {

	public int currentState;
	
	private boolean fullyUpdated, allUpdatesUndone;
	private int depth;
	
	private Timestamp timestamp;
	private HashMap<String, ReplayableArtifact> artifactCache = new HashMap<String, ReplayableArtifact>();
	private ArrayList<ChangeLogItem> sortedHistory = new ArrayList<ChangeLogItem>();
	private TreeSet<ChangeLogItem> history = new TreeSet<ChangeLogItem>(new ChangeLogItemComparator());
	private ReplayableArtifact lastChanged;
	private ReplayableArtifact[] roots;
	private ChangeLogItem currentChangeLogItem;
	private IReplayableCacheCompleter completer;
	private IArtifactFactory artifactFactory;
			
	public ReplayableSession(int depth, String...args) throws Exception {
	
		this.depth = depth;
		roots = new ReplayableArtifact[args.length];
		ReplayableSessionServiceFactory.assignSession(this);	
		
		for(int i=0; i<args.length; i++) {
			roots[i] = Neo4JServiceFactory.getNeo4JServiceManager().getReplayableArtifactService().getReplayableArtifact(args[i], depth);
			if(roots[i]==null) throw new IllegalArgumentException("No Artifact with the id " + args[i] + " exists");
		}				
		
		//the completer creates artifacts, which are in the present
		//not a part of the session, but were in the past		
		completer = new ReplayableCacheCompleter(this);
		completer.completeReplayableCache();
		
		//since some items may have been missing 
		//they have to be added again
		sortedHistory.addAll(history);
	
		fullyUpdated = true;
		allUpdatesUndone = false;		
		if(sortedHistory.isEmpty()) allUpdatesUndone = true;		
		timestamp = new Timestamp(System.currentTimeMillis());		
		currentState = getCurrentState();
		lastChanged = roots[0];
		
		//has to be checked out, since we don't want all artifacts, that will be
		//fetched in the future to check themselves into this sessions cache
		ReplayableSessionServiceFactory.checkOutSession();
		artifactFactory = JiraArtifactFactoryServiceFactory.getJiraArtifactFactory();
		
	}
	
	//when no specification about the depth of the session is given
	//a replayableSession of the entire Neo4JDatabase is created
	public ReplayableSession() {
		
		ReplayableSessionServiceFactory.assignSession(this);	
		
		Neo4JServiceFactory.getNeo4JServiceManager().getReplayableArtifactService().getAllReplayableArtifacts().iterator();
		Iterator<ChangeLogItem> changeLogItems = Neo4JServiceFactory.getNeo4JServiceManager().getChangeLogItemService().getAllChangeLogItems().iterator();

		while(changeLogItems.hasNext()) {
			history.add(changeLogItems.next());
		}
		
		//in this case we are not in need of assigning depths 
		//or completing the cache, since we just add the 
		//whole database in for analysis
		
		sortedHistory.addAll(history);
		
		fullyUpdated = true;
		allUpdatesUndone = false;		
		if(sortedHistory.isEmpty()) allUpdatesUndone = true;		
		timestamp = new Timestamp(System.currentTimeMillis());			
		currentState = getCurrentState();
		lastChanged = null;
	
		ReplayableSessionServiceFactory.checkOutSession();
		artifactFactory = JiraArtifactFactoryServiceFactory.getJiraArtifactFactory();
		
	}
	

	@Override
	public void forward() throws JsonParseException, JsonMappingException, IOException {
		
		ChangeLogItem changeLogItem = getNextItem();
		currentChangeLogItem = changeLogItem;
	
		if(changeLogItem==null) {
			fullyUpdated = true;
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "ReplayableSession: forward(): No more updates left to make");
		} else {
			ReplayableArtifact replayableArtifact = artifactCache.get(changeLogItem.getCorrespondingArtifactId());
			timestamp = changeLogItem.getTimestamp();
			replayableArtifact = replayableArtifact.updateArtifact(changeLogItem, artifactCache);
			lastChanged = replayableArtifact;
			allUpdatesUndone = false;
			
		}	
		
	}

	@Override
	public void backward() throws JsonParseException, JsonMappingException, IOException {
		
		ChangeLogItem changeLogItem = getLastItem();
		currentChangeLogItem = changeLogItem;

		if(changeLogItem==null) {
			allUpdatesUndone = true;
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "ReplayableSession: forward(): No more updates left to undo");
		} else {
			ReplayableArtifact replayableArtifact = artifactCache.get(changeLogItem.getCorrespondingArtifactId());
			timestamp = changeLogItem.getTimestamp();
			replayableArtifact.undoUpdate(changeLogItem, artifactCache);
			lastChanged = replayableArtifact;
			fullyUpdated = false;
		}	
		
	}

	@Override
	public void forward(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException {	
		while(!fullyUpdated&&timestamp.compareTo(sortedHistory.get(currentState).getTimestamp())>0) { 
			forward(); 
		}			
	}

	@Override
	public void backward(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException {	
		while(!allUpdatesUndone&&timestamp.compareTo(sortedHistory.get(currentState).getTimestamp())<0) { 
			backward();
		}	
	}

	@Override
	public void latest() throws JsonParseException, JsonMappingException, IOException {		
		do {forward();} while(!fullyUpdated);	
	}

	@Override
	public void oldest() throws JsonParseException, JsonMappingException, IOException {		
		do {backward();} while(!allUpdatesUndone);		
	}

	
	public void addChangeLogItems(ArrayList<ChangeLogItem> items) {
		history.addAll(items);
	}

	public void removeChangeLogItems(ArrayList<ChangeLogItem> items) {
		history.removeAll(items);
	}
	
	public Timestamp getTimeOfNextFutureItem() {
		if (fullyUpdated) return null;
		return sortedHistory.get(currentState).getTimestamp();
	}
	
	private ChangeLogItem getNextItem() {
		if(fullyUpdated) return null;
		ChangeLogItem changeLogItem = sortedHistory.get(currentState);
		if(currentState!=history.size()-1) {
			currentState++;
		} else {
			fullyUpdated = true;
		}
		return changeLogItem;
	}
	
	
	private ChangeLogItem getLastItem() {	
		if(allUpdatesUndone) return null;
		ChangeLogItem changeLogItem = sortedHistory.get(currentState);
		if(currentState!=0) {
			currentState--;
		} else {
			allUpdatesUndone = true;
		}
		return changeLogItem;
	}
	
	@Override
	public void travelToNextChange(boolean backward, String... artifactKeys) {
		
		try {
			
			boolean givenKeysAreValid = true;
			List<String> keys = Arrays.asList(artifactKeys);
			
			for(String key : keys) {
				if(getReplayableArtifactWithKey(key).isEmpty()) {
					givenKeysAreValid = false;
					break;
				}
			}
			
			if(givenKeysAreValid) {
				if(backward) {		
					backward();
					while(!areAllUpdatesUndone() && !keys.contains(getLastArtifactChanged().get().getIdInSource())) {
						backward();
					}				
				} else {				
					forward();
					while(!isFullyUpdated() && !keys.contains(getLastArtifactChanged().get().getIdInSource())) {
						forward();
					}				
				}
			} else {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "Connector: travelToNextChange(Timestamp): Input contains invalid key");
			}
			
		} catch (IOException e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.SEVERE, "Connector: travelToNextChange(Timestamp)" + e.getMessage());
		}
	}
	
	private int getCurrentState() {
		
		for(int i=0; i<history.size(); i++) {
			if(timestamp.equals(sortedHistory.get(i).getTimestamp())) {
				return i;
			}
		}
		
		return history.size()-1;
	}

	public Optional<ReplayableArtifact[]> getRoots() {
		return Optional.ofNullable(roots);
	}
	
	public HashMap<String, ReplayableArtifact> getArtifactCache() {
		return artifactCache;
	}

	public TreeSet<ChangeLogItem> getHistory() {
		return history;
	}

	@Override
	public void checkInArtifact(ReplayableArtifact replayableArtifact)  {
		replayableArtifact.setInReplayableSession(true);	
		artifactCache.put(replayableArtifact.getId(), replayableArtifact);	
	}
	
	public Optional<ReplayableArtifact> getLastArtifactChanged() {
		if(lastChanged ==null) return Optional.empty();
		return Optional.of(lastChanged);
	}

	public boolean isFullyUpdated() {
		return fullyUpdated;
	}

	public void setFullyUpdated(boolean fullyUpdated) {
		this.fullyUpdated = fullyUpdated;
	}

	public boolean areAllUpdatesUndone() {
		return allUpdatesUndone;
	}

	@Override
	public Optional<Artifact> getReplayableArtifact(String artifactId) {	
		return Optional.ofNullable(artifactCache.get(artifactId));
	}
	
	@Override
	public Optional<Artifact> getReplayableArtifactWithKey(String artifactKey) {		
		List<Artifact> result = artifactCache.values().stream().filter(a -> a.getIdInSource()!=null && a.getIdInSource().equals(artifactKey)).collect(Collectors.toList());
		if (result.isEmpty()) return Optional.empty();
		return Optional.of(result.get(0));
	}

	public ChangeLogItem getCurrentChangeLogItem() {
		return currentChangeLogItem;
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public HashMap<String, Artifact> getAllArtifactsInSession() {
		
		HashMap<String, Artifact> result = new HashMap<String, Artifact>();
		
		artifactCache.forEach((key, ra) -> {
			
			try {
				result.put(key, (ReplayableArtifact) JiraArtifactFactoryServiceFactory.getJiraArtifactFactory().deserialize(ra));
			} catch (Exception e) {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "ReplayableSession : getAllArtifactsInSession : Deserialization was not successful");
			}
			
		});
		
		return result;
		
	}

	@Override
	public Optional<Artifact[]> getRootElements() {
		
		if(roots ==null) return Optional.empty();
		
		for(ReplayableArtifact ra : roots) {
			try {
				ra = (ReplayableArtifact) JiraArtifactFactoryServiceFactory.getJiraArtifactFactory().deserialize(ra);
			} catch (Exception e) {				
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "ReplayableSession : getRoots() : Deserialization was not successful");
			}
		}
		
		return Optional.of(roots);
		
	}

	@Override
	public void jumpTo(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException {
		// TODO Auto-generated method stub
		if(this.timestamp.compareTo(timestamp)<0) {
			forward(timestamp);
		} else {
			backward(timestamp);
		}
	}

	@Override
	public Timestamp getCurrentTime() {
		return timestamp;
	}

	@Override
	public Optional<Artifact> fetchAndMonitor(String artifactKey) {
		Optional<Artifact> opt = getReplayableArtifactWithKey(artifactKey);
		
		if(!opt.isEmpty()) {
			try {
				for(Relation r :opt.get().getRelationsIncoming()) { r.setSource(artifactFactory.deserialize(r.getSource())); };
				for(Relation r :opt.get().getRelationsOutgoing()) { r.setSource(artifactFactory.deserialize(r.getDestination())); };
				return Optional.of(artifactFactory.deserialize(opt.get()));
			} catch (Exception e) {
				e.printStackTrace();
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "ReplayableSession(fetchAndMonitor(String artifactKey)) : artifact with given id cannot be fetched");
			}
		}
		
		return Optional.empty();
	}

	@Override
	public Optional<List<Artifact>> fetchDatabase() {
	
		List<Artifact> result = new ArrayList<Artifact>();
		
		getAllArtifactsInSession().forEach((k,a) -> {
			try {
				a = artifactFactory.deserialize(a);
			} catch (Exception e) {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "ReplayableSession(fetchAndMonitor(String artifactKey)) : artifact with key " + a.getIdInSource()+ " was not deserialized");
			}
			result.add(a);
		});
		
		return Optional.of(result);
		
	}
	
}
