/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.mailer;

import java.util.Date;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.hibernate.Timestamp;

/**
 *
 * @author admin
 */
public abstract class NotificationHandler {

	public abstract boolean handle();

	/**
	 *
	 */
	protected TransactionLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(messageCode);
		transactionsLog.setMessagedata(data);
		MfinoServiceProviderDAO mfinoServiceProviderDAO = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		MfinoServiceProvider msp = mfinoServiceProviderDAO.getById(1L);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
}
