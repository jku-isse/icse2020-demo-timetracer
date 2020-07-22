package core.artifactFactory.deltaGenerator;

import core.base.ChangeLogItem;

public interface IDeltaGenerator {
 
	public ChangeLogItem buildChangeLog( BaseChangeLogItem baseChangeLogItem);
	
}
