package core.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorLogger {

	private ArrayList<String> errors;
	private static final Logger LOGGER = Logger.getLogger(Logger.class.getName());
	
	public ErrorLogger() {
		
		  Handler consoleHandler = null;
	        Handler fileHandler  = null;
	        try{
	        	
	            consoleHandler = new ConsoleHandler();
	            fileHandler  = new FileHandler("./error.log");
	             
	            LOGGER.addHandler(consoleHandler);
	            LOGGER.addHandler(fileHandler);
	             
	            consoleHandler.setLevel(Level.ALL);
	            fileHandler.setLevel(Level.ALL);
	            
	            LOGGER.setLevel(Level.ALL);        
	            LOGGER.removeHandler(consoleHandler);     
	            
	        }catch(IOException exception){
	            LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
	        }	  	         
		
	}
	
	public void log(Level level, String errorMsg) {
		LOGGER.log(level, errorMsg);
	}

	
	public Collection<String> getErrorMessages(){
		return Collections.unmodifiableCollection(errors);
	}
	
	public boolean isEmpty() {
		return errors.isEmpty();
	}

}
	