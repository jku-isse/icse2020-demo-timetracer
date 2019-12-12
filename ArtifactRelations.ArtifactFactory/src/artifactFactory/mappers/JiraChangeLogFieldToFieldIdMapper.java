package artifactFactory.mappers;

import java.util.Map;

import core.artifactFactory.mappers.IJiraChangeLogFieldToFieldIdMapper;
import core.artifactFactory.mappers.IdValueMapper;

public class JiraChangeLogFieldToFieldIdMapper implements IJiraChangeLogFieldToFieldIdMapper{
		
	private IdValueMapper idTypeMapper, idNameMapper;
	
	public JiraChangeLogFieldToFieldIdMapper(IdValueMapper idNameMapper, IdValueMapper idTypeMapper){		
					
			this.idNameMapper = idNameMapper;
			this.idTypeMapper = idTypeMapper;

	}


	@Override
	@SuppressWarnings({ "unchecked" })
	public String map(String field) {		
		
		String fieldId;
		
		Map<String, String> map = (Map<String, String>) idTypeMapper.map(field);
		
		if(map!=null) { 	
		
			return field;			
			
		} else {
			
			fieldId = idNameMapper.reverseMap(field);			
			return fieldId;	
			
		}			
		
	}
	
}
