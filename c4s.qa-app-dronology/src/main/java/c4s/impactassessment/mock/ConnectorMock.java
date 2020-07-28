package c4s.impactassessment.mock;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import core.base.Artifact;
import core.base.Relation;
import core.connector.IConnector;
import core.fieldValues.common.*;
import core.fieldValues.jira.*;
import core.fieldTypes.utility.FieldType;



public class ConnectorMock implements IConnector {

	@Override
	public Optional<Artifact> fetchAndMonitor(String mockNumber) {
		Artifact a = new Artifact();
		switch (mockNumber) {
			case "1":case "UAV-149":
				a = getArtifactMock("UAV-149", "high", "Task", "UI - Vaadin MouseOver event", "resolved", "123", "Frodo Baggins");
				break;
			case "2":case "UAV-1232":
				a = getArtifactMock("UAV-1232", "medium", "Task", "Write script to generate dummy routing table data", "in_progress", "None", "Frodo Baggins");
				break;
			case "3":case "UAV-1216":
				a = getArtifactMock("UAV-1216", "medium", "Sub-Task", "Fix web project set-up issues", "open", "None", "None");
				break;
			case "x":case "UAV-XXX":
				a = getArtifactMock("UAV-XXX", "medium", "Bug", "Bugs shall not be added to the QA-App", "open", "None", "Frodo Baggins");
				break;
			case "UAV-0":default:
				a = getArtifactMock("UAV-0", "low", "Task", "Standard Task", "open", "None", "Frodo Baggins");
		}
		return Optional.ofNullable(a);
	}
	
	private Artifact getArtifactMock(String id, String priority, String issueType, String summary, String status, String fixVersion, String assignee) {
		Artifact a = new Artifact();
		a.setId(id);
		@SuppressWarnings("rawtypes")
		Map<String, FieldType> fields = new HashMap<>();
		
		Priority prio = new Priority();
		prio.setData(new HashMap<String, Object>());
		prio.setName(priority);
		fields.put("priority", new FieldType<Priority>("priority", "1", prio));
		
		IssueType it = new IssueType();
		it.setData(new HashMap<String, Object>());
		it.setName(issueType);
		fields.put("issueType", new FieldType<IssueType>("issueType", "2", it));
		
		StringFieldValue str = new StringFieldValue();
		str.setValue(summary);
		fields.put("summary", new FieldType<StringFieldValue>("summary", "3", str));
		
		Status state = new Status();
		state.setData(new HashMap<String, Object>());
		state.setName(status);
		fields.put("status", new FieldType<Status>("status", "4", state));
		
		str = new StringFieldValue();
		str.setValue(fixVersion);
		fields.put("fixVersion", new FieldType<StringFieldValue>("fixVersion", "5", str));
		
		str = new StringFieldValue();
		str.setValue(assignee);
		fields.put("assignee", new FieldType<StringFieldValue>("assignee", "6", str));
		
		a.setFields(fields);
		
		return a;
	}
	
	private Relation getRelationToDesignDefinition(Artifact a) {
		Artifact a2 = getArtifactMock("UAV-1161", "high", "Design Definition", "Select map display", "resolved", "456", "Frodo Baggins");
		Relation r = new Relation();
		r.setSource(a);
		r.setDestination(a2);
		return r;
	}
	private Relation getRelationToAnotherDesignDefinition(Artifact a) {
		Artifact a2 = getArtifactMock("UAV-313", "high", "Design Definition", "Display map takeoff", "resolved", "789", "Frodo Baggins");
		Relation r = new Relation();
		r.setSource(a);
		r.setDestination(a2);
		return r;
	}
	
	private Relation getRelationToRequirement(Artifact a) {
		Artifact a2 = getArtifactMock("UAV-447", "low", "Requirement", "Choreographed takeoff", "resolved", "456", "Frodo Baggins");
		Relation r = new Relation();
		r.setRelationType("realizes");
		r.setSource(a);
		r.setDestination(a2);
		return r;
	}

	@Override
	public Optional<List<Artifact>> fetchDatabase() {
		// TODO Auto-generated method stub
		return null;
	}

}
