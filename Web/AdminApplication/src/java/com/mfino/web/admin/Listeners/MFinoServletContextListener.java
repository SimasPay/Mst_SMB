/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.web.admin.Listeners;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jasypt.hibernate.encryptor.HibernatePBEEncryptorRegistry;

import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.util.EncryptionUtil;

/**
 * 
 * @author sandeepjs
 */
public class MFinoServletContextListener implements ServletContextListener {

	//private ILogger log = null;

	public void contextInitialized(ServletContextEvent contextEvent) {
		
		/*HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hibernateService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
		try {
			Authorization.Init();
		} finally{
			if(session!=null)
			{
				session.close();
			}
		}*/
		try {
			//Registers the String Encryptor
			HibernatePBEEncryptorRegistry registry = HibernatePBEEncryptorRegistry.getInstance();
			registry.registerPBEStringEncryptor("hibernateStringEncryptor", EncryptionUtil.getStringEncryptor());
//			registry.registerPBEBigDecimalEncryptor("hibernateBigDecimalEncryptor", EncryptionUtil.getBigDecimalEncryptor());
			//Register the Unique String Encryptor
			registry.registerPBEStringEncryptor("hibernateUniqueStringEncryptor", EncryptionUtil.getUniqueStringEncryptor());
			
			registry.registerPBEStringEncryptor("dbPasswordEncryptor", EncryptionUtil.getDBPasswordEncryptor());
			
			// Loading the permissions data.
//			Authorization.Init();
		} catch (Exception e) {
			throw new MfinoRuntimeException(e);
		}
	}

	public void contextDestroyed(ServletContextEvent contextEvent) {
	}
}
