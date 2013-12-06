/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mock.integrationtestharness.commons;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sunil
 */
public class ITHLogger {

    public static Logger getLogger() {
        return log;

    }
    private static Logger log = null;

    static {
        log = LoggerFactory.getLogger(ITHLogger.class);
    }

    private ITHLogger() {
    }

    
    public static Logger getInstance(String name) {
        return LoggerFactory.getLogger(name);
    }
    public static Logger logger;
}
