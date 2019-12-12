package artifactFactory.relationFactories;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import core.base.IdentifiableArtifact;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;

public class JiraRelationFactory implements IRelationFactory {
		
	@SuppressWarnings("unchecked")
	public Relation createRelation(ReplayableArtifact artifact, Object object ,boolean incoming) {
		 
		if(object==null) return null;	
		Relation relation = new Relation();	
		Map<String, Object> map = (Map<String, Object>) object;		
		relation.setId((String) map.get("id"));
				
		if(map.get("type") != null) {
			
			Map<String, Object> typeMap = (Map<String, Object>) map.get("type"); 
			
			relation.setRelationType((String) typeMap.get("id"));
		
			relation.setDestinationRole((String) typeMap.get("inward"));
			relation.setSourceRole((String) typeMap.get("outward"));
			
			Map<String, Object> issueMap;
			Boolean source;
			
			if(map.get("inwardIssue")!=null) {
				issueMap = (Map<String, Object>) map.get("inwardIssue"); 
				source= false;
			} else {
				issueMap = (Map<String, Object>) map.get("outwardIssue"); 
				source= true;
			}
								
			IdentifiableArtifact ia = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact((String) issueMap.get("id"));
			ReplayableArtifact otherArtifact = null;

			if(ia == null) {
			 	otherArtifact = new ReplayableArtifact();
				otherArtifact.setId((String) issueMap.get("id"));
			} else {
				otherArtifact = (ReplayableArtifact) ia;
			}
					
			if(source) {
				relation.setSource(artifact);
				relation.setDestination(otherArtifact);	
			} else {
				relation.setSource(otherArtifact);
				relation.setDestination(artifact);	
			}

			relation.setFromToId(((String) map.get("id")).concat(artifact.getId()));
			
		}
		
			
		return relation;
	}
	
	
	//TO-DO find more elegant solution for different relationshipTypes
	@SuppressWarnings("unchecked")
	public Relation createFieldTypeFromSubtask(Object object, ReplayableArtifact artifact) {
		 
		if(object==null) return null;
		Relation relation = new Relation();
		Map<String, Object> map = (Map<String, Object>) object;		
		
		relation.setId(((String) map.get("id")).concat(artifact.getId()));
		relation.setFromToId(((String) map.get("id")).concat(artifact.getId()));		
		
		relation.setDestinationRole("is subtask of");
		relation.setSourceRole("is parent of");
		
		IdentifiableArtifact ia = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact((String) map.get("id"));
		ReplayableArtifact otherArtifact = null;

		if(ia == null) {
		 	otherArtifact = new ReplayableArtifact();
			otherArtifact.setId((String) map.get("id"));
		} else {		
			otherArtifact = (ReplayableArtifact) ia;
		}
		
		relation.setSource(artifact);
		relation.setDestination(otherArtifact);
		
		return relation;
	}

	
	public Relation createFieldTypeFromEpicLink(String epicParentKey, ReplayableArtifact artifact) throws JsonParseException, JsonMappingException, IOException {
		
		if(epicParentKey==null) return null;
		Relation relation = new Relation();

		relation.setId("EPIC_LINK_" + artifact.getId());

		relation.setDestinationRole("is EPIC-CHILD of");
		relation.setSourceRole("is EPIC of");
				
		IdentifiableArtifact ia = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifactWithIdInSource(epicParentKey);
		ReplayableArtifact epicParent = null;
				
		if(ia == null) {
			
			epicParent = new ReplayableArtifact();
			//a JiraDatabaseConnect is unavoidable since the key
			//of the related artifact, which is used as id,
			//is not provided in the json-file	
			epicParent.setId((String) JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(epicParentKey));
		
		}  else {
			epicParent = (ReplayableArtifact) ia;
		}
			
		relation.setSource(epicParent);
		relation.setDestination(artifact);
		
		return relation;
	}
	
}
