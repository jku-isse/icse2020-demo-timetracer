package c4s.impactassessment.amqp;

import java.util.ArrayList;
import java.util.List;

import c4s.components.ProcessingState;

public class DeleteMessageProcessingState extends ProcessingState {

	List<String> wfIds = new ArrayList<>();
	
	public DeleteMessageProcessingState() {
		super.setProcessState(this.wfIds);
	}
	
	public List<String> getWorkflowIdsToDelete() {
		return wfIds;
	}	
	
}
