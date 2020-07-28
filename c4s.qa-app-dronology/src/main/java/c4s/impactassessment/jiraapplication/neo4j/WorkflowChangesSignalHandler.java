package c4s.impactassessment.jiraapplication.neo4j;

import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.WorkflowTask;

public interface WorkflowChangesSignalHandler {

	void signalChangedWorkflow(WorkflowInstance wfi);

	void signalNewTask(WorkflowTask wft);

	void signalChangedArtifact(AbstractArtifact art);

}