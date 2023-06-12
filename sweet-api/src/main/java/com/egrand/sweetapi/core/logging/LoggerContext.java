package com.egrand.sweetapi.core.logging;


import com.egrand.sweetapi.core.config.WebSocketServer;

import java.io.IOException;

/**
 * 日志上下文
 */
public interface LoggerContext {

	String LOGGER_NAME = "esb";

	ThreadLocal<String> SESSION = new InheritableThreadLocal<>();

	/**
	 * 打印日志
	 * re
	 *
	 * @param logInfo 日志信息
	 */
	static void println(String logInfo) {
		// 获取SessionId
		String sessionId = SESSION.get();
		if (sessionId != null) {
			try {
				WebSocketServer.sendInfo("log," + logInfo, sessionId);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 移除ThreadLocal中的sessionId
	 */
	static void remove() {
		SESSION.remove();
	}

	/**
	 * 生成appender
	 */
	void generateAppender();
}
