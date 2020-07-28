package c4s.impactassessment.amqp;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.rule.FactHandle;

import com.google.inject.Inject;

import c4s.amqp.IDeleteMessageHandler;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.components.DeleteMessage;
import c4s.components.ProcessingState;
import c4s.impactassessment.jiraapplication.JiraEventToKnowledgebasePusher;
import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
import c4s.impactassessment.neo4j.BasicServices;
import c4s.impactassessment.rulebase.KieSessionDomainWrapper;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

public class DeleteMessageHandler implements IDeleteMessageHandler {
	
	private static Logger log = LogManager.getLogger("IDeleteMessageHandler");

	@Inject
	private KieSessionDomainWrapper kieSession;
	
//	@Inject
//	JiraEventToKnowledgebasePusher jekp;
	@Inject
	BasicServices.WorkflowInstanceService wfiService;
	@Inject 
	BasicServices.WorkflowTaskService wftService;
	@Inject
	BasicServices.ArtifactService artService;
	@Inject
	BasicServices.DecisionNodeInstanceService dniService;
	@Inject
	RequestTracingInstrumentation rti;
	

	public DeleteMessageHandler() {
	}
	
	public void setKieSession(KieSessionDomainWrapper kieSession) {
		this.kieSession = kieSession;
	}
	
	@Override
	public ProcessingState preprocessDeleteMessage(DeleteMessage dm) {
//		log.debug("Preprocess QA delete message: " + dm);
		
		CorrelationTuple corr = new CorrelationTuple(dm.getCorrelationId(), "WorkflowInstanceDeleteRequest");
		rti.logDeleteMessageReceived(corr, dm);
		
		DeleteMessageProcessingState state = new DeleteMessageProcessingState(); 
		List<String> ids = dm.wfiIDs;
		if (ids.size() > 0) {
			state.setCorr(corr);
			state.getWorkflowIdsToDelete().addAll(ids);
			state.setErrorCode(200);
			state.setStatusMsg("Preprocessing of DeleteMessage successful");
		}
		else {
			state.setErrorCode(400);
			state.setStatusMsg("Preprocessing failed, DeleteMessage was empty");
		}
		return state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void continueProcessingDeleteMessage(ProcessingState preprocessedState) {
		DeleteMessageProcessingState state;
		if (preprocessedState instanceof DeleteMessageProcessingState)	{
			log.debug("Continue processing QA delete message");
			state = (DeleteMessageProcessingState) preprocessedState;
		} else {
			log.warn("Cannot continue processing delete message; expected DeleteMessageProcessingState but received: "+preprocessedState.getClass().toString());
			return;
		}
//		String before = kieSession.printKB(); this is debug output, only print from tests
		CorrelationTuple corr = (CorrelationTuple)((ProcessingState)preprocessedState).getCorr();		
		
		state.getWorkflowIdsToDelete().stream().forEach( id -> {
			// first remove from Workflow
			Optional<Map.Entry<WorkflowInstance, FactHandle>> wfi = kieSession.getWorkflowInstanceById(id);
			wfi.ifPresent(entry -> { // FIXME: reduce this strong coupling of deletion from workflow instatiation, 
				// Get jira and jama ids:
				String jiraKey = (String) entry.getKey().getEntry("JiraKey"); // brittle against changes of value in fields
				if (jiraKey != null) { 
					kieSession.deleteIssueAgent(jiraKey);
				}
				String jamaId = (String) entry.getKey().getEntry("JamaItemId");
				if (jamaId != null) {
					int itemId = Integer.parseInt(jamaId);
//					jamaC.removeJamaItemFromMonitoredItems(itemId);  // brittle against changes of value in JamaItemId fields
//					jamaC.removeJamaItemFromCache(itemId);
				}				
				//				rti.logDeleteMessageResponse("TOBEREPLACEDBYPOSRESPONSE")
				kieSession.deleteProcessInstance(entry.getKey().getId());
			});
			//WorkflowInstance workflow = wfiService.find(id);
			wfiService.deleteAllAbstractWorkflowInstanceObjectsByWorkflowInstanceId(id); // deletes also AbstractArtifacts
			wfiService.deleteWorkflowInstanceViaQuery(id);
			
			//wfiService.delete(id);
		});
		wfiService.invalidateSession(); // important as otherwise reinserting of same workflow wont work within same session
		//String between = kieSession.printKB();
		kieSession.fireAllRules();
		
		//System.out.println(before);
		//System.out.println(between);
		//System.out.println(kieSession.printKB());
	}
}