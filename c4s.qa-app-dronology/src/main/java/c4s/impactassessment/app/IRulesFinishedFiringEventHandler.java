package c4s.impactassessment.app;

import org.kie.api.runtime.KieSession;

public interface IRulesFinishedFiringEventHandler {

	
	public void handleRulesFinishedFiringEvent(KieSession kSession);

}
