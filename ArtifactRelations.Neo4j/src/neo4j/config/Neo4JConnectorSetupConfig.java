package neo4j.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;

import com.google.inject.AbstractModule;

import core.persistence.BasicServices.ArtifactService;
import core.persistence.BasicServices.ChangeLogItemService;
import core.persistence.BasicServices.RelationMemoryService;
import core.persistence.BasicServices.ReplayableArtifactService;
import core.persistence.BasicServices.StatusService;
import neo4j.connector.BasicServiceImpl.ArtifactServiceImpl;
import neo4j.connector.BasicServiceImpl.ChangeLogItemServiceImpl;
import neo4j.connector.BasicServiceImpl.RelationMemoryServiceImpl;
import neo4j.connector.BasicServiceImpl.ReplayableArtifactServiceImpl;
import neo4j.connector.BasicServiceImpl.StatusServiceImpl;

public class Neo4JConnectorSetupConfig extends AbstractModule{

	protected SessionFactory sessionFactory;
	
	public Neo4JConnectorSetupConfig() {
		try {
			
			Properties props = new Properties();
			props.load(new FileInputStream("local-app.properties"));
			
			String user = props.getProperty("neo4jUser");
			String pw = props.getProperty("neo4jPassword");
			String uri = props.getProperty("neo4jURI");

			Configuration config = new Configuration.Builder()
					.uri(uri)
					.credentials(user, pw)
					.build();
			
			sessionFactory = new SessionFactory(config, "core.base");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected void configure() {
		bind(SessionFactory.class).toInstance(sessionFactory);
		bind(ArtifactService.class).to(ArtifactServiceImpl.class).asEagerSingleton();
		bind(ChangeLogItemService.class).to(ChangeLogItemServiceImpl.class).asEagerSingleton();
		bind(RelationMemoryService.class).to(RelationMemoryServiceImpl.class).asEagerSingleton();
		bind(ReplayableArtifactService.class).to(ReplayableArtifactServiceImpl.class).asEagerSingleton();
		bind(StatusService.class).to(StatusServiceImpl.class).asEagerSingleton();
	}

}
