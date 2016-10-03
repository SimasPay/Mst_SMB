package com.mfino.tools.adjustments.services;

import java.util.Date;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.TransactionLogService;
import com.mfino.mce.core.util.MessageTypes;

public class TransactionLogServiceImpl implements TransactionLogService {

	@Override
	public TransactionLog createTransactionLog(CFIXMsg fix) {

		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionLog transactionsLog = new TransactionLog();
		transactionsLog.setMessagecode(MessageTypes.getMessageCode((CMBase) fix));
		transactionsLog.setMessagedata(fix.DumpFields());
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		MfinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setMfinoServiceProvider(msp);
		transactionsLog.setTransactiontime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;

	}

}
