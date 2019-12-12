package artifactFactory.relationFactories;

import core.base.Relation;
import core.base.ReplayableArtifact;

public interface IRelationFactory {

	public Relation createRelation(ReplayableArtifact a, Object relationData, boolean incoming);
	
}
