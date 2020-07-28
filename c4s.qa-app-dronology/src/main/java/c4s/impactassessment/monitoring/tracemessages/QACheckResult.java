package c4s.impactassessment.monitoring.tracemessages;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import c4s.analytics.monitoring.tracemessages.BaseTracingMessage;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.QACheckDocument.QAConstraint;
import c4s.impactassessment.workflowmodel.QACheckDocument.QAConstraint.EvaluationState;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;

public class QACheckResult extends BaseTracingMessage {

	String id;
	String type = QACheckDocument.class.getSimpleName();
	String wfid;
	List<MinimalConstraint> constraintResult;
	
	public QACheckResult(CorrelationTuple corr, QACheckDocument doc) {
		super(corr, "DB_UPDATE");
		mapDocumentToMinimalLogStructure(doc);
	}

	protected void mapDocumentToMinimalLogStructure(QACheckDocument doc) {
		this.id = doc.getId();
		this.wfid = doc.getWorkflowId();
		this.constraintResult = doc.getConstraintsReadonly().stream()
			.map(qac -> mapConstraint(qac))
			.collect(Collectors.toList());
	}		
	
	protected MinimalConstraint mapConstraint(QAConstraint qac) {
		MinimalConstraint mc = new MinimalConstraint(); 
		mc.state = qac.getEvaluationStatus();
		mc.id = qac instanceof RuleEngineBasedConstraint ? ((RuleEngineBasedConstraint) qac).getConstraintType() : qac.getId();
		mc.fulfilled = qac.getFulfilledForReadOnly().stream().map(rl -> rl.getId()).collect(Collectors.toSet());
		mc.unfulfilled = qac.getUnsatisfiedForReadOnly().stream().map(rl -> rl.getId()).collect(Collectors.toSet());
		return mc;
	}
	
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getWfid() {
		return wfid;
	}

	public void setWfid(String wfid) {
		this.wfid = wfid;
	}

	public List<MinimalConstraint> getConstraintResult() {
		return constraintResult;
	}

	public void setConstraintResult(List<MinimalConstraint> constraintResult) {
		this.constraintResult = constraintResult;
	}



	public static class MinimalConstraint {
		String id;
		EvaluationState state;
		Set<String> fulfilled;
		Set<String> unfulfilled;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public EvaluationState getState() {
			return state;
		}
		public void setState(EvaluationState state) {
			this.state = state;
		}
		public Set<String> getFulfilled() {
			return fulfilled;
		}
		public void setFulfilled(Set<String> fulfilled) {
			this.fulfilled = fulfilled;
		}
		public Set<String> getUnfulfilled() {
			return unfulfilled;
		}
		public void setUnfulfilled(Set<String> unfulfilled) {
			this.unfulfilled = unfulfilled;
		}
		
		
	}
}
