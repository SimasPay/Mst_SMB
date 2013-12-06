/**
 * 
 */
package com.mfino.service;

import com.mfino.service.impl.MoneyServiceImpl;

/**
 * @author Bala sunku
 *
 */
public class CoreServiceFactory {
	
	private static CoreServiceFactory factory;
	
	private HibernateService hibernateService;
	

	private CoreServiceFactory(){
		
	}
	
	public static CoreServiceFactory getInstance(){
		if(factory==null)
		{
			factory = new CoreServiceFactory();
		}
		return factory;
	}
	
	public MoneyService getMoneyService() {
		MoneyServiceImpl moneyService = new MoneyServiceImpl();
		return moneyService;
	}
	
	public void setHibernateService(HibernateService hibernateService){
		this.hibernateService = hibernateService;
	}
	
	public HibernateService getHibernateService(){
		return hibernateService;
	}
}
