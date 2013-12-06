/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.replicatool.listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;

import com.mfino.util.EncryptionUtil;

/**
 * 
 * @author sandeepjs
 */
public class MFinoServletContextListener implements ServletContextListener {


	public void contextInitialized(ServletContextEvent contextEvent) {
		// Registers the String Encryptor
		HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
		registry.registerPBEStringEncryptor("hibernateStringEncryptor",EncryptionUtil.getStringEncryptor());
		// Register the Unique String Encryptor
		registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor",EncryptionUtil.getUniqueStringEncryptor());
		registry.registerPBEStringEncryptor("dbPasswordEncryptor",EncryptionUtil.getDBPasswordEncryptor());

	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
