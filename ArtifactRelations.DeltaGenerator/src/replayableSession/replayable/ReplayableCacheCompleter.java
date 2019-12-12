package replayableSession.replayable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import core.ReplayableSession.IReplayableCacheCompleter;
import core.base.ChangeLogItem;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.services.Neo4JServiceFactory;
import replayableSession.session.ReplayableSession;

public class ReplayableCacheCompleter implements IReplayableCacheCompleter {

	private ReplayableSession session;
	private TreeSet<ChangeLogItem> history;
	private HashMap<String, ReplayableArtifact> artifactCache;
	
	public ReplayableCacheCompleter(ReplayableSession session) {
		this.session = session;
		this.history = session.getHistory();
		artifactCache = session.getArtifactCache();
	}
	

	@Override
	public void completeReplayableCache() {	
		for(ReplayableArtifact ra : session.getRoots().get()) {
			assignDepthValues(ra.getId(), session.getDepth());				
		}
	}
	
	
	private HashSet<ReplayableArtifact> getMissingArtifactsForAnArtifact(ReplayableArtifact ra, int depth) {
		
		HashSet<ReplayableArtifact> artifacts = new HashSet<ReplayableArtifact>();
		
		history.forEach(changeLogItem -> {
			
			if(changeLogItem.getArtifactId().equals(ra.getId())) {
			
				//we have found a changeLogItem for the artifact, 
				HashSet<ReplayableArtifact> current = getMissingArtifacts(ra, changeLogItem, depth);
				if(current!=null) artifacts.addAll(current);
				
			}
			
		});
		
		return artifacts;
		
	}
	
	
	private HashSet<ReplayableArtifact> getMissingArtifacts(ReplayableArtifact ra, ChangeLogItem changeLogItem, int depth) {
		
		HashSet<ReplayableArtifact> resultSet = new HashSet<ReplayableArtifact>();		
		if(changeLogItem.getInvolvedArtifactIds()!=null) {
			
			changeLogItem.getInvolvedArtifactIds().forEach( id -> {		
			
				ReplayableArtifact artifact = artifactCache.get(id);				
				
				if(artifact==null) {		
					
					artifact = Neo4JServiceFactory.getNeo4JServiceManager().getReplayableArtifactService().getReplayableArtifact(id, depth);
					
					if(artifact==null) {
						//when an artifact is not in the Neo4JDatabase it 
						//was stored with it's key as id
						artifact = new ReplayableArtifact();
						artifact.setIdInSource(id);
					}
				
					artifact.setDepth(depth);
					resultSet.add(artifact);	

					if(depth==0) artifact.setRequireLazyLoad(true);
					
				}
				
			});
			
			return resultSet;
			
		}
			
		return null;
	
	}

	private void assignDepthValues(String id, int depth) {

		ReplayableArtifact node = artifactCache.get(id);
		if(node==null) throw new RuntimeException();
		
		ArrayList<String> artifactQueue = new ArrayList<String>();
		artifactQueue.add(node.getId());
		node.setDepth(depth);
		int currentDepth;
		Iterator<Relation> incomingIterator, outgoingIterator;
		
		while(!artifactQueue.isEmpty()) {
			
			node = artifactCache.get(artifactQueue.remove(artifactQueue.size()-1));
			currentDepth = node.getDepth();

			if(currentDepth!=0) {
				
				//queuing the children of the current node for processing
				incomingIterator = node.getRelationsIncoming().iterator();
				while(incomingIterator.hasNext()) {
					ReplayableArtifact ra = (ReplayableArtifact) incomingIterator.next().getSource();
					if(ra.getDepth()<currentDepth-1) {
						ra.setDepth(currentDepth-1);
						artifactQueue.add(0, ra.getId()); 
					} 
				}	
				
				outgoingIterator = node.getRelationsOutgoing().iterator();
				while(outgoingIterator.hasNext()) {
					ReplayableArtifact ra = (ReplayableArtifact) outgoingIterator.next().getDestination();
					if(ra.getDepth()<currentDepth-1) {
						ra.setDepth(currentDepth-1);
						artifactQueue.add(0, ra.getId()); 
					} 
				}
				
				//only when we now the depth of a node, 
				//we are aware of whether we need it's
				//changeLogs or not
				history = collectChangeLogItems(history, node);
				//when fetching the artifacts from NEO4J
				//they are handed over to the session by 
				//through the PostLoad-Call
				getMissingArtifactsForAnArtifact(node, currentDepth-1);
			}
			
		}
				
		
	}
	
	private TreeSet<ChangeLogItem> collectChangeLogItems(TreeSet<ChangeLogItem> history, ReplayableArtifact ra) {
				
		if(ra.getDepth()>0) {
			Iterable<ChangeLogItem> items = Neo4JServiceFactory.getNeo4JServiceManager().getChangeLogItemService().findAllChangeLogsForAnArtifact(ra.getId());
			ArrayList<ChangeLogItem> historyIssue = new ArrayList<ChangeLogItem>();		
			items.forEach(item ->  { 
				item.setArtifactId(ra.getId());
				historyIssue.add(item); 
			});
			history.addAll(historyIssue);
		}
		 
		return history;
		
	}
	
}
