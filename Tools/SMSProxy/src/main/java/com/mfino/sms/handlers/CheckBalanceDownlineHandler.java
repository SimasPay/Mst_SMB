/**
 * 
 */
package com.mfino.sms.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCheckBalanceDownline;
import com.mfino.fix2notification.processor.CheckBalanceDownlineProcessor;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.SMSService;

/**
 * @author Deva
 *
 */
public class CheckBalanceDownlineHandler extends SMSHandler{
	
	private CMCheckBalanceDownline checkBalanceDownlineRequest = null;
	
	private String message = null;
	
	private String messageToSend;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 */
	public CheckBalanceDownlineHandler(CMCheckBalanceDownline cbdRequest, String actualMessage) {
		this.checkBalanceDownlineRequest = cbdRequest;
		this.message = actualMessage;
	}
	
	public boolean handle() {
		// We got a valid Request lets Log in to transactions log
		TransactionsLog transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_CheckBalanceDownline, message);
		checkBalanceDownlineRequest.setTransactionID(transactionsLog.getID());
		CheckBalanceDownlineProcessor checkBalanceProcessor = new CheckBalanceDownlineProcessor();
		try {
			NotificationWrapper notificationWrapper = checkBalanceProcessor.process(checkBalanceDownlineRequest);
			notificationWrapper.setTransactionId(transactionsLog.getID());
			NotificationMessageParser nmp = new NotificationMessageParser(notificationWrapper);
			messageToSend = nmp.buildMessage();
			System.out.println(transactionsLog.getID());
			try {
				SMSService service = new SMSService();
				service.setDestinationMDN(checkBalanceDownlineRequest.getSourceMDN());
				service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
				service.setMessage(messageToSend);
				service.setAccessCode(notificationWrapper.getAccessCode());
				log.info(messageToSend);
				if (notificationWrapper.getCompany() != null && notificationWrapper.getCompany().getsmsc() != null) {
					service.setSmsc(notificationWrapper.getCompany().getsmsc());
				}
				return service.send();
			} catch (Exception err) {
				log.error("Exception while sending sms: ", err);
			}
		} catch (Exception err) {
			log.error("Exception while sending sms: ", err);
		}
		return false;
	}

	/**
	 * @return the checkBalanceRequest
	 */
	public CMCheckBalanceDownline getCheckBalanceRequest() {
		return checkBalanceDownlineRequest;
	}

	/**
	 * @param checkBalanceRequest the checkBalanceRequest to set
	 */
	public void setCheckBalanceRequest(CMCheckBalanceDownline cbdRequest) {
		this.checkBalanceDownlineRequest = cbdRequest;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the messageToSend
	 */
	public String getMessageToSend() {
		return messageToSend;
	}

	/**
	 * @param messageToSend the messageToSend to set
	 */
	public void setMessageToSend(String messageToSend) {
		this.messageToSend = messageToSend;
	}
}
