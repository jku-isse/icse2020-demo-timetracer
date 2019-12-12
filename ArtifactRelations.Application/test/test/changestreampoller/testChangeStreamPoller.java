package test.changestreampoller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.Before;
import org.junit.Test;

import application.changestreampoller.ChangeStreamPoller;
import application.changestreampoller.MonitoringScheduler;
import application.connector.JiraToNeo4J;

public class testChangeStreamPoller {

    private JiraToNeo4J j2n4j;
    
    @Before
    public void init() throws FileNotFoundException, NoSuchMethodException, SecurityException, IOException {
    	j2n4j = new JiraToNeo4J();   
    }
    

    @Test
	public void testMonitoring() throws InterruptedException, FileNotFoundException, NoSuchMethodException, SecurityException, IOException {
	
    	CountDownLatch lock = new CountDownLatch(1);
		
    	final ScheduledExecutorService scheduler =
			     Executors.newScheduledThreadPool(1);
		
    	MonitoringScheduler ms = new MonitoringScheduler();
		ChangeStreamPoller csp = new ChangeStreamPoller(1);
		
		csp.setJtn4j(j2n4j);
		csp.initLastCacheRefresh();
		
		ms.registerAndStartTask(csp);
		
		scheduler.schedule(new Runnable() {
		       public void run() { ms.stopAndRemoveTask(csp); lock.countDown(); }
		  	}, 10, java.util.concurrent.TimeUnit.MINUTES);
		
		lock.await(12, java.util.concurrent.TimeUnit.MINUTES);
		
	}
    
    
}
