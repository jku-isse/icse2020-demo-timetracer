package c4s.impactassessment.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Neo4JReset {

	private static Injector injector;	
	private static  Properties props = new Properties();
	
	public static void main(String[] args) throws FileNotFoundException, IOException {				 
		props.load(new FileInputStream("local-app.properties"));
		purgeDB();
	}
		
	private static void purgeDB() {
			injector = Guice.createInjector(new Neo4JConnectorConfig(props));
			SessionFactory nsf = injector.getInstance(SessionFactory.class);
			Session session = nsf.openSession(); 
			session.purgeDatabase();	
			System.exit(0);
	}

}
