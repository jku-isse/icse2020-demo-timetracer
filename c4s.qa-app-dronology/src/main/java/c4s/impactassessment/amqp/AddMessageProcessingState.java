package c4s.impactassessment.amqp;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Set;

import c4s.components.ProcessingState;

public class AddMessageProcessingState extends ProcessingState {

//	protected Set<SimpleEntry<JamaItem, String>> tuples = new HashSet<>();
	protected int filterId = -1;
//	
//	public AddMessageProcessingState(Set<SimpleEntry<JamaItem, String>> tuples) {
//		if (tuples != null) {
//			this.tuples = tuples;
//			super.setProcessState(tuples);
//		}		
//	}
	
	public AddMessageProcessingState(int filterId) {
		this.filterId = filterId;
		super.setProcessState(filterId);
	}
	
//	public Set<SimpleEntry<JamaItem, String>> getTuplesToAdd() {
//		return tuples;
//	}	
	
	public int getFilterId() {
		return filterId;
	}
	
}
