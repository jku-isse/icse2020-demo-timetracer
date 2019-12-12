package test.jiraconnector;
import java.io.FileInputStream;
import java.net.URI;
import java.sql.Timestamp;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousSearchRestClient;

import c4s.jiralightconnector.IssueCache;
import jiraconnector.connector.AsynchronousIssueRestClientRawExtension;
import jiraconnector.connector.HttpClientFactory;



public class AsynchronousIssueRestClientRawExtensionTest {
	
	IssueCache cache  = null;
	HttpClient client;
	URI baseUri;
	
	@Before
	public void init() throws Exception {
		
		Properties props = new Properties(); 
		props.load(new FileInputStream("local-app.properties"));
		

		String uri =  props.getProperty("jiraServerURI");
		String username =  props.getProperty("jiraConnectorUsername");
		String pw =  props.getProperty("jiraConnectorPassword");
		
		baseUri = URI.create(uri);
		HttpClientFactory.init(uri, username, pw);
		client =  HttpClientFactory.createHttpClient();
		
	}
	
	@Test
	@SuppressWarnings("deprecation")
	public void fetchJson() {
		
		AsynchronousIssueRestClientRawExtension rawExtension = new AsynchronousIssueRestClientRawExtension(client, baseUri);
		System.out.println(rawExtension.getIssue("UAV-1288"));
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		ts.setMonth(6);
		System.out.println(rawExtension.fetchUpdatedIssuesSince(ts, 0, 50));
		
	}
	
	@Test
	public void fetchCertainAmountOfIssues() {
		
		AsynchronousIssueRestClientRawExtension rawExtension = new AsynchronousIssueRestClientRawExtension(client, baseUri);
		rawExtension.getIssues(0, 500);
		
	}
	
	@Test
	public void fetchIssueTypes() {
		
		AsynchronousIssueRestClientRawExtension rawExtension = new AsynchronousIssueRestClientRawExtension(client, baseUri);
		System.out.println(rawExtension.getIssueTypes());
		
	}
	
}
