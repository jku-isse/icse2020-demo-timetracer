package core.base;

import java.util.ArrayList;
import java.util.Map;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;
import org.neo4j.ogm.annotation.Transient;


@NodeEntity
public class IdentifiableRelationMemory {
	
	@Transient
	public static final String RELATION_MEMORY_ID = "1234567890";
	
	@Id
	protected String id;
	
	@Properties(prefix = "relation", allowCast = true)		
	protected Map<String, Object> relationMemory;
	
	public IdentifiableRelationMemory() {
		super();
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<LinkType> getRelationMemory() {
		ArrayList<LinkType> linkTypes = new ArrayList<LinkType>();
		
		relationMemory.forEach((x,linkType) -> {
			linkTypes.add(new LinkType((Map<String, Object>) linkType));
		});
		
		return linkTypes;
	}
	
	public void setRelationMemory(Map<String, Object> relationMemory) {
		this.relationMemory = relationMemory;
	}
		
}
