package c4s.rest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import c4s.nodes.*;
import c4s.repositories.*;
import c4s.repositories.WorkflowTaskRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
public class TestRestEndpoints {

	@Autowired
	private DecisionNodeDefinitionRepository dndRepo;
	@Autowired
	private DecisionNodeInstanceRepository dniRepo;
	@Autowired
	private QACheckDocumentRepository qaRepo;
	@Autowired
	private ResourceLinkRepository rsRepo;
	@Autowired
	private QAConstraintRepository reqRepo;
	@Autowired
	private TaskDefinitionRepository tdRepo;
	@Autowired
	private WorkflowInstanceRepository wfiRepo;
	@Autowired
	private WorkflowTaskRepository wftRepo;

	@Before
	public void setUp() {
		dndRepo.deleteAll();
		DecisionNodeDefinition dnd1 = new DecisionNodeDefinition();
		dnd1.setId("1");
		dndRepo.save(dnd1);
		DecisionNodeDefinition dnd2 = new DecisionNodeDefinition();
		dnd2.setId("2");
		dndRepo.save(dnd2);
		
		dniRepo.deleteAll();
		DecisionNodeInstance dni1 = new DecisionNodeInstance();
		dni1.setId("1");
		dniRepo.save(dni1);
		DecisionNodeInstance dni2 = new DecisionNodeInstance();
		dni2.setId("2");
		dniRepo.save(dni2);
		
//		qaRepo.deleteAll();
//		QACheckDocument qa1 = new QACheckDocument();
//		qa1.setId("1");
//		qaRepo.save(qa1);
//		QACheckDocument qa2 = new QACheckDocument();
//		qa2.setId("2");
//		qaRepo.save(qa2);
		
		rsRepo.deleteAll();
		ResourceLink rs1 = new ResourceLink();
		rs1.setHref("1");
		rsRepo.save(rs1);
		ResourceLink rs2 = new ResourceLink();
		rs2.setHref("2");
		rsRepo.save(rs2);
		
//		reqRepo.deleteAll();
//		QAConstraint req1 = new QAConstraint();
//		req1.setId("1");
//		reqRepo.save(req1);
//		QAConstraint req2 = new QAConstraint();
//		req2.setId("2");
//		reqRepo.save(req2);
		
		tdRepo.deleteAll();
		TaskDefinition td1 = new TaskDefinition();
		td1.setId("1");
		tdRepo.save(td1);
		TaskDefinition td2 = new TaskDefinition();
		td2.setId("2");
		tdRepo.save(td2);
		
//		wfiRepo.deleteAll();
//		WorkflowInstance wfi1 = new WorkflowInstance();
//		wfi1.setId("1");
//		wfiRepo.save(wfi1);
//		WorkflowInstance wfi2 = new WorkflowInstance();
//		wfi2.setId("2");
//		wfiRepo.save(wfi2);
		
		wftRepo.deleteAll();
		WorkflowTask wft1 = new WorkflowTask();
		wft1.setId("1");
		wftRepo.save(wft1);
		WorkflowTask wft2 = new WorkflowTask();
		wft2.setId("2");
		wftRepo.save(wft2);
	}
   
   @Test
   public void testFindAllDecisionNodeDefinition() {
//	   for (DecisionNodeDefinition dnd : dndRepo.findById("1")) {
//		   System.out.println("##########################################");
//		   System.out.println("dnd: "+dnd.getId());
//		   System.out.println("##########################################");
//	   }
       assertEquals(2, dndRepo.count());
   }
   
   @Test
   public void testFindAllDecisionNodeInstance() {
       assertEquals(2, dniRepo.count());
   }
   
   @Test
   public void testFindAllQACheckDocument() {
       assertEquals(2, qaRepo.count());
   }

   @Test
   public void testFindAllResourceLink() {
       assertEquals(2, rsRepo.count());
   }
   
   @Test
   public void testFindAllSWRequirementsReleased() {
       assertEquals(2, reqRepo.count());
   }
   
   @Test
   public void testFindAllTaskDefinition() {
       assertEquals(2, tdRepo.count());
   }
   
//   @Test
//   public void testFindAllWorkflowInstance() {
//       assertEquals(2, wfiRepo.count());
//   }
   
   @Test
   public void testFindAllWorkflowTask() {
       assertEquals(2, wftRepo.count());
   }
}