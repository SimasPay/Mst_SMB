/**
 * 
 */
package com.mfino.service.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.HibernateService;

/**
 * Default implementation of HibernateService
 * 
 * @author Chaitanya
 *
 */
public class HibernateServiceDefaultImpl implements HibernateService{
	
	private SessionFactory sessionFactory;
	
	private HibernateSessionHolder sessionHolder;

	@Override
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public HibernateSessionHolder getHibernateSessionHolder() {
		return sessionHolder;
	}

	@Override
	public void setHibernateSessionHolder(HibernateSessionHolder sessionHolder) {
		this.sessionHolder = sessionHolder;
	}

	@Override
	public Session getSession() {
		return getSessionFactory().getCurrentSession();
	}

}
