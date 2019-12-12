package test.deserialize;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import application.connector.Connector;
import artifactFactory.factories.JiraArtifactFactory;
import core.artifactFactory.factories.IArtifactFactory;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.base.IdentifiableArtifact;
import core.fieldValues.common.Array;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;

public class testDeserialize {
	
	private final String NAMES_MAP = "names", SCHEMA_MAP ="schema";
	
	Connector connector;
	IdentifiableArtifact iArtifact;
	Artifact artifact;
	IArtifactFactory artifactFactory;
	
	@Before
	public void init() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {

		connector = new Connector();	
		artifactFactory = JiraArtifactFactoryServiceFactory.getJiraArtifactFactory();
	}
	
	@SuppressWarnings({"rawtypes"})
	@Test 
	public void deserializeArtifact() throws Exception {
		
		System.out.println();
		System.out.println("---------------deserializeArtifact-------------------------");
		System.out.println();
		
		iArtifact = connector.fetchAndMonitor("11800").get();
		System.out.println(iArtifact.getProperties());

		artifact = artifactFactory.deserialize(iArtifact);
		System.out.println(((Array) artifact.getFields().get("fixVersions").getValue()).getItems());
		
	}
}

