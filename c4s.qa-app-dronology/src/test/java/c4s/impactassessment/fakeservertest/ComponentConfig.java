/*package c4s.impactassessment.fakeservertest;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.lightcouch.CouchDbProperties;

import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;

import c4s.impactassessment.app.FakeCommentConnector;
import c4s.impactassessment.app.ICommentConnector;
import c4s.impactassessment.app.INotificationDispatcher;
import c4s.impactassessment.app.NotificationSubsystemWiring;
import c4s.impactassessment.app.RuleEvaluationSubsystemWiring;
import c4s.impactassessment.connectors.excelrolemapper.ExcelBasedRoleMapper;
import c4s.impactassessment.connectors.excelrolemapper.ExcelRoleMapperFactory;
import c4s.impactassessment.connectors.excelrolemapper.IRoleMapper;
import c4s.impactassessment.jiraapplication.JiraRules;
import c4s.impactassessment.jiraconnector.JiraConnector;
import c4s.impactassessment.notification.DefaultTemplateProvider;
import c4s.impactassessment.notification.INotificationLogger;
import c4s.impactassessment.notification.INotificationProcessor;
import c4s.impactassessment.notification.NotificationLogger;
import c4s.impactassessment.notification.NotificationProcessor;
import c4s.impactassessment.ruleenginelogger.core.RuleEngineLogger;
import c4s.impactassessment.utils.ChangeEvent;

public class ComponentConfig extends AbstractModule{

	private Properties props = new Properties();
	
	public ComponentConfig(String args[]) {
		try {
			props.load(new FileInputStream("local-app.properties"));
			init(props);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	protected Properties getConfiguration() {
		return props;
	}
	
	IRoleMapper rm;	
	NotificationProcessor np;	
	INotificationLogger nl;
	ConcurrentLinkedQueue<ChangeEvent> queue;
	KieSession kSession;
	
	RuleEngineLogger rel;
	JiraConnector jc;
	//JiraRules jr;
	ICommentConnector cc;
	
	private void init(Properties props) throws EncryptedDocumentException, InvalidFormatException, IOException, URISyntaxException {
		this.props = props;
		rm = ExcelRoleMapperFactory.getRoleMapperForFileDirectory("./src/test/resources/RoleMapping.xlsx");
		
		CouchDbProperties properties = new CouchDbProperties()
				  .setDbName("testimpactassessmentnotification")
				  .setCreateDbIfNotExist(true)
				  .setProtocol("http")
				  .setHost(props.getProperty("notificationCouchDBip", "127.0.0.1"))
				  .setPort(Integer.parseInt(props.getProperty("notificationCouchDBport", "5984")))
				  .setUsername(props.getProperty("notificationCouchDBuser"))
				  .setPassword(props.getProperty("notificationCouchDBpassword"))
				  .setMaxConnections(100)
				  .setConnectionTimeout(0);
		nl = new NotificationLogger(properties);
		np = new NotificationProcessor();				
		np.init(props.getProperty("notificationTemplateFile"), DefaultTemplateProvider.getDefaultJiraCommentTemplate());
		
		CouchDbProperties ruleLogProperties = new CouchDbProperties()
				  .setDbName("testimpactassessmentrulelogs")
				  .setCreateDbIfNotExist(true)
				  .setProtocol("http")
				  .setHost(props.getProperty("notificationCouchDBip", "127.0.0.1"))
				  .setPort(Integer.parseInt(props.getProperty("notificationCouchDBport", "5984")))
				  .setUsername(props.getProperty("rulelogCouchDBuser"))
				  .setPassword(props.getProperty("rulelogCouchDBpassword"))
				  .setMaxConnections(100)
				  .setConnectionTimeout(0);
		
		//Init RuleEngine(JiraRules) with RuleEngineLogger(needs KieSession)
		rel = new RuleEngineLogger(this.getKSession(), props.getProperty("ruleLogProcessId"), ruleLogProperties);
		rel.setIgnorePackageNames(Lists.newArrayList("c4s.impactassessment.eventrules"));

		//Setup jiraConnector(retrieve issues from API)
		jc = new JiraConnector(new URI("http://localhost:8090"), getQueue(), null, null);
		jc.setUseFakeCache(true);
		
		cc = new FakeCommentConnector();
	}
	
	
	
	@Override
	protected void configure() {
		
		bindConstant().annotatedWith(RuleEvaluationSubsystemWiring.IssueKeyToMonitor.class).to(props.getProperty("issueKey"));
		bindConstant().annotatedWith(RuleEvaluationSubsystemWiring.JiraConnectorInterval.class).to(props.getProperty("jiraConnectorInterval"));
		bindConstant().annotatedWith(JiraRules.QueuePollFrequency.class).to(Long.valueOf(props.getProperty("ruleEngineRefreshFrequency")));
		bind(IRoleMapper.class).toInstance(rm);
		bind(INotificationProcessor.class).toInstance(np);
		bind(INotificationLogger.class).toInstance(nl);
		bind(ICommentConnector.class).toInstance(cc);
		bind(INotificationDispatcher.class).to(NotificationSubsystemWiring.class).asEagerSingleton();
		bind(JiraRules.class).asEagerSingleton();
		bind(RuleEngineLogger.class).toInstance(rel);
	}

	@Provides
	public ConcurrentLinkedQueue<ChangeEvent> getQueue() {
		//Queue to pipe ChangeEvents from jiraConnector to RuleEngine
		if (queue == null)
			queue = new ConcurrentLinkedQueue<ChangeEvent>();
		return queue;
	}
	
	@Provides
	public JiraConnector getJiraConnector() {
		return jc;
	}
	
	@Provides
	public KieSession getKSession() {
		if (kSession == null) {
			KieServices ks = KieServices.Factory.get();
			KieContainer kContainer = ks.getKieClasspathContainer();
			kSession = kContainer.newKieSession("ksession-rules");
		}
		return kSession;
	}
	
	@Provides
	public Properties getProps() {
		return props;
	}
}*/
