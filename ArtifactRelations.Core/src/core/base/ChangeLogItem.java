package core.base;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public abstract class ChangeLogItem implements Comparable<ChangeLogItem> {

	public ChangeLogItem() {

	}

	@Id
	protected String id;
	
	@Property
	protected String correspondingArtifactIdInSource;
	
	@Property
	protected String correspondingArtifactId;
	
	@Property
	protected String timeCreated;
	
	@Property
	protected String artifactId;
	
	public abstract ReplayableArtifact applyChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache);
	
	public abstract ReplayableArtifact undoChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache);
	
	public abstract HashSet<String> getInvolvedArtifactIds();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}
	
	public String getCorrespondingArtifactId() {
		return correspondingArtifactId;
	}

	public void setCorrespondingArtifactId(String correspondingArtifactId) {
		this.correspondingArtifactId = correspondingArtifactId;
	}

	public String getCorrespondingArtifactIdInSource() {
		return correspondingArtifactIdInSource;
	}

	public void setCorrespondingArtifactIdInSource(String correspondingArtifactIdInSource) {
		this.correspondingArtifactIdInSource = correspondingArtifactIdInSource;
	}
	
	public Timestamp getTimestamp() {
		if(timeCreated==null) return null;
		String modifiedTime = timeCreated.replace('T', ' ');
		modifiedTime = modifiedTime.substring(0, modifiedTime.indexOf('+'));
		return Timestamp.valueOf(modifiedTime);
	}	
	
	@Override
	public int compareTo(ChangeLogItem item) {
		//if(long )
		return 0;		
	}
	
}
