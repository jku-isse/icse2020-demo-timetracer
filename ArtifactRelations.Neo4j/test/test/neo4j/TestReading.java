package test.neo4j;

import org.junit.Before;
import org.junit.Test;

import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.base.IdentifiableRelation;
import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import neo4j.connector.Neo4JServiceManager;

public class TestReading {

	Neo4JServiceManager neo4jManager;
	  
	@Before
	public void setUp() throws Exception {	
		//establishing connection
		neo4jManager = new Neo4JServiceManager();
	}
	
	
	@Test
	public void fetchArtifactFromDatabase() {
		IdentifiableArtifact artifact =  neo4jManager.getArtifactService().getArtifact("11067", 1);
		System.out.println(artifact.getProperties());
	} 
	
	@Test
	public void fetchAllChangeLogItemsOfAnArtifact() {
		ChangeLogItemService service = neo4jManager.getChangeLogItemService();
		Iterable<ChangeLogItem> items = service.findAllChangeLogsForAnArtifact("UAV-211");
		items.forEach(x -> {
			System.out.println("--------------------------------------------------------------------");
			System.out.println(x.getId());
			System.out.println(x.getCorrespondingArtifactId());
			System.out.println(x.getCorrespondingArtifactIdInSource());
			System.out.println(x.getTimeCreated());
		});
	} 
	
	@Test
	public void fetchRelation() {
		
		ArtifactService service = neo4jManager.getArtifactService();
		
		IdentifiableRelation relation = service.getRelationWithKey("UAV-1003", "UAV-1002");
		
		System.out.println("-----------------------fetchRelation--------------------");
		System.out.println(relation);

	} 
	

	
}
