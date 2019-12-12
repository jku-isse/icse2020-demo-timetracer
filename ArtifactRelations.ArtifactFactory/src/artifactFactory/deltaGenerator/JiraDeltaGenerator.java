package artifactFactory.deltaGenerator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artifactFactories.commonTypeFactories.StringFactory;
import artifactFactory.mappers.JiraChangeLogFieldToFieldIdMapper;
import artifactFactory.mappers.JiraIdFactoryMapper;
import artifactFactory.mappers.TypeFactories;
import core.artifactFactory.deltaGenerator.IDeltaGenerator;
import core.artifactFactory.deltaGenerator.JiraChangeLogItem;
import core.artifactFactory.mappers.IdValueMapper;
import core.artifactFactory.typeFactories.IFieldTypeFactory;
import core.base.ChangeLogItem;
import core.base.IdentifiableRelationMemory;
import core.base.PropertyChangeLogItem;
import core.base.RelationChangeLogItem;
import core.fieldValues.common.Serializer;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;

public class JiraDeltaGenerator implements IDeltaGenerator{

	private IdentifiableRelationMemory relationMemory;
	private JiraIdFactoryMapper idFactoryMapper;
	private IdValueMapper idTypeMapper, idNameMapper;
	private JiraChangeLogFieldToFieldIdMapper fieldFactoryMapper;
	private Map<String, Object> names;
	private Map<String, Object> schema;
	
	@SuppressWarnings("unchecked")
	public JiraDeltaGenerator(Object schema, Object names) {
		
		this.names = (Map<String, Object>) names;
		this.schema = (Map<String, Object>) schema;
		
		this.idTypeMapper = new IdValueMapper(this.schema);
		this.idNameMapper = new IdValueMapper(this.names);
		this.fieldFactoryMapper = new JiraChangeLogFieldToFieldIdMapper(this.idNameMapper, this.idTypeMapper);
		this.idFactoryMapper = new JiraIdFactoryMapper(idTypeMapper, TypeFactories.Jira.getValue(), false);
			
		relationMemory = Neo4JServiceFactory.getNeo4JServiceManager().getRelationMemoryService().fetchRelationMemory();
		
	}
	
	@Override
	public ChangeLogItem buildChangeLog(JiraChangeLogItem jiraChangeLogItem) {
				
		boolean epicLink= false, epicChild=false, subtask=false;
		String relationship;
		
		if(jiraChangeLogItem.getField().equals("Link")||(subtask=jiraChangeLogItem.getField().equals("Parent"))||
				(epicLink=jiraChangeLogItem.getField().equals("Epic Link")||(epicLink=epicChild=jiraChangeLogItem.getField().equals("Epic Child")))) {
	
			RelationChangeLogItem changeLogItem = new RelationChangeLogItem();
			
			if(!epicLink) {
				
				if(subtask) {
					
					if(jiraChangeLogItem.getOldValue()!=null) {
						changeLogItem.setFromKey(jiraChangeLogItem.getFromString());
						changeLogItem.setFromId(jiraChangeLogItem.getOldValue());
					} 
					
					if(jiraChangeLogItem.getNewValue()!=null) {			 
						changeLogItem.setToKey(jiraChangeLogItem.getToString());
						changeLogItem.setToId(jiraChangeLogItem.getNewValue());		
					}		
					
					changeLogItem.setArtifactIsSource(false);

					changeLogItem.setSourceRole("is PARENT of");
					changeLogItem.setDestinationRole("is SUBTASK of");
					
				} else {
					
					if(jiraChangeLogItem.getOldValue()!=null) {
						relationship = jiraChangeLogItem.getFromString();
						changeLogItem.setFromKey(jiraChangeLogItem.getOldValue());
						//for a standardLink the id of source is not contained in the changeLog
						//and therefore needs to be fetched from JIRA during creation
						try {
							changeLogItem.setFromId(JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(jiraChangeLogItem.getOldValue()));
						} catch (Exception e) {
							changeLogItem.setFromId(changeLogItem.getFromKey());
						}
					} else {
						relationship = jiraChangeLogItem.getToString();
						changeLogItem.setToKey(jiraChangeLogItem.getNewValue());
						try {
							changeLogItem.setToId(JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(jiraChangeLogItem.getNewValue()));
						} catch (Exception e) {
							ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "JiraDeltaGenerator: Key is used as id since " + jiraChangeLogItem.getNewValue() + " is deletedInSource !");
							changeLogItem.setToId(changeLogItem.getToKey());
						}
					}				
					
					relationMemory.getRelationMemory().forEach(linkType -> {
						
						//in order to find out if we are dealing with an incoming 
						//or outgoing link we have to check with the JiraLinkType memory					
						if(relationship.contains(linkType.getInward().get())) {
							changeLogItem.setArtifactIsSource(false);
							changeLogItem.setSourceRole(linkType.getOutward().get());
							changeLogItem.setDestinationRole(linkType.getInward().get());
						}						
						
						if(relationship.contains(linkType.getOutward().get())) {
							changeLogItem.setArtifactIsSource(true);	
							changeLogItem.setSourceRole(linkType.getOutward().get());
							changeLogItem.setDestinationRole(linkType.getInward().get());			
						}	
	
					});
					
					if(changeLogItem.getSourceRole()==null) {
						ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "JiraDeltaGenerator: IssueLinkType Deleted: RelationChangeLog " + jiraChangeLogItem.getId() + "was not created!");
						return null;
					}	
				
				}
					
			} else {
				
				if(jiraChangeLogItem.getOldValue()!=null) {
					changeLogItem.setFromKey(jiraChangeLogItem.getFromString());
					changeLogItem.setFromId(jiraChangeLogItem.getOldValue());
				} 
				
				if(jiraChangeLogItem.getNewValue()!=null) {			 
					changeLogItem.setToKey(jiraChangeLogItem.getToString());
					changeLogItem.setToId(jiraChangeLogItem.getNewValue());		
				}		
				
				if(epicChild) {
					changeLogItem.setArtifactIsSource(true);
				} else {
					changeLogItem.setArtifactIsSource(false);
				}

				changeLogItem.setSourceRole("is EPIC of");
				changeLogItem.setDestinationRole("is EPIC-CHILD of");
			}
						
			changeLogItem.setArtifactId(jiraChangeLogItem.getArtifactId());
			changeLogItem.setCorrespondingArtifactIdInSource(jiraChangeLogItem.getCorrespondingArtifactIdInSource());
			changeLogItem.setCorrespondingArtifactId(jiraChangeLogItem.getArtifactId());
			changeLogItem.setId(jiraChangeLogItem.getId());
			changeLogItem.setTimeCreated(jiraChangeLogItem.getTimeCreated());
			
			return changeLogItem;
			
		} else {
			
			PropertyChangeLogItem changeLogItem= new PropertyChangeLogItem();

			//create entry in properties, which can be serialized
			try {
				updateFields(changeLogItem, jiraChangeLogItem);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			changeLogItem.setArtifactId(jiraChangeLogItem.getArtifactId());
			changeLogItem.setCorrespondingArtifactIdInSource(jiraChangeLogItem.getCorrespondingArtifactIdInSource());
			changeLogItem.setCorrespondingArtifactId(jiraChangeLogItem.getArtifactId());
			changeLogItem.setId(jiraChangeLogItem.getId());
			changeLogItem.setTimeCreated(jiraChangeLogItem.getTimeCreated());

			return changeLogItem;

		}
				
	}
	
	
	@SuppressWarnings("rawtypes")
	private ChangeLogItem updateFields(PropertyChangeLogItem changeLogItem, JiraChangeLogItem jiraChangeLogItem) throws JsonParseException, JsonMappingException, IOException {
		
		String fieldId = fieldFactoryMapper.map(jiraChangeLogItem.getField());
		IFieldTypeFactory fieldTypeFactory = idFactoryMapper.map(fieldId);		
		Object newData, oldData;
		String newDataString, oldDataString;
						
		try {
			newData = jsonToMap(jiraChangeLogItem.getNewValue());
			oldData = jsonToMap(jiraChangeLogItem.getOldValue());
		} catch(Exception e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "DeltaGenerator: updateFields(): " + e.getMessage() + " (" + jiraChangeLogItem.getField() + " cannot be deserialized)\n");
			return changeLogItem;
		}
			
		newDataString = jiraChangeLogItem.getToString();
		oldDataString = jiraChangeLogItem.getFromString();
	
		if(fieldTypeFactory!=null) {
					
			changeLogItem.setTo(createSerializedMap(fieldId, newDataString, newData, fieldTypeFactory));		
			changeLogItem.setFrom(createSerializedMap(fieldId, oldDataString, oldData, fieldTypeFactory));		
			
		}
				
		return changeLogItem;
	}
	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap<String, Object> createSerializedMap(String fieldId, String dataString, Object data, IFieldTypeFactory fieldTypeFactory){
		
		Serializer serializer;
		HashMap<String, Object> dataMap = new HashMap<String, Object>();
		
		if(data!=null) {
		
			Map<String, Object> arrayData = (Map<String, Object>) data;	

			if(arrayData.get("array") != null) {
				
				serializer = fieldTypeFactory.createFieldType(arrayData.get("array"));	
				if(serializer!=null) {
					dataMap.put(fieldId, serializer.serialize());
				} 
				
			} else {
				
				serializer = fieldTypeFactory.createFieldType(data);
				if(serializer!=null) {
					dataMap.put(fieldId, serializer.serialize());
				} 
				
			}
									
		} else {
		
			if(dataString==null) return null;
			fieldTypeFactory = new StringFactory();
			serializer = fieldTypeFactory.createFieldType(dataString);				
			if(serializer!=null) {
				dataMap.put(fieldId, serializer.serialize());
			} else {
				dataMap.put(fieldId, "null");
			}
			
		}
		
		return dataMap;
		
	}
	
	
	private Map<String, Object> jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {	
		if(json==null) return null;
		ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
	}
	
	

	
	
	
}


