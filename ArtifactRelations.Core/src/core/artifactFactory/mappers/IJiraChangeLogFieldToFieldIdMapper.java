package core.artifactFactory.mappers;

public interface IJiraChangeLogFieldToFieldIdMapper {

	/**
	 * if the given fieldIdentifier already is a valid fieldId the input will be returned,
	 * in case it is not the we are dealing with the fieldName, which will then be resolved to id.
	 * this method assumes that the input is taken from a BaseChangeLogItem-JSON-File
	 */	
	public String map(String field);
	
}
