package core.artifactFactory.mappers;

import java.util.Map;

import core.artifactFactory.typeFactories.IFieldTypeFactory;

public abstract class AbstractIdFactoryMapper {

	protected IdValueMapper idTypeMapper;
	@SuppressWarnings("rawtypes")
	protected IFieldTypeFactory resultFactory;
	protected boolean deserialize;
	@SuppressWarnings("rawtypes")	
	protected Map<String, IFieldTypeFactory> typeToFactory;

	/**
	 * for the construction a map for resolving types  to their IFieldTypeFactories<>
	 * and id's to their types t has to be provided as well as the information
	 * if the factory is used for de/serialization
	 */	
	@SuppressWarnings("rawtypes")
	public AbstractIdFactoryMapper(IdValueMapper idTypeMapper, Map<String, IFieldTypeFactory> typeToFactory, boolean deserialize) {		
		
		this.deserialize = deserialize;
		this.idTypeMapper = idTypeMapper;	
		this.typeToFactory = typeToFactory; 
		
	}
	
	/**
	 * given a fieldId, this method resolves it to the proper IFieldTypeFactory<>,
	 * which should be used to build a specific object from the fields content
	 */	
	@SuppressWarnings("rawtypes")
	public abstract IFieldTypeFactory map(String id); 	
	
	
}
