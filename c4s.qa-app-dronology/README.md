# Process Suppport App / Quality Assurance Process Support
## Configuration
Program expects configuration in local-app.properties in same folder as qasupport-x.y.z.SNAPSHOT-shaded.jar

## Example config template in app.properties: 
```
couchDBip=127.0.0.1
couchDBport=5984
jiraCacheCouchDBname=artifactcache
jiraCacheCouchDBuser=admin2
jiraCacheCouchDBpassword=c4simpactassessmentdemo
jiraServerURI=https://localhost:8080
jiraPollIntervalInMinutes=60
jiraConnectorUsername=jiraUserRead
jiraConnectorPassword=jiraPAssRead
jamaServerURI=https:localhost:8091
jamaUser=jamaUserRead
jamaPassword=jamaPassRead
jamaPollIntervalInMinutes=60
jamaOptionalKey=SUPERSECRETKEY
neo4jURI=bolt://localhost
neo4jUser=neo4j
neo4jPassword=c4s.neo4j
amqpHost=localhost
amqpPort=5672
amqpUser=user
amqpPassword=bitnami
rulefolder=rules
```

## Starting
Start via: 
java -cp qasupport-x.y.z-SNAPSHOT-shed.jar c4s.impactassessment.app.ImpactAssessmentAppSetup

## Execution Arguments
### -dontRememberMonitoredItems 
upon startup dont load previously loaded items (jama items, jira issues) and also dont remember them after shutdown.

### -runOffline
Dont connect to jira or jama servers and instead try to serve all jama items and jira issues from the cache. Will throw a null pointer exception if a cache miss leads to a fetch from server.

## Commands
all commands that require input, expect so on the next line followed by ENTER/Linebreak

### help
lists all available commands

### monitorJamaProject
Monitors item changes for a particular jama project identified by id. Jira Items are monitored automatically.

### monitorAllJamaProjects
Monitors item changes for all accessible jama projects, where accessible projects are defined by the credentials of the jama user under which this program is running.

Example use:
```
add[Enter]
470[Enter]
```

### insertFilter
Loads all items identified by a jama filter (use the filter id), extracts the upstream and downstream items, adds them to the cache, and inserts all jama items and jira issue pairs whenever the jama item references a Jira key. This functionality is also available via the webfrontend.
WARNING: make sure the filter only returns jama items. 


### insertJama
Load a single pair of Jama Item and Jira Issue, only added to the rule base when the jama item references a jira issue. This functionality is also available via the webfrontend.

### prefillJamaCache
Loads Jama item types and their pick options into the cache for faster fetching of items later on.

### checkJama
Polls Jama server for updates immediately rather than waiting for next poll interval.

### checkJira
Polls Jira server for updates immediately rather than waiting for next poll interval.

### triggerQA
Inserts for each workflow instance, i.e., Jira Issue, a constraint checking trigger for all quality constraints in the rule base. This is equivalent to clicking check all for each workflow from the web UI. This functionality is also available via the webfrontend.

### printJama
Outputs details of a Jama item for inspecting its properties, usefull when writing new constraints/rules.

### printKB
Outputs the current content of the rule base (for debug purpose). Inspect the log file for details as the console might be too full.

### quit
Closes the program (use CTRL-C if this hangs ;-) After quit, the command line interface will not take any more input.

### Example Execution
```
java -cp qasupport-x.y.z-SNAPSHOT-shed.jar c4s.impactassessment.app.ImpactAssessmentAppSetup -nonotificationsubsystem
[… startup logs appear …]
Press 'help' for available commands
monitorAllJamaProjects
{}
{}
{} 
insertFilter
12345
[… output …]
[ eventually ]
quit
```
