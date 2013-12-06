/**
 * 
 */
package com.mfino.service;

import org.apache.http.HttpResponse;

import com.mfino.fix.CFIXMsg;


/**
 * @author Deva
 *
 */
public interface SMSService{
	

	public CFIXMsg process(CFIXMsg msg);
	
    public boolean send();
	/**
	 * Send SMS based on the properties set. If <code>isAlert</code> is <code>true</code>,
	 * the Kannel configurations corresponding to SMS Alerts are used, otherwise configurations
	 * to SMS notifications are used.
	 *
	 * @return HttpResponse
	 */
	public HttpResponse send(boolean isAlert);
	
	 public void asyncSendSMS();

	/**
	 * @return the destinationMDN
	 */
	public String getDestinationMDN(); 

	/**
	 * @param destinationMDN the destinationMDN to set
	 */
	public void setDestinationMDN(String destinationMDN);

	/**
	 * @return the sourceMDN
	 */
	public String getSourceMDN();

	/**
	 * @param sourceMDN the sourceMDN to set
	 */
	public void setSourceMDN(String sourceMDN) ;

	/**
	 * @return the message
	 */
	public String getMessage();
	/**
	 * @param message the message to set
	 */
	public void setMessage(String message);

	/**
	 * @return the senderName
	 */
	public String getSenderName();

	/**
	 * @param senderName the senderName to set
	 */
	public void setSenderName(String senderName);
	
	/**
	 * @return the accessCode
	 */
	public String getAccessCode();

	/**
	 * @param accessCode the accessCode to set
	 */
	public void setAccessCode(String accessCode);

	/**
	 * @return the smsc
	 */
	public String getSmsc();

	/**
	 * @param smsc the smsc to set
	 */
	public void setSmsc(String smsc);

	/**
	 *
	 * @param transactionID
	 */
	public void setTransactionID(Long transactionID);
	
	/**
	 *
	 * @return
	 */
	public Long getTransactionID();

	/**
	 * @param notificationCode the notificationCode to set
	 */
	public void setNotificationCode(int notificationCode);

	/**
	 * @return the notificationCode
	 */
	public int getNotificationCode();
	
	/**
	 * 
	 * @return the sctlId
	 */
	public Long getSctlId();

	/**
	 * 
	 * @param sctlId
	 */
	public void setSctlId(Long sctlId);
	
	public boolean isDuplicateSMS();

	public void setDuplicateSMS(boolean isDuplicateSMS);

	public Long getNotificationLogDetailsID();

	public void setNotificationLogDetailsID(Long notificationLogDetailsID);

	public String getTransactionIdentifier();

	public void setTransactionIdentifier(String transactionIdentifier);
	
}
