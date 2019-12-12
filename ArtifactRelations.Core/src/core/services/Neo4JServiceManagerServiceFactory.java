package core.services;

import core.neo4j.INeo4JServiceManager;

public class Neo4JServiceManagerServiceFactory {

	private static boolean isInitialized = false;
	private static INeo4JServiceManager neo4JServiceManager;
	
	public static INeo4JServiceManager getJiraArtifactService() {
		if (!isInitialized)
			throw new RuntimeException("ServiceFactory not initalized");
		return neo4JServiceManager;
	}
	
	public static void init(INeo4JServiceManager neo4JServiceManager) {
		isInitialized = true;
		Neo4JServiceManagerServiceFactory.neo4JServiceManager = neo4JServiceManager;
	}
	
}
