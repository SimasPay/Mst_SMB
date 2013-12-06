package com.mfino.monitor.listeners;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;

public class MFinoServletRequestListener implements ServletRequestListener{

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void requestInitialized(ServletRequestEvent sre) {
		log.debug("MfinoServletRequestListener :: requestInitialized() ");
		HttpServletRequest request = (HttpServletRequest)sre.getServletRequest();
		log.debug("MfinoServletRequestListener :: requestInitialized() request.getServletPath()="+request.getServletPath()+", request.getRequestURL()="+request.getRequestURL());
		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		Session session = hibernateService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
	}
	
	@Override
	public void requestDestroyed(ServletRequestEvent sre) {
		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		Session session = sessionHolder.getSession();
		if((session != null) && (session.isOpen())){
			session.close();
		}
	}
}
