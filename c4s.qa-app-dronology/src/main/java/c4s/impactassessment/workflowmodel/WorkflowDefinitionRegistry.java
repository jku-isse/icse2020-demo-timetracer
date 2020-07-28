package c4s.impactassessment.workflowmodel;

import java.util.HashMap;

public class WorkflowDefinitionRegistry {

	
	
	private HashMap<String,WorkflowDefinition> wfds = new HashMap<>();
	
	public WorkflowDefinitionRegistry(TaskStateTransitionEventPublisher tstep) {
		initWorkflows(tstep);
	}
	
	// for now just have a fixed set of workflows, will be made flexible later
	public void initWorkflows(TaskStateTransitionEventPublisher tstep) {
		WPManagementWorkflow wfd = new WPManagementWorkflow();
		wfd.initWorkflowSpecification();		
		wfd.setTaskStateTransitionEventPublisher(tstep);
		wfds.put(wfd.getId(), wfd);
		
		WPWorkflow wpWF = new WPWorkflow();
		wpWF.initWorkflowSpecification();		
		wpWF.setTaskStateTransitionEventPublisher(tstep);
		wfds.put(wpWF.getId(), wpWF);
		
		SubWPWorkflow subWpWF = new SubWPWorkflow();
		subWpWF.initWorkflowSpecification();		
		subWpWF.setTaskStateTransitionEventPublisher(tstep);
		wfds.put(subWpWF.getId(), subWpWF);
		
		DronologyWorkflow dronologyWF = new DronologyWorkflow();
		dronologyWF.initWorkflowSpecification();
		dronologyWF.setTaskStateTransitionEventPublisher(tstep);
		wfds.put(dronologyWF.getId(), dronologyWF);
	}
	
	public WorkflowDefinition getDefinitionById(String wfdId) {
		return wfds.get(wfdId);
	}
	
}
