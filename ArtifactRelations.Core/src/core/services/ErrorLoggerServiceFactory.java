package core.services;

import core.base.ErrorLogger;

public class ErrorLoggerServiceFactory {

	private static boolean isInitialized = false;
	private static ErrorLogger errorLogger;
	
	public static ErrorLogger getErrorLogger() {
		if (!isInitialized)
			throw new RuntimeException("ServiceFactory not initalized");
		return errorLogger;
	}
	
	public static void init(ErrorLogger errorLogger) {
		isInitialized = true;
		ErrorLoggerServiceFactory.errorLogger = errorLogger;
	}

}
