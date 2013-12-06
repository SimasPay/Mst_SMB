/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.isorequests.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.IsoArtaClient;

import com.mfino.isorequests.listener.util.MultixConnectionListener;
import com.mfino.util.logging.ILogger;
import com.mfino.util.logging.LogFactory;

/**
 *
 * @author admin
 */
public class ContextListener implements ServletContextListener{
	
	private Logger log = LoggerFactory.getLogger(getClass());
    public void contextInitialized(ServletContextEvent sce) {
        try
        {
        String path =  IsoArtaClient.class.getProtectionDomain().getCodeSource()
                .getLocation().toString();
             path = path.substring(6,path.length()-23);
            System.setProperty("rootPath", path);
            ILogger logger = LogFactory.getLogger();
            MultixConnectionListener listener = new MultixConnectionListener(9999);
            MultixConnectionListener mobile8listener = new MultixConnectionListener(9993);
            MultixConnectionListener xlinklistener = new MultixConnectionListener(9990);
            //ArtajasaBankChannelServer listener = new ArtajasaBankChannelServer();
            listener.start();
            mobile8listener.start();
            xlinklistener.start();
//            Util.client = new IsoArtaClient();
//             Util.client.start();
        }
        catch(Exception error)
        {
          
         log.error(error.getMessage(), error);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {

    }
}
