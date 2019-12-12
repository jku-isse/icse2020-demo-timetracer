package core.services;

import core.neo4j.INeo4JServiceManager;

public class Neo4JServiceFactory {

	private static boolean isInitialized = false;
	private static INeo4JServiceManager n4jm;
	
	public static INeo4JServiceManager getNeo4JServiceManager() {
		if (!isInitialized)
			throw new RuntimeException("ServiceFactory not initalized");
		return n4jm;
	}
	
	public static void init(INeo4JServiceManager n4jm) {
		isInitialized = true;
		Neo4JServiceFactory.n4jm = n4jm;
	}

}

