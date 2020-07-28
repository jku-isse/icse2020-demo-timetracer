package c4s.impactassessment.monitoring;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import c4s.analytics.monitoring.tracemessages.BaseTracingMessage;


public class AnalyticsLogger  {

	private Logger log;
	
	AnalyticsLogger(String loggerPostfix) {
		this.log = LogManager.getLogger("c4s.analytics."+loggerPostfix);
	}
	
	protected void logTraceMessage(BaseTracingMessage msg, Level level) {
		log.log(level, new ObjectMessage(msg));
	}
	
	protected void logInfoTraceMessage(BaseTracingMessage msg) {
		logTraceMessage(msg, Level.INFO);
	}
}
