/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mailer;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.hibernate.Timestamp;
import java.util.Date;

/**
 *
 * @author admin
 */
public abstract class NotificationHandler {

	public abstract boolean handle();

	/**
	 *
	 */
	protected TransactionsLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
		MfinoServiceProviderDAO mfinoServiceProviderDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		mFinoServiceProvider msp = mfinoServiceProviderDAO.getById(1L);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
}
