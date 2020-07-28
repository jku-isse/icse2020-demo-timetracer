package c4s.impactassessment.amqp;

import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.rule.FactHandle;

import com.google.inject.Inject;

import c4s.amqp.IAddMessageHandler;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.components.AddMessage;
import c4s.components.ProcessingState;
import c4s.impactassessment.rulebase.KieSessionDomainWrapper;
import core.base.Artifact;
import core.connector.IConnector;

public class AddMessageHandler implements IAddMessageHandler {
	
	private static Logger log = LogManager.getLogger("IAddMessageHandler");

	@Inject
	private KieSessionDomainWrapper kieSession;
	@Inject
	IConnector connector;
		
	public AddMessageHandler() {
		
	}
	
	public void setKieSession(KieSessionDomainWrapper kieSession) {
		this.kieSession = kieSession;
	}

	@Override
	public ProcessingState preprocessAddMessage(AddMessage am) {
		log.debug("Preprocess QA add message: " + am);
		ProcessingState state;
		try {
			if (!am.filterID.equals("")) {
				state = new ProcessingState();		
				state.setErrorCode(500);
				state.setStatusMsg("Not implemented!");
			}
			else if (!am.featureID.equals("")) {				
				state = new ProcessingState();		
				state.setProcessState(am.featureID);
				state.setErrorCode(200);
				state.setStatusMsg("Preprocessing of AddMessage via Filter successful");		
			}
			else {
				state = new ProcessingState();
				//empty add message
				state.setErrorCode(400);
				state.setStatusMsg("Preprocessing failed, AddMessage was empty");
			}	
		} catch (Exception e) {
			state = new ProcessingState();
			state.setErrorCode(500);
			log.warn("Error adding process via JamaId "+am.featureID, e);
			state.setStatusMsg("Internal Error Processing Request:"+e.getClass().getName());				
		}
		
		return state;
	}

	@Override
	public void continueProcessingAddMessage(ProcessingState preprocessedState) {
		log.debug("Continue processing QA add message");
		String jiraKey = (String) preprocessedState.getProcessState();
		CorrelationTuple corr = new CorrelationTuple(preprocessedState.getCorrelationId(), "AddRequest");
		insertJiraIfNotExists(jiraKey, corr);
		kieSession.fireAllRules();
	}
	

	private boolean insertJiraIfNotExists(String jiraKey, CorrelationTuple corr) {
		Optional<Map.Entry<Artifact, FactHandle>> optA = kieSession.existsArtifact(jiraKey);
		if (optA.isPresent()) {
			log.info(String.format("Not inserting already loaded Jira issue: %s  ",jiraKey));
			return false;
		} else {
			Optional<Artifact> artifact = connector.fetchAndMonitor(jiraKey);
			if (artifact != null && artifact.isPresent()) {
				artifact.get().setDirtyFlag((short) (artifact.get().getDirtyFlag() + 1));
				kieSession.insertOrUpdateArtifact(corr, artifact.get());
//				kieSession.insertArtifactIfNotExists(corr, artifact.get());
			} else {
				log.warn("Jira issue wasn't inserted, no issue found with key: "+jiraKey);
			}
			return true;
		}
	}

}
