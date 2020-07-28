package c4s.impactassessment.monitoring;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.monitoring.tracemessages.GenericMapTracingMessage;
import c4s.jiralightconnector.IssueAgent;
import core.base.Artifact;

public class DomainObjectTracingInstrumentation extends AnalyticsLogger {

	DomainObjectTracingInstrumentation() {
		super(DomainObjectTracingInstrumentation.class.getSimpleName());
	}

	public void logJiraIssueInsertedInRuleBase(CorrelationTuple corr, Artifact a) {
		GenericMapTracingMessage msg = new GenericMapTracingMessage(corr, "RULEBASE_INSERT")
				.addFluent("id", a.getId())
				.addFluent("type", "JiraIssue");
		super.logInfoTraceMessage(msg);
	}
	
	public void logJiraIssueUpdatedInRuleBase(CorrelationTuple corr, Artifact a) {
		GenericMapTracingMessage msg = new GenericMapTracingMessage(corr, "RULEBASE_UPDATE")
				.addFluent("id", a.getId())
				.addFluent("type", "JiraIssue");
		super.logInfoTraceMessage(msg);	
	}
	
	public void logJiraIssueRemovedFromRuleBase(CorrelationTuple corr, Artifact a) {
		
	}


	
}
