package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.DecisionNodeDefinition;

@RepositoryRestResource(collectionResourceRel = "decisionNode", path = "decisionNode")
public interface DecisionNodeDefinitionRepository  extends Neo4jRepository<DecisionNodeDefinition, String> {

	List<DecisionNodeDefinition> findAll(@Param("depth") int depth);
	
    Optional<DecisionNodeDefinition> findById(String id, int depth);
    
}
