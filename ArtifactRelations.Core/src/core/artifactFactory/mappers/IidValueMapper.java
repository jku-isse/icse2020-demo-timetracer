package core.artifactFactory.mappers;

public interface IidValueMapper {
 
	/**
	 * fetches the item of the map with the given id 
	 */	
	public Object map(String id);
	
	/**
	 * given a value looks for the key that maps to it,
	 * returns the latest occurence in case of duplicate values
	 */	
	public String reverseMap(String value);

}
