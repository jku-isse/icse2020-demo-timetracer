package c4s.passiveprocessengine.reading;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.workflowmodel.DefaultWorkflowDefinition;
import c4s.passiveprocessengine.Neo4JConnectorSetupConfig;
import c4s.passiveprocessengine.workflowmodel.TestWPManagementWorkflow;

public class TestReadingOfProcDefinition {

	Injector injector;
	String tdID1 = "TESTTASKDEFINITIONID_1";
	String dndID1 = "TEST_DECISION_NODE_DEFINITION_ID_1";
	String tdID2 = "TESTTASKDEFINITIONID_2";
	DefaultWorkflowDefinition twfd;
	
	@Before
	public void setUp() throws Exception {
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
		SessionFactory nsf = injector.getInstance(SessionFactory.class);
		Session session = nsf.openSession(); 
		session.purgeDatabase();
		twfd = storeTestingWPProcess();
		// to obtain a new session for accessing the database
		injector = Guice.createInjector(new Neo4JConnectorSetupConfig());
	}
	
	private DefaultWorkflowDefinition storeTestingWPProcess() {
		WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		TestWPManagementWorkflow twfd = new TestWPManagementWorkflow();
		injector.injectMembers(twfd);
		wfdService.push(twfd);
		return twfd;
	}
	
	@Test
	public void testDefinitionLoading() {
		WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		DefaultWorkflowDefinition twfd = wfdService.find(TestWPManagementWorkflow.WORKPACKAGE_CHANGE_MANAGEMENT_WORKFLOW_TYPE);
		twfd.getId();
	}
}
