package core.services;

import core.persistence.IJiraArtifactService;

public class JiraServiceFactory {

	private static boolean isInitialized = false;
	private static IJiraArtifactService jiraArtifactService;
	
	public static IJiraArtifactService getJiraArtifactService() {
		if (!isInitialized)
			throw new RuntimeException("ServiceFactory not initalized");
		return jiraArtifactService;
	}
	
	public static void init(IJiraArtifactService jiraArtifactService) {
		isInitialized = true;
		JiraServiceFactory.jiraArtifactService = jiraArtifactService;
	}
	
}

