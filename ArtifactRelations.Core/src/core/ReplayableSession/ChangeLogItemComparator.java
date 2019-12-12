package core.ReplayableSession;

import java.util.Comparator;

import core.base.ChangeLogItem;

public class ChangeLogItemComparator implements Comparator<ChangeLogItem>{

	//is only used to put changes into the correct order
	//should not be used to query equality, since timestamps are not unique
	@Override
	public int compare(ChangeLogItem item1, ChangeLogItem item2) {
		int res = item1.getTimestamp().compareTo(item2.getTimestamp());
		if(res==0) return 1;
		else return res;
	}

}
