package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.TransactionsLog;
import com.mfino.service.TransactionsLogCoreService;

@Service("TransactionsLogCoreServiceImpl")
public class TransactionsLogCoreServiceImpl implements TransactionsLogCoreService {

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(TransactionsLog tl){
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		transactionsLogDAO.save(tl);
	}
}
