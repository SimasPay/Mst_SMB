package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.TransactionLogService;
@Service("TransactionLogServiceImpl")
public class TransactionLogServiceImpl implements TransactionLogService  {
	
	private TransactionsLogDAO tldao = DAOFactory.getInstance().getTransactionsLogDAO();

	/** save transactionLog details
	 * saves transactionLog
	 * @param transactionLog
	 */

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void save(TransactionLog transactionLog){
		tldao.save(transactionLog);
	}
	/**
	 * message , data and mfinoservicepovider with id 1 is set into transactionLog object 
	 * and saved into transactionlogtable
	 * @param messageCode
	 * @param data
	 * @return
	 */

	//@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW)
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public TransactionLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProvider	msp;
	    MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
	    msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
	
	
/**
 * saves details innto transactionLog
 * @param messageCode
 * @param data
 * @param parentTxnID
 * @return
 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public TransactionLog saveTransactionsLog(Integer messageCode, String data,Long parentTxnID)
	{
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProvider	msp;
        MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
        msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		if(parentTxnID!=null)
			transactionsLog.setParenttransactionid(new BigDecimal(parentTxnID));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW,rollbackFor=Throwable.class)
	public TransactionLog getById(Long txnLogID)
	{
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = transactionsLogDAO.getById(txnLogID);
		return transactionsLog;
	}
}
