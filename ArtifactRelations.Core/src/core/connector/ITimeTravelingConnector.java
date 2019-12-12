package core.connector;

import java.sql.Timestamp;

public interface ITimeTravelingConnector extends IConnector {

	/**
	 * 
	 * lets the database behind this connector travel
	 * to the given point in time.
	 * 
	 * @param ts
	 */
	public void travelTo(Timestamp ts);
	
	/**
	 * 
	 * travels from current time of the database to
	 * to the future or past in which a change of one of the specified artifact's
	 * did occur. 
	 * 
	 * In case no more things are left to do or undo, 
	 * the database will be completely updated or 
	 * the exact opposite will be the case.
	 * 
	 * @param ts
	 * @param backInTime
	 */
	public void travelToNextChange(boolean backward, String...artifactKeys);
	
	/**
	 * 
	 * returns the current location in time
	 * of this connector's replayableSession.
	 * 
	 * @return
	 */
	public Timestamp getCurrentTime();
	
}
