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
import core.artifactFactory.deltaGenerator.BaseChangeLogItem;
import core.artifactFactory.deltaGenerator.IDeltaGenerator;
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
	public ChangeLogItem buildChangeLog(BaseChangeLogItem baseChangeLogItem) {
				
		boolean epicLink= false, epicChild=false, subtask=false;
		String relationship;
		
		if(baseChangeLogItem.getField().equals("Link")||(subtask= baseChangeLogItem.getField().equals("Parent"))||
				(epicLink= baseChangeLogItem.getField().equals("Epic Link")||(epicLink=epicChild= baseChangeLogItem.getField().equals("Epic Child")))) {
	
			RelationChangeLogItem changeLogItem = new RelationChangeLogItem();
			
			if(!epicLink) {
				
				if(subtask) {
					
					if(baseChangeLogItem.getOldValue()!=null) {
						changeLogItem.setFromKey(baseChangeLogItem.getFromString());
						changeLogItem.setFromId(baseChangeLogItem.getOldValue());
					} 
					
					if(baseChangeLogItem.getNewValue()!=null) {
						changeLogItem.setToKey(baseChangeLogItem.getToString());
						changeLogItem.setToId(baseChangeLogItem.getNewValue());
					}		
					
					changeLogItem.setArtifactIsSource(false);

					changeLogItem.setSourceRole("is PARENT of");
					changeLogItem.setDestinationRole("is SUBTASK of");
					
				} else {
					
					if(baseChangeLogItem.getOldValue()!=null) {
						relationship = baseChangeLogItem.getFromString();
						changeLogItem.setFromKey(baseChangeLogItem.getOldValue());
						//for a standardLink the id of source is not contained in the changeLog
						//and therefore needs to be fetched from JIRA during creation
						try {
							changeLogItem.setFromId(JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(baseChangeLogItem.getOldValue()));
						} catch (Exception e) {
							changeLogItem.setFromId(changeLogItem.getFromKey());
						}
					} else {
						relationship = baseChangeLogItem.getToString();
						changeLogItem.setToKey(baseChangeLogItem.getNewValue());
						try {
							changeLogItem.setToId(JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(baseChangeLogItem.getNewValue()));
						} catch (Exception e) {
							ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, "JiraDeltaGenerator: Key is used as id since " + baseChangeLogItem.getNewValue() + " is deletedInSource !");
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
						ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "JiraDeltaGenerator: IssueLinkType Deleted: RelationChangeLog " + baseChangeLogItem.getId() + "was not created!");
						return null;
					}	
				
				}
					
			} else {
				
				if(baseChangeLogItem.getOldValue()!=null) {
					changeLogItem.setFromKey(baseChangeLogItem.getFromString());
					changeLogItem.setFromId(baseChangeLogItem.getOldValue());
				} 
				
				if(baseChangeLogItem.getNewValue()!=null) {
					changeLogItem.setToKey(baseChangeLogItem.getToString());
					changeLogItem.setToId(baseChangeLogItem.getNewValue());
				}		
				
				if(epicChild) {
					changeLogItem.setArtifactIsSource(true);
				} else {
					changeLogItem.setArtifactIsSource(false);
				}

				changeLogItem.setSourceRole("is EPIC of");
				changeLogItem.setDestinationRole("is EPIC-CHILD of");
			}
						
			changeLogItem.setArtifactId(baseChangeLogItem.getArtifactId());
			changeLogItem.setCorrespondingArtifactIdInSource(baseChangeLogItem.getCorrespondingArtifactIdInSource());
			changeLogItem.setCorrespondingArtifactId(baseChangeLogItem.getArtifactId());
			changeLogItem.setId(baseChangeLogItem.getId());
			changeLogItem.setTimeCreated(baseChangeLogItem.getTimeCreated());
			
			return changeLogItem;
			
		} else {
			
			PropertyChangeLogItem changeLogItem= new PropertyChangeLogItem();

			//create entry in properties, which can be serialized
			try {
				updateFields(changeLogItem, baseChangeLogItem);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			changeLogItem.setArtifactId(baseChangeLogItem.getArtifactId());
			changeLogItem.setCorrespondingArtifactIdInSource(baseChangeLogItem.getCorrespondingArtifactIdInSource());
			changeLogItem.setCorrespondingArtifactId(baseChangeLogItem.getArtifactId());
			changeLogItem.setId(baseChangeLogItem.getId());
			changeLogItem.setTimeCreated(baseChangeLogItem.getTimeCreated());

			return changeLogItem;

		}
				
	}
	
	
	@SuppressWarnings("rawtypes")
	private ChangeLogItem updateFields(PropertyChangeLogItem changeLogItem, BaseChangeLogItem baseChangeLogItem) throws JsonParseException, JsonMappingException, IOException {
		
		String fieldId = fieldFactoryMapper.map(baseChangeLogItem.getField());
		IFieldTypeFactory fieldTypeFactory = idFactoryMapper.map(fieldId);		
		Object newData, oldData;
		String newDataString, oldDataString;
						
		try {
			newData = jsonToMap(baseChangeLogItem.getNewValue());
			oldData = jsonToMap(baseChangeLogItem.getOldValue());
		} catch(Exception e) {
			ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "DeltaGenerator: updateFields(): " + e.getMessage() + " (" + baseChangeLogItem.getField() + " cannot be deserialized)\n");
			return changeLogItem;
		}
			
		newDataString = baseChangeLogItem.getToString();
		oldDataString = baseChangeLogItem.getFromString();
	
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


