package core.artifactFactory.deltaGenerator;

import core.base.ChangeLogItem;

public interface IDeltaGenerator {
 
	public ChangeLogItem buildChangeLog( JiraChangeLogItem jiraChangeLogItem);
	
}
