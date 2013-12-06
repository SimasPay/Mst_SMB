package com.mfino.service.impl;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SettlementSchedulerLogsDao;
import com.mfino.domain.SettlementSchedulerLogs;
import com.mfino.service.SettlementSchedulerLogsService;

@org.springframework.stereotype.Service("SettlementSchedulerLogsServiceImpl")
public class SettlementSchedulerLogsServiceImpl implements
		SettlementSchedulerLogsService {
	
	
	private static SettlementSchedulerLogsServiceImpl settlementSchedulerLogsServiceFactory;
	
	public static SettlementSchedulerLogsServiceImpl createInstance(){
		  if(settlementSchedulerLogsServiceFactory==null){
			  settlementSchedulerLogsServiceFactory = new SettlementSchedulerLogsServiceImpl();
		  }
		  
		  return settlementSchedulerLogsServiceFactory;
		 }
		 
	public static SettlementSchedulerLogsServiceImpl getInstance(){
		  if(settlementSchedulerLogsServiceFactory==null){
		   throw new RuntimeException("Instance is not already created");
		  }
		  return settlementSchedulerLogsServiceFactory;
		 }
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SettlementSchedulerLogs getByPartnerServiceId(Long partnerServiceId){
		SettlementSchedulerLogsDao settlementSchedulerDao = DAOFactory.getInstance().getSettlementSchedulerLogsDao();
		return settlementSchedulerDao.getByPartnerServiceId(partnerServiceId);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(SettlementSchedulerLogs settlementSchedulerLogs) {
		SettlementSchedulerLogsDao settlementSchedulerDao = DAOFactory.getInstance().getSettlementSchedulerLogsDao();
		settlementSchedulerDao.save(settlementSchedulerLogs);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SettlementSchedulerLogs getByJobId(String jobId){
		SettlementSchedulerLogsDao settlementSchedulerDao = DAOFactory.getInstance().getSettlementSchedulerLogsDao();
		return settlementSchedulerDao.getByJobId(jobId);
	}
}
