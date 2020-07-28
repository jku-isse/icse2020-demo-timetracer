package c4s.impactassessment.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.lightcouch.CouchDbClient;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.google.inject.AbstractModule;

import application.connector.TimeTravelingConnector;
import c4s.amqp.AMQPConsumer;
//import c4s.impactassessment.changecalculator.ChangeCalculator;
import c4s.amqp.IAMQPConsumer;
import c4s.amqp.IAddMessageHandler;
import c4s.amqp.ICheckMessageHandler;
import c4s.amqp.IDeleteMessageHandler;
import c4s.impactassessment.amqp.AddMessageHandler;
import c4s.impactassessment.amqp.CheckMessageHandler;
import c4s.impactassessment.amqp.DeleteMessageHandler;
import c4s.impactassessment.jiraapplication.neo4j.PersistUponRuleCompletion;
import c4s.impactassessment.monitoring.DomainObjectTracingInstrumentation;
import c4s.impactassessment.monitoring.DomainObjectUpdateTracingInstrumentation;
import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
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
import c4s.impactassessment.rulebase.KieSessionDomainWrapper;
import c4s.impactassessment.rulebase.OriginalKieSession;
import c4s.impactassessment.rulebase.PersistanceTriggeringKieSession;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEvent;
import c4s.impactassessment.workflowmodel.TaskStateTransitionEventPublisher;
import core.connector.IConnector;
import core.connector.ITimeTravelingConnector;


public class ComponentConfig extends AbstractModule implements IComponentConfig{

	protected Logger log = LogManager.getLogger(ComponentConfig.class);
	
	protected Properties props = new Properties();
	protected Set<String> argsList;
	public static Set<String> cliOptions = new HashSet<String>();

	
	public ComponentConfig(String args[]) {
//		System.out.println("Using log config: "+ ((org.apache.logging.log4j.core.LoggerContext) LogManager.getContext()).getConfiguration().toString());
		try {
			cliOptions.add(cliArgsDontRememberMonitoredItems);
			cliOptions.add(cliArgsRunOffline);
			System.out.println("Command Line Options available: "+cliOptions.toString());			
			this.argsList = new HashSet<String>(Arrays.asList(args));
			props.load(new FileInputStream("local-app.properties"));
			init();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Properties getConfiguration() {
		return props;
	}

	public static final String cliArgsDontRememberMonitoredItems = "-dontRememberMonitoredItems";
	protected boolean doRememberMonitoredItems = true;
	public static final String cliArgsRunOffline = "-runOffline";
	protected boolean doRunOffline = false;
	
	protected void init() {
		
		//Warn if an unsupported cli argument is used; just a warning, no consequences for the rest of the execution
		argsList.stream()
			.filter(arg -> !cliOptions.contains(arg))
			.forEach(arg -> {   System.out.println("Warning: unknown argument: "+arg); 
								log.warn("Unknown argument: "+arg);
			});
		argsList.stream()
		.filter(arg -> cliOptions.contains(arg))
		.forEach(arg -> { System.out.println("Applying argument: "+arg); 
						log.info("Applying argument: "+arg);
		});

		if(argsList.stream().anyMatch(arg -> arg.equalsIgnoreCase(cliArgsDontRememberMonitoredItems))) {
			doRememberMonitoredItems = false;
		}
		if(argsList.stream().anyMatch(arg -> arg.equalsIgnoreCase(cliArgsRunOffline))) {
			doRunOffline = true;
		}
		
		// COMPARE TO CONFIGURE ORDER IN NEXT METHOD
		// no setup of KieSession here
		// no setup of TaskStateTransitionEventPublisher here
		setupNeo4JSessionFactory();
		// no JiraEventToKnowledgebasePusher config here
		// no RuleEvaluationSubsystem config here
		setupIConnector();
		setupITimeTravelingConnector();
		setupIAMQPConsumer();
	}

	@Override
	protected void configure() {
		configureAnalyticsInstrumentation();
		configureKSessionBinding();
		configureTaskStateTransitionEventPublisherBinding();
		configureNeo4JOGMBinding();
		configureRulesFinishedFiringEventHandlerBinding();
		configureJiraEventToKnowledgebasePusherBinding();
		configureRuleEvaluationSubsystemBinding();			
		configureIConnectorBinding();
		configureITimeTravelingConnectorBinding();
		configureIAMQPConsumerBinding();
	}


	protected JiraRestClient jrc;
	protected CouchDbClient dbClient;
	



	@Override
	public void configureAnalyticsInstrumentation() {
		DomainObjectUpdateTracingInstrumentation instrumentation = new DomainObjectUpdateTracingInstrumentation();
		bind(RequestTracingInstrumentation.class).asEagerSingleton();
		bind(DomainObjectTracingInstrumentation.class).asEagerSingleton();
	}
	


	protected KieSession kSession;
	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#getKSession()
	 */
	@Override
	public void configureKSessionBinding() {
		if (kSession == null) {
			createKsessionFromFiles();
		}
		bind(KieSession.class).annotatedWith(OriginalKieSession.class).toInstance(kSession);
	}
	
	private void createKsessionFromResources() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		kSession = kContainer.newKieSession("ksession-rules");
	}
	
	protected String relPath = "";
	protected String relPathPrefix = "./c4s.qa-app-dronology/src/main/resources/"; // Mandatory to use src/main/resources/ as root for drl files,
	// see: line 79 of https://github.com/kiegroup/drools/blob/master/drools-compiler/src/main/java/org/drools/compiler/kie/builder/impl/KieBuilderImpl.java
	// http://lists.jboss.org/pipermail/rules-users/2013-December/034306.html
	
	private void createKsessionFromFiles() {
		String ruleFolder = props.getProperty("rulefolder", "rules");
		if (ruleFolder.startsWith(relPathPrefix)) {
			relPath = ruleFolder;
		} else {
			if (ruleFolder.startsWith("\\") || ruleFolder.startsWith("/"))
				ruleFolder = ruleFolder.substring(1);
			relPath = relPathPrefix + ruleFolder;
		}
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		try (Stream<Path> paths = Files.walk(Paths.get(relPath))) {
		    paths
		        .filter(Files::isRegularFile)
		        .filter(path -> path.getFileName().toString().endsWith(".drl"))
		        .forEach(path -> loadRuleFromPath(path, kfs, ks));
		} catch (IOException e) {
			throw new RuntimeException("Error loading file paths \n"+e.getMessage());
		}
		KieBuilder kb = ks.newKieBuilder(kfs);
		kb.buildAll(); 
		if (kb.getResults().hasMessages(Level.ERROR)) {
		    throw new RuntimeException("Rule Base Build Errors:\n" + kb.getResults().toString());
		}

		KieContainer kContainer = ks.newKieContainer(kb.getKieModule().getReleaseId());
		kSession = kContainer.newKieSession();
	}
	
	private void loadRuleFromPath(Path filePath, KieFileSystem kfs, KieServices ks) {
		
		//File file = filePath.toFile();
		try {
			String content = new String(Files.readAllBytes(filePath));
			String filename = relPath.substring(1)+"/"+filePath.getFileName(); //filePath.getFileName().toString()
			log.info("Loading rule file: "+filename);
			kfs.write(filename, ks.getResources().newReaderResource(new StringReader(content))
					.setResourceType(ResourceType.DRL));
		} catch (IOException e) {
			throw new RuntimeException("Rule File Loading error: \n"+e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#getTaskStateTransitionEventPublisher()
	 */
	@Override
	public void configureTaskStateTransitionEventPublisherBinding() {
		bind(TaskStateTransitionEventPublisher.class).toInstance(new TaskStateTransitionEventPublisher(){
			@Override
			public void publishEvent(TaskStateTransitionEvent event) {
				// No Op
			}});
	}

	protected SessionFactory sessionFactory;
	@Override
	public void setupNeo4JSessionFactory() {
		// dont call super, or experience runtime exception
		String uri = props.getProperty("neo4jTimeTravelingURI", "bolt://localhost");
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

	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#configureRulesFinishedFiringEventHandlerBinding()
	 */
	@Override
	public void configureRulesFinishedFiringEventHandlerBinding() {
		bind(IRulesFinishedFiringEventHandler.class).to(PersistUponRuleCompletion.class).asEagerSingleton();	
		//bind(KieSession.class).annotatedWith(PersistingKieSession.class).to(PersistanceTriggeringKieSession.class).asEagerSingleton();
		bind(KieSessionDomainWrapper.class).to(PersistanceTriggeringKieSession.class).asEagerSingleton();
	}



	@Override
	public void configureJiraEventToKnowledgebasePusherBinding() {
//		bind(JiraEventToKnowledgebasePusher.class).asEagerSingleton();
	}

	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#configureRuleEvaluationSubsystemBinding()
	 */
	@Override
	public void configureRuleEvaluationSubsystemBinding() {
		bind(IRuleEvaluationSubsystem.class).to(RuleEvaluationSubSystem.class).asEagerSingleton();
	}
	
	
	ITimeTravelingConnector connector;
	
	@Override
	public void setupIConnector() {
		// TODO Auto-generated method stub
		try {
			connector = new TimeTravelingConnector();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void configureIConnectorBinding() {
		// TODO Auto-generated method stub
		bind(ITimeTravelingConnector.class).toInstance(connector);
		bind(IConnector.class).toInstance(connector);
	}
	
	@Override
	public void setupITimeTravelingConnector() {
	
	}
	
	@Override
	public void configureITimeTravelingConnectorBinding() {

	}
	
	IAMQPConsumer amqp;
	//ICheckMessageHandler cmh;
	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#setupIAMQPConsumer()
	 */
	@Override
	public void setupIAMQPConsumer() {
		amqp = new AMQPConsumer(props);
	}
	
	/* (non-Javadoc)
	 * @see c4s.impactassessment.app.IComponentConfig#configureIAMQPConsumerBinding()
	 */
	@Override
	public void configureIAMQPConsumerBinding() {
		bind(IAMQPConsumer.class).toInstance(amqp);
		bind(ICheckMessageHandler.class).to(CheckMessageHandler.class).asEagerSingleton();
		bind(IAddMessageHandler.class).to(AddMessageHandler.class).asEagerSingleton();
		bind(IDeleteMessageHandler.class).to(DeleteMessageHandler.class).asEagerSingleton();
	}


}
