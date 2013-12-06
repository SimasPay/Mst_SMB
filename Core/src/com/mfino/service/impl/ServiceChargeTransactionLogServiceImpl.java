package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.service.ServiceChargeTransactionLogService;

@Service("ServiceChargeTransactionLogServiceImpl")
public class ServiceChargeTransactionLogServiceImpl implements
		ServiceChargeTransactionLogService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<ServiceChargeTransactionLog> getByStatus(Integer[] status){
		ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		return sctlDAO.getByStatus(status);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW)
	 public void save(ServiceChargeTransactionLog sctl){
			ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
			sctlDAO.save(sctl);
	 }
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	 public List<ServiceChargeTransactionLog> get(ServiceChargeTransactionsLogQuery query){
		 ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		 return sctlDAO.get(query);
	 }
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	 public ServiceChargeTransactionLog getById(Long serviceChargeTransactionLogId){
		 ServiceChargeTransactionLogDAO sctlDAO = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		 return sctlDAO.getById(serviceChargeTransactionLogId);
	 }
}
