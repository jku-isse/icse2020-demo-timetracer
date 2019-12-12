package core.persistence;

import java.util.List;

import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.base.IdentifiableRelation;
import core.base.IdentifiableRelationMemory;
import core.base.IdentifiableStatus;
import core.base.ReplayableArtifact;

public class BasicServices {

	public static interface ArtifactService extends IPersistable<IdentifiableArtifact>{
		public List<IdentifiableArtifact> deleteArtifactsById(String id);
		public Iterable<IdentifiableArtifact> getAllArtifacts();
		public void addArtifact(IdentifiableArtifact artifact);
		public IdentifiableArtifact getArtifact(String key);
		public IdentifiableArtifact getArtifactWithIdInSource(String idInSource);
		public IdentifiableArtifact getArtifactWithIdInSource(String idInSource, int depth);
		public IdentifiableArtifact getArtifact(String id, int depth);
		public IdentifiableRelation getRelation(String fromId, String toId);
		public IdentifiableRelation getRelationWithKey(String fromKey, String toKey);
		public void deleteEverything();
	}
	
	public static interface ChangeLogItemService extends IPersistable<ChangeLogItem>{
		public Iterable<ChangeLogItem> getAllChangeLogItems();
		public void addChangeLogItem(ChangeLogItem changeLogItem);
		public ChangeLogItem getChangeLogItem(String id);
		public void deleteChangeLogItem(String id);
		public Iterable<ChangeLogItem> findAllChangeLogsForAnArtifact(String id);
	}
	
	public static interface StatusService extends IPersistable<IdentifiableStatus>{
		public void pushStatus(IdentifiableStatus status);
		public IdentifiableStatus fetchStatus();
	}
	
	public static interface RelationMemoryService extends IPersistable<IdentifiableRelationMemory>{
		public void pushRelationMemory(IdentifiableRelationMemory relationMemory);
		public IdentifiableRelationMemory fetchRelationMemory();
	}
	
	public static interface ReplayableArtifactService extends IPersistable<ReplayableArtifact>{
		public Iterable<ReplayableArtifact> getAllReplayableArtifacts();
		public ReplayableArtifact getReplayableArtifact(String id, int depth);
		public ReplayableArtifact getReplayableArtifactWithIdInSource(String id, int depth);
	}

	@SuppressWarnings("rawtypes")
	public static interface SessionManager extends IPersistable {
		
	}
		
}
