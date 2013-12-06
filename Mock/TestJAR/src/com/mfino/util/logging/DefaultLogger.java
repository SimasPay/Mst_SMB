package com.mfino.util.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sandeepjs
 */
@Deprecated //should use the com.mfino.util.LogFactory
public class DefaultLogger {

    private static Logger log = null;


    /*
     * LOG levels from least serious to most serious.
     *
     * TRACE
     * DEBUG
     * INFO
     * WARN
     * ERROR
     *
     * Note: 1) If we use log4j by default trace is not logged.
     * Note: 2) There is no FATAL log-level.
     *
     */
    static {
        log = LoggerFactory.getLogger(DefaultLogger.class);
    }

    private DefaultLogger() {
    }

    //use named logger when you need to log into different appender
    //other than the default ones
    public static Logger getInstance(String name) {
        return LoggerFactory.getLogger(name);
    }

    public static void debug(String logMessage) {
        if (log.isDebugEnabled()) {
            log.debug(logMessage);
        }
    }

    public static void warn(String logMessage) {
        if (log.isWarnEnabled()) {
            log.warn(logMessage);
        }
    }

    public static void error(String logMessage) {
        if (log.isErrorEnabled()) {
            //always log the current stack trace when error
            log.error(logMessage, new Throwable());
        }
    }

    public static void error(String string, Throwable ex) {
        if (log.isErrorEnabled()) {
            log.error(string, ex);
        }
    }

    public static void info(String logMessage) {
        if (log.isInfoEnabled()) {
            log.info(logMessage);
        }
    }

    public static void trace(String logMessage) {
        if (log.isTraceEnabled()) {
            log.trace(logMessage);
        }
    }
}
