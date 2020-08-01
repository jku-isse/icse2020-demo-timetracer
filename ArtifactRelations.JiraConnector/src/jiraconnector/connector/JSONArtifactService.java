package jiraconnector.connector;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.persistence.IJiraArtifactService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONArtifactService implements IJiraArtifactService {


    private Map<String, Object> artifactMapKey;
    private Map<String, Object> artifactMapId;
    private Map<String, Object> versions;
    private Map<String, Object> projects;
    private Map<String, Object> status;
    private Map<String, Object> priorities;
    private Map<String, Object> users;
    private Map<String, Object> issueTypes;
    private Map<String, Object> issueLinkTypes;
    private Map<String, Object> names;
    private Map<String, Object> schema;


    public JSONArtifactService() throws IOException {
        Map<String, Object> map;
        StringBuilder sb = new StringBuilder();
        String line;

        BufferedReader br = new BufferedReader (new FileReader("Dronology_items.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        ArrayList<Object> issues = (ArrayList<Object>) map.get("issues");
        issues.addAll((ArrayList<Object>) map.get("issues"));

        sb = new StringBuilder();
        br = new BufferedReader (new FileReader("Dronology_items_1.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("issues"));



        artifactMapKey = new HashMap<>();
        artifactMapId = new HashMap<>();
        versions = new HashMap<>();
        projects = new HashMap<>();
        issueLinkTypes = new HashMap<>();
        priorities = new HashMap<>();
        names = new HashMap<>();
        schema = new HashMap<>();
        status = new HashMap<>();
        users = new HashMap<>();
        issueTypes = new HashMap<>();




        issues.forEach( issue -> {
            artifactMapKey.put((String) ((Map<String, Object>) issue).get("key"), issue);
            artifactMapId.put((String) ((Map<String, Object>) issue).get("id"), issue);
        });


        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("projects.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( project -> {
            projects.put((String) ((Map<String, Object>) project).get("id") ,project);
        });




        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("priorities.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( prio -> {
            priorities.put((String) ((Map<String, Object>) prio).get("id"), prio);
        });




        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("users.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( user -> {
            users.put((String) ((Map<String, Object>) user).get("key"), user);
        });




        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("issueTypes.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( issueType -> {
            issueTypes.put((String) ((Map<String, Object>) issueType).get("id"), issueType);
        });



        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("issueLinkTypes.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( issueLinkType -> {
            issueLinkTypes.put((String) ((Map<String, Object>) issueLinkType).get("id"), issueLinkType);
        });


        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("versions.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( version -> {
            versions.put((String) ((Map<String, Object>) version).get("id"), version);
        });



        sb = new StringBuilder();
        issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("status.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( status -> {
            this.status.put((String) ((Map<String, Object>) status).get("id"), status);
        });

        sb = new StringBuilder();
        br = new BufferedReader (new FileReader("namesAndSchema.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        names = (Map<String, Object>) map.get("names");
        schema = (Map<String, Object>) map.get("schema");

    }

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
    }

    @Override
    public Map<String, Object> getArtifact(String id) {
        return (Map<String, Object>) artifactMapId.get(id);
    }

    @Override
    public ArrayList<Object> getAllArtifacts() {
        ArrayList<Object> issues = new ArrayList<>();
        issues.addAll(artifactMapId.values());
        return issues;
    }

    @Override
    public ArrayList<Object> getAllUpdatedArtifacts(Timestamp timestamp)  {
        ArrayList<Object> issues = new ArrayList<>();
        return issues;
    }

    @Override
    public Map<String, Object> getStatus(String statusId) {
        Object o = status.get(statusId);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;
    }


    @Override
    public Map<String, Object>  getIssueType(String issueTypeId)  {
        Object o = issueTypes.get(issueTypeId);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;    }


    @Override
    public Map<String, Object>  getProject(String projectId) {
        Object o = projects.get(projectId);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;
    }


    @Override
    public Map<String, Object>  getUser(String userKey)  {
        Object o = users.get(userKey);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;
    }


    @Override
    public Map<String, Object>  getOption(String optionId)  {
        return null;
    }

    @Override
    public Map<String, Object>  getPriority(String priorityId) {
        Object o = priorities.get(priorityId);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;
    }

    @Override
    public Map<String, Object>  getVersion(String versionId) {
        Object o = versions.get(versionId);
        if(o != null) {
            return (Map<String, Object>) o;
        }
        return null;
    }


    @Override
    public Map<String, Object> getLinkTypes() {

        Map<String, Object> result = new HashMap<>();

        issueLinkTypes.values().forEach(linkType -> {
            result.put((String) ((Map<String, Object>) linkType).get("id"), linkType);
        });

        return result;
    }

    @Override
    public Map<String, Object> getNames() {
        return names;
    }

    @Override
    public Map<String, Object> getSchema() {
        return schema;
    }

    @Override
    public String getArtifactIdFromKey(String key) {
        return (String) ((Map<String, Object>) (artifactMapKey.get(key))).get("id");
    }
}
