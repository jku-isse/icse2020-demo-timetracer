package core.base;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.PostLoad;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import core.services.JiraArtifactFactoryServiceFactory;

@RelationshipEntity
public abstract class IdentifiableRelation {
	
	@Id
	protected String id;	
	
	@Property
	protected String fromToId;
	
	@Property
	protected String property;
	
	@StartNode
	protected Artifact source;
	
	@Property
	protected String sourceRole;
	
	@Property
	protected String destinationRole;
	
	@Property
	protected String relationType;
	
	@EndNode
	protected Artifact destination;
	
	public IdentifiableRelation() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public Artifact getSource() {
		return source;
	}
	

	public void setSource(Artifact source) {
		this.source = source;
	}
	
	public Artifact getDestination() {
		return destination;
	}

	public void setDestination(Artifact destination) {
		this.destination = destination;
	}

	public String getSourceRole() {
		return sourceRole;
	}

	public void setSourceRole(String sourceRole) {
		this.sourceRole = sourceRole;
	}

	public String getDestinationRole() {
		return destinationRole;
	}

	public void setDestinationRole(String destinationRole) {
		this.destinationRole = destinationRole;
	}

	public String getRelationType() {
		return relationType;
	}

	public void setRelationType(String relationType) {
		this.relationType = relationType;
	}

	public String getFromToId() {
		return fromToId;
	}

	public void setFromToId(String fromToId) {
		this.fromToId = fromToId;
	}
	
	@PostLoad
	private void deserialize() {		
		
		try {
			
			destination = JiraArtifactFactoryServiceFactory.
					getJiraArtifactFactory().deserialize(destination);
			
			source = JiraArtifactFactoryServiceFactory.
					getJiraArtifactFactory().deserialize(source);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((destination == null) ? 0 : destination.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((source == null) ? 0 : source.hashCode());
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
		IdentifiableRelation other = (IdentifiableRelation) obj;
		if (destination == null) {
			if (other.destination != null)
				return false;
		} else if (!destination.equals(other.destination))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	

}