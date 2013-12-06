package com.mfino.mce.backend.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

import com.mfino.mce.core.CoreDataWrapper;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.MoneyService;

/**
 * All backend services will extend from this.
 * @author sasidhar
 *
 */
public class BaseServiceImpl {
	
	protected MoneyService moneyService;
	
	public Log log = LogFactory.getLog(this.getClass());
	
	protected CoreDataWrapper coreDataWrapper;
	
	protected SessionFactory sessionFactory;
	
	public CoreDataWrapper getCoreDataWrapper() {
		return coreDataWrapper;
	}

	public void setCoreDataWrapper(CoreDataWrapper coreDataWrapper) {
		this.coreDataWrapper = coreDataWrapper;
	}
	
	public BackendResponse createResponseObject() 
	{
		return new BackendResponse();
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	public MoneyService getMoneyService() {
		return moneyService;
	}

	public void setMoneyService(MoneyService moneyService) {
		this.moneyService = moneyService;
	}
}
