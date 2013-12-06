/**
 * 
 */
package com.mfino.scheduler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.scheduler.service.BaseService;

/**
 * Default implementation of BaseService.
 * 
 * All Scheduler services should extend this class.
 * 
 * @author Chaitanya
 *
 */
public class BaseServiceImpl implements BaseService{
	
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	
	private HibernateSessionHolder hibernateSessionHolder = null;
	
	public HibernateSessionHolder getHibernateSessionHolder() {
		return hibernateSessionHolder;
	}

	public void setHibernateSessionHolder(
			HibernateSessionHolder hibernateSessionHolder) {
		this.hibernateSessionHolder = hibernateSessionHolder;
	}
	
}
