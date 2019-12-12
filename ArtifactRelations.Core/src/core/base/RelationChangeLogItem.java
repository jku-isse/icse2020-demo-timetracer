package core.base;

import java.util.HashMap;
import java.util.HashSet;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class RelationChangeLogItem extends ChangeLogItem {

	public RelationChangeLogItem() { 

	}
	
	@Property
	protected boolean artifactIsSource;
	
	@Property
	protected String fromId;
	
	@Property
	protected String fromKey;
	
	@Property
	protected String toKey;
	
	@Property  
	protected String toId;
	
	@Property
	protected String destinationRole;
	
	@Property
	protected String sourceRole;

	public boolean isArtifactIsSource() {
		return artifactIsSource;
	}

	public void setArtifactIsSource(boolean artifactIsSource) {
		this.artifactIsSource = artifactIsSource;
	}
	
	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getFromKey() {
		return fromKey;
	}

	public void setFromKey(String fromKey) {
		this.fromKey = fromKey;
	}

	public String getToKey() {
		return toKey;
	}

	public void setToKey(String toKey) {
		this.toKey = toKey;
	}

	public String getDestinationRole() {
		return destinationRole;
	}

	public void setDestinationRole(String destinationRole) {
		this.destinationRole = destinationRole;
	}

	public String getSourceRole() {
		return sourceRole;
	}

	public void setSourceRole(String sourceRole) {
		this.sourceRole = sourceRole;
	}
	
	@Override
	public ReplayableArtifact applyChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache) {

		Relation relation;

		if(fromKey==null) {
			
			relation = buildRelation(artifact, toId, toKey, cache);
			if(artifact.equals(relation.getSource())) {
				artifact.addRelationToOutgoing(relation);
			} else {
				artifact.addRelationToIncoming(relation);
			}
			
		} else {
			
			relation = buildRelation(artifact, fromId, fromKey, cache);
			if(artifact.equals(relation.getSource())) {
				artifact.removeRelationFromOutgoing(relation);
			} else {
				artifact.removeRelationFromIncoming(relation);
			}
			
			if(toKey!=null) {
			
				relation = buildRelation(artifact, toId, toKey, cache);
				if(artifact.equals(relation.getSource())) {
					artifact.addRelationToOutgoing(relation);
				} else {
					artifact.addRelationToIncoming(relation);
				}
				
			}
			
		}
		
		return artifact;
	}

	@Override
	public ReplayableArtifact undoChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache) {
		
		Relation relation;

		if(toId==null) {
			
			relation = buildRelation(artifact, fromId, fromKey, cache);
			if(artifact.equals(relation.getSource())) {
				artifact.addRelationToOutgoing(relation);
			} else {
				artifact.addRelationToIncoming(relation);
			}
			
		} else {
			
			relation = buildRelation(artifact, toId, toKey, cache);
			if(artifact.equals(relation.getSource())) {
				artifact.removeRelationFromOutgoing(relation);
			} else {
				artifact.removeRelationFromIncoming(relation);
			}
			
			if(fromId!=null) {
			
				relation = buildRelation(artifact, fromId, fromKey, cache);
				if(artifact.equals(relation.getSource())) {
					artifact.addRelationToOutgoing(relation);
				} else {
					artifact.addRelationToIncoming(relation);
				}
				
			}
			
		}
		
		return artifact;
	}

	//
	@Override
	public HashSet<String> getInvolvedArtifactIds() {
		//in case an issue is no longer a member of the database
		//there is no key stored anymore, the only thing left
		//then is the previous id, which serves as fromKey
		//or toKey in that special case
		HashSet<String> ids = new HashSet<String>();
		if(fromId!=null) ids.add(fromId);
		if(toId!=null) ids.add(toId);
		return ids;
	}

	private Relation buildRelation(ReplayableArtifact source, String destinationId, String destinationKey, HashMap<String, ReplayableArtifact> cache) {
	
		Relation relation = new Relation();
	
		ReplayableArtifact ra = cache.get(destinationId);
		if(ra==null) {
			ra = cache.get(destinationKey);
			if(ra==null) {
				ra = new ReplayableArtifact();
				ra.setIdInSource(destinationId);
			}
		}
		
		if(artifactIsSource) {
			relation.setSource(source);
			relation.setDestination(ra);
		} else {
			relation.setSource(ra);
			relation.setDestination(source);
		}
		
		relation.setSourceRole(sourceRole);
		relation.setDestinationRole(destinationRole);		
		relation.setId("Link-" + id);
			
		return relation;
		
	}
	
}
