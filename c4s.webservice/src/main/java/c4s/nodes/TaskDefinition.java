package c4s.nodes;

import java.util.Set;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class TaskDefinition{
	
	@Id
	private String id;

	@Relationship(type="SPECIFIED_BY", direction=Relationship.INCOMING)
	Set<WorkflowTask> tasks;
	
	public int getTasks() {
		int meaningfulItems = 0;
		if (tasks != null) {
			for (WorkflowTask t : tasks) {
				if (!t.getId().startsWith("PLACEHOLDER"))
					meaningfulItems++;
			}
		}
		return meaningfulItems;
	}

	public void setTasks(Set<WorkflowTask> tasks) {
		this.tasks = tasks;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
}
