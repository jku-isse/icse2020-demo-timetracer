package c4s.connection;

public class CheckMessage implements Message {
	
	public String constrId;
	public String constrType;
	public String wfiId;
	public String wftId;
	
	public CheckMessage(String constrId, String constrType, String wfiId, String wftId) {
		super();
		this.constrId = constrId;
		this.constrType = constrType;
		this.wfiId = wfiId;
		this.wftId = wftId;
	}
	
	@Override
	public String toString() {
		return "CheckMessage:\n"
				+ "   QA Constraint ID: '" + constrId + "'" + "\n"
				+ "   QA Constraint type: '" + constrType + "'" + "\n"
				+ "   Workflow instance ID: '" + wfiId + "'" + "\n"
				+ "   Workflow task ID: '" + wftId + "'";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((constrId == null) ? 0 : constrId.hashCode());
		result = prime * result + ((constrType == null) ? 0 : constrType.hashCode());
		result = prime * result + ((wfiId == null) ? 0 : wfiId.hashCode());
		result = prime * result + ((wftId == null) ? 0 : wftId.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CheckMessage other = (CheckMessage) obj;
		if (constrId == null) {
			if (other.constrId != null)
				return false;
		} else if (!constrId.equals(other.constrId))
			return false;
		if (constrType == null) {
			if (other.constrType != null)
				return false;
		} else if (!constrType.equals(other.constrType))
			return false;
		if (wfiId == null) {
			if (other.wfiId != null)
				return false;
		} else if (!wfiId.equals(other.wfiId))
			return false;
		if (wftId == null) {
			if (other.wftId != null)
				return false;
		} else if (!wftId.equals(other.wftId))
			return false;
		return true;
	}
}
