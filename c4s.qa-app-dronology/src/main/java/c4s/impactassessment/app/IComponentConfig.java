package c4s.impactassessment.app;

public interface IComponentConfig {

	void setupNeo4JSessionFactory();
	
	void setupIAMQPConsumer();

	void configureRulesFinishedFiringEventHandlerBinding();

	void configureRuleEvaluationSubsystemBinding();

	void configureNeo4JOGMBinding();

	void configureTaskStateTransitionEventPublisherBinding();

	void configureKSessionBinding();
	
	void configureIAMQPConsumerBinding();

	void configureJiraEventToKnowledgebasePusherBinding();

	void configureAnalyticsInstrumentation();

	void setupIConnector();

	void configureIConnectorBinding();

	void configureITimeTravelingConnectorBinding();

	void setupITimeTravelingConnector();

}