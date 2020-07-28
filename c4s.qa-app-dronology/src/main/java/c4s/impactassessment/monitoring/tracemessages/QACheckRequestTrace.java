package c4s.impactassessment.monitoring.tracemessages;

import c4s.analytics.monitoring.tracemessages.BaseTracingMessage;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;

public class QACheckRequestTrace extends BaseTracingMessage {

	String workflowId;
	String constraintTypeId;
	public static String CORRELATION_TYPE = "QA_CHECK_REQUEST";
	
	
	public QACheckRequestTrace(String requestCorrelationId, String workflowId, String constraintTypeId) {
		super(new CorrelationTuple(requestCorrelationId, CORRELATION_TYPE), "REQUEST_RECEIVED_AT_BACKEND");
		this.workflowId = workflowId;
		this.constraintTypeId = constraintTypeId;
	}
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getConstraintTypeId() {
		return constraintTypeId;
	}
	public void setConstraintTypeId(String constraintTypeId) {
		this.constraintTypeId = constraintTypeId;
	}
	
	
	
}
