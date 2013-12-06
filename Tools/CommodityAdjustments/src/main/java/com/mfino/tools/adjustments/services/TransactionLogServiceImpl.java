package com.mfino.tools.adjustments.services;

import java.util.Date;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.TransactionLogService;
import com.mfino.mce.core.util.MessageTypes;

public class TransactionLogServiceImpl implements TransactionLogService {

	@Override
	public TransactionsLog createTransactionLog(CFIXMsg fix) {

		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(MessageTypes.getMessageCode((CMBase) fix));
		transactionsLog.setMessageData(fix.DumpFields());
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		mFinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;

	}

}
