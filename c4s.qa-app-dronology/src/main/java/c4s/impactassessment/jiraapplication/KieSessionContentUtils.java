package c4s.impactassessment.jiraapplication;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import c4s.impactassessment.workflowmodel.ArtifactWrapper;
import core.base.Artifact;

public class KieSessionContentUtils {

	KieSession kieSession = null;
	
	public KieSessionContentUtils(KieSession kieSession) {
		this.kieSession = kieSession;
	}
	
	public List<Artifact> getIssueAgents() {
		throw new RuntimeException("Not implemented");
		/*List<IssueAgent> issueAgents = new ArrayList<IssueAgent>();
		QueryResults results = kieSession.getQueryResults("IssueAgents");
		for(QueryResultsRow row : results) {
			issueAgents.add((IssueAgent) row.get("issueAgent"));
		}
		return issueAgents;
		*/
	}
	
	public List<Artifact> getIssueAgentByKey(String key) {
		throw new RuntimeException("Not implemented");
		/*
		List<IssueAgent> issues = new ArrayList<IssueAgent>();
		QueryResults results = kieSession.getQueryResults("IssueAgentByKey", key);
		for(QueryResultsRow row : results) {
			issues.add((IssueAgent) row.get("issueAgent"));
		}
		return issues;
		*/
		/*
		 * query "IssueAgentByKey" (String issueKey)
	issueAgent : IssueAgent(key.equals(issueKey))
end
		 * */
	}
	
	public Optional<Map.Entry<Artifact, FactHandle>> getArtifactFactHandleById(String key) {
		QueryResults results = kieSession.getQueryResults("ArtifactByKey", key);
		for(QueryResultsRow row : results) {
			return Optional.of(new AbstractMap.SimpleEntry<>((Artifact) row.get("artifact"), row.getFactHandle("artifact")));
		}
		return Optional.empty();
		/*
		 * query "IssueAgentByKey" (String issueKey)
	issueAgent : IssueAgent(key.equals(issueKey))
end
		 * */
	}	
	
//	public List<ChangeEvent> getChangeEvents() {
//		List<ChangeEvent> changeEvents = new ArrayList<ChangeEvent>();
//		QueryResults results = kieSession.getQueryResults("ChangeEvents");
//		for(QueryResultsRow row : results) {
//			changeEvents.add((ChangeEvent) row.get("changeEvent"));
//		}
//		return changeEvents;
//	}
	
//	public List<JiraIssueExtension> getJiraIssueExtensions() {
//		List<JiraIssueExtension> jiraIssueExtensions = new ArrayList<>();
//		QueryResults results = kieSession.getQueryResults("JiraIssueExtensions");
//		for(QueryResultsRow row : results) {
//			jiraIssueExtensions.add((JiraIssueExtension) row.get("jiraIssueExtension"));
//		}		
//		return jiraIssueExtensions;
//	}
	
	public Optional<Map.Entry<ArtifactWrapper,FactHandle>> getArtifactWrapperById(String id) {
		QueryResults results = kieSession.getQueryResults("ArtifactWrappersById", id);
		for(QueryResultsRow row : results) {
			return Optional.of(new AbstractMap.SimpleEntry<ArtifactWrapper,FactHandle>( ((ArtifactWrapper) row.get("wrapper")), row.getFactHandle("wrapper")));
		}
		return Optional.empty();
	}
	
		
//	public List<JamaItem> getJamaItemById(int itemId) {
//		List<JamaItem> items = new ArrayList<JamaItem>();
//		QueryResults results = kieSession.getQueryResults("JamaItemById", itemId);
//		for(QueryResultsRow row : results) {
//			items.add((JamaItem) row.get("jamaItem"));
//		}
//		return items;
//	}
//	
//	public void deleteJamaItem(String itemId) {
//		QueryResults results = kieSession.getQueryResults("JamaItemById", itemId);
//		for(QueryResultsRow row : results) {
//			kieSession.delete(row.getFactHandle("jamaItem"));
//		}
//	}
//	
//	public Optional<Map.Entry<JamaItem,FactHandle>> getJamaItemFactHandleById(int itemId) {
//		
//		QueryResults results = kieSession.getQueryResults("JamaItemById", itemId);
//		for(QueryResultsRow row : results) {
//			return Optional.of(new AbstractMap.SimpleEntry<JamaItem,FactHandle>( (JamaItem) row.get("jamaItem"), row.getFactHandle("jamaItem")));
//		}
//		
//		//return Optional.empty();
//		// as jama items no longer directly added, ensure
//		Optional<Map.Entry<JamaItem,FactHandle>> result = Optional.empty();
//		Optional<Map.Entry<ArtifactWrapper,FactHandle>> artW = getArtifactWrapperById(""+itemId);
//		if (artW.isPresent()) {
//			result = artW.get().getKey().getWrappedArtifact() != null ? Optional.of(new AbstractMap.SimpleEntry<JamaItem,FactHandle>( (JamaItem)artW.get().getKey().getWrappedArtifact(), artW.get().getValue())) : Optional.empty();
//		} 
//		return result;
//	}
		
	
	public Optional<Map.Entry<Artifact,FactHandle>> deleteArtifact(String key) {
		QueryResults results = kieSession.getQueryResults("ArtifactByKey", key);
		for(QueryResultsRow row : results) {
			FactHandle fh = row.getFactHandle("artifact");
			Artifact ia = (Artifact) row.get("artifact");
			kieSession.delete(fh);
			return Optional.of(new AbstractMap.SimpleEntry<>(ia,fh));
		}
		return Optional.empty();
	}
	
		
	public void deleteProcessInstance(String processId) {
		// delete DNIs, TaskInstances, WorkflowInstance, JiraIssueExtension
		deleteById(processId, "JiraIssueExtensionsByProcessId", "jiraIssueExtension");
		deleteById(processId, "WorkflowInstanceByProcessId", "workflowInstance");
		deleteById(processId, "DecisionNodeInstanceByProcessId", "decisionNodeInstance"); 
		deleteById(processId, "WorkflowTaskByProcessId", "workflowTask");
		deleteById(processId, "ArtifactWrappersByProcessId", "wrapper");
		deleteById(processId, "AbstractArtifactsByProcessId", "abstractArtifact");
	}
	
	private void deleteById(String id, String queryName, String queryParameterName) {
		QueryResults results = kieSession.getQueryResults(queryName, id);
		for(QueryResultsRow row : results) {
			kieSession.delete(row.getFactHandle(queryParameterName));
			
			System.out.println(row.getFactHandle(queryParameterName));
		}
	}
	
	public void deleteAssessmentTicketState(String key) {
		QueryResults results = kieSession.getQueryResults("AssessmentTicketStatesByIssueKey", key);
		for(QueryResultsRow row : results) {
			kieSession.delete(row.getFactHandle("assessmentTicketState"));
		}
	}
	
	public List<Object> getContentExcluding(@SuppressWarnings("rawtypes") List<Class> excludeByClass) {
		return kieSession.getObjects().stream()
			.filter(obj -> !excludeByClass.contains(obj.getClass()))
			.collect(Collectors.toList());			
	}
	
	public List<Object> getContentLimitedTo(@SuppressWarnings("rawtypes") List<Class> includeByClass) {
		return kieSession.getObjects().stream()
			.filter(obj -> includeByClass.contains(obj.getClass()))
			.collect(Collectors.toList());			
	}
	
	
}
