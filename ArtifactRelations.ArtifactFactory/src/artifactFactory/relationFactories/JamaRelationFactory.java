package artifactFactory.relationFactories;

import core.base.IdentifiableArtifact;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.services.Neo4JServiceFactory;

public class JamaRelationFactory implements IRelationFactory{

	@Override
	public Relation createRelation(ReplayableArtifact artifact, Object relationData, boolean incoming) {
		
		String givenId;
		ReplayableArtifact otherArtifact;
		Relation relation;
		
		if(relationData==null) return null;
		relation = new Relation();
		givenId = "JamaItem"+((Integer) relationData);
		
		IdentifiableArtifact ia = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact(givenId);
		
		if(ia==null) {
			otherArtifact = new ReplayableArtifact();
			otherArtifact.setId(givenId);
		} else {
			otherArtifact = (ReplayableArtifact) ia; 
		}
				
		if(incoming) {
			relation.setSource(otherArtifact);
			relation.setDestination(artifact);
		} else {
			relation.setSource(artifact);
			relation.setDestination(otherArtifact);
		}
		
		relation.setId(relation.getSource().getId() + relation.getDestination().getId());
		relation.setFromToId(relation.getId());
		
		relation.setDestinationRole("is related to by");
		relation.setSourceRole("relates to");

		return relation;
		
	}
	
}
