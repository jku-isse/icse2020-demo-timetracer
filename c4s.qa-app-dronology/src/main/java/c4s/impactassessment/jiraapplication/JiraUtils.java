package c4s.impactassessment.jiraapplication;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.jiralightconnector.IssueAgent;
import core.base.Artifact;
import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.StringFieldValue;
import core.fieldValues.jira.IssueType;

public class JiraUtils {

	public static ResourceLink getResourceLink(IssueAgent ia) {
		String context = ia.getIssueType().getName();
		String href = ia.getSelf().toString();
		String asField = ia.getIssueType().getId()+"";
		String title = ia.getKey();
		return new ResourceLink(context, href, "self", asField ,"html", title);
	}
	
	
	public static ResourceLink getHumanReadableResourceLinkEndpoint(IssueAgent ia) {
		String context = ia.getIssueType().getName();
		URI uri = ia.getSelf();
		String port = uri.getPort() == -1 ? "" : uri.getPort()+"";
		String href = uri.getScheme()+"://"+uri.getHost()+port+"/browse/"+ia.getKey();
		String asField = ia.getIssueType().getId()+"";
		String title = ia.getKey();
		return new ResourceLink(context, href, "self", asField ,"html", title);
	}
	
	@SuppressWarnings("unchecked")
	public static ResourceLink getHumanReadableResourceLinkEndpoint(Artifact a) {
		@SuppressWarnings("rawtypes")
		Map<String, FieldType> artifactFields = a.getFields();
		FieldType<StringFieldValue> summary = artifactFields.get("summary");
		Optional<String> context = summary.getValue().getValue();
//		URI uri = a.getSelf();
//		String port = uri.getPort() == -1 ? "" : uri.getPort()+"";
//		String href = uri.getScheme()+"://"+uri.getHost()+port+"/browse/"+a.getId();
		FieldType<IssueType> issueType = artifactFields.get("issuetype");
		Optional<String> asField = issueType.getValue().getName();
		String title = a.getIdInSource();
		return new ResourceLink(context.orElse("failed"), a.getOrigin(), "self", asField.orElse("failed") ,"html", title);
	}
	
	// from this: href:
	// https://jiraclone.frequentis.frq/rest/api/latest/issue/416617
	// to:
	// https://jiraclone.frequentis.frq/browse/PVCSG-9
}
