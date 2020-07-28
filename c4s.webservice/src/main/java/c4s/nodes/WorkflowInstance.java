package c4s.nodes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class WorkflowInstance {
	
	@Id
	private String id;
	@Relationship(type="TASK_INSTANCES", direction=Relationship.OUTGOING)
	private Set<WorkflowTask> tasks = new HashSet<>();
	@Properties
	private Map<String, String> wfProps = new HashMap<>();

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Set<WorkflowTask> getTasks() {
		return tasks;
	}
	
	public Map<String, String> getWfProps(){
		return wfProps;
	}

}
