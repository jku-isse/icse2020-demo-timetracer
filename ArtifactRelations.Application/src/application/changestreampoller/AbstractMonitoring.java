package application.changestreampoller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.time.ZonedDateTime;

import com.google.inject.BindingAnnotation;

import c4s.analytics.monitoring.tracemessages.CorrelationTuple;


public abstract class AbstractMonitoring implements Runnable{

	protected String id;
	protected int intervalInMinutes = 1; //default value, once per hour
	protected ZonedDateTime lastSuccessfulUpdate = ZonedDateTime.now();
	protected CorrelationTuple runDueTo = new CorrelationTuple("DefaultMonitoring"+System.currentTimeMillis(), "DEFAULT_JIRA_MONITOR");
	
	public CorrelationTuple getRunDueTo() {
		return runDueTo;
	}

	public void setRunDueTo(CorrelationTuple runDueTo) {
		this.runDueTo = runDueTo;
	}
	
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.FIELD, ElementType.PARAMETER})
	@BindingAnnotation
	public @interface PollInterval {}
	
	public int getIntervalInMinutes() {
		return intervalInMinutes;
	}
	
	public void setInterval(int intervalInMinutes) {
		this.intervalInMinutes = intervalInMinutes;
	}
	
	public String getId() {
		return id;
	}
	
	public Duration getDurationSinceLastUpdate() {
		return Duration.between(ZonedDateTime.now(), lastSuccessfulUpdate);		
	}
}
