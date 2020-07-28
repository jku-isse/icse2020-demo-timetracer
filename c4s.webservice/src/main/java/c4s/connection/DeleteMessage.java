package c4s.connection;

import java.util.List;
import java.util.stream.Collectors;

public class DeleteMessage implements Message {

	public List<String> wfiIDs;

	public DeleteMessage(List<String> wfiIDs) {
		super();
		this.wfiIDs = wfiIDs;
	}
	
	@Override
	public String toString() {
		return "DeleteMessage\n"
				+ "   wfiIDs: " + wfiIDs.stream().collect(Collectors.joining(", "));
	}
}
