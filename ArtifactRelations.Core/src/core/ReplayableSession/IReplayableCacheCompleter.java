package core.ReplayableSession;

public interface IReplayableCacheCompleter {

	/** 
	* after a creating an instance of ReplayableSession, it becomes necessary
	* to fetch items from the database, which in the past would have been
	* a part of the session and add them as well as their history to the session
	*/
	public void completeReplayableCache();
	
}
