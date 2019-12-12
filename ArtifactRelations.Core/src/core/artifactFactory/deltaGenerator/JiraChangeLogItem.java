package core.artifactFactory.deltaGenerator;

import java.sql.Timestamp;

public class JiraChangeLogItem {

	public JiraChangeLogItem() {

	}

	protected String id;
	
	protected String correspondingArtifactIdInSource;
	
	protected String linkId;
	
	protected String timeCreated;
	
	protected String artifactId;
	
	protected String name;

	protected String field;
	
	protected String fieldType;
	
	protected String oldValue;
	
	protected String newValue;
	
	protected String fromString;
	
	protected String toString;

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

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTimeCreated() {
		return timeCreated;
	}

	public void setTimeCreated(String timeCreated) {
		this.timeCreated = timeCreated;
	}

	public String getFromString() {
		return fromString;
	}

	public void setFromString(String fromString) {
		this.fromString = fromString;
	}

	public String getToString() {
		return toString;
	}

	public void setToString(String toString) {
		this.toString = toString;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getCorrespondingArtifactIdInSource() {
		return correspondingArtifactIdInSource;
	}

	public void setCorrespondingArtifactIdInSource(String correspondingArtifactIdInSource) {
		this.correspondingArtifactIdInSource = correspondingArtifactIdInSource;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getLinkId() {
		return linkId;
	}
	
	public Timestamp getTimestamp() {
		if(timeCreated==null) return null;
		String modifiedTime = timeCreated.replace('T', ' ');
		modifiedTime = modifiedTime.substring(0, modifiedTime.indexOf('+'));
		return Timestamp.valueOf(modifiedTime);
	}
	
}
