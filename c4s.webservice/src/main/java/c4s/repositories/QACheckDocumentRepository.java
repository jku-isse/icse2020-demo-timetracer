package c4s.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.QACheckDocument;

@RepositoryRestResource(collectionResourceRel = "qachecks", path = "qachecks")
public interface QACheckDocumentRepository extends Neo4jRepository<QACheckDocument, String>{

	List<QACheckDocument> findAll(@Param("depth") int depth);
	
    Optional<QACheckDocument> findById(String id, int depth);
	
}
