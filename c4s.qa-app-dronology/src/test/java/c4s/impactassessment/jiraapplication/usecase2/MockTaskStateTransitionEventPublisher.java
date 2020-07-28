package c4s.impactassessment.jiraapplication.usecase2;

import c4s.impactassessment.workflowmodel.TaskStateTransitionEvent;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;

public class MockTaskStateTransitionEventPublisher implements TaskStateTransitionEventPublisher {

	@Override
	public void publishEvent(TaskStateTransitionEvent event) {
		// no op
	}

}
