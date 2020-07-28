package c4s.impactassessment.app;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kie.api.runtime.rule.FactHandle;

import c4s.amqp.IAMQPConsumer;
import c4s.amqp.IAddMessageHandler;
import c4s.amqp.ICheckMessageHandler;
import c4s.amqp.IDeleteMessageHandler;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.components.AddMessage;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;
import core.base.Artifact;
import core.connector.ITimeTravelingConnector;

public class RuleEvaluationSubSystem extends MinimalRuleEvaluationSubSystemWithoutJiraOrJama {

	private static Logger logger = LogManager.getLogger(RuleEvaluationSubSystem.class);
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Inject
	ITimeTravelingConnector connector;
//	@Inject
//	JiraEventToKnowledgebasePusher jekp;
	@Inject
	IAMQPConsumer amqp;
	@Inject 
	ICheckMessageHandler cmh;
	@Inject
	IAddMessageHandler amh;
	@Inject
	IDeleteMessageHandler dmh;
	
	@Override
	public void start() {
		super.start();	
		amqp.registerICheckMessageHandler(cmh);
		amqp.registerIAddMessageHandler(amh);
		amqp.registerIDeleteMessageHandler(dmh);
		
		// load monitored items
		reinsertMonitoredObjects();
		runCLI();
	}
	
	
	Scanner sc;
	boolean running = true;
	
	public void runCLI() {
		HashMap<String, Runnable> cmds = new HashMap<String, Runnable>();		
		cmds.put("quit", ()->quit() );
//		cmds.put("monitorJamaProject", ()->startMonitoringJamaProject(sc.nextLine()));
//		cmds.put("monitorAllJamaProjects", ()->jamaC.startMonitoringAllAccessibleJamaProjects());
//		cmds.put("insertFilter", ()->loadAllJamaAndJiraViaFilter(sc.nextLine()));
//		cmds.put("insertJama", ()->startProcessViaJama(sc.nextLine()));
//		cmds.put("checkJama", () -> jamaC.fetchUpdatesForAllItemsNow( getCorr() ));
//		cmds.put("checkJira", () -> jekp.fetchUpdatesForAllItemsNow( getCorr() ));
		cmds.put("triggerQA", ()->insertAllConstraintsTrigger());
		cmds.put("printKB", ()->printKB());
		cmds.put("setJiraDatabase", () ->  {
			System.out.println("enter year, month, day");
			sendJiraDatabaseThroughTime(sc.nextLine(), sc.nextLine(), sc.nextLine());
		});
		cmds.put("playForwardTillNextChangeOf", () ->  {
			System.out.println("enter artifactKeys(form: key1, key2, ....)");
			connector.travelToNextChange(false, sc.nextLine().split(", "));
		});
		cmds.put("playBackwardTillNextChangeOf", () ->  {
			System.out.println("enter artifactKeys(form: key1, key2, ....)");
			connector.travelToNextChange(true, sc.nextLine().split(", "));
		});
		cmds.put("insertJiraItem", () ->  {
			System.out.println("enter artifactKey");
			insertJiraItem(sc.nextLine());
		});
		cmds.put("insertJiraDatabase", () ->  {
			insertJiraDatabase();
		});
//		cmds.put("printJama", () -> printJama(sc.nextLine()));
//		cmds.put("prefillJamaCache", () -> prefillJamaCache());
		cmds.put("help", ()-> {System.out.println("Available Commands:"); cmds.keySet().stream().forEach(System.out::println);});
		sc = new Scanner(System.in);		
		System.out.println("Press 'help' for available commands");
		do {			
			String input = sc.nextLine();
			Runnable cmd = cmds.getOrDefault(input, ()->System.out.println("Press 'help' for available commands"));
			cmd.run();
		} while (running && sc.hasNextLine());
	}
	
	public void quit() {
		running = false;
		amqp.shutdown();
//		jamaC.stopAllMonitoring();		
//		jekp.shutdown();
		executor.shutdown();
	}
	
	int reqCounter = 0;
	private CorrelationTuple getCorr() {
		reqCounter++;
		return new CorrelationTuple("CLI"+reqCounter, "CLI");	
	}
	
	
	private void reinsertMonitoredObjects() {
//		jiraInst.getKeysOfAllMonitoredIssues().stream()
//			.forEach(key -> { IssueAgent issue = jiraInst.fetchAndMonitor(key);
//				              getkSession().insert(issue);
//				              });
//		getkSession().fireAllRules();
//		jamaC.startMonitoringAllAccessibleJamaProjects();
//		jamaC.getAllMonitoredItems().stream()
//			.forEach(id -> startProcessViaJama(id+"") );
	}
	
	
//	private void prefillJamaCache() {
//		try {
//			System.out.println("Prefetching Jama ItemTypes and their Picklist Options, this might take a while ...");
//			jamaC.prefetchItemTypesAndPickListOptions();
//		} catch (RestClientException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
//	private void startMonitoringJamaProject(String jamaId) {
//		try{
//			jamaC.startMonitoringJamaProject(Integer.parseInt(jamaId));
//		} catch (NumberFormatException e) {
//			System.out.println("Not a valid JamaId, provide a number: "+e.getMessage());
//		}	
//	}
	
	private void insertAllConstraintsTrigger() {
		kSession.getAllWorkflowInstances().stream()
			.map(entry -> entry.getKey())	
			.forEach(wfi -> {
				ConstraintTrigger ct = new ConstraintTrigger(wfi, getCorr());
				ct.addConstraint("*");
				getkSession().insertConstraintTrigger(ct);
				System.out.println(String.format("Inserting Constraintstrigger for WorkflowInstance: %s", wfi.getId()));
			});
		getkSession().fireAllRules();
	}
	
//	public void loadAllJamaAndJiraViaFilter(String filterId) {
//		try{
//			int id = Integer.parseInt(filterId);
//			CorrelationTuple corr = new CorrelationTuple(filterId, "JAMA_FILTER_ID"); //TODO replace with real correlation id from frontend request
//			Set<SimpleEntry<JamaItem, String>> items = jamaC.getAndMonitorAllJamaWPItemsForFilter(id);
//			logger.info(String.format("Loaded total of %s JamaItems", items.size()));
//			items.stream()
//			.filter(jamaJira -> {	boolean hasJira = jamaJira.getValue() != null;
//									if (!hasJira) { 
//										logger.info(String.format("Jama item %s has no associated Jira issue, ignoring item", jamaJira.getKey().getId()));
//									}
//									return hasJira; })
//			.forEach(jamaJira -> {
//				kSession.insertJamaItemIfNotExists(corr, jamaJira.getKey());
//				//insertJamaIfNotExists(jamaJira.getKey());				
//				String jiraKey = jamaJira.getValue();				
//				insertJiraIfNotExists(corr, jiraKey);		
//			});
//			getkSession().fireAllRules();
//		} catch (NumberFormatException e) {
//			logger.error("Not a valid JamaId, provide a number: "+e.getMessage());
//		}
//		
//	}

	
//	public void startProcessViaJama(String jamaItemId) {
//		try {						
//			Map.Entry<JamaItem,String> jamaJira = jamaC.getJamaItemAndItsJiraKey(Integer.parseInt(jamaItemId));
//			if (jamaJira.getKey() == null) {
//				System.out.println(" No JamaItem found with Id: "+jamaItemId);
//				return;
//			}
//			if (jamaJira.getValue() == null || jamaJira.getValue().isEmpty()) {
//				System.out.println(" No Jira Issue Key found in JamaItem with Id: "+jamaItemId);
//				return;
//			}
//			CorrelationTuple corr = new CorrelationTuple(jamaItemId, "JAMA_ITEM_ID"); //TODO replace with real correlation id from frontend request
//			kSession.insertJamaItemIfNotExists(corr, jamaJira.getKey());	
//			
//			String jiraKey = jamaJira.getValue();				
//			insertJiraIfNotExists(corr, jiraKey);			
//			getkSession().fireAllRules();			
//		} catch (NumberFormatException e) {
//			logger.error("Not a valid JamaId, provide a number: "+e.getMessage());
//		}	
//	}
	
//	public boolean insertJamaIfNotExists(JamaItem jamaItem) {
//		if (kscu.getJamaItemById(jamaItem.getId()).isEmpty()) {
//			getkSession().insert(jamaItem);
//			logger.info(String.format("Inserting Jama item: %s  ",jamaItem.getName()));
//			return true;
//		} else {
//			logger.info(String.format("Not inserting already loaded Jama item: %s  ",jamaItem.getName()));
//			return false;
//		}
//	}
	
	public boolean insertJiraIfNotExists(CorrelationTuple correlation, String jiraKey) {
		Optional<Map.Entry<Artifact, FactHandle>> optIa = kSession.existsArtifact(jiraKey);
		if (optIa.isPresent()) {
			logger.info(String.format("Not inserting already loaded Jira issue: %s  ",jiraKey));
			return false;
		} else {
			Artifact artifact = connector.fetchAndMonitor(jiraKey).get();
			kSession.insertArtifactIfNotExists(correlation, artifact);
			return true;
		}
		
//		if (kscu.getIssueAgentByKey(jiraKey).isEmpty() ) {
//			
//			if (issue != null) {
//				getkSession().insert(issue);	
//				return true; 
//			} else {
//				return false;
//			}
//		} else {
//			
//		}
	}
	
	public void sendJiraDatabaseThroughTime(String year, String month, String day) {
		
		Calendar cal = new GregorianCalendar();
	    cal.set(Calendar.YEAR, Integer.parseInt(year));
	    cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
	    cal.set(Calendar.DATE, Integer.parseInt(day));
		Timestamp ts = new Timestamp(cal.getTimeInMillis());
		
		connector.travelTo(ts);
		
	}
	
	public void insertJiraDatabase() {
		connector.fetchDatabase().get().forEach( a -> {
			insertJiraItem(a.getIdInSource());
		});
	}

	public void insertJiraItem(String jiraKey) {
		AddMessage msg = new AddMessage("", jiraKey);
		msg.setCorrelationId(getCorr().getCorrelationId());
		amh.continueProcessingAddMessage(amh.preprocessAddMessage(msg));
	}
	
	
	private void removeProcessViaJamaId(String jamaItemId) {
		// remove from kieSession
		// remove from neo4j
		// remove from jamaInst
		// remove from jiraInst plus jiracache
		
//				for(String childKey : keys) {
//					kscu.deleteIssueAgent(childKey);
//					kscu.deleteLinkReferences(childKey);
//					kscu.deleteAssessmentTicketState(childKey);
//				}
//				jc.removeRootIssue(key); 
//				kscu.deleteIssueAgent(key);
//				kscu.deleteLinkReferences(key);
//				kscu.deleteAssessmentTicketState(key);
//				kscu.deleteProcessInstance(key);
	}
	
//	public void printJama(String id) {
//		try {
//			int jid = Integer.parseInt(id);
//			Entry<JamaItem,String> entry = this.jamaC.getJamaItemAndItsJiraKey(jid);
//			if (entry != null && entry.getKey() != null) {
//				System.out.println(JamaUtils.getJamaItemDetails(entry.getKey()));
//				jamaC.removeJamaItemFromMonitoredItems(jid);
//			}
//		} catch (NumberFormatException e) {
//			e.printStackTrace();
//		}
//	}
	
	public void printKB() {
		System.out.println(this.getkSession().printKB());
//		List<Class> classesOfInterest = Arrays.asList(new Class[]{		
//				WorkflowTask.class, 
//				DecisionNodeInstance.class,
//				JiraIssueExtension.class,
//				ArtifactWrapper.class,
//				QACheckDocument.class});
//		System.out.println(String.format("Knowledgebase Content limited to types: %s", classesOfInterest));
//		kscu.getContentLimitedTo(classesOfInterest)
//			.stream().forEach(obj -> System.out.println(obj.toString()));
//		System.out.println("Knowledgebase Content END");
	}
}
