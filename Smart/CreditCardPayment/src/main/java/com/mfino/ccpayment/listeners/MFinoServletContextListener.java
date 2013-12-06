package com.mfino.ccpayment.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;

import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.util.EncryptionUtil;

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
        
		try {
			//Registers the String Encryptor
			HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
			registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
			//Register the Unique String Encryptor
			registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
			
			registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
			
			// Loading the permissions data.
//			Authorization.Init();
		} catch (Exception e) {
			throw new MfinoRuntimeException(e);
		}
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
    }
}
