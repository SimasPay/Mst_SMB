package com.mfino.dbcopytool.persistence;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class ReportDbHibernateUtil {

	private static  SessionFactory sessionFactory = buildSessionFactory();
	
	private static SessionFactory buildSessionFactory() {
		System.out.println("Initial SessionFactory creation report db");
		try {
			return new Configuration().configure("reportdb.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.print(ex);
			throw new ExceptionInInitializerError(ex);
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
