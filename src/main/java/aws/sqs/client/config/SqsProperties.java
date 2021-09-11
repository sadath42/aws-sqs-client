package aws.sqs.client.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "client")
@Configuration
public class SqsProperties {

	private int batchSize;

	private String queueUrl;

	private long waitTime;

	private long pollDelay;

	private int queueCapacity;

	private int corePoolSize;

	private int maxPoolSize;

	private int visibilityTimeOut;

	private int maxRetry;

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public String getQueueUrl() {
		return queueUrl;
	}

	public void setQueueUrl(String queueUrl) {
		this.queueUrl = queueUrl;
	}

	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

	public long getPollDelay() {
		return pollDelay;
	}

	public void setPollDelay(long pollDelay) {
		this.pollDelay = pollDelay;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getVisibilityTimeOut() {
		return visibilityTimeOut;
	}

	public void setVisibilityTimeOut(int visibilityTimeOut) {
		this.visibilityTimeOut = visibilityTimeOut;
	}

	public int getMaxRetry() {
		return maxRetry;
	}

	public void setMaxRetry(int maxRetry) {
		this.maxRetry = maxRetry;
	}

}
