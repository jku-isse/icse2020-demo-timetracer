package core.base;

import org.neo4j.ogm.annotation.RelationshipEntity;

@RelationshipEntity(type="RelatesTo")
public class Relation extends IdentifiableRelation{
	
	public Relation() {
		super();	
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		String sourceId ="null", destinationId="null";
		if(source!=null) {
			sourceId=source.getIdInSource();
			if(sourceId==null) sourceId = source.getId();
		}
		if(destination!=null) {
			destinationId=destination.getIdInSource();
			if(destinationId==null) destinationId = destination.getId();
		}

		sb.append("{id=" + id + 
				", sourceRole=" + sourceRole + 
				", destinationRole=" + destinationRole +
				", relationType=" + relationType +
				", source=" + sourceId +
				", destination= " + destinationId+ "}");

		return sb.toString();
	}	
	

	
}
