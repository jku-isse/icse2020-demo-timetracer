package jiraconnector.connector;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.ws.rs.core.UriBuilder;

import org.codehaus.jettison.json.JSONException;

import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos;
import com.atlassian.jira.rest.client.internal.async.AbstractAsynchronousRestClient;
import com.google.common.collect.Iterables;

import core.jira.IJiraRestClient;
import core.services.ErrorLoggerServiceFactory;
import io.atlassian.util.concurrent.Promise;

public class AsynchronousIssueRestClientRawExtension extends AbstractAsynchronousRestClient implements IJiraRestClient{

    private static final EnumSet<Expandos> DEFAULT_EXPANDS = EnumSet.of(Expandos.NAMES, Expandos.CHANGELOG, Expandos.SCHEMA, Expandos.TRANSITIONS);
    private static final Function<IssueRestClient.Expandos, String> EXPANDO_TO_PARAM = from -> from.name().toLowerCase();

    private HttpClient httpClient;
	private URI baseUri; 
	
	public AsynchronousIssueRestClientRawExtension(HttpClient client, URI baseUri) {
		super(client);	
		this.baseUri = baseUri;
		this.httpClient = client; 
	}
	
	public String getIssue(final String issueKey) {
		final UriBuilder uriBuilder = UriBuilder.fromUri(baseUri);
        final Iterable<Expandos> expands = Iterables.concat(DEFAULT_EXPANDS);
        uriBuilder.path("rest").path("api").path("2").path("issue").path(issueKey).queryParam("expand",
                StreamSupport.stream(expands.spliterator(), false).map(EXPANDO_TO_PARAM).collect(Collectors.joining(",")));
       
        return getAndParse(uriBuilder.build()).claim();
	}
	
	public String fetchUpdatedIssuesSince(Timestamp startFrom, int startAt, int maxResults) {
		
		LocalDateTime serverDate = startFrom.toLocalDateTime();
		String date = serverDate.getYear() + "-" + serverDate.getMonthValue() + "-" + serverDate.getDayOfMonth();
		String time  = serverDate.getHour() + ":" + serverDate.getMinute(); 	
		
		URI uri = URI.create(baseUri + "/rest/api/2/search?jql=updated%3E%27" + date + "%20" + time + "%27&expand=schema,names,transitions,changelog&startAt=" + startAt + "&maxResults=" + maxResults);       		
		return getAndParse(uri).claim();
	}
	
	public String getIssues(int startAt, int maxResults) {		
		URI uri = URI.create(baseUri + "/rest/api/2/search?expand=changelog&startAt=" + startAt + "&maxResults=" + maxResults);       
		return getAndParse(uri).claim();		
	}
	
	public String getNamesAndScheme() {		
		URI uri = URI.create(baseUri + "/rest/api/2/search?expand=names,schema&startAt=0&maxResults=1");       
		return getAndParse(uri).claim();		
	}
	
	public String getStatus(String id) {
		URI uri = URI.create(baseUri + "/rest/api/2/status/" + id);       
		return executeAnLogExceptions(uri, id);	
	}
	
	public String getUser(String key) {
		URI uri = URI.create(baseUri + "/rest/api/2/user?key=" + key);       
		return executeAnLogExceptions(uri, key);	
	}
	
	public String getProject(String id) {
		URI uri = URI.create(baseUri + "/rest/api/2/project/" + id);       
		return executeAnLogExceptions(uri, id);	
	}
	
	public String getIssueType(String id) {	
		URI uri = URI.create(baseUri + "/rest/api/2/issuetype/" + id); 		
		return executeAnLogExceptions(uri, id);	
	}
	
	public String getVersion(String id) {
		URI uri = URI.create(baseUri + "/rest/api/2/version/" + id);       
		return getAndParse(uri).claim();		
	}
	
	public String getPriority(String id) {
		URI uri = URI.create(baseUri + "/rest/api/2/priority/" + id);       
		return getAndParse(uri).claim();		
	}
	
	public String getOption(String id) {
		URI uri = URI.create(baseUri + "/rest/api/2/customFieldOption/" + id);       
		return getAndParse(uri).claim();		
	}
	
	public String getIssueTypes() {
		URI uri = URI.create(baseUri + "/rest/api/2/issueLinkType");       
		return getAndParse(uri).claim();		
	}
	
	protected final Promise<String> getAndParse(final URI uri) {
	        return callAndParse(httpClient.newRequest(uri).setAccept("application/json").get());
	}
	 
	protected final Promise<String> callAndParse(final ResponsePromise responsePromise) {
	        final ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
	            @Override
	            public String handle(Response response) throws JSONException, IOException {
	                final String body = (String) response.getEntity();
	                return body;
	            }
	        };
	        return callAndParse(responsePromise, responseHandler);
	}		
		
	private String executeAnLogExceptions(URI uri, String id) {
		try {
			return getAndParse(uri).claim();
		} catch(RuntimeException e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "id: " + id + " = error: " + e.getMessage());
			return null;
		}
	}

}
