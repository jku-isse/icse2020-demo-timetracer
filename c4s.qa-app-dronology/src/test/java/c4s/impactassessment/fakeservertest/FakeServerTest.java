package c4s.impactassessment.fakeservertest;

/*import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.google.gson.JsonObject;
import com.google.inject.Guice;
import com.google.inject.Injector;

import c4s.impactassessment.app.ImpactAssessmentAppSetup;

@SpringBootApplication
public class FakeServerTest {

	private ConfigurableApplicationContext ctx;
	
	@Before
	public void setUp() throws Exception {
		ctx = SpringApplication.run(FakeServerTest.class, new String[0]);
	}

	@After
	public void tearDown() throws Exception {
		ctx.close();
	}

	@Test
	public void test() throws InterruptedException, FileNotFoundException, IOException {
		ComponentConfig cc = new ComponentConfig(null);
		Injector injector = Guice.createInjector(cc);
		//injector.getInstance();
		ImpactAssessmentAppSetup iaas = new ImpactAssessmentAppSetup();
		injector.injectMembers(iaas);
		
		String data = "help\nadd\nTESTISSUE\nstart"; //Name doesn't matter since FakeServer provides issue anyways
		InputStream input = new ByteArrayInputStream(data.getBytes("UTF-8"));
		InputStream old = System.in;
		try {
		    System.setIn(input);
		    iaas.init(cc.getConfiguration());
		} finally {
		    System.setIn(old);
		}
		Thread.sleep(4000L);
	
		Properties props = cc.getProps();
		CouchDbProperties properties = new CouchDbProperties()
				  .setDbName("testimpactassessmentnotification")
				  .setCreateDbIfNotExist(true)
				  .setProtocol("http")
				  .setHost(props.getProperty("notificationCouchDBip", "127.0.0.1"))
				  .setPort(Integer.parseInt(props.getProperty("notificationCouchDBport", "5984")))
				  .setUsername(props.getProperty("unitTestCouchDBuser"))
				  .setPassword(props.getProperty("unitTestCouchDBpassword"))
				  .setMaxConnections(100)
				  .setConnectionTimeout(0);
		CouchDbClient dbClient = new CouchDbClient(properties);
		
		try {
			int count = dbClient.view("_all_docs").query(JsonObject.class).size();
		
			assertEquals(5, count);
		
		
			dbClient.context().deleteDB("testimpactassessmentnotification", "delete database");
			dbClient.context().deleteDB("testimpactassessmentrulelogs", "delete database");
		} catch(Exception e) {
			System.err.println("==================================================");
			System.err.println("Warning: Temporary Databases could not be deleted!");
			System.err.println("Remove manually before next unit test run!");
			System.err.println("==================================================");
		}
		
		dbClient.shutdown();
		dbClient.close();
	}

}*/
