package aws.sqs.client.consumer;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.Message;

import aws.sqs.client.config.SqsProperties;

@Service
public class MessagePoller {

	private static final Logger LOOGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Autowired
	private TimeEventHandler messageHandler;
	@Autowired
	private SqsMessageFetcher messageFetcher;

	@Autowired
	@Qualifier("taskExecutorPool")
	private ThreadPoolTaskExecutor poolTaskExecutor;

	@Autowired
	private AmazonSQS sqsClient;

	@Autowired
	@Qualifier("queueThreadPoolExecutor")
	private ScheduledThreadPoolExecutor pollerThreadPool;

	@Autowired
	private SqsProperties sqsProperties;

	@PostConstruct
	void start() {
		checkQueueExists();
		LOOGER.info("starting SqsMessagePoller");
		for (int i = 0; i < pollerThreadPool.getCorePoolSize(); i++) {
			LOOGER.info("starting SqsMessagePoller - thread {}", i);
			// schedule based on handler poll queue size and active treads ...
			// for throtlling the messages ...Else the tasks will be rejected..
			pollerThreadPool.scheduleWithFixedDelay(this::poll, 1, sqsProperties.getPollDelay(), TimeUnit.SECONDS);
		}
	}

	private void checkQueueExists() {
		final GetQueueUrlResult getQueueUrlResult = sqsClient.getQueueUrl(sqsProperties.getQueueUrl());
		LOOGER.info("Recieved queue url {}", getQueueUrlResult.getQueueUrl());
		final GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(
				getQueueUrlResult.getQueueUrl()).withAttributeNames("All");
		final GetQueueAttributesResult queueAttributes = sqsClient.getQueueAttributes(getQueueAttributesRequest);
		final String visbilityTimeoUt = queueAttributes.getAttributes().get("VisibilityTimeout");
		sqsProperties.setVisibilityTimeOut(Integer.parseInt(visbilityTimeoUt));
	}

	@PreDestroy
	void stop() {
		LOOGER.info("stopping SqsMessagePoller");
		pollerThreadPool.shutdownNow();
		poolTaskExecutor.destroy();
	}
	
	private void poll() {
		try {
			final List<Message> messages = messageFetcher.fetchMessages();
			for (final Message sqsMessage : messages) {
				poolTaskExecutor.submit(new Task(sqsClient, messageHandler, sqsMessage, sqsProperties));
			}
		} catch (final Exception e) {
			// We dont want the scheduler to be stopped
			LOOGER.error("Error while processing", e);
		}
	}

	

}
