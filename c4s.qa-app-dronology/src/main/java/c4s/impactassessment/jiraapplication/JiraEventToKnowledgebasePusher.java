package c4s.impactassessment.jiraapplication;

import com.google.inject.Inject;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.jiralightconnector.AbstractMonitoring;
import c4s.jiralightconnector.MonitoringScheduler;



public class JiraEventToKnowledgebasePusher {
		
	//@Inject
	//IJiraInstance jiraInst;
	@Inject 
	AbstractMonitoring csp;
	
	
	MonitoringScheduler ms = new MonitoringScheduler();
	
	public boolean fetchUpdatesForAllItemsNow(CorrelationTuple corr) {
		return ms.runAllMonitoringTasksSequentiallyOnceNow(corr);
	}
	
	@Inject
	public void init() { 			
		ms.registerAndStartTask(csp);
	}

	public void shutdown() {
		ms.shutdown();
	}
	
}
