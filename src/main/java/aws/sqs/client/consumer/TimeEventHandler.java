package aws.sqs.client.consumer;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aws.sqs.client.model.TimeEvent;

public class TimeEventHandler implements SqsMessageHandler<TimeEvent> {
	private static final Logger LOOGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Override
	public void handle(TimeEvent message) {
		LOOGER.info("processing message {}",message);
	}

	@Override
	public Class<TimeEvent> messageType() {
		return TimeEvent.class;
	}

}
