package aws.sqs.client.consumer;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import aws.sqs.client.config.SqsProperties;
import aws.sqs.client.model.TimeEvent;

public class Task implements Runnable {
	private static final Logger LOOGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Message message;
	private final AmazonSQS sqsClient;
	private final TimeEventHandler messageHandler;
	private final SqsProperties sqsProperties;
	private ObjectMapper objectMapper = new ObjectMapper();

	public Task(AmazonSQS sqsClient, TimeEventHandler messageHandler, Message message, SqsProperties sqsProperties) {
		this.message = message;
		this.sqsClient = sqsClient;
		this.messageHandler = messageHandler;
		this.sqsProperties = sqsProperties;
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	}

	@Override
	public void run() {
		TimeEvent event;
		try {
			event = objectMapper.readValue(message.getBody(), messageHandler.messageType());
			messageHandler.handle(event);
			acknowledgeMessage(message);
		} catch (JsonProcessingException e) {
			acknowledgeMessage(message);
			LOOGER.error("JsonProcessingException deletd the message from queue",e);
		}
		 
	}

	private void acknowledgeMessage(final Message sqsMessage) {
		sqsClient.deleteMessage(sqsProperties.getQueueUrl(), sqsMessage.getReceiptHandle());
		LOOGER.info("Acknowleged message {}", sqsMessage.getMessageId());
	}
}
