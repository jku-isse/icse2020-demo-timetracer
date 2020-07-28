package c4s.repositories;

import java.util.List;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import c4s.nodes.ResourceLink;

@RepositoryRestResource(collectionResourceRel = "rslinks", path = "rslinks")
public interface ResourceLinkRepository extends Neo4jRepository<ResourceLink, String>{
	
	List<ResourceLink> findByContext(@Param("context") String context);

}
