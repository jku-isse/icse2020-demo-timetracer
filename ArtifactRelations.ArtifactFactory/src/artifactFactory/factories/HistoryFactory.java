package artifactFactory.factories;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import artifactFactory.mappers.TypeGetterMapper;
import core.artifactFactory.deltaGenerator.JiraChangeLogItem;
import core.persistence.IJiraArtifactService;
import core.services.JiraServiceFactory;
import jiraconnector.connector.JiraArtifactService;

public class HistoryFactory {

	private IJiraArtifactService jiraArtifactService;
	private TypeGetterMapper typeGetterMapper;
	
	
	public HistoryFactory(TypeGetterMapper typeGetterMapper) throws NoSuchMethodException, SecurityException {
		jiraArtifactService  = JiraServiceFactory.getJiraArtifactService();
		this.typeGetterMapper = typeGetterMapper;
	}
	
	
	@SuppressWarnings("unchecked")
	public ArrayList<JiraChangeLogItem> buildChangeLog(Map<String, Object> artifactData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if(jiraArtifactService==null) {
			try {
				jiraArtifactService = new JiraArtifactService();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Map<String, Object> changeLog = (Map<String, Object>) artifactData.get("changelog");
		ArrayList<Object> histories = (ArrayList<Object>) changeLog.get("histories");
		String correspondingArtifactKey = (String) artifactData.get("key");
		String correspondingArtifactId = (String) artifactData.get("id");
		ArrayList<JiraChangeLogItem> changeLogItems = new ArrayList<JiraChangeLogItem>();

		for(int i=0; i<histories.size(); i++) {
			changeLogItems.addAll(buildChangeLog(correspondingArtifactKey, correspondingArtifactId, (Map<String, Object>) histories.get(i)));
		}
		
		return changeLogItems;
		
 	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<JiraChangeLogItem> buildChangeLog(String correspondingArtifactIdInSource, String correspondingArtifactId, Map<String, Object> itemData) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		ArrayList<JiraChangeLogItem> changeLogItems = new ArrayList<JiraChangeLogItem>();
		ArrayList<Map<String, Object>> items = (ArrayList<Map<String, Object>>) itemData.get("items");	
		String from, to, oldValue, newValue;		
		
		for(int i=0; i<items.size(); i++) {
			
			JiraChangeLogItem changeLogItem = new JiraChangeLogItem();
			
			changeLogItem.setCorrespondingArtifactIdInSource(correspondingArtifactIdInSource);
			changeLogItem.setTimeCreated((String) itemData.get("created"));
			changeLogItem.setId(((String) itemData.get("id")) + "_" + i);
			changeLogItem.setField((String) items.get(i).get("field"));	
			changeLogItem.setFieldType((String) items.get(i).get("fieldtype"));	
			
			changeLogItem.setFromString((String) items.get(i).get("fromString"));
			changeLogItem.setToString((String) items.get(i).get("toString"));
			changeLogItem.setArtifactId(correspondingArtifactId);
			
			//in case the method is null everything is treated like a string
			from = (String) items.get(i).get("from");
			to = (String) items.get(i).get("to");
			
			oldValue = typeGetterMapper.map(changeLogItem.getField(), jiraArtifactService, from);
			newValue = typeGetterMapper.map(changeLogItem.getField(), jiraArtifactService, to);

			if(oldValue==null) {
				changeLogItem.setOldValue(from);
			} else {
				changeLogItem.setOldValue(oldValue);
			}
			
			if(newValue==null) {
				changeLogItem.setNewValue(to);
			} else {
				changeLogItem.setNewValue(newValue);
			}
			
			changeLogItems.add(changeLogItem);
		}
		
		return changeLogItems;
	
	}
	
}
