package com.egrand.sweetapi.core.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class LogbackAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {
    @Override
    protected void append(ILoggingEvent event) {
        Formatter formatter = Formatter.create()
                .timestamp(event.getTimeStamp())
                .space()
                .level(event.getLevel().toString())
                .value(" --- [")
                .thread(event.getThreadName())
                .value("] ")
                .loggerName(event.getLoggerName())
                .value(": ")
                .value(event.getFormattedMessage())
                .newline();
        IThrowableProxy proxy = event.getThrowableProxy();
        if (proxy instanceof ThrowableProxy) {
            formatter.throwable(((ThrowableProxy) proxy).getThrowable());
        }
        LoggerContext.println(formatter.toString());
    }
}
