package core.base;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Transient;

import core.fieldTypes.utility.FieldType;
import core.utility.NullTerminator;

@NodeEntity
public class Artifact extends IdentifiableArtifact implements Cloneable{
	
	public Artifact() {
		super();
	}
	
	protected short dirtyFlag = 0;

	@SuppressWarnings("rawtypes")
	@Transient
	protected  Map<String,FieldType> fields;

	@SuppressWarnings("rawtypes")
	public Map<String, FieldType> getFields() {
		return fields;
	}

	@SuppressWarnings("rawtypes")
	public void setFields(Map<String, FieldType> fields) {
		this.fields = fields;
	}

	public Map<String, Object> serialize() {
		
		Map<String, Object> artifact = new HashMap<String, Object>();
		
		if(fields!=null) {
			fields.forEach((x,y) -> {
				
				if(y.getValue()!=null && y.getValue().serialize()!=null) {
					artifact.put(x, y.getValue().serialize());				
				} else {
					artifact.put(x, "null");
				}
			
			});
		}
			
		return NullTerminator.terminate(artifact);
		
	}

	public short getDirtyFlag() {
		return dirtyFlag;
	}

	public void setDirtyFlag(short dirtyFlag) {
		this.dirtyFlag = dirtyFlag;
	}
	
}
