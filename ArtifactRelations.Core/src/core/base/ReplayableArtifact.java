package core.base;

import java.io.IOException;
import java.util.HashMap;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.PostLoad;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.services.ReplayableSessionServiceFactory;

@NodeEntity
public class ReplayableArtifact extends Artifact implements IReplayableArtifact{
		
	protected transient int depth = -1; 
	
	public ReplayableArtifact() {
		
	}
	
	@PostLoad
	@Override
	public void lazyLoad() {
		super.lazyLoad();
		//add ChangeLogs to currentSession
		if(ReplayableSessionServiceFactory.isInitialized) {
			ReplayableSessionServiceFactory.getCurrentReplayableSession().checkInArtifact(this);	
		}
	}

	@Override
	public ReplayableArtifact updateArtifact(ChangeLogItem changeLogItem, HashMap<String, ReplayableArtifact> cache) throws JsonParseException, JsonMappingException, IOException {
		return changeLogItem.applyChange(this, cache);
	}

	@Override
	public ReplayableArtifact undoUpdate(ChangeLogItem changeLogItem, HashMap<String, ReplayableArtifact> cache) throws JsonParseException, JsonMappingException, IOException {
		return changeLogItem.undoChange(this, cache);
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}
