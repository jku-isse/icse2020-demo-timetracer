package c4s.impactassessment.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;

import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.neo4j.BasicServices.ArtifactService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowDefinitionService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.neo4j.BasicServices.WorkflowTaskService;
import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.AbstractWorkflowInstanceObject;
import c4s.impactassessment.workflowmodel.ArtifactWrapper;
import c4s.impactassessment.workflowmodel.DecisionNodeDefinition.States;
import c4s.impactassessment.workflowmodel.DecisionNodeInstance;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;
import c4s.impactassessment.workflowmodel.TaskDefinition;
import c4s.impactassessment.workflowmodel.TaskLifecycle.Events;
import c4s.impactassessment.workflowmodel.WPManagementWorkflow;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.WorkflowTask;
import c4s.impactassessment.workflowmodel.WorkflowTask.ArtifactInput;
import c4s.impactassessment.workflowmodel.WorkflowTask.ArtifactOutput;


public class PersistanceTester {

	private Injector injector;
	private WPManagementWorkflow twfd;
	private static  Properties props = new Properties();
	private ArtifactService artService;
	private SessionFactory nsf; 
	
	public static void main(String[] args) throws FileNotFoundException, IOException {				 
		props.load(new FileInputStream("app.properties"));
		PersistanceTester pt = new PersistanceTester();
		pt.init();
		pt.storeWPProcessWithSingleTaskAndQA();
		pt.storeWPProcessWMultiTaskAndMultiQA();
		System.exit(0);
	}

	private PersistanceTester() {
		
	}
	
	public void init() {
			injector = Guice.createInjector(new Neo4JConnectorConfig(props));
			nsf = injector.getInstance(SessionFactory.class);			
			
			twfd = new WPManagementWorkflow();
			injector.injectMembers(twfd); //internally calls initWorkflowSpecification upon injection
			WorkflowDefinitionService wfdService = injector.getInstance(WorkflowDefinitionService.class);
			wfdService.push(twfd);
			artService = injector.getInstance(ArtifactService.class);
			
	}
	
	private void storeWPProcessWithSingleTaskAndQA() {
		String wfID = "WF1";
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowTaskService wftService = injector.getInstance(WorkflowTaskService.class);
		
		ArtifactWrapper ticketArt1 = new ArtifactWrapper("TICKET1", WPManagementWorkflow.ARTIFACT_TYPE_JIRA_TICKET, null, null);
		
		//artService.push(ticketArt);
		
		
		WorkflowInstance wfi = twfd.createInstance(wfID);
		artService.deleteArtifactsByWorkflowInstanceId(wfi.getId());
		List<AbstractWorkflowInstanceObject> awos = wfi.enableWorkflowTasksAndDecisionNodes();
		wfiService.push(wfi);
		DecisionNodeInstance dni = (DecisionNodeInstance) awos.get(0);
		dni.completedDataflowInvolvingActivationPropagation();
		List<TaskDefinition> tds = dni.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
       	tds.stream().
       		forEach(td -> {        			
       			WorkflowTask wt = wfi.instantiateTask(td);
       			wt.addInput(new ArtifactInput(ticketArt1, WPManagementWorkflow.INPUT_ROLE_WPTICKET));
       			wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
       			Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
       			dni.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches       			
       		});				
		
       	QACheckDocument qa = new QACheckDocument("QA1", wfi);
		int itemId = 1;
		RuleEngineBasedConstraint srsConstraint = new RuleEngineBasedConstraint("REBC1", qa, "CheckSWRequirementReleased", wfi, "Have all SRSs of the WP been released?");	
		qa.addConstraint(srsConstraint);	
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=11", "self", "", "html", "SRS 11"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=12", "self", "", "html", "SRS 12"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=13", "self", "", "html", "SRS 13"));
		srsConstraint.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=14", "self", "", "html", "SRS 14"));
		srsConstraint.addAs(false, new ResourceLink("SRS", "http://testjama.frequentis/item=15", "self", "", "html", "SRS 15"));
       	// add to WFTask
		wfi.getWorkflowTasksReadonly().stream().forEach(wft -> {
			// there is only one here, so lets use this to add the QA document
			ArtifactOutput ao = new ArtifactOutput(qa, "QA_PROCESS_CONSTRAINTS_CHECK");
			wft.addOutput(ao);
			artService.push(qa);
			storeQADoc(qa);
		});
		
       	wfiService.push(wfi);
		List<WorkflowTask> deletedWFTs = wftService.deleteDetachedPlaceHolders();
		deletedWFTs.size();
	}
	
	private void storeWPProcessWMultiTaskAndMultiQA() {		
		String wfID = "WF2";
		WorkflowInstanceService wfiService = injector.getInstance(WorkflowInstanceService.class);
		WorkflowTaskService wftService = injector.getInstance(WorkflowTaskService.class);
		
		ArtifactWrapper ticketArt1 = new ArtifactWrapper("TICKET2", WPManagementWorkflow.ARTIFACT_TYPE_JIRA_TICKET, null, null);				
		//artService.push(ticketArt);
		
		WorkflowInstance wfi = twfd.createInstance(wfID);
		artService.deleteArtifactsByWorkflowInstanceId(wfi.getId());
		List<AbstractWorkflowInstanceObject> awos = wfi.enableWorkflowTasksAndDecisionNodes();
		wfiService.push(wfi);
		DecisionNodeInstance dni = (DecisionNodeInstance) awos.get(0);
		dni.completedDataflowInvolvingActivationPropagation();
		List<TaskDefinition> tds = dni.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
       	tds.stream().
       		forEach(td -> {        			
       			WorkflowTask wt = wfi.instantiateTask(td);
       			wt.addInput(new ArtifactInput(ticketArt1, WPManagementWorkflow.INPUT_ROLE_WPTICKET));
       			wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
       			Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
       			dni.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches       			
       		});				
		
       	QACheckDocument qa = new QACheckDocument("QA1-"+wfID, wfi);
		int itemId = 2;
		RuleEngineBasedConstraint srsConstraint = new RuleEngineBasedConstraint("REBC2", qa, "CheckSWRequirementReleased", wfi, "Have all SRSs of the WP been released?");	
		qa.addConstraint(srsConstraint);	
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=2", "self", "", "html", "SRS 2"));
		srsConstraint.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=3", "self", "", "html", "SRS 3"));
//		artService.push(qa);
//		
		RuleEngineBasedConstraint srsConstraintx = new RuleEngineBasedConstraint("REBC4", qa, "CheckSWRequirementReleased", wfi, "Have all SSD a downstream SRSs appended?");	
		qa.addConstraint(srsConstraintx);	
		srsConstraintx.addAs(true, new ResourceLink("SRS", "http://testjama.frequentis/item=1", "self", "", "html", "SRS 1"));
		artService.push(qa);
		
		storeQADoc(qa);
       	// add to WFTask
		Optional<WorkflowTask> wftOpt = wfi.getWorkflowTasksReadonly().stream()
				.map(wft -> {
					// there is only one here, so lets use this to add the QA document
					ArtifactOutput ao = new ArtifactOutput(qa, "QA_PROCESS_CONSTRAINTS_CHECK");
					wft.addOutput(ao);
					ResourceLink rl = WPManagementWorkflow.getLink("http://testjama.frequentis/sss=review1", "SSS Review Link");
					//rl.setWorkflowInstance(wft.getWorkflow());
					wft.addOutput(new ArtifactOutput(rl, WPManagementWorkflow.OUTPUT_ROLE_SSSREVIEW));
					return wft;
				}).findFirst();
		wftOpt.ifPresent(wft -> {
			wfi.getDecisionNodeInstancesReadonly().stream()
			.filter(dni1 -> dni1.getState().equals(States.AVAILABLE))
			.findAny().ifPresent(dni2 -> {
				dni2.activateInBranch(dni2.getInBranchForWorkflowTask(wft));
				dni2.completedDataflowInvolvingActivationPropagation();
				List<TaskDefinition> tds2 = dni2.getTaskDefinitionsForNonDisabledOutBranchesWithUnresolvedTasks();
				tds2.stream().
				forEach(td2 -> {        			
					WorkflowTask wt = wfi.instantiateTask(td2);
					wt.addInput(new ArtifactInput(ticketArt1, WPManagementWorkflow.INPUT_ROLE_WPTICKET));
					wt.signalEvent(Events.INPUTCONDITIONS_FULFILLED);       			
					Set<AbstractWorkflowInstanceObject> newDNIs = wfi.activateDecisionNodesFromTask(wt);       			
					dni2.consumeTaskForUnconnectedOutBranch(wt); // connect this task to the decision node instance on one of the outbranches
					QACheckDocument qa2 = new QACheckDocument("QA2-"+wfID, wfi);
					RuleEngineBasedConstraint srsConstraint2 = new RuleEngineBasedConstraint("REBC3", qa2, "CheckSWRequirementRelease", wfi, "Does every SRS have Release assigned?");
					qa2.addConstraint(srsConstraint2);	
					srsConstraint2.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=6", "self", "", "html", "SRS 6")));
					srsConstraint2.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=7", "self", "", "html", "SRS 7")));
					srsConstraint2.addAs(true, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=8", "self", "", "html", "SRS 8")));
					srsConstraint2.addAs(false, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=4", "self", "", "html", "SRS 4")));
					srsConstraint2.addAs(false, getOrCreate(new ResourceLink("SRS", "http://testjama.frequentis/item=5", "self", "", "html", "SRS 5")));
					ArtifactOutput ao = new ArtifactOutput(qa2, "QA_PROCESS_CONSTRAINTS_CHECK");
					wt.addOutput(ao);
					//artService.createOrUpdate(qa2);
					storeQADoc(qa2);
				});
			});
		});
		
       	wfiService.push(wfi);
		List<WorkflowTask> deletedWFTs = wftService.deleteDetachedPlaceHolders();
		deletedWFTs.size();
		
		
	}
	
	private void storeQADoc(QACheckDocument qadoc) {
		qadoc.getConstraintsReadonly().stream()		
		.map(qac -> { qac.getFulfilledForReadOnly().stream().forEach(rl -> artService.createOrUpdate(rl));
						qac.getUnsatisfiedForReadOnly().stream().forEach(rl -> artService.createOrUpdate(rl));
						return qac;
					})
		.map(qac -> {
			artService.createOrUpdate(qac);
			return qac;
			 });
		//artService.push(qadoc);
	}
	
	private ResourceLink getOrCreate(ResourceLink localCopy) {		
		AbstractArtifact aa = artService.find(localCopy.getId());
		if (aa == null)
			return localCopy;
		else return (ResourceLink) aa;
			
	}
	
	private void removeArtifactsByWFId(String wfid) {
		String query = "MATCH (aa:AbstractArtifact {wfi='"+wfid+"'} ) DETACH DELETE aa RETURN aa";		
		Iterable<AbstractArtifact> iter = nsf.openSession().query(AbstractArtifact.class, query, Collections.emptyMap());
		iter.forEach(aa -> System.out.println(aa.getId()));
	}
}
