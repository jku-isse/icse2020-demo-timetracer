package core.jira;

import core.persistence.IJiraArtifactService;

import java.lang.reflect.Method;

public enum JiraType {

    ASSIGNEE("assignee", "getUser"), CREATOR("creator", "getUser"), REPORTER("reporter", "getUser"),
    ISSUE_TYPE("issuetype", "getIssueType"), STATUS("status", "getStatus"), PRIORITY("priority", "getPriority"),
    PROJECT("project", "getProject"), VERSION("version", "getVersion");

    private String name;
    private Method method;

    JiraType(String name, String methodName) {
        this.name = name;
        try{
            this.method = IJiraArtifactService.class.getMethod(methodName, String.class);
        } catch (NoSuchMethodException nsm) {
            nsm.printStackTrace();
        }
    }

    public String getName(){
        return name;
    }

    public Method getMethod() {
        return method;
    }

}
