/**
 * 
 */
package com.mfino.smsalerts.listeners;

import javax.servlet.ServletContextEvent;

/**
 * @author Deva
 *
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {

	
	public void contextInitialized(ServletContextEvent contextEvent) {
/*        MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
        HibernateUtil.getCurrentSession().beginTransaction();
        FIXMessageHandler.msp = mfinoServiceProviderDAO.getById(1L);
        HibernateUtil.getCurrentTransaction().commit();*/
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}