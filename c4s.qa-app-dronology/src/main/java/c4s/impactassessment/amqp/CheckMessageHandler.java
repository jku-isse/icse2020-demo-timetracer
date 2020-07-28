package c4s.impactassessment.amqp;

import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.rule.FactHandle;

import com.google.inject.Inject;

import c4s.amqp.ICheckMessageHandler;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.components.CheckMessage;
import c4s.components.CheckProcessingState;
import c4s.components.ProcessingState;
import c4s.impactassessment.jiraapplication.JiraEventToKnowledgebasePusher;
import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
import c4s.impactassessment.rulebase.KieSessionDomainWrapper;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;

public class CheckMessageHandler implements ICheckMessageHandler {
	
	private static Logger log = LogManager.getLogger("ICheckMessageHandler");

	@Inject
	private KieSessionDomainWrapper kieSession;
	
	public CheckMessageHandler() {
		
	}
	
	public void setKieSession(KieSessionDomainWrapper kieSession) {
		this.kieSession = kieSession;
	}
	
	@Override
	public ProcessingState preprocessCheckMessage(CheckMessage cm) {
//		log.debug("Preprocess QA check message: " + cm);
		CorrelationTuple corr = new CorrelationTuple(cm.getCorrelationId(), "QualityCheckRequest");

//		rti.logCheckMessageReceived(corr, cm);
		
		CheckProcessingState state = new CheckProcessingState();
		state.setCorr(corr);
		state.setCm(cm);
		Optional<Map.Entry<WorkflowInstance, FactHandle>> optWFI = kieSession.getWorkflowInstanceById(cm.wfiId);
		if (!optWFI.isPresent()) {
			String s = String.format("Received QACheckMessage with WorkflowID %s that is not in the KieBase, please add via Jama WP/SubWP ID.", cm.wfiId);
			state.setErrorCode(400);
			state.setStatusMsg("Error in preprocessing: "+s);
			log.warn(s);
		}
		else {
			state.setProcessState(optWFI);
			state.setErrorCode(200);
			state.setStatusMsg("Preprocessing of CheckMessage successful");
		}
//		rti.logCheckMessageResponse(state);
		return state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void continueProcessingCheckMessage(ProcessingState preprocessedState) {		
		log.debug("Continue processing QA check message");
		if (preprocessedState instanceof CheckProcessingState) {
			CheckProcessingState state = (CheckProcessingState)preprocessedState;
			((Optional<Map.Entry<WorkflowInstance, FactHandle>>)preprocessedState.getProcessState()).ifPresent(entry -> {
				CorrelationTuple corr = ((CorrelationTuple)state.getCorr());			
//				jamaC.fetchUpdatesForAllItemsNow(corr);
//				jekp.fetchUpdatesForAllItemsNow(corr); 
//				rti.logDataUpdateFetchingComplete(corr);
				ConstraintTrigger ct = new ConstraintTrigger(entry.getKey(), corr);
				ct.addConstraint(state.getCm().constrType);
				kieSession.insertConstraintTrigger(ct);
				kieSession.fireAllRules();
			});
		}
	}

	
}