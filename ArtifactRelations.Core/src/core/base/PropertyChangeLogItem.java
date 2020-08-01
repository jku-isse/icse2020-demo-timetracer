package core.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Properties;

@NodeEntity
public class PropertyChangeLogItem extends ChangeLogItem {

	public PropertyChangeLogItem() {

	}

	@Properties(prefix = "from", allowCast = true)		
	private Map<String, Object> from;

	@Properties(prefix = "to", allowCast = true)		
	private Map<String, Object> to;

	private String field = "";
	
	public Map<String, Object> getFrom() {
		return from;
	}

	public void setFrom(Map<String, Object> from) {
		this.from = from;
	}

	public Map<String, Object> getTo() {
		return to;
	}

	public void setTo(Map<String, Object> to) {
		this.to = to;
	}

	@Override
	public ReplayableArtifact applyChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache) {
		if(to.isEmpty()) {
			if(!from.isEmpty()) {
				artifact.getProperties().remove(from.keySet().iterator().next());
			}
		} else {
			artifact.getProperties().putAll(to);
		}
		return artifact;
	}

	@Override
	public ReplayableArtifact undoChange(ReplayableArtifact artifact, HashMap<String, ReplayableArtifact> cache) {
		if(from.isEmpty()) {
			if(!to.isEmpty()) {
				artifact.getProperties().remove(to.keySet().iterator().next());
			}
		} else {
			artifact.getProperties().putAll(from);
		}
		return artifact;
	}

	@Override
	public HashSet<String> getInvolvedArtifactIds() {
		return null;
	} 
	
	public void setField(String field) {
		this.field = field;
	}

	public String getField() {
		return field;
	}

}
