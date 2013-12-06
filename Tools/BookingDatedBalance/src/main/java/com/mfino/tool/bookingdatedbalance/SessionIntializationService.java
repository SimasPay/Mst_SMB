package com.mfino.tool.bookingdatedbalance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


import com.mfino.dao.DAOFactory;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mfino.service.MoneyService;

/**
 * @author Maruthi
 */
public class SessionIntializationService {

	public Log log = LogFactory.getLog(this.getClass());
	
	
	private SessionFactory sessionFactory;
	private MoneyService moneyService;
	
	public SessionIntializationService(){

	}
	
	public void initializeDependencies(){
		this.moneyService = CoreServiceFactory.getInstance().getMoneyService();
	}
	
	public void initialize(){
		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
		sessionFactory = hibernateService.getSessionFactory();
		Session session = hibernateService.getSessionFactory().openSession();
		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
		sessionHolder.setSession(session);
		DAOFactory.getInstance().setHibernateSessionHolder(sessionHolder);
	}
	
	@Override
	protected void finalize() throws Throwable {
		Session session = DAOFactory.getInstance().getHibernateSessionHolder().getSession();
		if(null != session){
			if(session.isOpen()){
				session.close();
			}
		}
		super.finalize();
	}
	
}
