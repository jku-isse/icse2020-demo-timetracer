package c4s.connection;

public class ResponseMessage {
	
	public int errorCode;
	public String correlationId;
	public String statusMsg;
	
	@Override
	public String toString() {
		return "ResponseMessage:\n"
				+ "   Request status: '" + statusMsg + "'" + "\n"
				+ "   Error code: '" + errorCode + "'" + "\n"
				+ "   Correlation ID: '" + correlationId + "'";
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public void setCorrelationId(String correlationId) {
		this.correlationId = correlationId;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseMessage other = (ResponseMessage) obj;
		if (correlationId != other.correlationId)
			return false;
		if (errorCode != other.errorCode)
			return false;
		if (statusMsg == null) {
			if (other.statusMsg != null)
				return false;
		} else if (!statusMsg.equals(other.statusMsg))
			return false;
		return true;
	}

}
