package core.services;

import core.artifactFactory.factories.IArtifactFactory;

public class JiraArtifactFactoryServiceFactory {

	private static boolean isInitialized = false;
	private static IArtifactFactory artifactFactory;
	
	public static IArtifactFactory getJiraArtifactFactory() {
		if (!isInitialized)
			throw new RuntimeException("ServiceFactory not initalized");
		return artifactFactory;
	}
	
	public static void init(IArtifactFactory artifactFactory) {
		isInitialized = true;
		JiraArtifactFactoryServiceFactory.artifactFactory = artifactFactory;
	}
	
}
