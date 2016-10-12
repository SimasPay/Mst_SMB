package com.mfino.scheduler.service.interceptor;

import java.lang.reflect.Field;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class OpenSessionMethodInterceptor implements MethodInterceptor{
	private static Logger log = LoggerFactory.getLogger(OpenSessionMethodInterceptor.class);
	@Override
	public Object invoke(MethodInvocation target) throws Throwable {
		SessionFactory sessionFactory = null;
		Session session = null;
		Object obj = target.getThis();
		Field field = obj.getClass().getDeclaredField("txManager");
		field.setAccessible(true);
		HibernateTransactionManager txManager = (HibernateTransactionManager) field.get(obj);
		sessionFactory = txManager.getSessionFactory();
		session = sessionFactory.getCurrentSession();
		if(!TransactionSynchronizationManager.hasResource(sessionFactory)){
		log.info("Opening and Binding Session for thread : "+ Thread.currentThread().getName());
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		}
		target.proceed();
		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
		SessionFactoryUtils.closeSession(sessionHolder.getSession());
		log.info("Closing and Un-Binding Session for thread : "+ Thread.currentThread().getName());
		return null;
	}

	

}
