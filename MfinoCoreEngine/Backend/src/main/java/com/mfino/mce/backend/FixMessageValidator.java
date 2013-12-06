package com.mfino.mce.backend;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMFIXResponse;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.BackendRuntimeException;
import com.mfino.mce.core.CoreDataWrapper;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MCEUtil;
import com.mfino.mce.core.util.NotificationCodes;

/**
 * @author sasidhar
 * Validates all the fix messages.
 */
public class FixMessageValidator {
	
	
	private static Log log = LogFactory.getLog(FixMessageValidator.class);
	
	/*
	 * For any request, we need to do an initial validation the FIX message, and the we validate the FIX message 
	 * based on the request. 
	 */
	public static BackendResponse validateFixMessage(Integer messageCode, CFIXMsg requestFix, 
			CoreDataWrapper coreDataWrapper, BackendResponse responseFix){
		log.info("FixMessageValidator :: validate()");
		
		CMBase fixMessage = (CMBase)requestFix;
		
		if(fixMessage == null){
			log.info("Invalid FIX message");
			responseFix.setDescription("Fix Message is null");
			responseFix.setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());
			return responseFix;
		}
		
		log.debug(fixMessage.DumpFields());
		
		if(fixMessage.getParentTransactionID() == null){
			fixMessage.setParentTransactionID(0L);
		}
		
		try
		{
			TransactionsLog transactionLog = coreDataWrapper.saveTransactionsLog(messageCode, fixMessage.DumpFields());
			fixMessage.setTransactionID(transactionLog.getID());
			if((fixMessage.getParentTransactionID() == null) || (fixMessage.getParentTransactionID() == 0)){
				fixMessage.setParentTransactionID(transactionLog.getID());
			}
			
			fixMessage.setReceiveTime(new Timestamp(new Date()));
			fixMessage.setTransactionID(transactionLog.getID());
			
			if(fixMessage.getParentTransactionID() == 0){
				fixMessage.setParentTransactionID(transactionLog.getID());
			}
		}
		catch(BackendRuntimeException bre){
			log.error("Error creating TransactionLog messageCode="+messageCode, bre);
			responseFix.setDescription("Error writing TransactionLog to database messageCode="+messageCode);
			responseFix.setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());
			return responseFix;
		}
		
		return responseFix;
	}
}
