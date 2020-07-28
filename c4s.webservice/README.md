# Webservice

REST API that visualizes the workflows and their corresponding quality checks that are stored in a Neo4j database in a tabular form. Each line in the table represents one workflow. The workflow tree was traversed via breadth-first-search.

## Basic functionality

The overview of the workflow tasks indicate via the frame color if the quality checks of the task passed or if any of them failed. If the "Quality Check" button is enabled you can see if the task has quality checks appended.

Via the "Quality Check" button you can access the quality checks that are assinged to this workflow task. A modal pops up where all quality checks are listed.
The color indicates the evaluation status of this check
- green = success
- yellow = not yet evaluated
- red = failure

The Modal provides two possibilities to force a quality check via AMQP and the rule engine evaluates the new state.
- "Check" button: Checks only the appropriate quality check document 
- "Check all" button: Checks all quality check documents of this workflow task

### Limitations
- Thus the check via the rule engine takes some time you shouldn't overload the service with check requests, because the engine will work through the check requests  sequentially
- For now you won't get a notification if the messaging service is not available and your message couldn't be sent.
- The webservice won't update by itself if its state change, so you have to refresh the page manually.

## Setup

### Running services in the bitnami VM

##### Start Neo4j:
- sudo service neo4j start -publish=7474:7474 -publish=7687:7687
##### Start RabbitMQ:
- sudo /home/bitnami/rabbit/rabbitmq/scripts/ctl.sh start
##### Start Webservice:
- java -jar /home/binami/webservice/QAWebservice-0.0.2.jar

### Interfaces in host OS

- Neo4j available at port 7474
- RabbitMQ-Manager available at port 15672
- Web-Frontend available at port 8080
	- REST-Endpoints at 8080/api