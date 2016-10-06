package com.mfino.mce.backend.impl;

import java.util.Date;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.TransactionLogService;
import com.mfino.mce.core.util.MessageTypes;

/**
 * @author sasidhar
 */
public class TransactionLogServiceImpl extends BaseServiceImpl implements TransactionLogService{
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRES_NEW)
	public TransactionLog createTransactionLog(CFIXMsg fix){
		
		log.info("TransactionLogServiceImpl :: createTransactionLog Begin");
		
		CMBase fixMessage = (CMBase)fix;
		TransactionLog transactionLog = null;
		
		try
		{
			transactionLog = coreDataWrapper.saveTransactionsLog(MessageTypes.getMessageCode(fixMessage), fixMessage.DumpFields(), 
					fixMessage.getParentTransactionID());
			
			fixMessage.setTransactionID(transactionLog.getId().longValue());
			if((fixMessage.getParentTransactionID() == null) || (fixMessage.getParentTransactionID() == 0)){
				fixMessage.setParentTransactionID(transactionLog.getId().longValue());
			}
			
			fixMessage.setReceiveTime(new Timestamp(new Date()));
			fixMessage.setTransactionID(transactionLog.getId().longValue());
			
			if(fixMessage.getParentTransactionID() == 0){
				fixMessage.setParentTransactionID(transactionLog.getId().longValue());
			}
		}
		catch(BackendRuntimeException bre){
			log.error("TransactionLogServiceImpl : Error creating TransactionLog messageCode="+fixMessage.getClass(), bre);
			return null;
		}
		
		return transactionLog;
	}
}
