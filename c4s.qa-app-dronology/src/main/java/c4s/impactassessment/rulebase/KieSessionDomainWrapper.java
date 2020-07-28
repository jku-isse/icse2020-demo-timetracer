package c4s.impactassessment.rulebase;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.kie.api.runtime.rule.FactHandle;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;
import c4s.impactassessment.workflowmodel.WorkflowInstance;
import c4s.impactassessment.workflowmodel.constraints.ConstraintTrigger;
import core.base.Artifact;

public interface KieSessionDomainWrapper {

//	public Optional<FactHandle> updateJamaItemOnlyIfExists(CorrelationTuple correlation, JamaItem jamaItem);
//	public FactHandle insertJamaItemIfNotExists(CorrelationTuple correlation, JamaItem jamaItem);
//	public FactHandle insertOrUpdateJamaItem(CorrelationTuple correlation, JamaItem jamaItem);
//	public boolean deleteJamaItemIfExists(CorrelationTuple correlation, int itemId);
	

	public Optional<Map.Entry<Artifact, FactHandle>> existsArtifact(String issueKey);
	public Optional<FactHandle> updateArtifactOnlyIfExists(CorrelationTuple correlation, Artifact ia);
	public FactHandle insertArtifactIfNotExists(CorrelationTuple correlation, Artifact artifact);
	public FactHandle insertOrUpdateArtifact(CorrelationTuple correlation, Artifact artifact);
	public Optional<Artifact> deleteArtifactIfExists(CorrelationTuple correlation, String issueKey);
	
	public int fireAllRules();
	
	public void setGlobals(Map<String,Object> globals);
	public FactHandle insertConstraintTrigger(ConstraintTrigger ct);
	public Optional<Map.Entry<WorkflowInstance, FactHandle>> getWorkflowInstanceById(String id);
	public List<Map.Entry<WorkflowInstance, FactHandle>> getAllWorkflowInstances();
	
	public void deleteProcessInstance(String processId);
	public void deleteIssueAgent(String key);
//	public void deleteJamaItem(String itemId);
	public String printKB();
	
}
