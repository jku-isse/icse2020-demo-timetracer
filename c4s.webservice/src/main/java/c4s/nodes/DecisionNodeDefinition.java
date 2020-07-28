package c4s.nodes;

import java.util.Set;

import org.neo4j.ogm.annotation.*;

@NodeEntity
public class DecisionNodeDefinition {
	
	@Id
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}