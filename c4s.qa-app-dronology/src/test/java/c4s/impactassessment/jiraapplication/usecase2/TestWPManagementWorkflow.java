package c4s.impactassessment.jiraapplication.usecase2;

import java.util.UUID;

import com.google.inject.Inject;

import c4s.impactassessment.workflowmodel.ArtifactType;
import c4s.impactassessment.workflowmodel.DecisionNodeDefinition;
import c4s.impactassessment.workflowmodel.DefaultBranchDefinition;
import c4s.impactassessment.workflowmodel.DefaultWorkflowDefinition;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.TaskDefinition;
import c4s.impactassessment.workflowmodel.WorkflowDefinition;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

public class TestWPManagementWorkflow extends DefaultWorkflowDefinition implements WorkflowDefinition {

	public static final String WORKPACKAGE_CHANGE_MANAGEMENT_WORKFLOW_TYPE = "WORKPACKAGE_CHANGE_MANAGEMENT_WORKFLOW_TYPE";	
	
	public static final String TASKTYPE_SSS_REVISING = "SSS_REVISING";
	public static final String TASKTYPE_SSDD_REVISING = "SSDD_REVISING";
	public static final String TASKTYPE_FEDD_REVISING = "FEDD_REVISING";
	public static final String TASKTYPE_SSS_TO_SRS_MAPPING = "SSS_TO_SRS_MAPPING";
	
	public static final String INPUT_ROLE_WPTICKET = "INPUT_ROLE_WPTICKET";
	public static final String OUTPUT_ROLE_SSSREVIEW = "OUTPUT_ROLE_SSSREVIEW";
	public static final String OUTPUT_ROLE_SSDDREVIEW = "OUTPUT_ROLE_SSDDREVIEW";
	public static final String OUTPUT_ROLE_FEDDREVIEW = "OUTPUT_ROLE_FEDDREVIEW";
	public static final String OUTPUT_ROLE_SSS_SRS_MAPPINGREVIEW = "OUTPUT_ROLE_SSS_SRS_MAPPINGREVIEW";
	public static final String ARTIFACT_TYPE_JIRA_TICKET = "ARTIFACT_TYPE_JIRA_TICKET";
	public static final String ARTIFACT_TYPE_RESOURCE_LINK = "ARTIFACT_TYPE_RESOURCE_LINK";
	
	//private List<TaskDefinition> taskDefinitions = new ArrayList<TaskDefinition>();
	//private List<DecisionNodeDefinition> dnds = new ArrayList<DecisionNodeDefinition>();
	
	

	public TestWPManagementWorkflow() {
		super(WORKPACKAGE_CHANGE_MANAGEMENT_WORKFLOW_TYPE);		
	}

	@Inject
	public void initWorkflowSpecification() {
		TaskDefinition tdSSS = getSSSRevisingTaskDefinition();
		getWorkflowTaskDefinitions().add(tdSSS);
		TaskDefinition tdSSDD = getSSDDRevisingTaskDefinition();
		getWorkflowTaskDefinitions().add(tdSSDD);
		TaskDefinition tdFEDD = getFEDDRevisingTaskDefinition();
		getWorkflowTaskDefinitions().add(tdFEDD);
		TaskDefinition tdSSS2SRS = getSSS_SRS_MappingTaskDefinition();
		getWorkflowTaskDefinitions().add(tdSSS2SRS);

		getDecisionNodeDefinitions().add(getSSSKickOff(tdSSS));
		getDecisionNodeDefinitions().add(getSSS2SSDD(tdSSS, tdSSDD));
		getDecisionNodeDefinitions().add(getSSDD2FEDDandSRSMapping(tdSSDD, tdFEDD, tdSSS2SRS));
	}
	
	private TaskDefinition getSSSRevisingTaskDefinition() {
		TaskDefinition td = new TaskDefinition(TASKTYPE_SSS_REVISING, this);
		td.getExpectedInput().put(INPUT_ROLE_WPTICKET, new ArtifactType(ARTIFACT_TYPE_JIRA_TICKET));
		td.getExpectedOutput().put(OUTPUT_ROLE_SSSREVIEW, new ArtifactType(ARTIFACT_TYPE_RESOURCE_LINK));
		return td;
	}
	
	private TaskDefinition getSSDDRevisingTaskDefinition() {
		TaskDefinition td = new TaskDefinition(TASKTYPE_SSDD_REVISING, this);
		td.getExpectedInput().put(INPUT_ROLE_WPTICKET, new ArtifactType(ARTIFACT_TYPE_JIRA_TICKET));
		td.getExpectedOutput().put(OUTPUT_ROLE_SSDDREVIEW, new ArtifactType(ARTIFACT_TYPE_RESOURCE_LINK));
		return td;
	}
	
	private TaskDefinition getFEDDRevisingTaskDefinition() {
		TaskDefinition td = new TaskDefinition(TASKTYPE_FEDD_REVISING, this);
		td.getExpectedInput().put(INPUT_ROLE_WPTICKET, new ArtifactType(ARTIFACT_TYPE_JIRA_TICKET));
		td.getExpectedOutput().put(OUTPUT_ROLE_FEDDREVIEW, new ArtifactType(ARTIFACT_TYPE_RESOURCE_LINK));
		return td;
	}
	
	private TaskDefinition getSSS_SRS_MappingTaskDefinition() {
		TaskDefinition td = new TaskDefinition(TASKTYPE_SSS_TO_SRS_MAPPING, this);
		td.getExpectedInput().put(INPUT_ROLE_WPTICKET, new ArtifactType(ARTIFACT_TYPE_JIRA_TICKET));
		td.getExpectedOutput().put(OUTPUT_ROLE_SSS_SRS_MAPPINGREVIEW, new ArtifactType(ARTIFACT_TYPE_RESOURCE_LINK));
		return td;
	}
	
	private DecisionNodeDefinition getSSSKickOff(TaskDefinition tdSSS) {
		DecisionNodeDefinition dn = new DecisionNodeDefinition("SSSKickOff", this, DecisionNodeDefinition.NO_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE);
		dn.addOutBranchDefinition(new DefaultBranchDefinition("SSSin", tdSSS, false, true, dn));		
		return dn;
	}
	
	private DecisionNodeDefinition getSSS2SSDD(TaskDefinition tdSSS, TaskDefinition tdSSDD) {
		DecisionNodeDefinition dn = new DecisionNodeDefinition("SSS2SSDD", this, DecisionNodeDefinition.HAVING_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE);
		dn.addInBranchDefinition(new DefaultBranchDefinition("SSSout", tdSSS, false, false, dn));
		dn.addOutBranchDefinition(new DefaultBranchDefinition("SSDDin", tdSSDD, false, true, dn));
		return dn;
	}
	
	private DecisionNodeDefinition getSSDD2FEDDandSRSMapping(TaskDefinition tdSSDD, TaskDefinition tdFEDD, TaskDefinition tdSSS2SRSMapping) {
		DecisionNodeDefinition dn = new DecisionNodeDefinition("SSDD2FEDDandSRSMapping", this, DecisionNodeDefinition.HAVING_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE, DecisionNodeDefinition.NO_EXTERNAL_RULE);
		dn.addInBranchDefinition(new DefaultBranchDefinition("SSDDout", tdSSDD, false, false, dn));
		dn.addOutBranchDefinition(new DefaultBranchDefinition("FEDDin", tdFEDD, false, true, dn));
		dn.addOutBranchDefinition(new DefaultBranchDefinition("SRS2SSSMappingin", tdSSS2SRSMapping, false, true, dn));
		return dn;
	}
		

	
	


	

	@Override
	public WorkflowInstance createInstance(String withOptionalId) {
		String wfid = withOptionalId != null ? withOptionalId : this.id+"#"+UUID.randomUUID().toString();
		WorkflowInstance wfi = new WorkflowInstance(wfid, this, pub);
		
		return wfi;
	}

	public static ResourceLink getLink(String href, String title) {
		return new ResourceLink(ARTIFACT_TYPE_RESOURCE_LINK, href, "self", "", "application/json", title);
	}
	
}
