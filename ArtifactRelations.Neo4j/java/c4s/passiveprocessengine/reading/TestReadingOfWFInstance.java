package c4s.passiveprocessengine.reading;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowTaskService;
import c4s.impactassessment.workflowmodel.AbstractWorkflowInstanceObject;
import c4s.impactassessment.workflowmodel.ArtifactWrapper;
import c4s.impactassessment.workflowmodel.DecisionNodeInstance;
import c4s.impactassessment.workflowmodel.TaskDefinition;
import c4s.impactassessment.workflowmodel.TaskLifecycle.Events;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.WorkflowTask;
import c4s.impactassessment.workflowmodel.WorkflowTask.ArtifactInput;
import c4s.passiveprocessengine.Neo4JConnectorSetupConfig;
import c4s.passiveprocessengine.workflowmodel.TestWPManagementWorkflow;

public class TestReadingOfWFInstance {

	Injector injector;
	String wfID = "WF1";
	WorkflowInstance wfi;
	
	@Before
	public void setUp() throws Exception {
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
		SessionFactory nsf = injector.getInstance(SessionFactory.class);
		Session session = nsf.openSession(); 
		session.purgeDatabase();
		wfi	= storeTestingWPProcessInstance();
		// to obtain a new session for accessing the database
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
	}
	
	private WorkflowInstance storeTestingWPProcessInstance() {
		WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowTaskService wftService = injector.getInstance(WorkflowTaskService.class);
		
		ArtifactWrapper ticketArt = new ArtifactWrapper("TICKET1", TestWPManagementWorkflow.ARTIFACT_TYPE_JIRA_TICKET, null, null);
		ArtifactService artService = injector.getInstance(ArtifactService.class);
		artService.push(ticketArt);
		TestWPManagementWorkflow twfd = new TestWPManagementWorkflow();
		injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
		wfdService.push(twfd);
		WorkflowInstance wfi = twfd.createInstance(wfID);
		List<AbstractWorkflowInstanceObject> awos = wfi.enableWorkflowTasksAndDecisionNodes();
		wfiService.push(wfi);
		DecisionNodeInstance dni = (DecisionNodeInstance) awos.get(0);
		dni.completedDataflowInvolvingActivationPropagation();
		List<TaskDefinition> tds = dni.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
       	tds.stream().
       		forEach(td -> {        			
       			WorkflowTask wt = wfi.instantiateTask(td);
       			wt.addInput(new ArtifactInput(ticketArt, TestWPManagementWorkflow.INPUT_ROLE_WPTICKET));
       			wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
       			Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
       			dni.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches       			
       		});				
		wfiService.push(wfi);
		List<WorkflowTask> deletedWFTs = wftService.deleteDetachedPlaceHolders();
		return wfi;
	}
	
	@Test
	public void testReadInstance(){
		//WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		//DefaultWorkflowDefinition dwfd = wfdService.find(TestWPManagementWorkflow.WORKPACKAGE_CHANGE_MANAGEMENT_WORKFLOW_TYPE);
		
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowInstance wfiFromDB = wfiService.find(wfID);
		wfiFromDB.getId();
		Set<WorkflowTask> tasks = wfiFromDB.getWorkflowTasksReadonly();
		System.out.println(tasks.size());
	}
}
