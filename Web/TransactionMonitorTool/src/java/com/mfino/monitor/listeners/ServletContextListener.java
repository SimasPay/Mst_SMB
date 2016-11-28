package com.mfino.monitor.listeners;

import javax.servlet.ServletContextEvent;

import org.jasypt.hibernate4.encryptor.HibernatePBEEncryptorRegistry;

import com.mfino.util.EncryptionUtil;

/**
 * @author Satya
 *
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {

	
	public void contextInitialized(ServletContextEvent contextEvent) {
        HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
		//Register the Unique String Encryptor
		registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
		registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
        
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
