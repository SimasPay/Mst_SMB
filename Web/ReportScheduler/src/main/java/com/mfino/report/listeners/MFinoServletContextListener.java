/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;
import com.mfino.util.EncryptionUtil;

/**
 * 
 * @author sandeepjs
 */
public class MFinoServletContextListener implements ServletContextListener {

	//private ILogger log = null;

	public void contextInitialized(ServletContextEvent contextEvent) {
			HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
			registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
			registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
			registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
		}


	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
