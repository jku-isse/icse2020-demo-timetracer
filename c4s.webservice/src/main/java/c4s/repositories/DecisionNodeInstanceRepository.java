package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.DecisionNodeInstance;

@RepositoryRestResource(collectionResourceRel = "decisionNodeInstances", path = "decisionNodeInstances")
public interface DecisionNodeInstanceRepository extends Neo4jRepository<DecisionNodeInstance, String>{

	List<DecisionNodeInstance> findAll(@Param("depth") int depth);
	
    Optional<DecisionNodeInstance> findById(String id, int depth);
	
}
