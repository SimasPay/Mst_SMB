/**
 * 
 */
package com.mfino.sms.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCheckBalance;
import com.mfino.fix2notification.processor.CheckBalanceProcessor;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.SMSService;

/**
 * @author Deva
 *
 */
public class CheckBalanceHandler extends SMSHandler{
	
	private CMCheckBalance checkBalanceRequest = null;
	
	private String message = null;
	
	private String messageToSend;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	/**
	 * 
	 */
	public CheckBalanceHandler(CMCheckBalance checkBalanceRequest, String actualMessage) {
		this.checkBalanceRequest = checkBalanceRequest;
		this.message = actualMessage;
	}
	
	public boolean handle() {
		// We got a valid Request lets Log in to transactions log
		TransactionsLog transactionsLog = saveTransactionsLog(CmFinoFIX.MessageType_CheckBalance, message);
		checkBalanceRequest.setTransactionID(transactionsLog.getID());
		CheckBalanceProcessor checkBalanceProcessor = new CheckBalanceProcessor();
		try {
			NotificationWrapper notificationWrapper = checkBalanceProcessor.process(checkBalanceRequest);
			notificationWrapper.setTransactionId(transactionsLog.getID());
			NotificationMessageParser nmp = new NotificationMessageParser(notificationWrapper);
			messageToSend = nmp.buildMessage();
			System.out.println(transactionsLog.getID());
			try {
				SMSService service = new SMSService();
				service.setDestinationMDN(checkBalanceRequest.getSourceMDN());
				service.setSourceMDN(notificationWrapper.getSMSNotificationCode());
				service.setMessage(messageToSend);
				service.setAccessCode(notificationWrapper.getAccessCode());
				if (notificationWrapper.getCompany() != null && notificationWrapper.getCompany().getsmsc() != null) {
					service.setSmsc(notificationWrapper.getCompany().getsmsc());
				}
				return service.send();
			}  catch (Exception err) {
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
	public CMCheckBalance getCheckBalanceRequest() {
		return checkBalanceRequest;
	}

	/**
	 * @param checkBalanceRequest the checkBalanceRequest to set
	 */
	public void setCheckBalanceRequest(CMCheckBalance checkBalanceRequest) {
		this.checkBalanceRequest = checkBalanceRequest;
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
