package test.artifactfactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import artifactFactory.factories.JiraArtifactFactory;
import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.ErrorLogger;
import core.base.IdentifiableArtifact;
import core.base.Relation;
import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.Array;
import core.fieldValues.common.Serializer;
import core.fieldValues.jira.Option;
import core.fieldValues.jira.Version;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.JiraArtifactService;
import neo4j.connector.Neo4JServiceManager;

public class BaseTesterJiraArtifactFactory {

	Map<String, Object> json;
	JiraArtifactFactory artifactFactory;
	Artifact artifact, artifact1, artifact2;
	
	@Before
	public void initServices() throws URISyntaxException, IOException, NoSuchMethodException, SecurityException {
		
		//initializing the artifactService
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 
		Neo4JServiceFactory.init(n4jm);
		
	    JiraArtifactService jiraArtifactService = new JiraArtifactService();
		JiraServiceFactory.init(jiraArtifactService);
		ErrorLoggerServiceFactory.init(new ErrorLogger());
	    
		artifactFactory = new JiraArtifactFactory(jiraArtifactService.getSchema(), jiraArtifactService.getNames());
		JiraArtifactFactoryServiceFactory.init(artifactFactory);
		
		artifact1 = artifactFactory.deserialize(Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact("11800"));
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchOptionArrayAndRevealInformation() {
				
		FieldType<Array<Serializer>> optionArrayField = ((FieldType<Array<Serializer>>) (artifact1.getFields().get("customfield_10100")));
		Array<Serializer> optionsField = optionArrayField.getValue();
		Object[] options = optionsField.getItems();
		StringBuilder sb = new StringBuilder();
		Option option;
		
		System.out.println();
		System.out.println("---------------fetchOptionArrayAndRevealInformation-------------------------");
		System.out.println();
		System.out.println(sb);
		
		for(int i=0; i<options.length; i++) {
			
			option = (Option) options[i];
			
			System.out.println(option.serialize());

			
		}

	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void fetchVersionArrayAndRevealInformation() {
				
		FieldType<Array<Serializer>> versionArrayField = ((FieldType<Array<Serializer>>) (artifact1.getFields().get("fixVersions")));
		Array<Serializer> versionsField = versionArrayField.getValue();
		Object[] versions = versionsField.getItems();
		StringBuilder sb = new StringBuilder();
		Version version;
		
		System.out.println();
		System.out.println("---------------fetchVersionArrayAndRevealInformation-------------------------");
		System.out.println();
		System.out.println(sb);
		
		for(int i=0; i<versions.length; i++) {
			
			version = (Version) versions[i];
			
			System.out.println(version.serialize());
			
		}
		
	}
	
	
	
	@Test
	public void fetchAllFieldsAndRevealInformation() {
				
		
		System.out.println();
		System.out.println("---------------fetchAllFieldsAndRevealInformation-------------------------");
		System.out.println();

		artifact1.getFields().forEach((x,y) -> {
			
				System.out.println( x + ":      " + y.getValue());
				
		});
		
		
		
	}
	
	
	@Test
	public void checkRelationsips() {
				
		
		System.out.println();
		System.out.println("---------------checkRelationships-------------------------");
		System.out.println();
		
		System.out.println("Outgoing");
		System.out.println(artifact1.getRelationsOutgoing());

		System.out.println("Incoming");
		System.out.println(artifact1.getRelationsIncoming());
		
	}
	
	@Test 
	public void serializeArtifact() {
		
		System.out.println();
		System.out.println("---------------serializeArtifact-------------------------");
		System.out.println();
	
		Map<String, Object> map = artifact1.serialize();
		System.out.println(map);
		
	}
	
	@Test 
	public void createChangeLogItem() throws JsonParseException, JsonMappingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		JiraArtifactService service = new JiraArtifactService();
		
		Map<String, Object> artifact = service.getArtifact("UAV-158");
					
		ArrayList<ChangeLogItem> items = artifactFactory.buildChangeLog(artifact);
		
		items.forEach(item -> {
				
			System.out.println(item.getArtifactId());
			System.out.println(item.getCorrespondingArtifactIdInSource());
			System.out.println(item.getInvolvedArtifactIds());
			
		});

				
		
	}
	
	@Test 
	public void testChangeLogItemTimestamp() throws JsonParseException, JsonMappingException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
		
		JiraArtifactService service = new JiraArtifactService();
		
		Map<String, Object> artifact = service.getArtifact("UAV-1287");
					
		ArrayList<ChangeLogItem> items = artifactFactory.buildChangeLog(artifact);
		
		items.forEach(item -> {
			
			System.out.println("-----------------------------------------------------------");
			System.out.println(item.getTimestamp());
		
		});			
		
	}
	
	@Test
	public void testLazyLoading() {
		
		IdentifiableArtifact a;

		a = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifactWithIdInSource("UAV-1287");
		System.out.println(a.doesRequireLazyLoad());
		
		Iterator<Relation> iterator = a.getRelationsOutgoing().iterator();
		Artifact b = a.getRelationsOutgoing().iterator().next().getDestination();
		System.out.println(b.getId());
		System.out.println(b.getRelationsOutgoing());
		System.out.println(b.getRelationsIncoming());

		iterator = b.getRelationsIncoming().iterator();
		iterator.next();
		Artifact c = iterator.next().getSource();
		System.out.println(c.getId());
		System.out.println(c.getRelationsOutgoing());
		System.out.println(c.getRelationsIncoming());

		iterator = c.getRelationsOutgoing().iterator();

		Artifact d = iterator.next().getDestination();
		while(!d.getId().equals("10128")) d = iterator.next().getDestination();
		System.out.println(d.getId());
		System.out.println(d.getRelationsOutgoing());
		System.out.println(d.getRelationsIncoming());
	}
	
	@Test
	public void deserialize() {
		Artifact a = (Artifact) Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifactWithIdInSource("UAV-1009");
		artifactFactory.deserialize(a);
				
		a.getFields().forEach((x,y) -> {
			
			System.out.println( x + ":      " + y.getValue());
			
	});
	
	
	}
	
}
