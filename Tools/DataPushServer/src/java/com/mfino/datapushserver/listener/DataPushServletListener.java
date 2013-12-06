/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.datapushserver.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Web application lifecycle listener.
 * @author admin
 */

public class DataPushServletListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        //LogFactory.init(ConfigurationUtil.getLogRootPath(), "mfino_DataPush.log");     
    }

    public void contextDestroyed(ServletContextEvent sce) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}