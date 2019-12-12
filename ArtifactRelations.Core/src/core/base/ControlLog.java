package core.base;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ControlLog {

	private ArrayList<String> entries;
    private String source, subTopic;
    private StringBuilder sb;
	
	public ControlLog(String source) {
		this.source = source;
		entries = new ArrayList<String>();
		sb = new StringBuilder();
	}
	
	public void addLog(String entryMsg) {
		entries.add(entryMsg);
		writeToErrorLog(entryMsg);
	}
	
	public Collection<String> getErrorMessages(){
		return Collections.unmodifiableCollection(entries);
	}
	
	public boolean isEmpty() {
		return entries.isEmpty();
	}
	
	private void writeToErrorLog(String errorMsg) {
			sb.append(source + ": "+ subTopic + ": " + errorMsg + "\n");
	}

	public ArrayList<String> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<String> entries) {
		this.entries = entries;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setSubTopic(String subTopic) {
		this.subTopic = subTopic;
	}
	
	public void writeToFile() {		
		try{            
			FileWriter fw= new FileWriter(source + ".txt");    
			fw.write(sb.toString());    
	        fw.close();          
	    } catch(Exception e){
	    	    	
	    }              
	}
	
}
