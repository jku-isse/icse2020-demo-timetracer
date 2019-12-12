package test.connector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import application.connector.TimeTravelingConnector;
import core.base.Artifact;
import core.base.ControlLog;
import core.base.IdentifiableArtifact;
import core.connector.ITimeTravelingConnector;

public class testConnector {

	private ITimeTravelingConnector connector;
	private Artifact art1, art2, art2Rel;
	private IdentifiableArtifact art3, art4;
	
	@Before
	public void init() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		connector = new TimeTravelingConnector();
		
	}
	
	@Test
	public void testfetchItem() {	
			
		connector.travelTo(new Timestamp(1483278518000l));
		art1 = connector.fetchAndMonitor("UAV-533").get();		
		
		assertNotEquals(null, art1);
		assertEquals("UAV-533", art1.getIdInSource());

	}
	
	@Test
	public void testLazyLoad() {	
			
		//check if iterating through the relations is possible
		art1 = connector.fetchAndMonitor("UAV-1195").get();		
		
		assertNotEquals(null, art1);
		
		art1.getRelationsOutgoing().forEach(relation -> {
			art3 = relation.getDestination();
		});
		
		assertNotEquals(null, art1);
		
		art3.getRelationsOutgoing().forEach(relation -> {
			art4 = relation.getDestination();
		});
		
		assertNotEquals(null, art3);
		
		art4.getRelationsIncoming().forEach(relation -> {
			relation.getDestination().getProperties();
		});
		
		assertNotEquals(null, art4);
		
	}
	
	@Test 
	public void checkIfInstancesAreTheSame() {
				
		art1 = connector.fetchAndMonitor("UAV-862").get();	
		art2 = connector.fetchAndMonitor("UAV-861").get();
	
		art1.getRelationsOutgoing().forEach(r -> {
			
			if(r.getDestination().getId().equals("11022")) {
				art2Rel = (Artifact) r.getDestination();
			}
			
		});
		
		art2 = connector.fetchAndMonitor("UAV-861").get();
		
		System.out.println(art1);
		System.out.println(art2);
		System.out.println(art2Rel);

		assertEquals(true, art2Rel.equals(art2));
		
	}
	
	@Test
	public void testConnectorPastItem() {
		
		//recreating a past occurrence of the database 
		//and logging it to the file 'testConnector.txt'
		
		connector.travelTo(new Timestamp(1445685238000l));	
		List<Artifact> database = connector.fetchDatabase().get();			
		ControlLog log = new ControlLog("testConnector");
	
		database.forEach( ra -> {
			
			log.addLog("Key : " + ra.getIdInSource());
			log.addLog("Properties : " + ra.getProperties());	
			log.addLog("Incoming : " + ra.getRelationsIncoming());	
			log.addLog("Outgoing : " + ra.getRelationsOutgoing());	
			
		});
		
		log.writeToFile();
		
	}
	
	@Test
	public void testJumpToNextItemChange() {
		
		//timestamps taken from json-file of selected nodes
		connector.travelToNextChange(true, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-12 14:22:39"));
		connector.travelToNextChange(true, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-12 10:09:04"));	
		connector.travelToNextChange(true, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-11 12:20:21"));
		connector.travelToNextChange(true, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2018-08-30 14:44:09"));
		
		
		connector.travelToNextChange(false, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2018-08-30 14:44:09"));
		connector.travelToNextChange(false, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-11 12:20:21"));
		connector.travelToNextChange(false, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-12 10:09:04"));	
		connector.travelToNextChange(false, "UAV-1196","UAV-968");
		assertEquals(connector.getCurrentTime(), Timestamp.valueOf("2019-06-12 14:22:39"));
		
			
		List<Artifact> database = connector.fetchDatabase().get();		
		ControlLog log = new ControlLog("testConnector");
		
		database.forEach( ra -> {
			
			log.addLog("Key : " + ra.getIdInSource());
			log.addLog("Properties : " + ra.getProperties());	
			log.addLog("Incoming : " + ra.getRelationsIncoming());	
			log.addLog("Outgoing : " + ra.getRelationsOutgoing());	
			
		});
		
		log.writeToFile();
		
	}
	
}
