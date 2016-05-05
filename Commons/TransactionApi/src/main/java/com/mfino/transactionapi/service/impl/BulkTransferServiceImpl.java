/**
 *
 */
package com.mfino.transactionapi.service.impl;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Notification;
import com.mfino.domain.Pocket;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.XMLResult;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.money.BulkDistributionHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Bala Sunku
 *
 */
@Service("BulkTransferServiceImpl")
public class BulkTransferServiceImpl implements BulkTransferService{

	private Logger log = LoggerFactory.getLogger(BulkTransferServiceImpl.class);
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Autowired
	@Qualifier("BulkDistributionHandlerImpl")
	private BulkDistributionHandler bulkDistributionHandler;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	public boolean processEntry(BulkUploadEntry bue, BulkUpload bulkUpload, Pocket srcPocket, ChannelCode channelCode, int i) {
		boolean isTxnSuccess = false;
		log.info("Transfering the Amount " + bue.getAmount() + " To destination " + bue.getDestMDN() + 
				" As part of Bulk Transfer --> " + bulkUpload.getID());
		// creating the Transaction Details object to make Transfer Inquiry call
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(bulkUpload.getMDN());
		transactionDetails.setSrcPocketId(srcPocket.getID());
		transactionDetails.setDestMDN(bue.getDestMDN());
		transactionDetails.setSourcePIN("mFino260");
		transactionDetails.setAmount(bue.getAmount());
		transactionDetails.setSourceMessage(bulkUpload.getDescription());
		transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER);
		transactionDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
		transactionDetails.setCc(channelCode);
		
		XMLResult result =  null;
		
		log.info("Calling the Bulk Distribution Handler....");
			result = (XMLResult)bulkDistributionHandler.handle(transactionDetails);
			
			if (result != null) {
				bue.setServiceChargeTransactionLogID(result.getSctlID());
				bue.setFirstName(result.getFirstName());
				bue.setLastName(result.getLastName());
				bue.setIsTrfToSuspense(result.isTrfToSuspense());
				
				if (GeneralConstants.RESPONSE_CODE_SUCCESS.equals(result.getResponseStatus())) {
					if (result.getTxnStatus() == 0) {
						isTxnSuccess = true;
						bue.setFailureReason("");
						log.info("Setting the bulk upload entry " + i + " status to completed");
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Completed);
					}
					else if (result.getTxnStatus() == 1) {
						log.info("Setting the bulk upload entry " + i + " status to failed");
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
						String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : getMessage(result, bulkUpload);
						bue.setFailureReason(failureReason);
					}
					else {
						log.info("Setting the bulk upload entry " + i + " status to pending --> " + CmFinoFIX.TransactionsTransferStatus_Pending);
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Pending);
					}
				}
				else {
					log.info("Setting the bulk upload entry " + i + " status to failed --> " + CmFinoFIX.TransactionsTransferStatus_Failed);
					bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
					String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : getMessage(result, bulkUpload);
					bue.setFailureReason(failureReason);
				}
			}
			else {
				log.info("Setting the bulk upload entry " + i + " status to failed as the result is null");
				bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
				bue.setFailureReason("Fails the transaction as result is null");
			}
		bulkUploadEntryService.saveBulkUploadEntry(bue);
		return isTxnSuccess;
	}

	private String getMessage(XMLResult result, BulkUpload bulkupload) {
		String msg = result.getNotificationCode() + "";
		Notification notification = notificationService.getByNotificationCodeAndLang(result.getNotificationCode(), CmFinoFIX.Language_English);
		if (notification != null) {
			msg = notification.getCodeName();
		}
		return msg;
	}	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void failTheBulkTransfer(BulkUpload bulkUpload, String  failureReason) {
		log.info("Bulk Transfer Failed --> " + bulkUpload.getID());
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Failed);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkUpload.setFailureReason(failureReason);
		bulkUploadService.save(bulkUpload);
		sendNotification(bulkUpload, "Bulk transfer failed", CmFinoFIX.NotificationCode_BulkTransferRequestFailedToPartner);
	}

	/**
	 * Send SMS Notification
	 * @param bulkupload
	 * @param notificationCode
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void sendNotification(BulkUpload bulkupload, String subject, Integer notificationCode) {

		User bulkTrfUser = bulkupload.getUser();
		NotificationWrapper notification = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notification.setLanguage(language);
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
		notification.setCode(notificationCode);
		notification.setBulkTransferId(bulkupload.getID());
		notification.setSctlID(bulkupload.getServiceChargeTransactionLogID());
		notification.setFirstName(bulkTrfUser.getUsername());
		String message = notificationMessageParserService.buildMessage(notification,true);
		
		mailService.asyncSendEmail(bulkTrfUser.getEmail(),bulkTrfUser.getUsername(), subject, message);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void sendEmailBulkUploadSummary(BulkUpload bulkUpload)
	{
		User bulkTrfUser = bulkUpload.getUser();
		String to=bulkTrfUser.getEmail();
		String name= bulkTrfUser.getUsername();

		List<BulkUploadEntry> bulkUploadEntries = bulkUploadEntryService.getNotCompleteBulkUploadEntriesForBulkUpload(bulkUpload.getID());
		int nofSuccessfulTransactions = bulkUpload.getTransactionsCount().intValue() - bulkUpload.getFailedTransactionsCount();

		String emailMsg = 	"Bulk Upload ID:" + bulkUpload.getID() +
							"\nTotal Amount to be distributed:" + bulkUpload.getTotalAmount() +
							"\nMoney distributed:" + bulkUpload.getSuccessAmount() +
							"\nTotal number of successful transfers:" + nofSuccessfulTransactions +
							"\nNo of failed transfers:" + bulkUpload.getFailedTransactionsCount() +
							"\nList of failed transfers:";
		Iterator<BulkUploadEntry> it = bulkUploadEntries.iterator();
		while(it.hasNext())
		{
			BulkUploadEntry bulkUploadEntry = it.next();
			String destMDN = bulkUploadEntry.getDestMDN();
			BigDecimal amount = bulkUploadEntry.getAmount();
			String failureReason = bulkUploadEntry.getFailureReason();
			if(bulkUploadEntry.getStatus().equals(CmFinoFIX.TransactionsTransferStatus_Failed))
			{
				emailMsg = emailMsg.concat("\n\tDestinationMDN = " + destMDN + ", Amount="+ amount + ", Failure Reason:"+ failureReason);
			}
		}
		mailService.asyncSendEmail(to,name, "Bulk Upload Summary", emailMsg);
	}
}
