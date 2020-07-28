package c4s.impactassessment.jiraapplication.usecase2.neo4j.constraints;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.amqp.AMQPConsumer;
import c4s.amqp.IAddMessageHandler;
import c4s.amqp.ICheckMessageHandler;
import c4s.amqp.IDeleteMessageHandler;
import c4s.components.AddMessage;
import c4s.components.CheckMessage;
import c4s.components.DeleteMessage;
import c4s.components.ProcessingState;
import c4s.impactassessment.jiraapplication.usecase2.TestWPManagementWorkflow;
import c4s.impactassessment.jiraapplication.usecase2.neo4j.Neo4JConnectorConfig;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;


public class TestAMQPWithNeo4jWithoutJama {

	
	private Injector injector;
	private TestWPManagementWorkflow twfd;
	private AMQPConsumer amqp;
	private CountDownLatch countdown;
	private WorkflowInstanceService wfiService;
	private int i;

	
	@Before
	public void setUp() throws Exception {
		countdown = new CountDownLatch(5);
		
		injector = Guice.createInjector(new Neo4JConnectorConfig());
		SessionFactory nsf = injector.getInstance(SessionFactory.class);
		nsf.openSession();
		
		twfd = new TestWPManagementWorkflow();
		injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
		
		wfiService = injector.getInstance(WorkflowInstanceService.class);
		
		
		Properties props = new Properties();
		props.put("amqpHost", "192.168.91.128");
		
		amqp = new AMQPConsumer(props);
		i = 0;
		amqp.registerIAddMessageHandler(new IAddMessageHandler() {
			@Override
			public ProcessingState preprocessAddMessage(AddMessage am) {
				System.out.println("error message is set to 200!!");
				System.out.println(am);
				
//				WorkflowInstance wfi = twfd.createInstance("testWFI"+i, null);
//				i++;

//				wfiService.push(wfi);
				countdown.countDown();
				
				ProcessingState ps = new ProcessingState();
				ps.setErrorCode(200);
				ps.setStatusMsg("Preprocessing successful!");
				ps.setProcessState(new Object());//dummy
				ps.setCorrelationId("ididididid");
				return ps;
			}

			@Override
			public void continueProcessingAddMessage(ProcessingState preprocessedState) {
				System.out.println("CONTINUE of AMH");
				
			}
		});
		amqp.registerICheckMessageHandler(new ICheckMessageHandler() {
			@Override
			public ProcessingState preprocessCheckMessage(CheckMessage cm) {
				System.out.println("error message is set to 400!!");
				System.out.println(cm);
				
//				WorkflowInstance wfi = twfd.createInstance("testWFI"+i, null);
//				i++;

//				wfiService.push(wfi);
				countdown.countDown();
				
				ProcessingState ps = new ProcessingState();
				ps.setErrorCode(400);
				ps.setStatusMsg("Preprocessing failed!");
				ps.setProcessState(new Object());//dummy
				ps.setCorrelationId("ididididid");
				return ps;
			}

			@Override
			public void continueProcessingCheckMessage(ProcessingState preprocessedState) {
				System.out.println("CONTINUE of CMH");//won't show up because error code is >400
			}
		});
		
		amqp.registerIDeleteMessageHandler(new IDeleteMessageHandler() {
			@Override
			public ProcessingState preprocessDeleteMessage(DeleteMessage dm) {
				System.out.println("error message is set to 200!!");
				System.out.println(dm);
				
//				WorkflowInstance wfi = twfd.createInstance("testWFI"+i, null);
//				i++;

//				wfiService.push(wfi);
				countdown.countDown();
				
				ProcessingState ps = new ProcessingState();
				ps.setErrorCode(200);
				ps.setStatusMsg("Preprocessing successful!");
				ps.setProcessState(new Object());//dummy
				ps.setCorrelationId("ididididid");
				return ps;
			}

			@Override
			public void continueProcessingDeleteMessage(ProcessingState preprocessedState) {
				System.out.println("CONTINUE of DMH");
			}
		});
	
	}
	
	@Test
	public void testAMQPCallFromWebserviceAndWriteIntoNeo4j() {
		try {
			countdown.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		amqp.shutdown();
//		for (int j = 0; j < 5; j++) {
//			if (wfiService.find("testWFI"+j) != null)
//				wfiService.delete("testWFI"+j);
//		}
	}

}
