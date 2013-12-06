/**
 * 
 */
package com.mfino.sms.handlers;

import java.util.Date;

import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;

/**
 * @author Deva
 *
 */
public abstract class SMSHandler {
	
	public abstract boolean handle();
	
	/**
	 * 
	 */
	protected TransactionsLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = new TransactionsLogDAO();
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
		MfinoServiceProviderDAO mfinoServiceProviderDAO = new MfinoServiceProviderDAO();
		mFinoServiceProvider msp = mfinoServiceProviderDAO.getById(1L);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
}
