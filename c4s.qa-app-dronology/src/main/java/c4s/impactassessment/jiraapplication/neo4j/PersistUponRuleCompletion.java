package c4s.impactassessment.jiraapplication.neo4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.drools.core.event.DefaultAgendaEventListener;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultRuleRuntimeEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.runtime.KieSession;

import com.google.inject.Inject;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.app.IRulesFinishedFiringEventHandler;
import c4s.impactassessment.monitoring.RequestTracingInstrumentation;
import c4s.impactassessment.neo4j.BasicServices;
import c4s.impactassessment.neo4j.BasicServices.WorkflowInstanceService;
import c4s.impactassessment.rulebase.OriginalKieSession;
import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.AbstractWorkflowInstanceObject;
import c4s.impactassessment.workflowmodel.Artifact;
import c4s.impactassessment.workflowmodel.QACheckDocument;
import c4s.impactassessment.workflowmodel.RuleEngineBasedConstraint;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.WorkflowTask;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;

public class PersistUponRuleCompletion implements IRulesFinishedFiringEventHandler, WorkflowChangesSignalHandler {

	private static Logger log = LogManager.getLogger(PersistUponRuleCompletion.class);
	
	@Inject
	BasicServices.WorkflowInstanceService wfiService;
	@Inject 
	BasicServices.WorkflowTaskService wftService;
	@Inject
	BasicServices.ArtifactService artService;
	@Inject 
	RequestTracingInstrumentation rti;
	
	KieSession kSession;
	
	private Set<WorkflowInstance> changedWFIs = new HashSet<WorkflowInstance>();
	private Set<AbstractArtifact> changedArt = new HashSet<AbstractArtifact>(); // tracking more complex artifacts that need dedicated persistance
	private boolean taskAdded = false;
	
	@Inject
	public PersistUponRuleCompletion(@OriginalKieSession KieSession kieSession) {
		this.kSession = kieSession;
	}
	
	@Inject //annotated so this is run after injection
	private void init() {
		WorkflowCreatedListener wfcListener = new WorkflowCreatedListener(wfiService, this);
		kSession.addEventListener(wfcListener);
		WorkflowChangeListener wfListener = new WorkflowChangeListener(Arrays.asList(new String[]{"c4s.impactassessment.process.input","c4s.impactassessment.process.execution"}), this);
		kSession.addEventListener(wfListener);
//		TaskCreatedListener tcListener = new TaskCreatedListener(this);
//		kSession.addEventListener(tcListener);
	}
	
	@Override
	public void signalChangedArtifact(AbstractArtifact art) {
		changedArt.add(art);
	}
	
	/* (non-Javadoc)
	 * @see c4s.impactassessment.jiraapplication.neo4j.WorkflowChangesSignalHandler#signalChangedWorkflow(c4s.impactassessment.workflowmodel.WorkflowInstance)
	 */
	@Override
	public void signalChangedWorkflow(WorkflowInstance wfi) {
		changedWFIs.add(wfi);
	}
	
	/* (non-Javadoc)
	 * @see c4s.impactassessment.jiraapplication.neo4j.WorkflowChangesSignalHandler#signalNewTask(c4s.impactassessment.workflowmodel.WorkflowTask)
	 */
	@Override
	public void signalNewTask(WorkflowTask wft) {
		taskAdded = true;
		if (wft.getWorkflow() != null)
			changedWFIs.add(wft.getWorkflow());
	}
	
	@Override
	public void handleRulesFinishedFiringEvent(KieSession kSession) {
		changedWFIs.stream()
			.map(wfi -> { log.debug(String.format("Pushing changes to Neo4J for workflowID %s", wfi.getId()));
				return wfi;
				})
			.forEach(wfi -> wfiService.push(wfi));
		changedWFIs.clear();
		
		if (taskAdded) {
			wftService.deleteDetachedPlaceHolders();
			taskAdded = false;
		}
		changedArt.stream() // ensure we remove links that are no longer used.
			.filter(art -> art instanceof QACheckDocument)
			.map(QACheckDocument.class::cast)
			.forEach(qadoc -> qadoc.getConstraintsReadonly().stream()
								.forEach(constr -> constr.signalCheckingComplete()));
		
		changedArt.stream()
			.filter(art -> !(art instanceof QACheckDocument))	
			.map(art -> { log.debug(String.format("Pushing changes to Neo4J for Artifact %s", art.getId()));
				return art;
			})
			.forEach(art ->  artService.push(art));
		
		/*
		 * 
		 * */
		
		changedArt.stream() // ensure we remove links that are no longer used.
			.filter(art -> art instanceof QACheckDocument)
			.map(QACheckDocument.class::cast)
			.forEach(qadoc -> { 
							log.debug(String.format("Pushing QACheckDocument changes to Neo4J for Artifact %s", qadoc.getId()));
							rti.logQualityCheckDocumentUpdate(qadoc);
							artService.createOrUpdate(qadoc);							
							//qadoc.getConstraintsReadonly().stream()
							//.forEach(constr -> constr.signalCheckingComplete()) 							
//							qadoc.getConstraintsReadonly().stream()
//								.forEach(qac -> { qac.getFulfilledForReadOnly().stream().forEach(rl -> artService.push(rl));
//												qac.getUnsatisfiedForReadOnly().stream().forEach(rl -> artService.push(rl));
//											});
							
			});
		
		changedArt.clear();
	}

//	public class TaskCreatedListener extends DefaultRuleRuntimeEventListener {
//
//		WorkflowChangesSignalHandler purc;
//		
//		public TaskCreatedListener(WorkflowChangesSignalHandler purc) {
//			this.purc = purc;
//		}
//		
//		@Override
//		public void objectInserted(ObjectInsertedEvent event) {
//			Object o = event.getObject();
//			if (o instanceof WorkflowTask) {
//				purc.signalNewTask((WorkflowTask) o);
//			}		
//		}
//
//	}
	
	class WorkflowChangeListener extends DefaultAgendaEventListener {

		WorkflowChangesSignalHandler purc;
		List<String> packageNames;
		
		public WorkflowChangeListener(List<String> rulesFromPackagesToListenTo, WorkflowChangesSignalHandler purc) {
			super();
			this.packageNames = rulesFromPackagesToListenTo;
			this.purc = purc;
		}

		@Override
		public void afterMatchFired(AfterMatchFiredEvent event) {
			String packageName = event.getMatch().getRule().getPackageName();
			if (packageNames.contains(packageName)) {
				event.getMatch().getObjects().stream()
					.filter(o -> o instanceof AbstractWorkflowInstanceObject)
					.map(o -> (AbstractWorkflowInstanceObject)o)
					.map(wfio -> wfio.getWorkflow())
					.filter(wfi -> wfi != null)
					.distinct()
					.forEach(wfi -> purc.signalChangedWorkflow(wfi));								
				
				Optional<CorrelationTuple> ctCorr = event.getMatch().getObjects().stream()
						.filter(o -> o instanceof ConstraintTrigger)
						.map(ConstraintTrigger.class::cast)
						.map(ct -> ct.getRequestCorrelation())
						.findAny();
				
				event.getMatch().getObjects().stream()
				.filter(o -> o instanceof RuleEngineBasedConstraint)
				.map(RuleEngineBasedConstraint.class::cast)
				.map(rebc -> rebc.getParentArtifact())
				.filter(parent -> parent instanceof AbstractArtifact)
				.map(AbstractArtifact.class::cast)
				.distinct()
				.forEach(aart -> { purc.signalChangedArtifact(aart); 
									if (aart instanceof QACheckDocument && ctCorr.isPresent()) {
										aart.setLastChangeDueTo(ctCorr.get());
									}
								});
			}
		}
	}
	
	class WorkflowCreatedListener extends DefaultRuleRuntimeEventListener {

		BasicServices.WorkflowInstanceService wfiService;
		WorkflowChangesSignalHandler purc;
		
		public WorkflowCreatedListener(WorkflowInstanceService wfiService, WorkflowChangesSignalHandler purc) {
			super();
			this.wfiService = wfiService;
			this.purc = purc;
		}

		@Override
		public void objectInserted(ObjectInsertedEvent event) {
			Object o = event.getObject();
			if (o instanceof WorkflowInstance) {
				// whenever we insert a new workflow instance, lets remove the artifacts for the old one from the Neo4J db
				WorkflowInstance wfi = (WorkflowInstance) o;
				artService.deleteArtifactsByWorkflowInstanceId(wfi.getId());
				wfiService.push(wfi);
				return;
			}
			if (o instanceof RuleEngineBasedConstraint) {
				Artifact parent = ((RuleEngineBasedConstraint) o).getParentArtifact();
				if (parent instanceof AbstractArtifact)
					purc.signalChangedArtifact((AbstractArtifact) parent);
				return;
			}
			if (o instanceof WorkflowTask) {
				purc.signalNewTask((WorkflowTask) o);
				return;
			}
		}

		@Override
		public void objectDeleted(ObjectDeletedEvent event) {
			Object o = event.getOldObject();
			if (o instanceof WorkflowInstance) {
				wfiService.delete(((WorkflowInstance) o).getId());
			}	
		}

	}
}
