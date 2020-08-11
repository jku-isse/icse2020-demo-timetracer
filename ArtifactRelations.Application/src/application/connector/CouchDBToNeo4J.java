package application.connector;

import artifactFactory.factories.JiraArtifactFactory;
import c4s.jiralightconnector.CouchDBIssueCache;
import c4s.jiralightconnector.IssueAgent;
import c4s.jiralightconnector.IssueCache;
import c4s.jiralightconnector.JiraInstance;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import core.application.userInterface.IServiceToNeo4J;
import core.base.*;
import core.persistence.BasicServices;
import core.persistence.IJiraArtifactService;
import core.services.ErrorLoggerServiceFactory;
import core.services.JiraArtifactFactoryServiceFactory;
import core.services.JiraServiceFactory;
import core.services.Neo4JServiceFactory;
import jiraconnector.connector.CouchDBArtifactService;
import jiraconnector.connector.JSONArtifactService;
import neo4j.connector.Neo4JServiceManager;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

public class CouchDBToNeo4J implements IServiceToNeo4J {

    private JiraArtifactFactory artifactFactory;

    private BasicServices.ArtifactService artifactService;
    private BasicServices.ChangeLogItemService changeLogItemService;
    private BasicServices.StatusService statusService;


    /**
     * Establishes a connection to the service as well as Neo4J
     * and initializes the helperClasses for
     * communicating with both service and database
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws SecurityException
     */
    public CouchDBToNeo4J() throws FileNotFoundException, IOException, NoSuchMethodException, SecurityException {

        Neo4JServiceManager n4jm = new Neo4JServiceManager();

        IJiraArtifactService artifactService = new CouchDBArtifactService();
        JiraServiceFactory.init(artifactService);

        Neo4JServiceFactory.init(n4jm);
        this.artifactService = Neo4JServiceFactory.getNeo4JServiceManager().getArtifactService();
        changeLogItemService = Neo4JServiceFactory.getNeo4JServiceManager().getChangeLogItemService();
        statusService = Neo4JServiceFactory.getNeo4JServiceManager().getStatusService();

        updateRelationMemory();
        ErrorLoggerServiceFactory.init(new ErrorLogger());
        artifactFactory = new JiraArtifactFactory(artifactService.getSchema(), artifactService.getNames());
        JiraArtifactFactoryServiceFactory.init(artifactFactory);

    }




    //this method fetches all data and overwrites the stored artifact
    @Override
    public Artifact issueToNeo4J(String issueKey) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        //first we check if this item is already a part of the database
        String id = JiraServiceFactory.getJiraArtifactService().getArtifactIdFromKey(issueKey);
        ReplayableArtifact artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);

        //secondly the artifactData is fetched from the server
        Object artifactData = JiraServiceFactory.getJiraArtifactService().getArtifact(issueKey);
        updateRelationMemory();

        transportArtifact(artifact, artifactData);

        return artifact;

    }




    //this method fetches all data and overwrites the stored artifact
    @SuppressWarnings("unchecked")
    @Override
    public void fetchCompleteServiceDatabase() throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        //this method can be used to do an initial fetch, were everything is loaded without
        //consideration of already
        ArrayList<Object> issues = JiraServiceFactory.getJiraArtifactService().getAllArtifacts();
        String id;
        ReplayableArtifact artifact;

        //in order to be able to keep the database updated we have to store
        //a status object, which contains a timeStamp holding the last updateTime
        IdentifiableStatus status = new IdentifiableStatus();
        status.setLastUpdate(System.currentTimeMillis());
        statusService.push(status);


        for(int i=0; i<issues.size(); i++) {

            id = ((Map<String, Object>)issues.get(i)).get("id").toString();
            artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);
            transportArtifact(artifact, issues.get(i));
            ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, (i+1) + " of " + issues.size() + " issues have been fetched from Jira and pushed to Neo4J! ");
        }

    }


    @SuppressWarnings("unchecked")
    @Override
    public void fetchDatabaseDelta()
            throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        String id;
        Timestamp lastUpdated = new Timestamp(statusService.fetchStatus().getLastUpdate());
        ArrayList<Object> issues = JiraServiceFactory.getJiraArtifactService().getAllUpdatedArtifacts(lastUpdated);
        ReplayableArtifact artifact;

        IdentifiableStatus status = new IdentifiableStatus();
        status.setLastUpdate(System.currentTimeMillis());
        statusService.push(status);


        for(int i=0; i<issues.size(); i++) {

            id = (String) ((Map<String, Object>)issues.get(i)).get("id");
            artifact = (ReplayableArtifact) artifactService.getArtifact(id, 1);
            transportArtifact(artifact, issues.get(i));
            ErrorLoggerServiceFactory.getErrorLogger().log(Level.INFO, (i+1) + " of " + issues.size() + " issues were updated! ");

        }

    }

    @SuppressWarnings("unchecked")
    public ReplayableArtifact transportArtifact(ReplayableArtifact ra, Object artifactData) throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        if(ra==null) {
            ra = artifactFactory.createArtifact(artifactData);
        } else {
            ra = artifactFactory.updateArtifact(artifactData, ra);
        }

        ArrayList<ChangeLogItem> changeLogItems = artifactFactory.buildChangeLog((Map<String, Object>) artifactData);
        artifactService.addArtifact(ra);
        changeLogItems.forEach(item -> 	{changeLogItemService.addChangeLogItem(item);});

        return ra;
    }

    public void updateRelationMemory() throws JsonParseException, JsonMappingException, IOException {

        BasicServices.RelationMemoryService relationMemoryService = Neo4JServiceFactory.getNeo4JServiceManager().getRelationMemoryService();
        IdentifiableRelationMemory relationMemory = relationMemoryService.fetchRelationMemory();
        relationMemory.setRelationMemory(JiraServiceFactory.getJiraArtifactService().getLinkTypes());
        relationMemoryService.push(relationMemory);

    }

    public void purgeNeo4Database() {
        artifactService.deleteEverything();
    }




    @Override
    public Artifact issueToNeo4J(ReplayableArtifact ra)
            throws JsonParseException, JsonMappingException, IOException, NoSuchMethodException, SecurityException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO Auto-generated method stub
        return null;
    }

}
