package c4s.impactassessment.jiraapplication.usecase2.constraints;

import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.jiraapplication.usecase2.neo4j.Neo4JConnectorConfig;
import c4s.impactassessment.neo4j.BasicServices;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

public class TestConstraintPersistance {

	// no kiesession necessary, just persistance, just test storing of constraint doc in neo4j db
	Injector inj;
	
	@Before
	public void setUp() throws Exception {
		inj = Guice.createInjector(new Neo4JConnectorConfig());
		
	}

	@Test
	public void testConstraintPersistance() {
		QACheckDocument qa = new QACheckDocument("QA1", null);
		// WP in Jama: https://jamatest.frequentis.com/perspective.req#/items/6984365?projectId=133
		int itemId = 6984365;
		
		RuleEngineBasedConstraint srsConstraint2 = new RuleEngineBasedConstraint("REBC3", qa, "CheckSWRequirementRelease", new WorkflowInstance("CVCSXO-13015", null, null), "Does every SRS have Release assigned?");
		qa.addConstraint(srsConstraint2);	
		srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=6", "self", "", "html", "SRS 6"));
		srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=7", "self", "", "html", "SRS 7"));
		srsConstraint2.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=8", "self", "", "html", "SRS 8"));
		srsConstraint2.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=4", "self", "", "html", "SRS 4"));
		srsConstraint2.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=5", "self", "", "html", "SRS 5"));
		BasicServices.ArtifactService artS = inj.getInstance(BasicServices.ArtifactService.class);
		boolean result = qa.areAllConstraintsFulfilled();
		System.out.println(qa);
		assertFalse(result);				
		artS.push(qa);
	}
	
}
