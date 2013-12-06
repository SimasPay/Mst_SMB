/**
 * 
 */
package com.mfino.service;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mfino.hibernate.session.HibernateSessionHolder;

/**
 * Service Class to handle SessionFactory, HibernateSessionHolder and other Hibernate related functions.
 * 
 * @author Chaitanya
 *
 */
public interface HibernateService {

	public SessionFactory getSessionFactory();
	
	public void setSessionFactory(SessionFactory sessionFactory);
	
	public HibernateSessionHolder getHibernateSessionHolder();
	
	public void setHibernateSessionHolder(HibernateSessionHolder sessionHolder);
	
	public Session getSession();
	
}
