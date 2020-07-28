package c4s.impactassessment.jira;

import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

public class TestJiraUtil {

	@Test
	public void testJiraIssueBrowseLink() {
		URI uri = URI.create("https://jiraclone.frequentis.frq/rest/api/latest/issue/416617");
		String uri2 = transformURI(uri, "PVCSG-9");
		System.out.println(uri2);
		assertEquals("https://jiraclone.frequentis.frq/browse/PVCSG-9", uri2);
	}
	
	private String transformURI(URI uri, String key) {
		String port = uri.getPort() == -1 ? "" : uri.getPort()+"";
		String href = uri.getScheme()+"://"+uri.getHost()+port+"/browse/"+key;
		return href;
	}

}
