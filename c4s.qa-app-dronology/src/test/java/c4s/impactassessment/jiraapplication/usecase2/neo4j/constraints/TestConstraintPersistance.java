package c4s.impactassessment.jiraapplication.usecase2.neo4j.constraints;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.jiraapplication.usecase2.neo4j.Neo4JConnectorConfig;
import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;
import c4s.impactassessment.workflowmodel.WPManagementWorkflow;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

public class TestConstraintPersistance {

	
	static Injector injector;
	static WPManagementWorkflow twfd;
	static ArtifactService artService;
	private static  Properties props = new Properties();
	private static SessionFactory nsf;
	
	@BeforeClass
	public static void setUp() throws Exception {
		props.load(new FileInputStream("local-app.properties"));
		injector = Guice.createInjector(new Neo4JConnectorConfig(props));
		nsf = injector.getInstance(SessionFactory.class);
		Session session = nsf.openSession(); 
		//session.purgeDatabase();
		
		twfd = new WPManagementWorkflow();
		injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
		
		//twfd = new TestWPManagementWorkflow();
		//injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
		//WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
		//wfdService.push(twfd);
		artService = injector.getInstance(ArtifactService.class);
	}	
	
	@Test
	public void testStoreQACheckDocument() {		
		String wfid = "WP-TEST";
		WorkflowInstance wfi = twfd.createInstance(wfid);
		//artService.deleteArtifactsByWorkflowInstanceId(wfi).stream()
		//	.forEach(aa -> System.out.println("Deleted: "+ aa.getId()));
		removeArtifactsByWFId(wfid);
							
       	QACheckDocument qa = new QACheckDocument("QA1", wfi);				
		RuleEngineBasedConstraint srsConstraint = new RuleEngineBasedConstraint("REBC2", qa, "CheckSWRequirementReleased", wfi, "Have all SRSs of the WP been released?");	
		qa.addConstraint(srsConstraint);	
		srsConstraint.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=1", "self", "", "html", "SRS 1")));
		srsConstraint.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=2", "self", "", "html", "SRS 2")));
		srsConstraint.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=3", "self", "", "html", "SRS 3")));
		artService.createOrUpdate(qa);       							
		artService.createOrUpdate(srsConstraint);
		
	}

	private ResourceLink getOrCreate(ResourceLink localCopy) {		
//		AbstractArtifact aa = artService.find(localCopy.getId());
//		if (aa == null)
//			return localCopy;
//		else return (ResourceLink) aa;
		return localCopy;
			
	}
	
	private void removeArtifactsByWFId(String wfid) {
		String query = "MATCH (aa:AbstractArtifact) WHERE aa.wfi = $wfid DETACH DELETE aa";	
		HashMap<String,String> paraMap = new HashMap<String, String>();
		paraMap.put("wfid", wfid);
		Iterable<AbstractArtifact> iter = nsf.openSession().query(AbstractArtifact.class, query, paraMap);
		iter.forEach(aa -> System.out.println(aa.getId()));
	}

}
