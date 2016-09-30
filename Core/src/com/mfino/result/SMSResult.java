package com.mfino.result;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank.CGEntries;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.SMSService;
public class SMSResult extends Result
{

    private Logger log = LoggerFactory.getLogger(this.getClass());
    
	private NotificationMessageParserService notificationMessageParserService;

    private SMSService     Service;
    
    private NotificationService notificationService;

    private String         SenderNotificationCode;

    public void render() throws Exception
    {
	String message = null;
	String code = null;
	Pocket pocket = null;
	List<CommodityTransfer> ctList = null;
	List<CGEntries> lastBankTrxnsList = null;
	if (getPocketList() != null && getPocketList().size() > 0)
	{
	    pocket = getPocketList().get(0);
	}
	if (getTransactionList() != null && getTransactionList().size() > 0)
	{
	    ctList = getTransactionList();
	}
	if (getLastBankTrxnList() != null && getLastBankTrxnList().size() > 0) {
    	lastBankTrxnsList = getLastBankTrxnList();
    }
	if (getMultixResponse() == null)
	{
	    StringBuilder messageBuilder = new StringBuilder();
	    NotificationWrapper notificationWrapper = getNotificationWrapper(notificationService,CmFinoFIX.NotificationMethod_SMS);
	    notificationWrapper.setSourcePocket(pocket);
	    if (ctList != null)
	    {
	    	messageBuilder.append('(');
	    	messageBuilder.append(notificationWrapper.getCode());
	    	messageBuilder.append(')');
		for (CommodityTransfer commodityTransfer : ctList)
		{
		    notificationWrapper.setCommodityTransfer(commodityTransfer);
		    messageBuilder.append(notificationMessageParserService.buildMessage(notificationWrapper, false));
		    messageBuilder.append("\r\n");
		}
	    }else if (lastBankTrxnsList != null) {
			for (CGEntries entry : lastBankTrxnsList) {
				notificationWrapper.setLastBankTrxnEntry(entry);
				messageBuilder.append(notificationMessageParserService.buildMessage(notificationWrapper, false));
				messageBuilder.append("\r\n");
			}	    	
	    } else
	    {
		messageBuilder.append(notificationMessageParserService.buildMessage(notificationWrapper, true));
	    }
	    setSenderNotificationCode(notificationWrapper.getSMSNotificationCode());
	    message = messageBuilder.toString();
	}
	else
	{
	    CMJSError response = (CMJSError) getMultixResponse();
	    message = response.getErrorDescription();
	    int startIndex = message.indexOf('(');
	    int endIndex = message.indexOf(')');
	    if (startIndex != -1 && endIndex != -1)
	    {
		code = message.substring(startIndex + 1, endIndex);
		message = message.substring(startIndex);
	    }
	    else
	    {
		code = "0";
	    }
	    // TransferID=1002510
	    startIndex = message.indexOf("TransferID");
	    if (startIndex != -1)
	    {
		message = message.substring(0, startIndex);
	    }
	    NotificationQuery query = new NotificationQuery();
	    query.setNotificationCode(Integer.parseInt(code));
	    query.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
	    query.setLanguage(getLanguage());
	    query.setCompany(getCompany());
	    List list = notificationService.getLanguageBasedNotificationsByQuery(query);
	    if (list.size() > 0)
	    {
		Notification notification = (Notification) list.get(0);
		setSenderNotificationCode(notification.getSmsnotificationcode());
	    }
	}

	Service.setSourceMDN(getSenderNotificationCode());
	if (Service == null)
	{
	    log.error("SMSService not set for SMSResult");
	    throw new Exception("SMSService of SMSResult is null");
	}
	if (Service.getSourceMDN() == null)
	{
	    Service.setSourceMDN("837");
	}
	if (Service.getDestinationMDN() == null)
	{
	    log.error("Destination MDN number not set for smsservice");
	    throw new Exception("Destination MDN number not set for smsservice");
	}

	Service.setMessage(message);
	Service.send();
    }

    public void setSMSService(SMSService service)
    {
	this.Service = service;
    }

    public SMSService getSMSService()
    {
	return Service;
    }

    public void setSenderNotificationCode(String senderNotificationCode)
    {
	SenderNotificationCode = senderNotificationCode;
    }

    public String getSenderNotificationCode()
    {
	return SenderNotificationCode;
    }

	/**
	 * @return the notificationMessageParserService
	 */
	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	/**
	 * @param notificationMessageParserService the notificationMessageParserService to set
	 */
	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}

	/**
	 * @return the notificationService
	 */
	public NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * @param notificationService the notificationService to set
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
