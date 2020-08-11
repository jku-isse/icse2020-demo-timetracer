package jiraconnector.connector;

import c4s.jiralightconnector.CouchDBIssueCache;
import c4s.jiralightconnector.IssueAgent;
import c4s.jiralightconnector.IssueCache;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import core.persistence.IJiraArtifactService;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.*;
import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

public class CouchDBArtifactService implements IJiraArtifactService {

    private ArrayList<Object> artifacts;
    private Map<String, Object> artifactCacheId, artifactCacheKey;
    private Map<String, Object> issueLinkTypes, names, schema;
    private ArrayList<String> keys;
    private IssueCache cache;

    public CouchDBArtifactService() throws IOException {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("app-testing.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CouchDbProperties dbprops = new CouchDbProperties()
                .setDbName(props.getProperty("jiraCacheCouchDBname", "artifactcache"))
                .setCreateDbIfNotExist(true)
                .setProtocol("http")
                .setHost(props.getProperty("couchDBip", "127.0.0.1"))
                .setPort(Integer.parseInt(props.getProperty("couchDBport", "5984")))
                .setUsername(props.getProperty("jiraCacheCouchDBuser","admin1"))
                .setPassword(props.getProperty("jiraCacheCouchDBpassword","c4simpactassessmentdemo"))
                .setMaxConnections(100)
                .setConnectionTimeout(0);
        CouchDbClient dbClient = new CouchDbClient(dbprops);
        cache = new CouchDBIssueCache(dbClient);

        Map<String, Object> map;
        StringBuilder sb;
        String line;

        keys = new ArrayList<>();
        sb = new StringBuilder();
        BufferedReader br = new BufferedReader (new FileReader("issueKeys_Frequentis_CouchDB.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        keys.addAll((ArrayList<String>) map.get("monitoredIssueKeys"));

        issueLinkTypes = new HashMap<String, Object>();
        sb = new StringBuilder();
        ArrayList<Object> issues = new ArrayList<>();
        br = new BufferedReader (new FileReader("issueLinkTypes.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        issues.addAll((ArrayList<Object>) map.get("data"));

        issues.forEach( issueLinkType -> {
            issueLinkTypes.put((String) ((Map<String, Object>) issueLinkType).get("id"), issueLinkType);
        });

        sb = new StringBuilder();
        br = new BufferedReader (new FileReader("schemaAndNames.json"));
        while((line=br.readLine())!=null) {sb.append(line);}
        br.close();
        map = jsonToMap(sb.toString());
        names = (Map<String, Object>) map.get("names");
        schema = (Map<String, Object>) map.get("schema");


        artifacts = new ArrayList<>();
        Optional<IssueAgent> ia;

        Map<String, Object> artifact, fields, hiostories;

        artifactCacheKey = new HashMap<>();
        artifactCacheId = new HashMap<>();

        for(String key : keys) {
            ia = cache.getFromCache(key);
            if(ia.isPresent()) {

                fields = jsonToMap(ia.get().toJson().toString());

                artifact = new HashMap<>();

                artifact.put("id", fields.get("id"));
                artifact.put("key", fields.get("key"));
                artifact.put("self", fields.get("self"));

                hiostories = new HashMap<>();
                hiostories.put("histories", fields.get("changelog"));
                artifact.put("changelog", hiostories);



                fields.put("issuetype", fields.get("issueType"));
                fields.remove("issueType");
                fields.remove("id");
                fields.remove("key");
                fields.remove("changelog");
                fields.remove("issueFields");
                fields.remove("issuelinks");
                fields.remove("subtasks");
                fields.remove("components");

                removeNullValuesFromMap(fields);

                artifact.put("fields", fields);

                artifacts.add(artifact);
                artifactCacheKey.put(key, artifact);
                artifactCacheId.put(Long.toString(ia.get().getId()), artifact);

            }
        }

    }

    private void removeNullValuesFromMap(Object object) {

        Map<String, Object>  map;
        ArrayList<Object> list;

        if(object instanceof Map) {
            map = (Map<String, Object>) object;
            for (Map.Entry entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    removeNullValuesFromMap(entry.getValue());
                } else {
                    entry.setValue("null");
                }
            }
        } else if(object instanceof ArrayList){
            list = (ArrayList<Object>) object;
            list.forEach(item -> {
                removeNullValuesFromMap(item);
            });
        }

    }

    @Override
    public Map<String, Object> getArtifact(String id) throws JsonParseException, JsonMappingException, IOException {
        return (Map<String, Object>) artifactCacheId.get(id);
    }

    @Override
    public ArrayList<Object> getAllArtifacts() throws JsonParseException, JsonMappingException, IOException {
        return artifacts;
    }

    @Override
    public ArrayList<Object> getAllUpdatedArtifacts(Timestamp timestamp) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getStatus(String statusId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getIssueType(String issueTypeId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getProject(String projectId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getUser(String userKey) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getOption(String optionId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getPriority(String priorityId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getVersion(String versionId) throws JsonParseException, JsonMappingException, IOException {
        return null;
    }

    @Override
    public Map<String, Object> getLinkTypes() throws JsonParseException, JsonMappingException, IOException {
        return issueLinkTypes;
    }

    @Override
    public Map<String, Object> getNames() throws JsonParseException, JsonMappingException, IOException {
        return names;
    }

    @Override
    public Map<String, Object> getSchema() throws JsonParseException, JsonMappingException, IOException {
        return schema;
    }

    @Override
    public String getArtifactIdFromKey(String key) throws JsonParseException, JsonMappingException, IOException {
        Optional<IssueAgent> ia = cache.getFromCache(key);

        if(ia.isPresent()) {
            return Long.toString(ia.get().getId());
        }

        return null;
    }

    private static Map<String, Object> jsonToMap(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, new TypeReference<Map<String, Object>>(){});
    }

}
