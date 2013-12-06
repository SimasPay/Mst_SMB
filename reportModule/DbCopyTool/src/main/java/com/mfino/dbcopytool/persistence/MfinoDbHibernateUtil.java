package com.mfino.dbcopytool.persistence;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MfinoDbHibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();
	private static ApplicationContext ac;
	private static Logger log = LoggerFactory.getLogger("MfinoDbHibernateUtil");

	private static SessionFactory buildSessionFactory() {

		try {
			ac = new ClassPathXmlApplicationContext(
					"spring-datasource-beans.xml");
			SessionFactory sf = (SessionFactory) ac.getBean("sessionFactory");
			return sf;
		} catch (Exception e) {
			log.error("Initial SessionFactory creation failed." + e);
			throw new ExceptionInInitializerError(e);
		}

	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}
