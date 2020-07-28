package c4s.impactassessment.workflowmodel.constraints;


import java.util.HashSet;
import java.util.Set;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.Artifact;
import c4s.impactassessment.workflowmodel.ArtifactType;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

public class ConstraintTrigger extends AbstractArtifact {

	public static ArtifactType at = new ArtifactType(ConstraintTrigger.class.getSimpleName());
	//private List<String> workflowTaskIds = new ArrayList<>();
	//private String workflowTaskId = null;
	private Set<String> constraintsToTrigger = new HashSet<String>();
	private CorrelationTuple requestCorrelation;
	
	@Override
	public Artifact getParentArtifact() {
		return null;
	}

	@Deprecated
	public ConstraintTrigger() {}
	
	public ConstraintTrigger(WorkflowInstance wfi) {
		super(null, at, wfi);
	}
	
	public ConstraintTrigger(WorkflowInstance wfi, CorrelationTuple requestCorrelation) {
		super(null, at, wfi);
		this.requestCorrelation = requestCorrelation;
	}

	public Set<String> getConstraintsToTrigger() {
		return constraintsToTrigger;
	}

	public void setConstraintsToTrigger(Set<String> constraintsToTrigger) {
		this.constraintsToTrigger = constraintsToTrigger;
	}
	
	public void addConstraint(String constraintType) {
		this.constraintsToTrigger.add(constraintType);
	}
	
//	public void setWorkflowTaskId(String id) {
//		workflowTaskId = id;
//	}
//	
//	public List<String> getWftIds() {
//		return workflowTaskIds;
//	}
//	
//	public void setWftIds(List<String> l) {
//		workflowTaskIds = l;
//	}
	
	public boolean doesConstraintTypeMatchConstraintsToTrigger(String constraintType) {
		return this.constraintsToTrigger.stream()
			//.filter(c -> c.endsWith(constraintType))
			.filter(c -> c.equals(constraintType))
			.findAny()
			.isPresent();
	}

	public CorrelationTuple getRequestCorrelation() {
		return requestCorrelation;
	}

	public void setRequestCorrelation(CorrelationTuple requestCorrelation) {
		this.requestCorrelation = requestCorrelation;
	}

}