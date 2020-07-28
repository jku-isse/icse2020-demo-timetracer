package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.TaskDefinition;

@RepositoryRestResource(collectionResourceRel = "taskdefs", path = "taskdefs")
public interface TaskDefinitionRepository  extends Neo4jRepository<TaskDefinition, String> {

	List<TaskDefinition> findAll(@Param("depth") int depth);
	
	Optional<TaskDefinition> findById(String id, int depth);

}
