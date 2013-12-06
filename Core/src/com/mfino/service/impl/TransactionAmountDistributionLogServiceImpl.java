package com.mfino.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionAmountDistributionLogDAO;
import com.mfino.dao.query.TransactionAmountDistributionQuery;
import com.mfino.domain.TransactionAmountDistributionLog;
import com.mfino.service.TransactionAmountDistributionLogService;

@org.springframework.stereotype.Service("TransactionAmountDistributionLogServiceImpl")
public class TransactionAmountDistributionLogServiceImpl implements
		TransactionAmountDistributionLogService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(TransactionAmountDistributionLog tadl){
		TransactionAmountDistributionLogDAO tadlDAO = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
		tadlDAO.save(tadl);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<TransactionAmountDistributionLog> get(TransactionAmountDistributionQuery query){
		TransactionAmountDistributionLogDAO tadlDAO = DAOFactory.getInstance().getTransactionAmountDistributionLogDAO();
		return tadlDAO.get(query);
	}
}
