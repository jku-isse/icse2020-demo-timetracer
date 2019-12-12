package core.services;

import core.ReplayableSession.IReplayableSession;

public class ReplayableSessionServiceFactory {
	
	public static boolean isInitialized = false;
	private static IReplayableSession replayableSession;
	
	public static IReplayableSession getCurrentReplayableSession() {
		if (!isInitialized)
			throw new RuntimeException("No Session is assigned");
		return replayableSession;
	}
	
	public static void assignSession(IReplayableSession replayableSession) {
		isInitialized = true;
		ReplayableSessionServiceFactory.replayableSession = replayableSession;
	}
	
	public static void checkOutSession() {
		isInitialized = false;
		ReplayableSessionServiceFactory.replayableSession = null;
	}
	
}