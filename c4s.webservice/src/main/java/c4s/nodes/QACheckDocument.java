package c4s.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class QACheckDocument{
	
	@Id 
	String id;
	@Property(name="wfi")
	String workflowId;
	@Relationship(type="HAS_CONSTRAINTS", direction=Relationship.OUTGOING)
	Set<QAConstraint> constraints = new HashSet<QAConstraint>();
	
	
	@Override
	public String toString() {
		return "QACheckDocument [id=" + id + ", constraints=" + constraints + "]";
	}

	@Deprecated
	public QACheckDocument() {
		super();
	}
	
	public String getId() {
		return id;
	}
	
	public Set<QAConstraint> getConstraints() {
		return constraints;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}
}
