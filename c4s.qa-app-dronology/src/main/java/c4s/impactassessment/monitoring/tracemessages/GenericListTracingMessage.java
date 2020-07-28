package c4s.impactassessment.monitoring.tracemessages;

import java.util.ArrayList;
import java.util.List;

import c4s.analytics.monitoring.tracemessages.BaseTracingMessage;
import c4s.analytics.monitoring.tracemessages.CorrelationTuple;

public class GenericListTracingMessage extends BaseTracingMessage {

	List<String> body = new ArrayList<>();
	
	public GenericListTracingMessage(CorrelationTuple corrlationInfo, String eventType) {
		super(corrlationInfo, eventType);
	}

	public List<String> getBody() {
		return body;
	}

	public void setBody(List<String> body) {
		this.body = body;
	}


}
