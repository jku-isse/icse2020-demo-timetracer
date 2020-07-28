package c4s.impactassessment.jiraapplication.usecase2.jiramock;

import java.io.IOException;
import java.net.URISyntaxException;

import org.codehaus.jettison.json.JSONException;
import org.junit.Before;
import org.junit.Test;

import c4s.jiralightconnector.IssueAgent;

public class JiraMockTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testLoadIssueFromFile() throws URISyntaxException, IOException, JSONException {
		String issueKey = "PVCSG-2";
		IssueAgent ia = JiraReplayer.getLatestIssueAgent(issueKey);
		assert(!ia.getFixVersions().isEmpty());
		assert(ia.getStatus() != null);
	}

}
