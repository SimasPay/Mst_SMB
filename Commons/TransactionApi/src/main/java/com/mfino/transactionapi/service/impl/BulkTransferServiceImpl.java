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
import com.mfino.domain.MfinoUser;
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
		log.info("Transfering the Amount " + bue.getAmount() + " To destination " + bue.getDestmdn() + 
				" As part of Bulk Transfer --> " + bulkUpload.getId());
		// creating the Transaction Details object to make Transfer Inquiry call
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(bulkUpload.getMdn());
		transactionDetails.setSrcPocketId(srcPocket.getId().longValue());
		transactionDetails.setDestMDN(bue.getDestmdn());
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
				bue.setServicechargetransactionlogid(BigDecimal.valueOf(result.getSctlID()));
				bue.setFirstname(result.getFirstName());
				bue.setLastname(result.getLastName());
				bue.setIstrftosuspense((short) (result.isTrfToSuspense()?1:0));
				
				if (GeneralConstants.RESPONSE_CODE_SUCCESS.equals(result.getResponseStatus())) {
					if (result.getTxnStatus() == 0) {
						isTxnSuccess = true;
						bue.setFailurereason("");
						log.info("Setting the bulk upload entry " + i + " status to completed");
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Completed);
					}
					else if (result.getTxnStatus() == 1) {
						log.info("Setting the bulk upload entry " + i + " status to failed");
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
						String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : getMessage(result, bulkUpload);
						bue.setFailurereason(failureReason);
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
					bue.setFailurereason(failureReason);
				}
			}
			else {
				log.info("Setting the bulk upload entry " + i + " status to failed as the result is null");
				bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
				bue.setFailurereason("Fails the transaction as result is null");
			}
		bulkUploadEntryService.saveBulkUploadEntry(bue);
		return isTxnSuccess;
	}

	private String getMessage(XMLResult result, BulkUpload bulkupload) {
		String msg = result.getNotificationCode() + "";
		Notification notification = notificationService.getByNotificationCodeAndLang(result.getNotificationCode(), CmFinoFIX.Language_English);
		if (notification != null) {
			msg = notification.getCodename();
		}
		return msg;
	}	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void failTheBulkTransfer(BulkUpload bulkUpload, String  failureReason) {
		log.info("Bulk Transfer Failed --> " + bulkUpload.getId());
		bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Failed);
		bulkUpload.setDeliverydate(new Timestamp());
		bulkUpload.setFailurereason(failureReason);
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

		MfinoUser bulkTrfUser = bulkupload.getMfinoUser();
		NotificationWrapper notification = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notification.setLanguage(language);
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_Email);
		notification.setCode(notificationCode);
		notification.setBulkTransferId(bulkupload.getId().longValue());
		notification.setSctlID(bulkupload.getServicechargetransactionlogid().longValue());
		notification.setFirstName(bulkTrfUser.getUsername());
		String message = notificationMessageParserService.buildMessage(notification,true);
		
		mailService.asyncSendEmail(bulkTrfUser.getEmail(),bulkTrfUser.getUsername(), subject, message);
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void sendEmailBulkUploadSummary(BulkUpload bulkUpload)
	{
		MfinoUser bulkTrfUser = bulkUpload.getMfinoUser();
		String to=bulkTrfUser.getEmail();
		String name= bulkTrfUser.getUsername();

		List<BulkUploadEntry> bulkUploadEntries = bulkUploadEntryService.getNotCompleteBulkUploadEntriesForBulkUpload(bulkUpload.getId().longValue());
		int nofSuccessfulTransactions = (int)(bulkUpload.getTransactionscount() - bulkUpload.getFailedtransactionscount());

		String emailMsg = 	"Bulk Upload ID:" + bulkUpload.getId() +
							"\nTotal Amount to be distributed:" + bulkUpload.getTotalamount() +
							"\nMoney distributed:" + bulkUpload.getSuccessamount() +
							"\nTotal number of successful transfers:" + nofSuccessfulTransactions +
							"\nNo of failed transfers:" + bulkUpload.getFailedtransactionscount() +
							"\nList of failed transfers:";
		Iterator<BulkUploadEntry> it = bulkUploadEntries.iterator();
		while(it.hasNext())
		{
			BulkUploadEntry bulkUploadEntry = it.next();
			String destMDN = bulkUploadEntry.getDestmdn();
			BigDecimal amount = bulkUploadEntry.getAmount();
			String failureReason = bulkUploadEntry.getFailurereason();
			if(bulkUploadEntry.getStatus()==(CmFinoFIX.TransactionsTransferStatus_Failed))
			{
				emailMsg = emailMsg.concat("\n\tDestinationMDN = " + destMDN + ", Amount="+ amount + ", Failure Reason:"+ failureReason);
			}
		}
		mailService.asyncSendEmail(to,name, "Bulk Upload Summary", emailMsg);
	}
}
