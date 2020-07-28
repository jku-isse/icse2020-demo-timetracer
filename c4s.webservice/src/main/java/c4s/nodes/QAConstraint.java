package c4s.nodes;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity("QAConstraint")
public class QAConstraint {

	@Id
	private String id;
	@Property
	String description;
	@Relationship(type="FULFILLED_CONSTRAINT")
	Set<ResourceLink> fulfilledFor = new HashSet<ResourceLink>();
	@Relationship(type="FAILED_CONSTRAINT")
	Set<ResourceLink> unsatisfiedFor = new HashSet<ResourceLink>();						
	@Property
	EvaluationState evaluationStatus = EvaluationState.NOT_YET_EVALUATED;
	@Property
	String evaluationStatusMessage = "";
	@Property
	Instant lastEvaluated = Instant.MIN;
	@Property
	Instant lastChanged = Instant.MIN;		
	@Property
	int orderInParentDoc = 0;
	@Property(name="wfi")
	String workflowId;
	@Property
	String constraintType;

	@Deprecated
	public QAConstraint() {
		super();
	}

	public boolean isFulfilled() {
		return evaluationStatus.equals(EvaluationState.SUCCESS) && unsatisfiedFor.isEmpty();
	}

	public String getId() {
		return id;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<ResourceLink> getFulfilled() {
		return fulfilledFor;
	}

	public Set<ResourceLink> getUnsatisfied() {
		return unsatisfiedFor;
	}

	public EvaluationState getEvaluationStatus() {
		return evaluationStatus;
	}

	public void setEvaluationStatus(EvaluationState evaluationStatus) {
		this.evaluationStatus = evaluationStatus;
	}				

	public String getEvaluationStatusMessage() {
		return evaluationStatusMessage;
	}

	public void setEvaluationStatusMessage(String evaluationStatusMessage) {
		this.evaluationStatusMessage = evaluationStatusMessage;
	}

	public Instant getLastEvaluated() {
		return lastEvaluated;
	}

	public void setLastEvaluated(Instant lastEvaluated) {
		this.lastEvaluated = lastEvaluated;
	}

	public Instant getLastChanged() {
		return lastChanged;
	}

	public int getOrderInParentDoc() {
		return orderInParentDoc;
	}

	public void setOrderInParentDoc(int orderInParentDoc) {
		this.orderInParentDoc = orderInParentDoc;
	}
	
	public String getWorkflowId() {
		return workflowId;
	}
	
	public String getConstraintType() {
		return constraintType;
	}

	public void setConstraintType(String constraintType) {
		this.constraintType = constraintType;
	}
	
	public static enum EvaluationState {
		NOT_YET_EVALUATED,
		SUCCESS,
		FAILURE
	}
	
	@Override
	public String toString() {
		return "QAConstraint [id=" + id + ", fulfilledFor=" + fulfilledFor + ", unsatisfiedFor=" + unsatisfiedFor
				+ ", evaluationStatus=" + evaluationStatus + ", evaluationStatusMessage=" + evaluationStatusMessage
				+ ", lastEvaluated=" + lastEvaluated + ", lastChanged=" + lastChanged + "]";
	}
}
