package c4s.connection;

import java.io.IOException;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.ExceptionHandler;
import com.rabbitmq.client.TopologyRecoveryException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AlreadyClosedException;
import com.google.gson.Gson;

public class AMQPPublisher {

	private static Logger log = LogManager.getLogger("AMQPPublisher");
	private Gson gson = new Gson();

	private ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private static String EXCHANGE_NAME;
	
	AMQPPublisher() {
		this(new Properties());
	}

	AMQPPublisher(Properties config) {
		factory = new ConnectionFactory();
		factory.setHost(config.getProperty("amqpHost", "localhost"));
	    factory.setPort(Integer.parseInt(config.getProperty("amqpPort", ""+ConnectionFactory.DEFAULT_AMQP_PORT)));
	    factory.setUsername(config.getProperty("amqpUser", "user"));
	    factory.setPassword(config.getProperty("amqpPassword", "bitnami"));
	    factory.setAutomaticRecoveryEnabled(true);
	    factory.setNetworkRecoveryInterval(5000);
	    EXCHANGE_NAME = config.getProperty("amqpQueueName", "qacheckqueue");
		connect();
	}
	
	private boolean connect() {
	    try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(EXCHANGE_NAME, "direct");
			log.info("connected");
			return true;
		} catch (IOException | TimeoutException e) {
			log.error("AMQP connection couldn't be established "+e);
		}
	    return false;
	}
	
	ResponseMessage sendToAMQP(String json, String topic) {
		boolean connected = true;
		if (channel == null) {
			log.warn("channel is null, try reconnecting..");
			connected = connect();
		}
		if (connected){
			try {
				final String corrId = UUID.randomUUID().toString();

		        String replyQueueName = channel.queueDeclare().getQueue();
		        AMQP.BasicProperties props = new AMQP.BasicProperties
		                .Builder()
		                .correlationId(corrId)
		                .replyTo(replyQueueName)
		                .build();
				channel.basicPublish(EXCHANGE_NAME, topic, props, json.getBytes("UTF-8"));
				log.debug(json + " was sent");
				final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
		       
		        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
		            if (delivery.getProperties().getCorrelationId().equals(corrId)) {
		                response.offer(new String(delivery.getBody(), "UTF-8"));
		            }
		        }, consumerTag -> {
		        });

		        String result = response.poll(3, TimeUnit.SECONDS);
		        ResponseMessage rm = gson.fromJson(result, ResponseMessage.class);
		        channel.basicCancel(ctag);
	        
		        return rm;
			} catch (IOException | NullPointerException | AlreadyClosedException e) {
				log.error(e);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
		return null;
	}
}
