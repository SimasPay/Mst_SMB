/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.sms.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author sandeepjs
 */
public class MFinoServletContextListener implements ServletContextListener {


	public void contextInitialized(ServletContextEvent contextEvent) {
        //LogFactory.init(ConfigurationUtil.getLogRootPath(), "mfino_SMSProxy.log");
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
