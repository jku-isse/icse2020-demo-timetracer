package c4s.impactassessment.monitoring;

import java.util.Set;

import org.apache.logging.log4j.Level;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.monitoring.tracemessages.GenericListTracingMessage;
import c4s.jiralightconnector.analytics.JiraUpdateTracingInstrumentation;

public class DomainObjectUpdateTracingInstrumentation extends AnalyticsLogger implements JiraUpdateTracingInstrumentation {

	public DomainObjectUpdateTracingInstrumentation() {
		super(DomainObjectUpdateTracingInstrumentation.class.getSimpleName());
	}

	@Override
	public void logJiraPollResult(CorrelationTuple corr, Set<String> changedIssueKeys) {
		GenericListTracingMessage msg = new GenericListTracingMessage(corr, "JIRA_ITEMS_POLL_RESULT");
		msg.getBody().addAll(changedIssueKeys);
		super.logTraceMessage(msg, Level.INFO);
	}

	@Override
	public void logJiraUpdateResult(CorrelationTuple corr, Set<String> updatedCachedIssueKeys) {
		GenericListTracingMessage msg = new GenericListTracingMessage(corr, "JIRA_ITEMS_UPDATED_AFTER_POLL");
		msg.getBody().addAll(updatedCachedIssueKeys);
		super.logTraceMessage(msg, Level.INFO);
	}
	
}
