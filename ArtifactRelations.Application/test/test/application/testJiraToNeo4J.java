package test.application;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import application.connector.JSONToNeo4J;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import application.connector.JiraToNeo4J;

public class testJiraToNeo4J {

	private JSONToNeo4J jtn4j;
	
	@Before
	public void init() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {
		
		jtn4j = new JSONToNeo4J();
		
	}
	
	@Test
	public void testTransferItem() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
	//	jtn4j.purgeNeo4Database();			
		jtn4j.issueToNeo4J("UAV-242");

	}
	
	@Test
	public void testfillDatabase() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		jtn4j.purgeNeo4Database();			
		jtn4j.fetchCompleteServiceDatabase();

	}
	
	@Test
	public void testUpdateRelationMemory() throws JsonParseException, JsonMappingException, IOException {
		
		jtn4j.updateRelationMemory();

	}
	
	
	@Test
	public void testUpdateDatabase() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		jtn4j.fetchDatabaseDelta();

	}
	
}
