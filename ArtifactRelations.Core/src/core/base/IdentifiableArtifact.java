package core.base;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.PostLoad;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import core.services.ErrorLoggerServiceFactory;
import core.services.Neo4JServiceFactory;

@NodeEntity
public abstract class IdentifiableArtifact {
	
	@Id
	protected String id;
	
	@Property 
	protected String service;
	
	@Property
	protected int relationCount;
	
	@Property
	protected String idInSource;
	
	@Property
	protected String origin;
	
	@Property
	protected String updateTimestamp;

	@Property 
	protected boolean epicParent;
	
	@Property
	protected boolean deletedInOrigin;
	
	@Property
	protected boolean fullyFetched;
	
	protected transient boolean requiresLazyLoad = true;
	protected transient boolean inReplayableSession = false;

	@Relationship(type="RelatesTo", direction=Relationship.INCOMING)
	protected Set<Relation> relationsIncoming = new HashSet<Relation>();
	
	@Relationship(type="RelatesTo", direction=Relationship.OUTGOING)
	protected Set<Relation> relationsOutgoing = new HashSet<Relation>();
	
	public IdentifiableArtifact() {
		fullyFetched = false;
	}
	
	@Properties(prefix = "property", allowCast = true)		
	protected Map<String, Object> properties;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdInSource() {
		return idInSource;
	}

	public void setIdInSource(String idInSource) {
		this.idInSource = idInSource;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
	
	public String getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(String updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public boolean isDeletedInOrigin() {
		return deletedInOrigin;
	}

	public void setDeletedInOrigin(boolean deletedInOrigin) {
		this.deletedInOrigin = deletedInOrigin;
	}

	public boolean isFullyFetched() {
		return fullyFetched;
	}

	public void setFullyFetched(boolean fullyFetched) {
		this.fullyFetched = fullyFetched;
	}

	public void setRequireLazyLoad(boolean requiresLazyLoad) {
		this.requiresLazyLoad = requiresLazyLoad;
	}
	
	public boolean doesRequireLazyLoad() {
		return requiresLazyLoad;
	}
	
	public int getRelationCount() {
		return relationCount;
	}

	public void setRelationCount(int relationCount) {
		this.relationCount = relationCount;
	}

	public boolean isRequiresLazyLoad() {
		return requiresLazyLoad;
	}

	public boolean isEpicParent() {
		return epicParent;
	}

	public void setEpicParent(boolean epicParent) {
		this.epicParent = epicParent;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public String getService() {
		return service;
	}

	/**
	 * returns all relations, that have this item as a destination.
	 * since the artifacts on the other end are also a part of the relation, 
	 * it is made sure that those artifacts are always loaded too.
	 * therefore one can iterate through the entire graph structure
	 * as long as the connection to the Neo4J-database is stable
	 * 
	 * @return Set<Relation>
	 */
	public final Set<Relation> getRelationsIncoming() {
		
		if(requiresLazyLoad) {
			if(inReplayableSession) throw new RuntimeException("the provided depth of the ReplayableStructure cannot be exceeded");
			try {
				IdentifiableArtifact a = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().find(getId());	
				if(a==null) throw new Exception("This artifact is not contained in the database");
				a.relationsIncoming.forEach(r -> addRelationToIncoming(r));
			} catch (Exception e) {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "IdentifiableArtifact: getRelationsIncoming(): " + e.getMessage());
			}
		}
		
		return Collections.unmodifiableSet(relationsIncoming);	
	}

	/**
	 * returns all relations, that have this item as a source.
	 * since the artifacts on the other end are also a part of the relation, 
	 * it is made sure that those artifacts are always loaded too.
	 * therefore one can iterate through the entire graph structure
	 * as long as the connection to the Neo4J-database is stable
	 * 
	 * @return Set<Relation>
	 */
	public Set<Relation> getRelationsOutgoing() {
				
		if(requiresLazyLoad) {
			if(inReplayableSession) throw new RuntimeException("the provided depth of the ReplayableStructure cannot be exceeded");
			try {
				IdentifiableArtifact a = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().find(getId());	
				if(a==null) throw new Exception("This artifact is not contained in the database");
				a.relationsOutgoing.forEach(r -> addRelationToOutgoing(r));
			} catch (Exception e) {
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "IdentifiableArtifact: getRelationsOutgoing(): " + e.getMessage());
			}
		}
	
		return Collections.unmodifiableSet(relationsOutgoing);	
	}

	@PostLoad
	public void lazyLoad() {
		if(getRelationCount() != getRealRelationCount()) {
			requiresLazyLoad = true;
		} else {
			requiresLazyLoad = false;
		}
	}
	
	/**
	 * returns the sum of all outgoing and incoming relations
	 * 
	 * @return int
	 */
	public int getRealRelationCount() {
		return relationsIncoming.size() + relationsOutgoing.size();
	}
		
	/**
	 * adds a relation to the set of incoming relations
	 */
	public void addRelationToIncoming(Relation relation) {
		if(relation.getDestination().getId().equals(getId())) {
			if(!relationsIncoming.contains(relation)) {
				if(relationsIncoming.add(relation)) {
					relationCount++;
				}
			}
		}
	}
	
	/**
	 * adds a relation to the set of outgoing relations
	 */
	public void addRelationToOutgoing(Relation relation) {
		if(relation.getSource().getId().equals(getId())) {
			if(!relationsOutgoing.contains(relation)) {
				if(relationsOutgoing.add(relation)) {
					relationCount++;
				}
			}
		}
	}
	
	/**
	 * removes a relation from the set of incoming relations.
	 * returns null if the relations was not found.
	 * 
	 * @return Relation
	 */
	public Relation removeRelationFromIncoming(Relation relation) {
		
		Iterator<Relation> iterator = relationsIncoming.iterator();
		Relation dummy;
		
		while(iterator.hasNext()) {
			dummy = iterator.next();

			if(dummy.equals(relation)) {
				relationsIncoming.remove(dummy);
				return dummy;
			}
			
		}

		return null;
	}
	
	/**
	 * removes a relation from the set of outgoing relations
	 * returns null if the relations was not found.
	 * 
	 * @return Relation
	 */
	public Relation removeRelationFromOutgoing(Relation relation) {
		
		Iterator<Relation> iterator = relationsOutgoing.iterator();
		Relation dummy;
		
		while(iterator.hasNext()) {
			dummy = iterator.next();
			if(dummy.equals(relation)) {
				relationsOutgoing.remove(dummy);
				return dummy;
			}
			
		}

		return null;
	}
	
	/**
	 * returns a timestamp, which indicates the actuality of the artifact
	 * in terms of updates
	 * 
	 * @return Timestamp
	 */
	public Timestamp getTimestamp() {
		if(updateTimestamp!=null) return Timestamp.valueOf(updateTimestamp);
		return null;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idInSource == null) ? 0 : idInSource.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IdentifiableArtifact other = (IdentifiableArtifact) obj;
		if (id == null) {
			if (idInSource == null) {
				if (other.idInSource != null)
					return false;
			} else if (!idInSource.equals(other.idInSource))
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public boolean isInReplayableSession() {
		return inReplayableSession;
	}

	public void setInReplayableSession(boolean inReplayableSession) {
		this.inReplayableSession = inReplayableSession;
	}
	
	@Override
	public Artifact clone() {
		
		Artifact a = new Artifact();
		
		a.deletedInOrigin = deletedInOrigin;
		a.epicParent = epicParent;
		a.fullyFetched = fullyFetched;
		a.id = id;
		a.idInSource = idInSource;
		a.inReplayableSession = inReplayableSession;
		a.origin = origin;
		a.relationCount = relationCount;
		a.relationsIncoming = relationsIncoming;
		a.relationsOutgoing = relationsOutgoing;
		a.requiresLazyLoad = requiresLazyLoad;
		a.service = service;
		a.updateTimestamp = updateTimestamp;
		
		a.properties = new HashMap<String, Object>();
		properties.forEach( (k,v) -> {
			
		});
		
		return a;	
		
	}
	
	
	
}
	
	


