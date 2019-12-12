package neo4j.connector;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

public class Neo4JSessionFactory {
	    
    private static SessionFactory sessionFactory;

    protected Neo4JSessionFactory(Configuration config) {
    	if (sessionFactory == null) {
    		
    	}
    }

    public Session getNeo4jSession() {
        return sessionFactory.openSession();
    }
}