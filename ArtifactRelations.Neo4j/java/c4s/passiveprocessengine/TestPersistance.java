package c4s.passiveprocessengine;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.workflowmodel.ArtifactType;
import c4s.impactassessment.workflowmodel.DecisionNodeDefinition;
import c4s.impactassessment.workflowmodel.DefaultBranchDefinition;
import c4s.impactassessment.workflowmodel.DefaultWorkflowDefinition;
import c4s.impactassessment.workflowmodel.TaskDefinition;
import c4s.passiveprocessengine.workflowmodel.TestWPManagementWorkflow;

public class TestPersistance {

	Injector injector;
	String tdID1 = "TESTTASKDEFINITIONID_1";
	String dndID1 = "TEST_DECISION_NODE_DEFINITION_ID_1";
	String tdID2 = "TESTTASKDEFINITIONID_2";
	
	@Before
	public void setUp() throws Exception {
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
		
		// inject Neo4JSessionFactory
		// store example task defintion in graph
	}

	@Test
	public void testStoringTaskDefinition() {
		WorkflowDefinitionService service = injector.getInstance(WorkflowDefinitionService.class);
		DefaultWorkflowDefinition twfd = new DefaultWorkflowDefinition("TESTDEFINITON");
		TaskDefinition td = new TaskDefinition(tdID1, twfd);
		td.getExpectedInput().put("INPUT1", new ArtifactType("ARTTYPE1"));
		td.getExpectedOutput().put("OUTPUT1", new ArtifactType("ARTTYPE1"));
		td.getExpectedOutput().put("OUTPUT2", new ArtifactType("ARTTYPE2"));
		twfd.getWorkflowTaskDefinitions().add(td);
		service.push(twfd);
	}
	
	@Test
	public void testStoringWorkflowDefinition() {
		WorkflowDefinitionService service = injector.getInstance(WorkflowDefinitionService.class);
		DefaultWorkflowDefinition twfd = new DefaultWorkflowDefinition("TESTDEFINITON");
		TaskDefinition td1 = new TaskDefinition(tdID1, twfd);
		td1.getExpectedInput().put("INPUT1", new ArtifactType("ARTTYPE1"));
		td1.getExpectedOutput().put("OUTPUT1", new ArtifactType("ARTTYPE1"));
		td1.getExpectedOutput().put("OUTPUT2", new ArtifactType("ARTTYPE2"));
		
		TaskDefinition td2 = new TaskDefinition(tdID2, twfd);
		td2.getExpectedOutput().put("OUTPUT1", new ArtifactType("ARTTYPE1"));
		td2.getExpectedInput().put("INPUT1", new ArtifactType("ARTTYPE1"));
		td2.getExpectedInput().put("INPUT2", new ArtifactType("ARTTYPE2"));
		
		DecisionNodeDefinition dnd = new DecisionNodeDefinition(dndID1, twfd, false, true, true);
		dnd.addInBranchDefinition(new DefaultBranchDefinition("InBranch1", td1, false, true, dnd));
		dnd.addOutBranchDefinition(new DefaultBranchDefinition("OutBranch1", td2, true, true, dnd));
		
		
		twfd.getWorkflowTaskDefinitions().add(td1);
		twfd.getWorkflowTaskDefinitions().add(td2);
		twfd.getDecisionNodeDefinitions().add(dnd);
		service.push(twfd);
	}
	
	@Test
	public void testStoringWPProcess() {
		WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		TestWPManagementWorkflow twfd = new TestWPManagementWorkflow();
		twfd.initWorkflowSpecification();
		wfdService.push(twfd);
	}
}
