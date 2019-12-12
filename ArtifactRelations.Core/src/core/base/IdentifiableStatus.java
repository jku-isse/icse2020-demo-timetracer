package core.base;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class IdentifiableStatus {

	public static final String STATUS_ID = "status_0";
	
	@Id
	protected String id = STATUS_ID;
	
	@Property
	protected long lastUpdate;

	public IdentifiableStatus() {
		
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
}
