package test.artifactfactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import artifactFactory.factories.JamaArtifactFactory;
import core.base.Artifact;
import core.base.ErrorLogger;
import core.fieldTypes.utility.FieldType;
import core.fieldValues.common.StringFieldValue;
import core.services.ErrorLoggerServiceFactory;
import core.services.Neo4JServiceFactory;
import neo4j.connector.Neo4JServiceManager;

public class BaseTesterJamaArtifactFactory {

	private Map<String, Object> map;
	private Map<String, Object> itemData;
	private Map<String, Object> itemTypeData;
	private JamaArtifactFactory itemFactory;
	private Map<String, Object> schema;
	
	@SuppressWarnings("unchecked")
	@Before
	public void initTestData() throws JsonParseException, JsonMappingException, IOException {
		
		Neo4JServiceManager n4jm = new Neo4JServiceManager(); 				
		Neo4JServiceFactory.init(n4jm);
		ErrorLoggerServiceFactory.init(new ErrorLogger());	
		
		String line;
		StringBuilder sb = new StringBuilder();
		
		BufferedReader br = new BufferedReader (new FileReader("test.json"));		
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		map = jsonToMap(sb.toString());
		sb = new StringBuilder();
		
		br = new BufferedReader (new FileReader("items.json"));		
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		itemData = jsonToMap(sb.toString());
		sb = new StringBuilder();
		
		br = new BufferedReader (new FileReader("itemTypes.json"));		
		while((line=br.readLine())!=null) {sb.append(line);}
		br.close();
		itemTypeData = jsonToMap(sb.toString());
		
		ArrayList<Object> itemTypes = (ArrayList<Object>) itemTypeData.get("itemTypes");
		schema = setUpSchemaForItemTypes(itemTypes);
		itemFactory = new JamaArtifactFactory(schema);
		
	}
	
	@Test
	public void testJamaArtifactFactory() {
		
		Artifact a = itemFactory.createArtifact(map);
		Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().addArtifact(a);

		System.out.println(a.getId());
		System.out.println(a.getIdInSource());
		System.out.println(a.getOrigin());
		System.out.println(a.getProperties());
		System.out.println(a.getRelationsIncoming());
		System.out.println(a.getRelationsOutgoing());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testDeserializeJamaArtifact() throws Exception {
		
		Artifact a = itemFactory.deserialize(Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact("JamaItem10007428"));

		FieldType<StringFieldValue> field = (FieldType<StringFieldValue>) a.getFields().get("description");		
		System.out.println(field.getValue().getValue().get());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testJamaArtifactFactoryWithCompleteDatabase() {
		
		((ArrayList<Object>) itemData.get("items")).forEach( item -> {
			
			String id = (String) ((Map<String, Object>) item).get("_id");
			Artifact a;
			
			if((a=(Artifact) Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().getArtifact(id))==null) {
				Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().addArtifact(itemFactory.createArtifact(item));
			} else {
				try {
					Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService().addArtifact(itemFactory.updateArtifact(item, a));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		});
		
	}
	
	private static Map<String, Object> jsonToMap(String json) throws JsonParseException, JsonMappingException, IOException {	
	  
		ObjectMapper mapper = new ObjectMapper();
	    return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});

	}
	
	@SuppressWarnings("unchecked")
	private Map<String, Object> setUpSchemaForItemTypes(ArrayList<Object> itemTypes) {
		
		
		//TO-DO: there must be a smarter way to get the fieldTypes
		
		//creating a schema for resolving a field to its type
		Map<String, Object> schema = new HashMap<String, Object>();
		
		itemTypes.forEach((itemType) -> {
			
			Map<String, Object> typeData = (Map<String, Object>) itemType;
			typeData = (Map<String, Object>) typeData.get("data");			
			ArrayList<Object> fields = (ArrayList<Object>) typeData.get("fields");
			
			fields.forEach((fieldType) -> {
				
				Map<String, Object> type = (Map<String, Object>) fieldType;				
				schema.put((String) type.get("name"), type.get("fieldType"));
				
			});
			
		});
		
		return schema;
		
	}
	
}

