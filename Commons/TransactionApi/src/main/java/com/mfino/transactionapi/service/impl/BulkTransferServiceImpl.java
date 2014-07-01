/**
 *
 */
package com.mfino.transactionapi.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
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

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.XMLResult;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.MailService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.money.MoneyTransferHandler;
import com.mfino.transactionapi.handlers.money.TransferInquiryHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.EncryptionUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("BulkTransferServiceImpl")
public class BulkTransferServiceImpl implements BulkTransferService{

	private Logger log = LoggerFactory.getLogger(BulkTransferServiceImpl.class);
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;

	@Autowired
	@Qualifier("TransferInquiryHandlerImpl")
	private TransferInquiryHandler transferInquiryHandler;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	@Autowired
	@Qualifier("MoneyTransferHandlerImpl")
	private MoneyTransferHandler moneyTransferHandler;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	/**
	 * Read the Bulk transfer data line by line and Transfers the amount to Destination pockets with out Service Charge.
	 * Destination pocket may be Emoney or Bank based on the Transaction amount
	 * Also make an entry into bulk_upload_entry table for each transaction.
	 * @param bulkUpload
	 * @param pin
	 * @param pocket
	 * @param channelCode
	 * @return
	 */
	public BulkUpload processBulkTransferData(BulkUpload bulkUpload) throws IOException {
		BulkUploadEntry bue = null;
		int successCount = 0;
		BigDecimal successAmount = BigDecimal.ZERO;

		String pin = EncryptionUtil.getDecryptedString(bulkUpload.getPin());
		Pocket pocket = pocketService.getSuspencePocket(bulkUpload.getUser());
		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);

		BufferedReader bufferedReader = new BufferedReader(new StringReader(bulkUpload.getInFileData()));
		String line = null;
		for (int i=1; (line = bufferedReader.readLine()) != null; i++) {
			try {
				Integer transferStatus = CmFinoFIX.TransferStatus_Initialized;
				String lineData[] = line.split(",");
				String firstName = lineData[0];
				String lastName = lineData[1];
				String destMDN = subscriberService.normalizeMDN(lineData[2]);
				BigDecimal amount = new BigDecimal(lineData[3]);
				log.info("Transfering the Amount " + amount + " To destination " + destMDN + " As part of Bulk Transfer --> " + bulkUpload.getID());

				// Setting the Bulk upload entry
				bue = new BulkUploadEntry();
				bue.setUploadID(bulkUpload.getID());
				bue.setLineNumber(i);
				bue.setStatus(transferStatus);
				bue.setAmount(amount);
				bue.setDestMDN(destMDN);
				bue.setFirstName(firstName);
				bue.setLastName(lastName);

				// Getting the Destination pocket type based on the transaction amount and subscriber KYC level.
				// If the Amount > 100000 and bank pocket is active then destination pocket type is bank pocket.
				// else destination pocket type is eMoney pocket (SVA).
				String destPocketCode = ApiConstants.POCKET_CODE_SVA;
				BigDecimal lakh = new BigDecimal("100000");
				if (amount.compareTo(lakh) > 0) {
					Pocket bankPocket = subscriberService.getDefaultPocket(destMDN, CmFinoFIX.PocketType_BankAccount, CmFinoFIX.Commodity_Money);
					if (bankPocket != null && CmFinoFIX.PocketStatus_Active.equals(bankPocket.getStatus())) {
						destPocketCode = ApiConstants.POCKET_CODE_BANK;
					}
				}
				log.info("Destination pocket type for MDN " + destMDN + " is --> " + destPocketCode);

				// creating the Transaction Details object to make Transfer Inquiry call
				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setSourceMDN(bulkUpload.getMDN());
				transactionDetails.setSourcePocketId(pocket.getID().toString());
				transactionDetails.setSrcPocketId(pocket.getID());
				transactionDetails.setDestMDN(destMDN);
				transactionDetails.setFirstName(firstName);
				transactionDetails.setLastName(lastName);
				transactionDetails.setSourcePIN(pin);
				transactionDetails.setAmount(amount);
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_SUB_BULK_TRANSFER);
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER_INQUIRY);
				transactionDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
				transactionDetails.setDestPocketCode(destPocketCode);
				transactionDetails.setCc(channelCode);
				
				log.info("Calling the TransferInquiryHandler ....");
				XMLResult result = (XMLResult)transferInquiryHandler.handle(transactionDetails);
				
				if (result != null) {
					bue.setServiceChargeTransactionLogID(result.getSctlID());
					bue.setFailureReason(result.getMessage());
					bue.setIsUnRegistered(result.isUnRegistered());
					if ( CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode()) ||
							CmFinoFIX.NotificationCode_TransferToUnRegisteredConfirmationPrompt.toString().equals(result.getCode())) {

						transactionDetails.setTransferId(result.getTransferID());
						transactionDetails.setConfirmString("true");
						transactionDetails.setParentTxnId(result.getParentTransactionID());
					
						log.info("Calling the MoneyTransferHamdler ....");
						result = (XMLResult)moneyTransferHandler.handle(transactionDetails);

						if (result != null && result.getDetailsOfPresentTransaction()!= null) {
							CommodityTransfer ct = result.getDetailsOfPresentTransaction();
							if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
								bue.setFailureReason("");
								successCount ++;
								successAmount = successAmount.add(amount);
							} else {
								bue.setFailureReason(result.getMessage());
							}
							log.info("Setting the bulk upload entry " + i + " status to --> " + ct.getTransferStatus());
							bue.setStatus(ct.getTransferStatus());
						}
						// No Response from back end after confirming the transfer
						else {
							log.info("Setting the bulk upload entry " + i + " status to pending --> " + CmFinoFIX.TransactionsTransferStatus_Pending);
							bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Pending);
						}
					}
					else {
						log.info("Setting the bulk upload entry " + i + " status to falied --> " + CmFinoFIX.TransactionsTransferStatus_Failed);
						bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
						String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : result.getNotificationCode()+"";
						bue.setFailureReason(failureReason);
					}
					bulkUploadEntryService.saveBulkUploadEntry(bue);
				}
			} catch (Exception e) {
				log.error("Error: While processing the line number " + i + " for Bulk Transfer --> " + bulkUpload.getID());
			}
		}

		bulkUpload.setSuccessAmount(successAmount);
		int failedCount = bulkUpload.getTransactionsCount().intValue() - successCount;
		bulkUpload.setFailedTransactionsCount(failedCount);

		return bulkUpload;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void failTheBulkTransfer(BulkUpload bulkUpload, String  failureReason) {
		log.info("Bulk Transfer Failed --> " + bulkUpload.getID());
		bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Failed);
		bulkUpload.setDeliveryDate(new Timestamp());
		bulkUpload.setFailureReason(failureReason);
		bulkUploadService.save(bulkUpload);
		sendNotification(bulkUpload, CmFinoFIX.NotificationCode_BulkTransferRequestFailedToPartner);
	}

	/**
	 * Send SMS Notification
	 * @param bulkupload
	 * @param notificationCode
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void sendNotification(BulkUpload bulkupload, Integer notificationCode) {

		String mdn = bulkupload.getMDN();
		NotificationWrapper notification = new NotificationWrapper();
		Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
		notification.setLanguage(language);
		notification.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		notification.setCode(notificationCode);
		notification.setBulkTransferId(bulkupload.getID());
		notification.setSctlID(bulkupload.getServiceChargeTransactionLogID());
		SubscriberMDN smdn = subscriberMdnService.getByMDN(mdn);
		if(smdn != null)
		{
			notification.setFirstName(smdn.getSubscriber().getFirstName());
			notification.setLastName(smdn.getSubscriber().getLastName());
		}
		String message = notificationMessageParserService.buildMessage(notification,true);

		smsService.setDestinationMDN(mdn);
		smsService.setMessage(message);
		smsService.setNotificationCode(notification.getCode());
		smsService.setSctlId(notification.getSctlID());
		smsService.asyncSendSMS();
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void sendEmailBulkUploadSummary(BulkUpload bulkUpload, BigDecimal moneyAvailbleBeforeTheJob, BigDecimal moneyAvailbleAfterTheJob)
	{
		Subscriber subscriber = subscriberMdnService.getByMDN(bulkUpload.getMDN()).getSubscriber();
		String to=subscriber.getEmail();
		String name= subscriber.getFirstName();

		List<BulkUploadEntry> bulkUploadEntries = bulkUploadEntryService.getBulkUploadEntriesForBulkUpload(bulkUpload.getID());
		int nofSuccessfulTransactions = bulkUpload.getTransactionsCount().intValue() - bulkUpload.getFailedTransactionsCount();
		ServiceChargeTransactionLog  serviceChargeTransactionLog = serviceChargeTransactionLogService.getById(bulkUpload.getServiceChargeTransactionLogID());

		String emailMsg = 	"Bulk Upload ID:" + bulkUpload.getID() +
							"\nTotal money available before the Job:" +moneyAvailbleBeforeTheJob +
							"\nMoney available after job:" + moneyAvailbleAfterTheJob +
							"\nMoney distributed:" + bulkUpload.getSuccessAmount() +
							"\nService charge applied:" + ((serviceChargeTransactionLog != null) ? serviceChargeTransactionLog.getCalculatedCharge().toString() : "") +
							"\nTotal number of successful transfers:" + nofSuccessfulTransactions +
							"\nNo of failed transactions:" + bulkUpload.getFailedTransactionsCount() +
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
