/**
 * 
 */
package com.mfino.webapi.listeners;

import javax.servlet.ServletContextEvent;

import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;

import com.mfino.util.EncryptionUtil;

/**
 * @author Deva
 *
 */
public class ServletContextListener implements javax.servlet.ServletContextListener {

	
	public void contextInitialized(ServletContextEvent contextEvent) {
        //LogFactory.init(ConfigurationUtil.getLogRootPath(), "mfino_webapi.log");
        //Registers the String Encryptor
		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
		//Register the Unique String Encryptor
		registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
		registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
        
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}