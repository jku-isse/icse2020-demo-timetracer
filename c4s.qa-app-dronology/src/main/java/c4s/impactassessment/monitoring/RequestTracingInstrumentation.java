package c4s.impactassessment.monitoring;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.components.AddMessage;
import c4s.components.CheckMessage;
import c4s.components.DeleteMessage;
import c4s.components.ProcessingState;
import c4s.impactassessment.monitoring.tracemessages.GenericMapTracingMessage;
import c4s.impactassessment.monitoring.tracemessages.QACheckResult;
import c4s.impactassessment.workflowmodel.QACheckDocument;

public class RequestTracingInstrumentation extends AnalyticsLogger {

	
	RequestTracingInstrumentation() {
		super(RequestTracingInstrumentation.class.getSimpleName());
	}

	public void logCheckMessageReceived(CorrelationTuple corr, CheckMessage msg) {
		super.logInfoTraceMessage(new GenericMapTracingMessage(corr, "CheckMessageRequest")
				.addFluent("ConstraintType", msg.constrType)
				.addFluent("WorkflowInstanceId", msg.wfiId));
	}
	
	public void logDeleteMessageReceived(CorrelationTuple corr, DeleteMessage msg) {
		super.logInfoTraceMessage(new GenericMapTracingMessage(corr, "DeleteMessageRequest")
				.addFluent("WorkflowInstanceIds", String.join(", ", msg.wfiIDs)));
	}
	
	public void logAddMessageReceived(CorrelationTuple corr, AddMessage msg) {
		super.logInfoTraceMessage(new GenericMapTracingMessage(corr, "AddMessageRequest")
				.addFluent("FeatureItemId", msg.featureID)
				.addFluent("JamaFilterId", msg.filterID));
	}
	
	public void logCheckMessageResponse(ProcessingState response) {
		super.logInfoTraceMessage(new GenericMapTracingMessage((CorrelationTuple) response.getCorr(), "CheckMessageResponse")
				.addFluent("ResponseCode", ""+response.getErrorCode())
				.addFluent("ResponseMessage", response.getStatusMsg()));
	}
	
	public void logDataUpdateFetchingComplete(CorrelationTuple corr) {
		super.logInfoTraceMessage(new GenericMapTracingMessage(corr, "CheckMessageDataFetchingComplete"));
	}
	
	public void logCheckMessageRequestCompletedInRuleEngine(CorrelationTuple corr) {
		super.logInfoTraceMessage(new GenericMapTracingMessage(corr, "ConstraintEvaluationComplete"));
	}
	
	public void logQualityCheckDocumentUpdate(QACheckDocument updatedQADoc) {
		super.logInfoTraceMessage(new QACheckResult(updatedQADoc.getLastChangeDueTo().orElse(new CorrelationTuple("RequestUnknown", "QualityCheckRequest")), updatedQADoc));				
	}
}
