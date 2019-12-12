package core.artifactFactory.factories;

public enum Services {

	Jira("Jira"),	Jama("Jama");
	
	String value;
	
	Services(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
}
