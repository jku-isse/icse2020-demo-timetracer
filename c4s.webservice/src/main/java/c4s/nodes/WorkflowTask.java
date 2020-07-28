package c4s.nodes;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class WorkflowTask {

	@Id
	private String id;
	@Relationship(type="SPECIFIED_BY", direction = Relationship.OUTGOING)
	private TaskDefinition definition;
	@Relationship(type="TASK_IO", direction = Relationship.OUTGOING)
	private ResourceLink resource;
	@Relationship(type="TASK_IO", direction = Relationship.OUTGOING)
	private QACheckDocument checkDoc;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public TaskDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(TaskDefinition definition) {
		this.definition = definition;
	}

	public ResourceLink getResource() {
		return resource;
	}

	public void setResource(ResourceLink resource) {
		this.resource = resource;
	}

	public QACheckDocument getCheckDoc() {
		return checkDoc;
	}

	public void setCheckDoc(QACheckDocument checkDoc) {
		this.checkDoc = checkDoc;
	}
	
}
