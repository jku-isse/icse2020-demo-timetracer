package core.connector;

import core.ReplayableSession.IReplayableSession;

public interface IUpdatedTimeTravelingConnector extends IConnector {

	/**
	 * returns a ReplayableSession for all artifacts located in the 
	 * given database, which allows the user the play back and forth between
	 * the changes stored
	 * 
	 * @return IReplayableSession
	 */
	public IReplayableSession getSessionForEntireDatabase();
	
	/**
	 * returns a ReplayableSession for all artifacts given, taking into account all
	 * of their neighbors until the provided depth.
	 * 
	 * @return IReplayableSession
	 */
	public IReplayableSession getSession(int depth, String...artifactKeys);
	
}
