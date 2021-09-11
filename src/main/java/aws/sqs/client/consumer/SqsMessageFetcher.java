package aws.sqs.client.consumer;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;

import aws.sqs.client.config.SqsProperties;

@Service
class SqsMessageFetcher {
	private static final Logger LOOGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Autowired
  private AmazonSQS sqsClient;
  @Autowired
  private SqsProperties properties;

  List<Message> fetchMessages() {

    final ReceiveMessageRequest request = new ReceiveMessageRequest()
        .withMaxNumberOfMessages(properties.getBatchSize()).withQueueUrl(properties.getQueueUrl())
        .withWaitTimeSeconds((int) properties.getWaitTime()).withAttributeNames("All")
        .withMessageAttributeNames("All");
    final ReceiveMessageResult result = sqsClient.receiveMessage(request);

    if (result.getSdkHttpMetadata().getHttpStatusCode() != 200) {
      LOOGER.error("got error response from SQS queue {}: {}", properties.getQueueUrl(),
          result.getSdkHttpMetadata());
      return Collections.emptyList();
    }

    LOOGER.info("polled {} messages from SQS queue {}", result.getMessages().size(),
        properties.getQueueUrl());

    return result.getMessages();
  }

}
