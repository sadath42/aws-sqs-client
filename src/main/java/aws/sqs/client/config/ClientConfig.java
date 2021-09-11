package aws.sqs.client.config;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

@Configuration
public class ClientConfig {

	private static final Logger LOOGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	@Autowired
	private SqsProperties sqs;

	@Bean
	public AmazonSQS amazonSQS() {
		return AmazonSQSClientBuilder.standard().withRegion(Regions.AP_SOUTHEAST_2)
				.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
	}

	// If value is higher their will be visibility time out and
	// And duplicate message will be processed.If a message takes 30secs to
	// process..
	// then 100th message will be processed at (100/10*30)300th second which should
	// be greater than
	// visibility // time out.. To avoid duplicates..
	/*
	 * Re-Queues a rejected final into the thread pool's blocking queue, making the
	 * submitting thread wait until the threadpool has capacity again.
	 */
	@Bean("taskExecutorPool")
	public ThreadPoolTaskExecutor taskExecutorPool() {

		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(sqs.getCorePoolSize());
		// We shall e tracking the queue size to throtel the message consumption...
		executor.setMaxPoolSize(sqs.getMaxPoolSize());
		executor.setQueueCapacity(sqs.getQueueCapacity());
		executor.setThreadNamePrefix("taskExecutor_thread");
		executor.setRejectedExecutionHandler((r, thexecutor) -> {
			try {
				LOOGER.warn("Queue is full hence  size is {} waiting for the queue capacity",
						thexecutor.getQueue().size());
				thexecutor.getQueue().put(r);
			} catch (final InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		executor.initialize();
		return executor;
	}
}
