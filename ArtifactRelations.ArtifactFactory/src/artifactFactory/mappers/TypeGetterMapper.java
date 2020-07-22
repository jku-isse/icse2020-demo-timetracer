package artifactFactory.mappers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import core.artifactFactory.mappers.IdValueMapper;
import core.persistence.IJiraArtifactService;
import core.services.ErrorLoggerServiceFactory;
import jiraconnector.connector.JiraArtifactService;

public class TypeGetterMapper {
		
	private IdValueMapper idTypeMapper, idNameMapper;
	private Map<String, Method> typeToMethod =  new HashMap<String, Method>();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public TypeGetterMapper(IdValueMapper idNameMapper, IdValueMapper idTypeMapper) throws NoSuchMethodException, SecurityException {		
					
			this.idNameMapper = idNameMapper;
			this.idTypeMapper = idTypeMapper;
		
			Class clazz = JiraArtifactService.class;
			clazz.getMethods();
		
			//those are the fields of type jira
			//they are not resolved via name-json
			//since they don't start with a capital letter
			//typeToMethod.put("user", clazz.getMethod("getUser", String.class));	
			typeToMethod.put("user", clazz.getMethod("getUser", String.class));	
			typeToMethod.put("status", clazz.getMethod("getStatus", String.class));	
			typeToMethod.put("project", clazz.getMethod("getProject", String.class));	
			typeToMethod.put("version", clazz.getMethod("getVersion", String.class));	
			typeToMethod.put("issuetype", clazz.getMethod("getIssueType", String.class));
			typeToMethod.put("priority", clazz.getMethod("getPriority", String.class));
			typeToMethod.put("version", clazz.getMethod("getVersion", String.class));
			typeToMethod.put("option", clazz.getMethod("getOption", String.class));

	}

	
	@SuppressWarnings("unchecked")
	public String map(String field, IJiraArtifactService service, String id) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {		
		
		String type = null;
		String itemType;
		String fieldId;
		Method method;
		
		//find the corresponding schema						
		//return a String for creating an instance of that object
		Map<String, String> map = (Map<String, String>) idTypeMapper.map(field);
		
		if(map!=null) { 
			
			type = map.get("type");	
						
			//if the current type is an array, we have to check for the itemType
			//we only get the type directly in case we are dealing with a jiraField
			if(type.equals("array")) {
				itemType = map.get("items");
				return createArrayData(service, id, itemType);
			} else {
				method = typeToMethod.get(type);
				if(method==null) return null;
				if(id!=null) {
					return (String) method.invoke(service, id);
				}
			}
		
		} else {
			
			//in this case we get the name from the value field
			fieldId = idNameMapper.reverseMap(field);
			map = (Map<String, String>) idTypeMapper.map(fieldId);
			
			if(map!=null) {
			
				type = map.get("type");

				if(type.equals("array")) {
					itemType = map.get("items");
					return createArrayData(service, id, itemType);
				} else {
					method = typeToMethod.get(type);
					if(method==null) return null;
					return (String) method.invoke(service, id);
				}
			}
			
		}
			
		ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "TypeGetterMapper: map(" + field + ", " + service + "," + id + "): No method for serializing the field: " + field);
		return null;
		
	}
	

	private String createArrayData(IJiraArtifactService service, String id, String itemType) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		if(id == null || id.length()==0) return null;
				
		Method method;
		
		if(id.charAt(0)=='['){
			id = id.substring(1, id.length()-1);
		}
		
		String[] values = id.split(",");	
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\"array\":[");
				
		method = typeToMethod.get(itemType);	
		
		if(method==null) {
			
			if(itemType.equals("string")) {
			
				for(int i=0; i<values.length; i++) {
							
					if(values[i].charAt(0)==' ') {
						values[i] = values[i].substring(1);
					}
					
					sb.append("{\"value\":\"" + values[i] + "\"}");
				
					if(!(i+1==values.length)) {
						sb.append(",");
					}
					
				}
				
			} else {
				
				ErrorLoggerServiceFactory.getErrorLogger().log(Level.WARNING, "TypeGetterMapper: createArrayData(IJiraArtifactService service, String id, String itemType): No method for serializing the field: " + itemType);
				return null;
			
			}
			
		} else {
		
			for(int i=0; i<values.length; i++) {

				if(values[i].charAt(0)==' ') values[i] = values[i].substring(1);
				sb.append(method.invoke(service, values[i]));
			
				if(!(i+1==values.length)) {
					sb.append(",");
				}	
				
			}	
			
		}	
	
		sb.append("]}");
									
		return sb.toString();
	
	}
	
	
}
