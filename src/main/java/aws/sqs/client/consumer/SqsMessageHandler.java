package aws.sqs.client.consumer;

public interface SqsMessageHandler<T> {

	void handle(T message);
	 Class<T> messageType();

}
