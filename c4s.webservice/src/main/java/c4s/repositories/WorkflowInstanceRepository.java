package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.WorkflowInstance;

@SuppressWarnings("unused")
@RepositoryRestResource(collectionResourceRel = "wfi", path = "wfi")
public interface WorkflowInstanceRepository extends Neo4jRepository<WorkflowInstance, String>{
	
	List<WorkflowInstance> findAll(@Param("depth") int depth);
	
	Optional<WorkflowInstance> findById(String id, int depth);
}
