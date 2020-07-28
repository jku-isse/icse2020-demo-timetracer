package c4s.impactassessment.workflowmodel;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.workflowmodel.QACheckDocument.QAConstraint;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;

@NodeEntity
public class RuleEngineBasedConstraint extends QAConstraint {

	@Deprecated
	public RuleEngineBasedConstraint() { super();
	}
	
	public RuleEngineBasedConstraint(String id, QACheckDocument parent, String constraintType, WorkflowInstance wfi, String description) {
		super(id, parent, wfi);
		if (id == null && constraintType != null && wfi != null && wfi.getId() != null) { // then super has set a random uuid one, we override this here for predictable ids if constrainttype and wfi are not null
			this.id = wfi.getId()+"-"+constraintType;
		}
		this.constraintType = constraintType;
		super.description = description;
	}
	
	@Property
	private String constraintType;
	
	public String getConstraintType() {
		return constraintType;
	}

	@Override
	public void checkConstraint() {
		// no op, check is triggered/run by rule engine, only output stored here
	}

	@Override
	public String toString() {
		return "RuleEngineBasedConstraint [constraintType=" + constraintType + ", fulfilledFor=" + fulfilledFor
				+ ", unsatisfiedFor=" + unsatisfiedFor + ", evaluationStatus=" + evaluationStatus
				+ ", evaluationStatusMessage=" + evaluationStatusMessage + ", lastEvaluated=" + lastEvaluated
				+ ", lastChanged=" + lastChanged + "]";
	}

	public boolean isAffectedBy(ConstraintTrigger ct) {
		if (ct.getConstraintsToTrigger().contains("*") || ct.doesConstraintTypeMatchConstraintsToTrigger(getConstraintType()))
			return true;
		return false;
	}
	
	public void setEvaluated(CorrelationTuple lastChangeDueTo) {
		setEvaluationStatusMessage("");
		setEvaluationStatus(EvaluationState.SUCCESS);
		setLastChangeDueTo(lastChangeDueTo);
	}
	
}
