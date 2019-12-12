package c4s.passiveprocessengine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.AbstractModule;

import c4s.impactassessment.neo4j.BasicServiceImpl.ArtifactServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.ArtifactTypeServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.DecisionNodeDefinitionServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.DecisionNodeInstanceServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.TaskDefinitionServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowDefinitionServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowInstanceServiceImpl;
import c4s.impactassessment.neo4j.BasicServiceImpl.WorkflowTaskServiceImpl;
import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.neo4j.BasicServices.ArtifactTypeService;
import c4s.impactassessment.neo4j.BasicServices.DecisionNodeDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.DecisionNodeInstanceService;
import c4s.impactassessment.neo4j.BasicServices.TaskDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowTaskService;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEvent;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;

public class Neo4JConnectorSetupConfig extends AbstractModule{

	protected SessionFactory sessionFactory;
	
	public Neo4JConnectorSetupConfig() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("local-app.properties"));
			String uri = props.getProperty("neo4jURI", "bolt://localhost");
			String user = props.getProperty("neo4jUser", "neo4j");
			String pw = props.getProperty("neo4jPassword", "neo4j");

			Configuration config = new Configuration.Builder()
					.uri(uri)
					.credentials(user, pw)
					.build();
			
			sessionFactory = new SessionFactory(config, "c4s.impactassessment.workflowmodel", "c4s.passiveprocessengine.workflowmodel");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void configure() {
		bind(SessionFactory.class).toInstance(sessionFactory);
		
		bind(ArtifactService.class).to(ArtifactServiceImpl.class).asEagerSingleton();
		bind(ArtifactTypeService.class).to(ArtifactTypeServiceImpl.class).asEagerSingleton();
		bind(TaskDefinitionService.class).to(TaskDefinitionServiceImpl.class).asEagerSingleton();
		bind(WorkflowDefinitionService.class).to(WorkflowDefinitionServiceImpl.class).asEagerSingleton();
		bind(WorkflowInstanceService.class).to(WorkflowInstanceServiceImpl.class).asEagerSingleton();
		bind(WorkflowTaskService.class).to(WorkflowTaskServiceImpl.class).asEagerSingleton();
		bind(DecisionNodeInstanceService.class).to(DecisionNodeInstanceServiceImpl.class).asEagerSingleton();
		bind(DecisionNodeDefinitionService.class).to(DecisionNodeDefinitionServiceImpl.class).asEagerSingleton();
		
		bind(TaskStateTransitionEventPublisher.class).toInstance(new TaskStateTransitionEventPublisher(){

			@Override
			public void publishEvent(TaskStateTransitionEvent event) {
				System.out.println("StateTransitionEvent: "+event);
			}});
	}

}
