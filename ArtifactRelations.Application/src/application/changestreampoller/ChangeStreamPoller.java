package application.changestreampoller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import application.connector.JiraToNeo4J;
import core.base.IdentifiableStatus;
import core.services.ErrorLoggerServiceFactory;
import core.services.Neo4JServiceFactory;

public class ChangeStreamPoller extends AbstractMonitoring implements Runnable{

	private static Logger log = LogManager.getLogger("ChangeStreamPoller");
		
	private IdentifiableStatus status;
	private JiraToNeo4J jtn4j;
	
	@Inject
	public ChangeStreamPoller(@PollInterval int pollIntervalInMinutes) {
		super.lastSuccessfulUpdate = ZonedDateTime.now(); // will be override in initLastCacheRefresh, otherwise assume cache is empty and fetching pulls in latest versions
		super.setInterval(pollIntervalInMinutes);
	}
	
	@Inject
	public void initLastCacheRefresh() {
		status = Neo4JServiceFactory.getNeo4JServiceManager().getStatusService().fetchStatus();
		Timestamp timestamp = new Timestamp(status.getLastUpdate());
		super.lastSuccessfulUpdate = timestamp.toLocalDateTime().atZone(ZoneId.systemDefault());
	}

	@Override
	public void run() {
		
		ZonedDateTime pollTime = ZonedDateTime.now().minus(1l, ChronoUnit.MINUTES); // one minute overlap just to be on the save side.
		log.debug("Fetching at: "+pollTime.toString());
		
		try {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "ChangeStreamPoller: start Update");
			jtn4j.fetchDatabaseDelta();
			super.lastSuccessfulUpdate = pollTime;
			status.setLastUpdate(System.currentTimeMillis());
			Neo4JServiceFactory.getNeo4JServiceManager().getStatusService().push(status);
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "ChangeStreamPoller: completed Update");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | IOException e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "ChangeStreamPoller: Fetching the updates from Jira was not successful");
		}
				
	}
	
	public void setJtn4j(JiraToNeo4J jtn4j) {
		this.jtn4j = jtn4j;
	}
	
}
