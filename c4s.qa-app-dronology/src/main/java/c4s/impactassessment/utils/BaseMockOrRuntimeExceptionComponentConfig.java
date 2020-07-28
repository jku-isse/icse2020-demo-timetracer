package c4s.impactassessment.utils;

import java.util.Map;
import java.util.Properties;
//import java.util.concurrent.BlockingQueue;
//import java.util.concurrent.LinkedBlockingQueue;

//import javax.swing.event.ChangeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.KieSession;

import com.google.inject.AbstractModule;
//import com.google.inject.Provides;
import c4s.impactassessment.app.IComponentConfig;
import c4s.impactassessment.app.IRulesFinishedFiringEventHandler;
import c4s.impactassessment.monitoring.DomainObjectTracingInstrumentation;
import c4s.impactassessment.monitoring.DomainObjectUpdateTracingInstrumentation;
import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEvent;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;

public abstract class BaseMockOrRuntimeExceptionComponentConfig extends AbstractModule implements IComponentConfig  {

	public static String notConfigured = "NOT CONFIGURED";
	protected Properties props = new Properties();
	
	

	@Override
	public void setupNeo4JSessionFactory() {
		throw new RuntimeException(notConfigured);
	}

//	@Override
//	public void setupJamaConnector() {
//		throw new RuntimeException(notConfigured);
//	}
	
	@Override
	public void setupIConnector() {
		throw new RuntimeException(notConfigured);
	}
	
	@Override
	public void setupITimeTravelingConnector() {
		throw new RuntimeException(notConfigured);		
	}
	
	@Override
	public void setupIAMQPConsumer() {
		throw new RuntimeException(notConfigured);
	}

	@Override
	public void configureAnalyticsInstrumentation() {
		DomainObjectUpdateTracingInstrumentation instrumentation = new DomainObjectUpdateTracingInstrumentation();
		bind(RequestTracingInstrumentation.class).asEagerSingleton();
//		bind(JamaUpdateTracingInstrumentation.class).toInstance(instrumentation);
		bind(DomainObjectTracingInstrumentation.class).asEagerSingleton();
	}
	
	@Override
	public void configureRulesFinishedFiringEventHandlerBinding() {
		bind(IRulesFinishedFiringEventHandler.class).toInstance(new IRulesFinishedFiringEventHandler() {
			private Logger log = LogManager.getLogger("IRulesFinishedFiringEventHandler");
			@Override
			public void handleRulesFinishedFiringEvent(KieSession kSession) {
				log.debug("Ignoring call");
			}
		});
	}

	@Override
	public void configureRuleEvaluationSubsystemBinding() {
		throw new RuntimeException(notConfigured);
	}

//	@Override
//	public void configureJamaConnectorBinding() {
//		throw new RuntimeException(notConfigured);
//	}

	@Override
	public void configureJiraEventToKnowledgebasePusherBinding() {
		throw new RuntimeException(notConfigured);
	}

	@Override
	public void configureNeo4JOGMBinding() {
		throw new RuntimeException(notConfigured);
	}

//	protected BlockingQueue<ChangeEvent> queue;
//	@Override
//	@Provides
//	public BlockingQueue<ChangeEvent> getQueue() {
//		//Queue to pipe ChangeEvents from jiraConnector to RuleEngine
//		if (queue == null)
//			queue = new LinkedBlockingQueue<ChangeEvent>(1000);
//		return queue;
//	}

	protected TaskStateTransitionEventPublisher tstep;
	@Override
	public void configureTaskStateTransitionEventPublisherBinding() {
		bind(TaskStateTransitionEventPublisher.class).toInstance(new TaskStateTransitionEventPublisher(){
			private Logger log = LogManager.getLogger("TaskStateTransitionEventPublisher");
			@Override
			public void publishEvent(TaskStateTransitionEvent event) {
				log.debug("Ignoring StateTransitionEvent: "+event);
			}});
	}

	@Override
	public void configureKSessionBinding() {
		throw new RuntimeException(notConfigured);
	}
	
	@Override
	public void configureIConnectorBinding() {
		throw new RuntimeException(notConfigured);
	}
	@Override
	public void configureITimeTravelingConnectorBinding() {
		throw new RuntimeException(notConfigured);		
	}

	@Override
	public void configureIAMQPConsumerBinding() {
		throw new RuntimeException(notConfigured);
	}

	@Override
	protected void configure() {
		configureAnalyticsInstrumentation();
		configureRulesFinishedFiringEventHandlerBinding();
		configureTaskStateTransitionEventPublisherBinding();
		
		
	}
	
}
