/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.listener;

import com.mfino.isorequests.listener.util.Util;
import com.mfino.util.logging.ILogger;
import com.mfino.util.logging.LogFactory;
import javax.servlet.ServletContext;
import test.IsoArtaClient;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author admin
 */
public class ContextListener implements ServletContextListener{
    public void contextInitialized(ServletContextEvent sce) {
        try
        {
        String path =
            IsoArtaClient.class.getProtectionDomain().getCodeSource()
                .getLocation().toString();
        path = path.substring(6,path.length()-23);
            System.setProperty("rootPath", path);
            ILogger logger = LogFactory.getLogger();
            Util.client = new IsoArtaClient();
             Util.client.start();
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
