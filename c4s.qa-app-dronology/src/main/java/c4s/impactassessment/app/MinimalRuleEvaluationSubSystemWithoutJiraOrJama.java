package c4s.impactassessment.app;

import java.util.HashMap;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
import c4s.impactassessment.rulebase.KieSessionDomainWrapper;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;
import c4s.impactassessment.workflowmodel.WorkflowDefinitionRegistry;

public class MinimalRuleEvaluationSubSystemWithoutJiraOrJama implements IRuleEvaluationSubsystem {

	private static Logger log = LogManager.getLogger(MinimalRuleEvaluationSubSystemWithoutJiraOrJama.class);

	//@Inject @PersistingKieSession
	//private KieSession kSession;
	@Inject
	KieSessionDomainWrapper kSession;
	@Inject
	TaskStateTransitionEventPublisher tstep;
	@Inject
	RequestTracingInstrumentation rti;
	
	
	@Inject // to enable autocalling
	@Override
	public void start() {
		HashMap<String, Object> globals = new HashMap<String, Object>();
		//globals.put("log", log);
		WorkflowDefinitionRegistry wfdReg = new WorkflowDefinitionRegistry(tstep);
		//globals.put("wfdReg", wfdReg);
		//globals.put("analytics", rti);
		kSession.setGlobals(globals);
	}


	public KieSessionDomainWrapper getkSession() {
		return kSession;
	}


	public void setkSession(KieSessionDomainWrapper kSession) {
		this.kSession = kSession;
	}
	

}
