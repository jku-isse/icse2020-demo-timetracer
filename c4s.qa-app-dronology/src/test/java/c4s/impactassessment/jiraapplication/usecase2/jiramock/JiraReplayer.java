package c4s.impactassessment.jiraapplication.usecase2.jiramock;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.internal.json.IssueJsonParser;

import c4s.impactassessment.changecalculator.ChangeCalculator;
import c4s.impactassessment.jira.FakeJiraCache;
import c4s.impactassessment.utils.ChangeEvent;
import c4s.jiralightconnector.IssueAgent;

public class JiraReplayer {

	private static ChangeCalculator cc = new ChangeCalculator(null, new FakeJiraCache());	
	
	public static IssueAgent getOriginIssueAgent(String issueID) {
		try {
			IssueAgent latestAgent = new IssueAgent(loadIssue(issueID+".json"));
			List<ChangeEvent> newEvents = cc.getChangeEvents(latestAgent);
			IssueAgent v0Agent = IssueAgent.fromJson(newEvents.remove(0).body);
			return v0Agent;
		} catch (URISyntaxException | IOException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static IssueAgent getLatestIssueAgent(String issueID) throws URISyntaxException, IOException, JSONException {
		return new IssueAgent(loadIssue(issueID+".json"));
	}
	
	public static Issue loadIssue(String issueFileName) throws URISyntaxException, IOException, JSONException {
		URL resourceURL =JiraReplayer.class.getResource(issueFileName);
		Path path = Paths.get(resourceURL.toURI());
		String body = new String(Files.readAllBytes(path),Charset.forName("UTF-8"));
		JSONObject issueAsJson = new JSONObject(body);
		Issue issue = new IssueJsonParser().parse(issueAsJson);
		
		return issue;
	}
}
