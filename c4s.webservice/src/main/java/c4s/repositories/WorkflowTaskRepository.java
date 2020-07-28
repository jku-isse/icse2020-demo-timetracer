package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.WorkflowTask;

@RepositoryRestResource(collectionResourceRel = "wftasks", path = "wftasks")
public interface WorkflowTaskRepository extends Neo4jRepository<WorkflowTask, String>{

	List<WorkflowTask> findAll(@Param("depth") int depth);
	
    Optional<WorkflowTask> findById(String id, int depth);
	
}
