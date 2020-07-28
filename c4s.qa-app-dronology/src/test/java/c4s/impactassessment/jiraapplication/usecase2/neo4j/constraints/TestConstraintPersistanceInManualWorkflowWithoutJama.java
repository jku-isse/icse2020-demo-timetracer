package c4s.impactassessment.jiraapplication.usecase2.neo4j.constraints;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.jiraapplication.usecase2.TestWPManagementWorkflow;
import c4s.impactassessment.jiraapplication.usecase2.neo4j.Neo4JConnectorConfig;
import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowTaskService;
import c4s.impactassessment.workflowmodel.AbstractWorkflowInstanceObject;
import c4s.impactassessment.workflowmodel.ArtifactWrapper;
import c4s.impactassessment.workflowmodel.DecisionNodeDefinition.States;
import c4s.impactassessment.workflowmodel.DecisionNodeInstance;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;
import c4s.impactassessment.workflowmodel.TaskDefinition;
import c4s.impactassessment.workflowmodel.TaskLifecycle.Events;
import c4s.impactassessment.workflowmodel.WPManagementWorkflow;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.WorkflowTask;
import c4s.impactassessment.workflowmodel.WorkflowTask.ArtifactInput;
import c4s.impactassessment.workflowmodel.WorkflowTask.ArtifactOutput;


public class TestConstraintPersistanceInManualWorkflowWithoutJama {

	
	static Injector injector;
	static TestWPManagementWorkflow twfd;
	
	@BeforeClass
	public static void setUp() throws Exception {
		injector = Guice.createInjector(new Neo4JConnectorConfig());
		SessionFactory nsf = injector.getInstance(SessionFactory.class);
		Session session = nsf.openSession(); 
		session.purgeDatabase();
		
		twfd = new TestWPManagementWorkflow();
		injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
		WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		wfdService.push(twfd);
	}	
	
	@Test
	public void testStoringWPProcessWithSingleTaskAndQA() {
		String wfID = "WF1";
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowTaskService wftService = injector.getInstance(WorkflowTaskService.class);
		
		ArtifactWrapper ticketArt1 = new ArtifactWrapper("TICKET1", TestWPManagementWorkflow.ARTIFACT_TYPE_JIRA_TICKET, null, null);
		ArtifactService artService = injector.getInstance(ArtifactService.class);
		//artService.push(ticketArt);
		
		WorkflowInstance wfi = twfd.createInstance(wfID);
		List<AbstractWorkflowInstanceObject> awos = wfi.enableWorkflowTasksAndDecisionNodes();
		wfiService.push(wfi);
		DecisionNodeInstance dni = (DecisionNodeInstance) awos.get(0);
		dni.completedDataflowInvolvingActivationPropagation();
		List<TaskDefinition> tds = dni.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
       	tds.stream().
       		forEach(td -> {        			
       			WorkflowTask wt = wfi.instantiateTask(td);
       			wt.addInput(new ArtifactInput(ticketArt1, TestWPManagementWorkflow.INPUT_ROLE_WPTICKET));
       			wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
       			Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
       			dni.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches       			
       		});				
		
       	QACheckDocument qa = new QACheckDocument("QA1", wfi);
		RuleEngineBasedConstraint srsConstraint = new RuleEngineBasedConstraint("REBC1", qa, "CheckSWRequirementReleased", wfi, "Have all SRSs of the WP been released?");	
		qa.addConstraint(srsConstraint);	
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=11", "self", "", "html", "SRS 11"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=12", "self", "", "html", "SRS 12"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=13", "self", "", "html", "SRS 13"));
		srsConstraint.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=14", "self", "", "html", "SRS 14"));
		srsConstraint.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=15", "self", "", "html", "SRS 15"));
       	// add to WFTask
		wfi.getWorkflowTasksReadonly().stream().forEach(wft -> {
			// there is only one here, so lets use this to add the QA document
			ArtifactOutput ao = new ArtifactOutput(qa, "QA_PROCESS_CONSTRAINTS_CHECK");
			wft.addOutput(ao);
			artService.push(qa);
		});
		
       	wfiService.push(wfi);
		List<WorkflowTask> deletedWFTs = wftService.deleteDetachedPlaceHolders();
		deletedWFTs.size();
	}

	@Test
	public void testStoringWPProcessWMultiTaskAndMultiQA() {
		String wfID = "WF2";
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowTaskService wftService = injector.getInstance(WorkflowTaskService.class);
		
		ArtifactWrapper ticketArt1 = new ArtifactWrapper("TICKET2", TestWPManagementWorkflow.ARTIFACT_TYPE_JIRA_TICKET, null, null);
		ArtifactService artService = injector.getInstance(ArtifactService.class);
		//artService.push(ticketArt);
		
		WorkflowInstance wfi = twfd.createInstance(wfID);
		List<AbstractWorkflowInstanceObject> awos = wfi.enableWorkflowTasksAndDecisionNodes();
		wfiService.push(wfi);
		DecisionNodeInstance dni = (DecisionNodeInstance) awos.get(0);
		dni.completedDataflowInvolvingActivationPropagation();
		List<TaskDefinition> tds = dni.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
       	tds.stream().
       		forEach(td -> {        			
       			WorkflowTask wt = wfi.instantiateTask(td);
       			wt.addInput(new ArtifactInput(ticketArt1, TestWPManagementWorkflow.INPUT_ROLE_WPTICKET));
       			wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
       			Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
       			dni.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches       			
       		});				
		
       	QACheckDocument qa = new QACheckDocument("QA1-"+wfID, wfi);
		int itemId = 2;
		RuleEngineBasedConstraint srsConstraint = new RuleEngineBasedConstraint("REBC2", qa, "CheckSWRequirementReleased", wfi, "Have all SRSs of the WP been released?");	
		qa.addConstraint(srsConstraint);	
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=1", "self", "", "html", "SRS 1"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=2", "self", "", "html", "SRS 2"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=3", "self", "", "html", "SRS 3"));
		artService.push(qa);
       	// add to WFTask
		Optional<WorkflowTask> wftOpt = wfi.getWorkflowTasksReadonly().stream()
				.map(wft -> {
					// there is only one here, so lets use this to add the QA document
					ArtifactOutput ao = new ArtifactOutput(qa, "QA_PROCESS_CONSTRAINTS_CHECK");
					wft.addOutput(ao);
					ResourceLink rl = TestWPManagementWorkflow.getLink("http://testjama.frequentis/sss=review1", "SSS Review Link");
				//	rl.setWorkflowInstance(wft.getWorkflow());
					wft.addOutput(new ArtifactOutput(rl, WPManagementWorkflow.OUTPUT_ROLE_SSSREVIEW));
					return wft;
				}).findFirst();
		wftOpt.ifPresent(wft -> {
			wfi.getDecisionNodeInstancesReadonly().stream()
			.filter(dni1 -> dni1.getState().equals(States.AVAILABLE))
			.findAny().ifPresent(dni2 -> {
				dni2.activateInBranch(dni2.getInBranchForWorkflowTask(wft));
				dni2.completedDataflowInvolvingActivationPropagation();
				List<TaskDefinition> tds2 = dni2.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
				tds2.stream().
				forEach(td2 -> {        			
					WorkflowTask wt = wfi.instantiateTask(td2);
					wt.addInput(new ArtifactInput(ticketArt1, TestWPManagementWorkflow.INPUT_ROLE_WPTICKET));
					wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
					Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
					dni2.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches
					QACheckDocument qa2 = new QACheckDocument("QA2-"+wfID, wfi);
					RuleEngineBasedConstraint srsConstraint2 = new RuleEngineBasedConstraint("REBC3", qa2, "CheckSWRequirementRelease", wfi, "Does every SRS have Release assigned?");
					qa2.addConstraint(srsConstraint2);	
					srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=6", "self", "", "html", "SRS 6"));
					srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=7", "self", "", "html", "SRS 7"));
					srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=8", "self", "", "html", "SRS 8"));
					srsConstraint2.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=4", "self", "", "html", "SRS 4"));
					srsConstraint2.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=5", "self", "", "html", "SRS 5"));
					ArtifactOutput ao = new ArtifactOutput(qa2, "QA_PROCESS_CONSTRAINTS_CHECK");
					wt.addOutput(ao);
					artService.push(qa2);
				});
			});
		});
		
       	wfiService.push(wfi);
		List<WorkflowTask> deletedWFTs = wftService.deleteDetachedPlaceHolders();
		deletedWFTs.size();
		
		
	}

}
