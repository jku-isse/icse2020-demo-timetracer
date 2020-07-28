package c4s.impactassessment.jiraapplication.usecase2.neo4j.constraints;

import java.util.Properties;

import org.kie.api.runtime.KieSession;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import c4s.impactassessment.app.IRulesFinishedFiringEventHandler;
import c4s.impactassessment.jiraapplication.neo4j.PersistUponRuleCompletion;
import c4s.impactassessment.jiraapplication.usecase2.MinimalRuleEvaluationSubsystemWithoutJiraOrJamaComponentConfig;
import c4s.impactassessment.neo4j.BasicServiceImpl.ArtifactServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.ArtifactTypeServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.DecisionNodeInstanceServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.TaskDefinitionServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowDefinitionServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowInstanceServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowTaskServiceImpl;
import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.neo4j.BasicServices.ArtifactTypeService;
import c4s.impactassessment.neo4j.BasicServices.DecisionNodeInstanceService;
import c4s.impactassessment.neo4j.BasicServices.TaskDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowTaskService;
import c4s.impactassessment.rulebase.PersistanceTriggeringKieSession;
import c4s.impactassessment.rulebase.PersistingKieSession;

public class MinimalPersistingRuleEvaluationSubsystemComponentConfig extends MinimalRuleEvaluationSubsystemWithoutJiraOrJamaComponentConfig{

	public MinimalPersistingRuleEvaluationSubsystemComponentConfig(Properties props) {
		super.props.putAll(props);
	}
	
	@Override
	protected void configure() {
		super.configure();
		configureNeo4JOGMBinding();
		configureRulesFinishedFiringEventHandlerBinding();
	}

	protected SessionFactory sessionFactory;
	@Override
	public void setupNeo4JSessionFactory() {
		// dont call super, or experience runtime exception
		String uri = props.getProperty("neo4jURI", "bolt://localhost");
		String user = props.getProperty("neo4jUser", "neo4j");
		String pw = props.getProperty("neo4jPassword", "neo4j");
		
		Configuration config = new Configuration.Builder()
				.uri(uri)
				.credentials(user, pw)
				.build();
		
		sessionFactory = new SessionFactory(config, "c4s.impactassessment.workflowmodel", "c4s.passiveprocessengine.workflowmodel");
	}

	@Override
	public void configureNeo4JOGMBinding() {
		if (sessionFactory == null) {
			setupNeo4JSessionFactory();
		}
		// dont call super, or experience runtime exception
		bind(SessionFactory.class).toInstance(sessionFactory);		
		bind(ArtifactService.class).to(ArtifactServiceImpl.class).asEagerSingleton();
		bind(ArtifactTypeService.class).to(ArtifactTypeServiceImpl.class).asEagerSingleton();
		bind(TaskDefinitionService.class).to(TaskDefinitionServiceImpl.class).asEagerSingleton();
		bind(WorkflowDefinitionService.class).to(WorkflowDefinitionServiceImpl.class).asEagerSingleton();
		bind(WorkflowInstanceService.class).to(WorkflowInstanceServiceImpl.class).asEagerSingleton();
		bind(WorkflowTaskService.class).to(WorkflowTaskServiceImpl.class).asEagerSingleton();
		bind(DecisionNodeInstanceService.class).to(DecisionNodeInstanceServiceImpl.class).asEagerSingleton();
	}

	@Override
	public void configureRulesFinishedFiringEventHandlerBinding() {
		bind(IRulesFinishedFiringEventHandler.class).to(PersistUponRuleCompletion.class).asEagerSingleton();
		bind(KieSession.class).annotatedWith(PersistingKieSession.class).to(PersistanceTriggeringKieSession.class).asEagerSingleton();
	}
	
	
}
