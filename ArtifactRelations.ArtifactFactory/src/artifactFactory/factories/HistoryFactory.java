package artifactFactory.factories;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import core.artifactFactory.deltaGenerator.BaseChangeLogItem;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

public class HistoryFactory {	

	private static int changeCountCurArtifact = 0;
	
	@SuppressWarnings("unchecked")
	public ArrayList<BaseChangeLogItem> buildChangeLog(Map<String, Object> artifactData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		changeCountCurArtifact = 0;

		Map<String, Object> changeLog = (Map<String, Object>) artifactData.get("changelog");
		ArrayList<Object> histories = (ArrayList<Object>) changeLog.get("histories");
		String correspondingArtifactKey = (String) artifactData.get("key");
		String correspondingArtifactId = artifactData.get("id").toString();
		ArrayList<BaseChangeLogItem> changeLogItems = new ArrayList<BaseChangeLogItem>();

		for(int i=0; i<histories.size(); i++) {
			changeLogItems.addAll(buildChangeLog(correspondingArtifactKey, correspondingArtifactId, (Map<String, Object>) histories.get(i)));
		}
		
		return changeLogItems;
		
 	}

	
	@SuppressWarnings("unchecked")
	public ArrayList<BaseChangeLogItem> buildChangeLog(String correspondingArtifactIdInSource, String correspondingArtifactId, Map<String, Object> itemData) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		ArrayList<BaseChangeLogItem> changeLogItems = new ArrayList<BaseChangeLogItem>();
		ArrayList<Map<String, Object>> items = (ArrayList<Map<String, Object>>) itemData.get("items");
		String from, to;

		
		for(int i=0; i<items.size(); i++) {
			
			BaseChangeLogItem changeLogItem = new BaseChangeLogItem();

			if(itemData.get("id") == null) {
				changeLogItem.setId("change_" + changeCountCurArtifact + "_" + correspondingArtifactIdInSource);

			} else {
				changeLogItem.setId(itemData.get("id") + "_" + i);
			}

			changeCountCurArtifact++;

			changeLogItem.setCorrespondingArtifactIdInSource(correspondingArtifactIdInSource);
			changeLogItem.setTimeCreated((String) itemData.get("created"));
			changeLogItem.setField((String) items.get(i).get("field"));
			changeLogItem.setFieldType((String) items.get(i).get("fieldtype"));

			if(changeLogItem.getFieldType()==null) {
				changeLogItem.setFieldType((String) items.get(i).get("fieldType"));
			}

			changeLogItem.setFromString((String) items.get(i).get("fromString"));
			changeLogItem.setToString((String) items.get(i).get("toString"));
			changeLogItem.setArtifactId(correspondingArtifactId);
			
			//in case the method is null everything is treated like a string
			from = (String) items.get(i).get("from");
			to = (String) items.get(i).get("to");
			
			//oldValue = typeGetterMapper.map(changeLogItem.getField(), from);
			//newValue = typeGetterMapper.map(changeLogItem.getField(), to);

			
		//	if(oldValue==null) {
			changeLogItem.setOldValue(from);
		//	} else {
		//		changeLogItem.setOldValue(oldValue);
		//	}
			
		//	if(newValue==null) {
			changeLogItem.setNewValue(to);
		//	} else {
		//		changeLogItem.setNewValue(newValue);
		//	}
			
			changeLogItems.add(changeLogItem);
		}
		
		return changeLogItems;
	
	}
	
}
