package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.QAConstraint;

@RepositoryRestResource(collectionResourceRel = "reqs", path = "reqs")
public interface QAConstraintRepository extends Neo4jRepository<QAConstraint, String>{

	List<QAConstraint> findAll(@Param("depth") int depth);
	
    Optional<QAConstraint> findById(String id, int depth);
	
}
