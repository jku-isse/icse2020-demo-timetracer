package c4s.impactassessment.app;

import c4s.jiralightconnector.IssueAgent;

public interface ICommentConnector {
	
	public boolean addComment(IssueAgent issueAgent, String body);

}
