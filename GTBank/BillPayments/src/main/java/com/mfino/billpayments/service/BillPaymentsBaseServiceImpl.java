package com.mfino.billpayments.service;

import org.hibernate.SessionFactory;

import com.mfino.mce.backend.impl.BaseServiceImpl;

/**
 * @author Sasi
 *
 */
public class BillPaymentsBaseServiceImpl extends BaseServiceImpl {

	protected SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
}
