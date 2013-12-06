/**
 * 
 */
package com.mfino.sms.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactionDetails;
import com.mfino.fix2notification.processor.GetTransactionDetailsProcessor;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.SMSService;

/**
 * @author admin
 *
 */
public class TransactionDetailsHandler extends SMSHandler {

	private CMGetTransactionDetails transactionDetails = null;
	
	private String message = null;
	
	private String messageToSend;
	
	private Logger log = LoggerFactory.getLogger(getClass());
		
	public TransactionDetailsHandler(CMGetTransactionDetails transactionDetails, 
			String actualMessage) {
		this.transactionDetails = transactionDetails;
		this.message = actualMessage;
	}
	/* This service would give the details of the transaction
	 * give id as a parameter for a merchant.
	 */
	@Override
	public boolean handle() {

		TransactionsLog transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_GetTransactionDetails, message);
		transactionDetails.setTransactionID(transactionsLog.getID());
		try {
			
				GetTransactionDetailsProcessor getTransactionDetails = new GetTransactionDetailsProcessor();
				NotificationWrapper notificationWrapper = getTransactionDetails.process(transactionDetails);
				notificationWrapper.setTransactionId(transactionsLog.getID());
				NotificationMessageParser nmp = new NotificationMessageParser(notificationWrapper);
				messageToSend = nmp.buildMessage();
				System.out.println(transactionsLog.getID());
				try {
					SMSService service = new SMSService();
					service.setDestinationMDN(transactionDetails.getSourceMDN());
					service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
					service.setMessage(messageToSend);
					service.setAccessCode(notificationWrapper.getAccessCode());
					log.info(messageToSend);
					if (notificationWrapper.getCompany() != null && notificationWrapper.getCompany().getsmsc() != null) {
						service.setSmsc(notificationWrapper.getCompany().getsmsc());
					}
					service.send();
				} catch (Exception err) {
					log.error("Exception while sending sms: ", err);
				}
			
		} catch (Exception err) {
			log.error("Exception while sending sms: ", err);
			return false;
		}
		return true;
	}

}
