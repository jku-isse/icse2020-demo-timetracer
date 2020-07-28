package c4s.connection;

public class AddMessage implements Message {

	public String filterID;
	public String featureID;
	
	public AddMessage(String filterID, String featureID) {
		super();
		this.filterID = filterID;
		this.featureID = featureID;
	}
	
	@Override
	public String toString() {
		return "AddMessage\n"
				+ "   Filter ID: '" + filterID + "'\n"
				+ "   Feature ID: '" + featureID + "'";
	}

}
