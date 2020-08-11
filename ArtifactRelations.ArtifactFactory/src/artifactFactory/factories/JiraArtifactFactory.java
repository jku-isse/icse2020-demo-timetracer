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

import artifactFactory.deltaGenerator.JiraDeltaGenerator;
import artifactFactory.mappers.JiraIdFactoryMapper;
import artifactFactory.mappers.TypeFactories;
import artifactFactory.mappers.TypeGetterMapper;
import artifactFactory.relationFactories.JiraRelationFactory;
import core.artifactFactory.factories.IArtifactFactory;
import core.artifactFactory.factories.Services;
import core.artifactFactory.mappers.IdValueMapper;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.base.Artifact;
import core.base.ChangeLogItem;
import core.base.IdentifiableArtifact;
import core.base.Relation;
import core.base.ReplayableArtifact;
import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.Serializer;
import core.services.ErrorLoggerServiceFactory;
import jiraconnector.connector.JiraArtifactService;

public class JiraArtifactFactory implements IArtifactFactory {

	protected Map<String, Object> names;
	protected Map<String, Object> schema;
	
	protected IdValueMapper idNameMapper;
	protected IdValueMapper idTypeMapper;	
	protected TypeGetterMapper typeGetterMapper;
	
	protected HistoryFactory historyFactory;
	protected JiraDeltaGenerator deltaGenerator;
	
	@SuppressWarnings("unchecked")
	public JiraArtifactFactory(Object schema, Object names) throws NoSuchMethodException, SecurityException {
		
		this.names = (Map<String,Object>) names;		
		this.schema = (Map<String,Object>) schema;
		
		this.names.put("fixVersions", "Fix Version");
		this.names.put("versions", "Affected Version");
		
		idNameMapper = new IdValueMapper(this.names);
		idTypeMapper = new IdValueMapper(this.schema);
		
		historyFactory = new HistoryFactory();
		deltaGenerator = new JiraDeltaGenerator(this.schema, this.names);
		
	}
	
	@Override
	public ReplayableArtifact createArtifact(Object data) throws JsonParseException, JsonMappingException, IOException {		
		if(data==null) return null;	
		ReplayableArtifact artifact = new ReplayableArtifact();
		return setUpData(artifact, data);		
	}
	
	@Override
	public ReplayableArtifact updateArtifact(Object data, Artifact artifact) throws JsonParseException, JsonMappingException, IOException {
		if(data==null||artifact==null) return null;	
		ReplayableArtifact ra = (ReplayableArtifact) artifact;
		ra.setRelationCount(ra.getRealRelationCount());
		return setUpData(ra, data);		
	}
	
	@SuppressWarnings("unchecked")
	private ReplayableArtifact setUpData(ReplayableArtifact artifact, Object data) throws JsonParseException, JsonMappingException, IOException {
		
		Map<String, Object> map = (Map<String, Object>) data;
		
		artifact.setId(map.get("id").toString());
		artifact.setService(Services.Jira.getValue());
		artifact.setIdInSource((String) map.get("key"));
		artifact.setOrigin((String) map.get("self"));

		artifact.setFields(setUpFields(map.get("fields"), false));
		artifact.setUpdateTimestamp((new Timestamp(System.currentTimeMillis())).toString());
	
		artifact = setUpRelations(map, artifact);
		artifact.setProperties(artifact.serialize());	
		artifact.setFullyFetched(true);
		
		return artifact;
		
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, FieldType> setUpFields(Object object, boolean Deserialize) {

		Map<String, Object> rawFields = (Map<String, Object>) object;
		Map<String, FieldType> fields = new HashMap<String, FieldType>();

		JiraIdFactoryMapper idFactoryMapper = new JiraIdFactoryMapper(idTypeMapper, TypeFactories.Jira.getValue(), Deserialize);
		deleteAllNotRequiredFields(rawFields);
		
		rawFields.forEach((x,y) -> {
					
			try {
				//fieldName serves as id(stored in x)
				//for customFields id!=name 
				String name = (String) idNameMapper.map(x);
				IFieldTypeFactory factory = idFactoryMapper.map(x);	


				if(factory!=null&&name!=null) {
					Serializer fieldType = factory.createFieldType(y);
					fields.put(x, new FieldType<Serializer>(name, x, fieldType));
				}
			} catch (Exception e) {
				
				throw e;
			}		
			
		});
		
		return fields;

	}
	
	

	private void deleteAllNotRequiredFields(Map<String, Object> fields) {	
		if(fields!=null) {
			fields.remove("worklog");
			fields.remove("issueLinks");
			fields.remove("parent");
		}		
	}
	
	
	
	@SuppressWarnings("unchecked")
	private ReplayableArtifact setUpRelations(Object object, ReplayableArtifact artifact) throws JsonParseException, JsonMappingException, IOException {
		
		Map<String, Object> json = (Map<String, Object>) object;
		Map<String, Object> fields = (Map<String, Object>) json.get("fields");	
		ArrayList<Object> issueLinks = (ArrayList<Object>) fields.get("issuelinks");
		ArrayList<Object> subtasks = (ArrayList<Object>) fields.get("subtasks");
		String epicParentID = (String) fields.get("customfield_10001");		
		JiraRelationFactory relationFactory = new JiraRelationFactory();
		
		if(issueLinks!=null) {
			
			issueLinks.forEach(x -> {
				
				Relation r = relationFactory.createRelation(artifact,x ,false);
				
				if(r.getSource().getId().equals(artifact.getId())) {
					artifact.addRelationToOutgoing(r);
				} else {
					artifact.addRelationToIncoming(r);
				}
								
			});
			
		}
	
		if(subtasks!=null) {
			subtasks.forEach(x -> {								
				Relation r = relationFactory.createFieldTypeFromSubtask(x, artifact);				
				artifact.addRelationToOutgoing(r);		
				r.getDestination().addRelationToIncoming(r);
			});
		}
	
	
		if(epicParentID!=null) {					
			Relation r = relationFactory.createFieldTypeFromEpicLink(epicParentID, artifact);
			artifact.addRelationToIncoming(r);	
			r.getSource().addRelationToOutgoing(r);
		}
		
		return artifact;
		
	}
	
	
	public Artifact deserialize(IdentifiableArtifact iArtifact) {		
		if(iArtifact==null) return null;
		Artifact artifact = (Artifact) iArtifact;	
		if(!iArtifact.isFullyFetched()) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "JiraArtifactFactory: deserialize: Deserialiation of incomplete artifacts not possible");
			return artifact;
		}
		artifact.setFields(setUpFields(iArtifact.getProperties(), true));	
		return artifact;		
	}
	
	
	public ArrayList<ChangeLogItem> buildChangeLog(Map<String, Object> artifactData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ArrayList<ChangeLogItem> items = new ArrayList<ChangeLogItem>();
		historyFactory.buildChangeLog(artifactData).forEach( item -> {
			ChangeLogItem change = deltaGenerator.buildChangeLog(item);
			if(change!=null) items.add(change);
		});
		return items;	
 	}

}
