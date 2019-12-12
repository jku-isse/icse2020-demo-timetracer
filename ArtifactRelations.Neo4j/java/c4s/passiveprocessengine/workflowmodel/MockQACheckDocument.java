package c4s.passiveprocessengine.workflowmodel;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import c4s.impactassessment.workflowmodel.AbstractArtifact;
import c4s.impactassessment.workflowmodel.Artifact;
import c4s.impactassessment.workflowmodel.ArtifactType;
import c4s.impactassessment.workflowmodel.ResourceLink;
import c4s.impactassessment.workflowmodel.WorkflowInstance;

@NodeEntity
public class MockQACheckDocument extends AbstractArtifact{

	@Override
	public Artifact getParentArtifact() {
		return null;
	}

	public MockQACheckDocument(){}
	
	public MockQACheckDocument(String id, WorkflowInstance wfi) {
		super(id, new ArtifactType("MockQA"), wfi);
	}
	
	
	@Relationship(type="FULFILLED_CONSTRAINT")
	Set<ResourceLink> fulfilledFor = new HashSet<ResourceLink>();
	
	public boolean addAs(boolean satisfied, ResourceLink... links) {
		for (ResourceLink rl : links) {	
			fulfilledFor.add(rl);
		}
		return true;
	}
}
