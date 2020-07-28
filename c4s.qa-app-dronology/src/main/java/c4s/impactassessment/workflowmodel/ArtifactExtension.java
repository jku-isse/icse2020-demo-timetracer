package c4s.impactassessment.workflowmodel;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity
public class ArtifactExtension extends AbstractArtifact implements Artifact {

	private static final long serialVersionUID = 1L;

	private Artifact artifact;
	
	@Property
	String key;
	
	@Deprecated
	public ArtifactExtension() {
		super();
	}
	
	public ArtifactExtension(Artifact artifact) {
		super();
		this.artifact  = artifact;
		this.key = artifact.getId();
		this.id = artifact.getId();
	}

	@Override
	public ArtifactType getType() {
		return new ArtifactType(ArtifactTypes.ARTIFACT_TYPE_JIRA_TICKET);
	}
	
	@Override
	public Artifact getParentArtifact() {
		return null;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public String getKey() {
		return key;
	}
	
	@Override
	public String getId() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
