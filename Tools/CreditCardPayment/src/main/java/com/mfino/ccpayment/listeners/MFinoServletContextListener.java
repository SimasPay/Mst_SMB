package com.mfino.ccpayment.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.mfino.security.Authorization;
import com.mfino.util.HibernateUtil;

/**
 * Application Lifecycle Listener implementation class MFinoServletContextListener
 *
 */
public class MFinoServletContextListener implements ServletContextListener {

    /**
     * Default constructor. 
     */
    public MFinoServletContextListener() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //LogFactory.init(ConfigurationUtil.getLogRootPath(), "mfino_ccpayment.log");
        try {
            HibernateUtil.getCurrentTransaction().begin();
            // Loading the permissions data.
            Authorization.Init();
        } finally {
            HibernateUtil.getCurrentTransaction().rollback();
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
    }
}
