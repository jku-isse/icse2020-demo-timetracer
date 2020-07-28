package c4s.impactassessment.rulebase;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.inject.Inject;

import org.kie.api.KieBase;
import org.kie.api.command.Command;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.Agenda;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.FactHandle.State;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.app.IRulesFinishedFiringEventHandler;
import c4s.impactassessment.jiraapplication.KieSessionContentUtils;
import c4s.impactassessment.monitoring.DomainObjectTracingInstrumentation;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;
import c4s.jiralightconnector.IssueAgent;
import core.base.Artifact;

public class PersistanceTriggeringKieSession implements KieSession, KieSessionDomainWrapper {

	
	private KieSession kSession;
	private KieSessionContentUtils kscu;
	@Inject
	private IRulesFinishedFiringEventHandler rffeh;
	@Inject
	private DomainObjectTracingInstrumentation analytics;
	
	@Inject
	public PersistanceTriggeringKieSession(@OriginalKieSession KieSession wrappedSession) {
		this.kSession=wrappedSession;
		this.kscu = new KieSessionContentUtils(this.kSession);
	}
	
	public void addEventListener(RuleRuntimeEventListener listener) {
		kSession.addEventListener(listener);
	}

	public void addEventListener(ProcessEventListener listener) {
		kSession.addEventListener(listener);
	}

	public void removeEventListener(RuleRuntimeEventListener listener) {
		kSession.removeEventListener(listener);
	}

	public int fireAllRules() {		
		int count = kSession.fireAllRules();
		rffeh.handleRulesFinishedFiringEvent(kSession);
		return count;
	}

	public ProcessInstance startProcess(String processId) {
		return kSession.startProcess(processId);
	}

	public void removeEventListener(ProcessEventListener listener) {
		kSession.removeEventListener(listener);
	}

	public KieRuntimeLogger getLogger() {
		return kSession.getLogger();
	}

	public <T extends SessionClock> T getSessionClock() {
		return kSession.getSessionClock();
	}

	public int fireAllRules(int max) {
		int count =  kSession.fireAllRules(max);
		rffeh.handleRulesFinishedFiringEvent(kSession);
		return count;
	}

	public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
		return kSession.getRuleRuntimeEventListeners();
	}

	public Collection<ProcessEventListener> getProcessEventListeners() {
		return kSession.getProcessEventListeners();
	}

	public void setGlobal(String identifier, Object value) {
		kSession.setGlobal(identifier, value);
	}

	public void halt() {
		kSession.halt();
	}

	public void addEventListener(AgendaEventListener listener) {
		kSession.addEventListener(listener);
	}

	public ProcessInstance startProcess(String processId, Map<String, Object> parameters) {
		return kSession.startProcess(processId, parameters);
	}

	public Object getGlobal(String identifier) {
		return kSession.getGlobal(identifier);
	}

	public void removeEventListener(AgendaEventListener listener) {
		kSession.removeEventListener(listener);
	}

	public Globals getGlobals() {
		return kSession.getGlobals();
	}

	public Calendars getCalendars() {
		return kSession.getCalendars();
	}

	public Environment getEnvironment() {
		return kSession.getEnvironment();
	}

	public KieBase getKieBase() {
		return kSession.getKieBase();
	}

	public int fireAllRules(AgendaFilter agendaFilter) {
		int count = kSession.fireAllRules(agendaFilter);
		rffeh.handleRulesFinishedFiringEvent(kSession);
		return count;
	}

	public Collection<AgendaEventListener> getAgendaEventListeners() {
		return kSession.getAgendaEventListeners();
	}

	public void registerChannel(String name, Channel channel) {
		kSession.registerChannel(name, channel);
	}

	public String getEntryPointId() {
		return kSession.getEntryPointId();
	}

	public void unregisterChannel(String name) {
		kSession.unregisterChannel(name);
	}

	public FactHandle insert(Object object) {
		return kSession.insert(object);
	}

	public Agenda getAgenda() {
		return kSession.getAgenda();
	}

	public int fireAllRules(AgendaFilter agendaFilter, int max) {				
		int count =  kSession.fireAllRules(agendaFilter, max);
		rffeh.handleRulesFinishedFiringEvent(kSession);
		return count;
	}

	public Map<String, Channel> getChannels() {
		return kSession.getChannels();
	}

	public KieSessionConfiguration getSessionConfiguration() {
		return kSession.getSessionConfiguration();
	}

	public EntryPoint getEntryPoint(String name) {
		return kSession.getEntryPoint(name);
	}

	public void retract(FactHandle handle) {
		kSession.retract(handle);
	}

	public ProcessInstance createProcessInstance(String processId, Map<String, Object> parameters) {
		return kSession.createProcessInstance(processId, parameters);
	}

	public Collection<? extends EntryPoint> getEntryPoints() {
		return kSession.getEntryPoints();
	}

	public void fireUntilHalt() {
		throw new RuntimeException("Calling fireUntilHalt will never lead to persistance, not what you would except of this wrapper");
		//kSession.fireUntilHalt();
	}

	public void delete(FactHandle handle) {
		kSession.delete(handle);
	}

	public QueryResults getQueryResults(String query, Object... arguments) {
		return kSession.getQueryResults(query, arguments);
	}

	public <T> T execute(Command<T> command) {
		return kSession.execute(command);
	}

	public void delete(FactHandle handle, State fhState) {
		kSession.delete(handle, fhState);
	}

	public void fireUntilHalt(AgendaFilter agendaFilter) {
		throw new RuntimeException("Calling fireUntilHalt will never lead to persistance, not what you would except of this wrapper");
		//kSession.fireUntilHalt(agendaFilter);
	}

	public LiveQuery openLiveQuery(String query, Object[] arguments, ViewChangedEventListener listener) {
		return kSession.openLiveQuery(query, arguments, listener);
	}

	public ProcessInstance startProcessInstance(long processInstanceId) {
		return kSession.startProcessInstance(processInstanceId);
	}

	public void update(FactHandle handle, Object object) {
		kSession.update(handle, object);
	}

	public void update(FactHandle handle, Object object, String... modifiedProperties) {
		kSession.update(handle, object, modifiedProperties);
	}

	public void signalEvent(String type, Object event) {
		kSession.signalEvent(type, event);
	}

	public FactHandle getFactHandle(Object object) {
		return kSession.getFactHandle(object);
	}

	@Deprecated
	public int getId() {
		return kSession.getId();
	}

	public long getIdentifier() {
		return kSession.getIdentifier();
	}

	public void dispose() {
		kSession.dispose();
	}

	public void signalEvent(String type, Object event, long processInstanceId) {
		kSession.signalEvent(type, event, processInstanceId);
	}

	public Object getObject(FactHandle factHandle) {
		return kSession.getObject(factHandle);
	}

	public Collection<? extends Object> getObjects() {
		return kSession.getObjects();
	}

	public void destroy() {
		kSession.destroy();
	}

	public void submit(AtomicAction action) {
		kSession.submit(action);
	}

	public Collection<ProcessInstance> getProcessInstances() {
		return kSession.getProcessInstances();
	}

	public <T> T getKieRuntime(Class<T> cls) {
		return kSession.getKieRuntime(cls);
	}

	public Collection<? extends Object> getObjects(ObjectFilter filter) {
		return kSession.getObjects(filter);
	}

	public <T extends FactHandle> Collection<T> getFactHandles() {
		return kSession.getFactHandles();
	}

	public <T extends FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
		return kSession.getFactHandles(filter);
	}

	public ProcessInstance getProcessInstance(long processInstanceId) {
		return kSession.getProcessInstance(processInstanceId);
	}

	public long getFactCount() {
		return kSession.getFactCount();
	}

	public ProcessInstance getProcessInstance(long processInstanceId, boolean readonly) {
		return kSession.getProcessInstance(processInstanceId, readonly);
	}

	public void abortProcessInstance(long processInstanceId) {
		kSession.abortProcessInstance(processInstanceId);
	}

	public WorkItemManager getWorkItemManager() {
		return kSession.getWorkItemManager();
	}
	
	@Override
	public void deleteProcessInstance (String processId) {
		kscu.deleteProcessInstance(processId);
	}
	
	@Override
	public void deleteIssueAgent (String key) {
		kscu.deleteArtifact(key);
	}
	
//	@Override
//	public void deleteJamaItem (String itemId) {
//		kscu.deleteJamaItem(itemId);
//	}
	
	@Override
	public String printKB() {
		StringBuilder sb = new StringBuilder();
//		List<Class> classesOutOfInterest = Arrays.asList(new Class[]{		
////				JamaItem.class,
//				Object.class
//				});
		sb.append(String.format("###########Knowledgebase Content excluding types: %s###########\n", "NONE"));
		//kscu.getContentExcluding(classesOutOfInterest)
		this.kSession.getObjects()
			.stream().forEach(obj -> sb.append(obj.toString()+"\n"));
		sb.append("###########Knowledgebase Content END###########\n");
		return sb.toString();
	}

//	@Override
//	public FactHandle insertJamaItemIfNotExists(CorrelationTuple corr, JamaItem jamaItem) {
//		return kscu.getJamaItemFactHandleById(jamaItem.getId()).orElseGet(() -> {
//			analytics.logJamaItemInsertedInRuleBase(corr, jamaItem);
//			return new AbstractMap.SimpleEntry<JamaItem,FactHandle>(jamaItem, kSession.insert(wrapJamaItemInArtifactWrapper(jamaItem, corr)));
//		}).getValue();
//		
//		if (kscu.getJamaItemById(jamaItem.getId()).isEmpty()) {
//			getkSession().insert(jamaItem);
//			logger.info(String.format("Inserting Jama item: %s  ",jamaItem.getName()));
//			return true;
//		} else {
//			logger.info(String.format("Not inserting already loaded Jama item: %s  ",jamaItem.getName()));
//			return false;
//		}
//	}
	
//	private ArtifactWrapper wrapJamaItemInArtifactWrapper(JamaItem item, CorrelationTuple corr) {
//		ArtifactWrapper artW = new ArtifactWrapper(item.getId()+"", "JamaItem", null, item);
//		artW.setLastChangeDueTo(corr);
//		return artW;
//	}

//	@Override
//	public FactHandle insertOrUpdateJamaItem(CorrelationTuple corr, JamaItem jamaItem) {
//		Optional<Map.Entry<ArtifactWrapper,FactHandle>> opAW = kscu.getArtifactWrapperById(jamaItem.getId()+"");
//		if (opAW.isPresent()) {
//			opAW.get().getKey().updateWrappedArtifact(jamaItem);
//			opAW.get().getKey().setLastChangeDueTo(corr);
//			analytics.logJamaItemUpdatedInRuleBase(corr, jamaItem);
//			kSession.update(opAW.get().getValue(), opAW.get().getKey());
//			return opAW.get().getValue();
//		} else {
//			analytics.logJamaItemInsertedInRuleBase(corr, jamaItem);
//			return kSession.insert(wrapJamaItemInArtifactWrapper(jamaItem, corr));
//		}
//	}
	
//	@Override
//	public Optional<FactHandle> updateJamaItemOnlyIfExists(CorrelationTuple correlation, JamaItem jamaItem) {
//		Optional<Map.Entry<ArtifactWrapper,FactHandle>> opAW = kscu.getArtifactWrapperById(jamaItem.getId()+"");
//		if (opAW.isPresent()) {
//			opAW.get().getKey().updateWrappedArtifact(jamaItem);
//			analytics.logJamaItemUpdatedInRuleBase(correlation, jamaItem);
//			kSession.update(opAW.get().getValue(), opAW.get().getKey());
//			return Optional.of(opAW.get().getValue());
//		}
//		return Optional.empty();
//	}

//	@Override
//	public boolean deleteJamaItemIfExists(CorrelationTuple corr, int itemId) {
//		Optional<Map.Entry<JamaItem,FactHandle>> opItem = kscu.getJamaItemFactHandleById(itemId);
//		if (opItem.isPresent()) {			
//			analytics.logJamaItemRemovedFromRuleBase(corr, opItem.get().getKey());
//			kSession.delete(opItem.get().getValue());
//			return true;
//		} else return false;
//	}

	@Override
	public FactHandle insertArtifactIfNotExists(CorrelationTuple corr, Artifact a) {
		return kscu.getArtifactFactHandleById(a.getId()).orElseGet( () -> {
			analytics.logJiraIssueInsertedInRuleBase(corr, a);
			return new AbstractMap.SimpleEntry<Artifact, FactHandle>(a, kSession.insert(a));
		}).getValue();
	}

	@Override
	public FactHandle insertOrUpdateArtifact(CorrelationTuple corr, Artifact a) {
		Optional<Map.Entry<Artifact, FactHandle>> optIssue = kscu.getArtifactFactHandleById(a.getId());
		if (optIssue.isPresent()) {
			analytics.logJiraIssueUpdatedInRuleBase(corr, a);
			kSession.update(optIssue.get().getValue(), a);
			return optIssue.get().getValue();
		} else {
			analytics.logJiraIssueInsertedInRuleBase(corr, a);
			return kSession.insert(a);
		}
	}
	
	@Override
	public Optional<FactHandle> updateArtifactOnlyIfExists(CorrelationTuple correlation, Artifact a) {
		Optional<Map.Entry<Artifact, FactHandle>> optIssue = kscu.getArtifactFactHandleById(a.getId());
		if (optIssue.isPresent()) {
			analytics.logJiraIssueUpdatedInRuleBase(correlation, a);
			kSession.update(optIssue.get().getValue(), a);
			return Optional.of(optIssue.get().getValue());
		}
		return Optional.empty();
	}

	@Override
	public Optional<Artifact> deleteArtifactIfExists(CorrelationTuple corr, String artifactKey) {
		Optional<Map.Entry<Artifact, FactHandle>> optFH = kscu.deleteArtifact(artifactKey);
		optFH.ifPresent(entry -> analytics.logJiraIssueRemovedFromRuleBase(corr,entry.getKey()) );
		return optFH.map(entry -> entry.getKey());		
	}

	@Override
	public FactHandle insertConstraintTrigger(ConstraintTrigger ct) {
		return kSession.insert(ct);
	}
	
	@Override
	public void setGlobals(Map<String, Object> globals) {
		globals.entrySet().stream().forEach(entry -> kSession.setGlobal(entry.getKey(), entry.getValue()));
	}

	@Override
	public Optional<Map.Entry<Artifact, FactHandle>> existsArtifact(String issueKey) {
		Optional<Map.Entry<Artifact, FactHandle>> optIssue = kscu.getArtifactFactHandleById(issueKey);
		return optIssue;
	}

	@Override
	public Optional<Entry<WorkflowInstance, FactHandle>> getWorkflowInstanceById(String id) {
		QueryResults results = kSession.getQueryResults("WorkflowInstanceByProcessId", id);
		for (QueryResultsRow row : results) {
			return Optional.of(new AbstractMap.SimpleEntry<WorkflowInstance, FactHandle>((WorkflowInstance) row.get("workflowInstance"), row.getFactHandle("workflowInstance")));
		}
		return Optional.empty();
	}

	@Override
	public List<Entry<WorkflowInstance, FactHandle>> getAllWorkflowInstances() {
		List<Entry<WorkflowInstance, FactHandle>> wfis = new ArrayList<>();
		QueryResults results = kSession.getQueryResults("WorkflowInstances");
		for(QueryResultsRow row : results) {
			wfis.add(new AbstractMap.SimpleEntry<>((WorkflowInstance) row.get("workflowInstances"), row.getFactHandle("workflowInstances")));
		}
		return wfis;
	}

}
