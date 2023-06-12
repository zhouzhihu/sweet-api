package com.egrand.sweetapi.plugin.task;


import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("dynamic-api.task")
public class TaskConfig {

	/**
	 * 是否启用定时任务
	 */
	private boolean enable = true;

	/**
	 * 线程池相关配置
	 */
	private final Pool pool = new Pool();

	/**
	 * 关闭时相关配置
	 */
	private final Shutdown shutdown = new Shutdown();

	/**
	 * 线程池前缀
	 */
	private String threadNamePrefix = "esb-task-";

	public Pool getPool() {
		return this.pool;
	}

	public Shutdown getShutdown() {
		return this.shutdown;
	}

	public String getThreadNamePrefix() {
		return this.threadNamePrefix;
	}

	public void setThreadNamePrefix(String threadNamePrefix) {
		this.threadNamePrefix = threadNamePrefix;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public static class Pool {

		/**
		 * 线程池大小
		 */
		private int size = Runtime.getRuntime().availableProcessors();

		public int getSize() {
			return this.size;
		}

		public void setSize(int size) {
			this.size = size;
		}

	}

	public static class Shutdown {

		/**
		 * 关闭时是否等待任务执行完毕，默认为false
		 */
		private boolean awaitTermination;

		/**
		 * 关闭时最多等待任务执行完毕的时间
		 */
		private Duration awaitTerminationPeriod;

		public boolean isAwaitTermination() {
			return this.awaitTermination;
		}

		public void setAwaitTermination(boolean awaitTermination) {
			this.awaitTermination = awaitTermination;
		}

		public Duration getAwaitTerminationPeriod() {
			return this.awaitTerminationPeriod;
		}

		public void setAwaitTerminationPeriod(Duration awaitTerminationPeriod) {
			this.awaitTerminationPeriod = awaitTerminationPeriod;
		}

	}
}
