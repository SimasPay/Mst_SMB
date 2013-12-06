/**
 * 
 */
package com.mfino.smsalerts.listeners;

import javax.servlet.ServletContextEvent;

import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.handlers.FIXMessageHandler;

/**
 * @author Deva
 *
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {

	
	public void contextInitialized(ServletContextEvent contextEvent) {
        //LogFactory.init(ConfigurationUtil.getLogRootPath(), "mfino_smsAlerts.log");
        MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
        FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}