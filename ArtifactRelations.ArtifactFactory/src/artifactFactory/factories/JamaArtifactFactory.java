package artifactFactory.factories;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import artifactFactory.mappers.JamaIdFactoryMapper;
import artifactFactory.mappers.TypeFactories;
import artifactFactory.relationFactories.JamaRelationFactory;
import core.artifactFactory.factories.IArtifactFactory;
import core.artifactFactory.factories.Services;
import core.artifactFactory.mappers.AbstractIdFactoryMapper;
import core.artifactFactory.mappers.IdValueMapper;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.base.JamaArtifact;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.Serializer;
import core.services.ErrorLoggerServiceFactory;

public class JamaArtifactFactory implements IArtifactFactory{

	private Map<String, Object> data;
	private Map<String, Object> schema;
	protected IdValueMapper idTypeMapper;	
	private ArrayList<Integer> upstream, downstream;
	private JamaRelationFactory relationFactory;
	
	public JamaArtifactFactory(Map<String, Object> schema) throws IOException {	    
		relationFactory = new JamaRelationFactory();
		this.schema = schema;
		idTypeMapper = new IdValueMapper(this.schema);
	}
	
	@Override
	public Artifact createArtifact(Object data) {
		if(data==null) return null;	
		JamaArtifact artifact = new JamaArtifact();
		return setUpData(artifact, data);
	}

	@Override
	public Artifact updateArtifact(Object data, Artifact artifact) throws Exception {
		if(data==null||artifact==null) return null;	
		ReplayableArtifact ra = (ReplayableArtifact) artifact;
		ra.setRelationCount(ra.getRealRelationCount());
		return setUpData(ra, data);		
	}
	
	private Artifact setUpData(ReplayableArtifact artifact, Object json) {
		
		initMapsWithJson(json);
		
		artifact.setId("JamaItem"+(Integer) data.get("id"));
		artifact.setService(Services.Jama.getValue());
		artifact.setIdInSource(artifact.getId());
		artifact.setOrigin("/items/" + artifact.getId());
		artifact.setUpdateTimestamp((new Timestamp(System.currentTimeMillis())).toString());
	
		//TO-DO: is there a schema according to which fields may be mapped to types
		artifact.setFields(setUpFields(data.get("fields"), false));
		artifact.setProperties(artifact.serialize());	

		artifact = setUpRelations(upstream, downstream, artifact);
		artifact.setFullyFetched(true);
		
		return artifact;
	}
	
	private ReplayableArtifact setUpRelations(ArrayList<Integer> upstream, ArrayList<Integer> downstream, ReplayableArtifact artifact) {

		//upstream : incoming, downstream : outgoing
		
		if(upstream!=null) {
			upstream.forEach((id) -> {		
				Relation r = relationFactory.createRelation(artifact, id, true);
				artifact.addRelationToIncoming(r);
				r.getSource().addRelationToOutgoing(r);
			});
		}
		
		if(downstream!=null) {
			downstream.forEach((id) -> {
				Relation r = relationFactory.createRelation(artifact, id, false);
				artifact.addRelationToOutgoing(relationFactory.createRelation(artifact, id, false));
				r.getDestination().addRelationToIncoming(r);
			});
		}	
		
		return artifact;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, FieldType> setUpFields(Object object, boolean Deserialize) {

		Map<String, Object> rawFields = (Map<String, Object>) object;
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		AbstractIdFactoryMapper idFactoryMapper = new JamaIdFactoryMapper(idTypeMapper, TypeFactories.Jama.getValue(), Deserialize);
		//deleteAllNotRequiredFields(rawFields);
		
		rawFields.forEach((x,y) -> {
					
			IFieldTypeFactory factory = idFactoryMapper.map(x);	
			
			if(factory!=null) {
				Serializer fieldType = factory.createFieldType(y);
				fields.put(x, new FieldType<Serializer>(x, x, fieldType));
			}		
			
		});
		
		return fields;
		
	}

	@Override
	public Artifact deserialize(IdentifiableArtifact iArtifact) throws Exception {	
		if(iArtifact==null) return null;
		Artifact artifact = (Artifact) iArtifact;	
		if(!iArtifact.isFullyFetched()) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "JamaArtifactFactory: deserialize: Deserialiation of incomplete artifacts not possible");
			return artifact;
		}
		artifact.setFields(setUpFields(iArtifact.getProperties(), true));	
		return artifact;				
	}

	@Override
	public ArrayList<ChangeLogItem> buildChangeLog(Map<String, Object> artifactData)
			throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// TODO Auto-generated method stub
		//Activities have to be queried and properly transformed into a common changeLog
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private void initMapsWithJson(Object json) {
		Map<String, Object> map = (Map<String, Object>) json;
		data = (Map<String, Object>) map.get("data");
		upstream = (ArrayList<Integer>) map.get("upstream");
		downstream = (ArrayList<Integer>) map.get("downstream");
	}
	

}
