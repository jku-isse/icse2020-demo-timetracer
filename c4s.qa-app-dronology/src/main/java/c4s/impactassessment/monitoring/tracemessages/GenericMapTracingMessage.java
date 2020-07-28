package c4s.impactassessment.monitoring.tracemessages;

import java.util.HashMap;
import java.util.Map;

import c4s.analytics.monitoring.tracemessages.BaseTracingMessage;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;

public class GenericMapTracingMessage extends BaseTracingMessage {

	Map<String, String> body = new HashMap<String, String>();
	
	public GenericMapTracingMessage(CorrelationTuple corrlationInfo, String eventType) {
		super(corrlationInfo, eventType);
	}

	public Map<String, String> getBody() {
		return body;
	}

	public void setBody(Map<String, String> body) {
		this.body = body;
	}

	public GenericMapTracingMessage addFluent(String key, String value) {
		body.put(key, value);
		return this;
	}
}
