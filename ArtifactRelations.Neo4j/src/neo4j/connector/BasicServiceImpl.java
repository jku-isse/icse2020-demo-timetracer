package neo4j.connector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.base.IdentifiableRelation;
import core.base.IdentifiableRelationMemory;
import core.base.IdentifiableStatus;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.RelationMemoryService;
import core.persistence.BasicServices.ReplayableArtifactService;
import core.persistence.BasicServices.StatusService;


public class BasicServiceImpl {

	
	public static class ArtifactServiceImpl extends Persistable<IdentifiableArtifact> implements ArtifactService {

		public ArtifactServiceImpl() {
			DEPTH_ENTITY = 1;
		}
		
		
		@Override
		Class<IdentifiableArtifact> getEntityType() {
			return IdentifiableArtifact.class;
		}

		@Override
		public List<IdentifiableArtifact> deleteArtifactsById(String id) {
		
			List<IdentifiableArtifact> deletedWFArts = new ArrayList<IdentifiableArtifact>();
			String query = "MATCH (aa:IdentifiableArtifact) WHERE aa.wfi = $wfid DETACH DELETE aa";	
			HashMap<String,String> paraMap = new HashMap<String, String>();
			Iterable<IdentifiableArtifact> iter = getSession().query(IdentifiableArtifact.class, query, paraMap);
			iter.forEach(aa -> deletedWFArts.add(aa));
			return deletedWFArts; // will never return something as query doesnt return anything, even when adding RETURN aa 
		
		}

		@Override
		public void addArtifact(IdentifiableArtifact artifact) {
			createOrUpdate(artifact);			
		}


		@Override
		public IdentifiableArtifact getArtifact(String id) {
			IdentifiableArtifact ia = find(id);		
			if(ia == null) return null;		
			return ia;
		}
		
		@Override
		public IdentifiableArtifact getArtifact(String id, int depth) {
			IdentifiableArtifact ia = find(id, depth);	
			if(ia == null) return null;
			return ia;
		}


		@Override
		public void deleteEverything() {
			purgeDatabase();
		}


		@Override
		public IdentifiableRelation getRelation(String fromId, String toId) {
			IdentifiableArtifact artifact = getArtifact(fromId);	
			if(artifact==null) return null;
			Iterator<Relation> iterator = artifact.getRelationsOutgoing().iterator();
			IdentifiableRelation r;
						
			while(iterator.hasNext()) {
				r = iterator.next();
				if(r.getDestination().getId().equals(toId)) return r;
			}
			
			return null;
		}


		@Override
		public IdentifiableArtifact getArtifactWithIdInSource(String idInSource) {
			Iterable<IdentifiableArtifact> artifacts = loadAllEntities("idInSource", idInSource, 1);
			if(artifacts.iterator().hasNext()) return artifacts.iterator().next();
			return null;
		}


		@Override
		public IdentifiableRelation getRelationWithKey(String fromIdInSource, String toIdInSource) {
			IdentifiableArtifact artifact = getArtifactWithIdInSource(fromIdInSource);	
			if(artifact==null) return null;
			Iterator<Relation> iterator = artifact.getRelationsOutgoing().iterator();
			IdentifiableRelation r;
						
			while(iterator.hasNext()) {
				r = iterator.next();
				if(r.getDestination().getIdInSource().equals(toIdInSource)) return r;
			}
			
			return null;
		}


		@Override
		public Iterable<IdentifiableArtifact> getAllArtifacts() {
			return findAll();
		}


		@Override
		public IdentifiableArtifact getArtifactWithIdInSource(String idInSource, int depth) {
			
			Iterable<IdentifiableArtifact> artifacts = loadAllEntities("idInSource", idInSource, depth);
			if(artifacts.iterator().hasNext()) return artifacts.iterator().next();
			
			IdentifiableArtifact ia = find(idInSource, depth);	
			return ia;
		}	
		
	}
	
	public static class ChangeLogItemServiceImpl extends Persistable<ChangeLogItem> implements ChangeLogItemService {

		@Override
		Class<ChangeLogItem> getEntityType() {
			return ChangeLogItem.class;
		}

		@Override
		public void addChangeLogItem(ChangeLogItem changeLogItem) {
			createOrUpdate(changeLogItem);
		}

		@Override
		public void deleteChangeLogItem(String id) {
			delete(id);
		}

		@Override
		public ChangeLogItem getChangeLogItem(String id) {
			return find(id);
		}

		@Override
		public Iterable<ChangeLogItem> findAllChangeLogsForAnArtifact(String id) {
			return loadAllEntities("correspondingArtifactId", id, 1);
		}

		@Override
		public Iterable<ChangeLogItem> getAllChangeLogItems() {
			return findAll();
		}
		
	}
	
	
	public static class RelationMemoryServiceImpl extends Persistable<IdentifiableRelationMemory> implements RelationMemoryService {

		@Override
		public void pushRelationMemory(IdentifiableRelationMemory relationMemory) {
			createOrUpdate(relationMemory);
		}

		@Override
		public IdentifiableRelationMemory fetchRelationMemory() {
			IdentifiableRelationMemory relationMemory = find(IdentifiableRelationMemory.RELATION_MEMORY_ID);
			if(relationMemory!=null) return relationMemory;
			relationMemory = new IdentifiableRelationMemory();
			relationMemory.setId(IdentifiableRelationMemory.RELATION_MEMORY_ID);
			return relationMemory;
		}

		@Override
		Class<IdentifiableRelationMemory> getEntityType() {
			return IdentifiableRelationMemory.class;
		}
	
	}
	
	public static class ReplayableArtifactServiceImpl extends Persistable<ReplayableArtifact> implements ReplayableArtifactService {

		@Override
		Class<ReplayableArtifact> getEntityType() {
			return ReplayableArtifact.class;
		}

		@Override
		public ReplayableArtifact getReplayableArtifact(String id, int depth) {
			ReplayableArtifact ra = find(id, depth);	
			if(ra == null) return null;
			return ra;
		}

		@Override
		public ReplayableArtifact getReplayableArtifactWithIdInSource(String idInSource, int depth) {
			Iterable<ReplayableArtifact> artifacts = loadAllEntities("idInSource", idInSource, depth);
			if(artifacts.iterator().hasNext()) return artifacts.iterator().next();			
			ReplayableArtifact ra = find(idInSource, depth);	
			return ra;
		}

		@Override
		public Iterable<ReplayableArtifact> getAllReplayableArtifacts() {
			return findAll();
		}

	}
	
	
	public static class StatusServiceImpl extends Persistable<IdentifiableStatus> implements StatusService {

		@Override
		Class<IdentifiableStatus> getEntityType() {
			return IdentifiableStatus.class;
		}
		
		@Override
		public void pushStatus(IdentifiableStatus status) {
			createOrUpdate(status);
		}

		@Override
		public IdentifiableStatus fetchStatus() {
			IdentifiableStatus status = find(IdentifiableStatus.STATUS_ID);
			if(status==null) throw new RuntimeException("No initial fetch was done"); 
			return status;
		}

	}
	
}
