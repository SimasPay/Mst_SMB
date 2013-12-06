package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.BulkUploadDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectBulkTranfer;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.result.XMLResult;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.transactionapi.handlers.money.BulkTransferHandler;
import com.mfino.transactionapi.handlers.money.BulkTransferInquiryHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.ApproveRejectBulkTransferProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.util.EncryptionUtil;

@Service("ApproveRejectBulkTransferProcessorImpl")
public class ApproveRejectBulkTransferProcessorImpl extends BaseFixProcessor implements ApproveRejectBulkTransferProcessor{

	private DAOFactory daoFactory = DAOFactory.getInstance();
	private BulkUploadDAO buDao =daoFactory.getBulkUploadDAO();


	@Autowired
	@Qualifier("BulkTransferInquiryHandlerImpl")
	private BulkTransferInquiryHandler bulkTransferInquiryHandler;
	
	@Autowired
	@Qualifier("BulkTransferHandlerImpl")
	private BulkTransferHandler bulkTransferHandler;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("BulkTransferServiceImpl")
	private BulkTransferService bulkTransferService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;

	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Override
 	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSApproveRejectBulkTranfer realMsg = (CMJSApproveRejectBulkTranfer) msg;
		CMJSError err=new CMJSError();

		log.info("Approve / Reject Bulk Transfer --> " + realMsg.getBulkUploadID() );
		if (realMsg.getBulkUploadID() != null) {
			BulkUpload bu =  bulkUploadService.getById(realMsg.getBulkUploadID());

			if (bu != null && CmFinoFIX.BulkUploadDeliveryStatus_Uploaded.equals(bu.getDeliveryStatus()) ) {

				bu.setApproverComments(realMsg.getAdminComment());
				if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
					log.info("Bulk Transfer Request is Approved.");
					bu.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
					bu.setDeliveryDate(new Timestamp());
					bulkUploadService.save(bu);
					err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					err.setErrorDescription(MessageText._("Successfully Approved the Bulk Transfer."));

					transferFromSourcePocketToDestinationSuspensePocket(realMsg.getBulkUploadID());

				}
				else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
					log.info("Bulk transfer Request is Rejected.");
					bu.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Rejected);
					bu.setDeliveryDate(new Timestamp());
					bulkUploadService.save(bu);
					err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					err.setErrorDescription(MessageText._("Rejected the Bulk Transfer."));
					sendNotification(bu, CmFinoFIX.NotificationCode_BulkTransferRequestRejectedToPartner);
				}
				else {
					log.info("Approve / Reject failed because of invalid action");
		            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		            err.setErrorDescription(MessageText._("Inavlid Admin Action"));
		            return err;
				}
			} else {
				log.info("Approve / Reject failed because of null value");
	            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed please try again after some time."));
			}

		} else {
			log.info("Approve / Reject failed because of null value");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed please try again after some time"));
		}
		return err;
	}

	private void transferFromSourcePocketToDestinationSuspensePocket(Long bulkTransferId)
	{

 		BulkUpload bulkUpload = bulkUploadService.getById(bulkTransferId);


		Pocket sourcePocket = bulkUpload.getPocketBySourcePocket();
		Pocket destPocket = pocketService.getSuspencePocket(bulkUpload.getUser());
		log.info("Getting the Destination suspence pocket for the bulk upload user --> " + (destPocket != null ? destPocket.getID() : null));
		if (destPocket == null) {
			bulkTransferService.failTheBulkTransfer(bulkUpload, "Suspence pocket is not Defined");
			return;
		}
		String pin = EncryptionUtil.getDecryptedString(bulkUpload.getPin());
 		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);

		log.info("Creating the Bulk Transfer Inquiry object");
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(bulkUpload.getMDN());
		transactionDetails.setDestMDN(bulkUpload.getMDN());
		transactionDetails.setAmount(bulkUpload.getTotalAmount());
		transactionDetails.setSourcePIN(pin);
		transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_BULK_TRANSFER);
		transactionDetails.setChannelCode(channelCode.getChannelCode());
		transactionDetails.setSrcPocketId(sourcePocket.getID());
		transactionDetails.setDestinationPocketId(destPocket.getID());
		transactionDetails.setCc(channelCode);

		if (CmFinoFIX.PocketType_BankAccount.equals(sourcePocket.getPocketTemplate().getType())) {
			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
		}
		else if (CmFinoFIX.PocketType_SVA.equals(sourcePocket.getPocketTemplate().getType())) {
			transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		}


		XMLResult result = (XMLResult)bulkTransferInquiryHandler.handle(transactionDetails);

		if (result != null) {
			bulkUpload.setServiceChargeTransactionLogID(result.getSctlID());
			bulkUploadService.save(bulkUpload);
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode())) {
				log.info("Creating the Bulk Transfer Confirmation object");
 				TransactionDetails td = new TransactionDetails();
				
				td.setSourceMDN(bulkUpload.getMDN());
				td.setDestMDN(bulkUpload.getMDN());
				td.setSrcPocketId(sourcePocket.getID());
				td.setDestinationPocketId(destPocket.getID());
				td.setServletPath(CmFinoFIX.ServletPath_Subscribers);
				td.setChannelCode(channelCode.getChannelCode());

				td.setParentTxnId(result.getParentTransactionID());
				td.setTransferId(result.getTransferID());
				td.setConfirmString("true");
				td.setCc(channelCode);
				
				result = (XMLResult)bulkTransferHandler.handle(td);

				if (result != null && result.getDetailsOfPresentTransaction()!= null) {
					CommodityTransfer ct = result.getDetailsOfPresentTransaction();
					if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
						log.info("Trnasfer from source Pocket to Destination suspense pocket completed");
					}
					else {
						log.info("Failing the Bulk Transfer " + bulkUpload.getID() + "  as the Transaction is failed.");
						String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : result.getNotificationCode()+"";
						bulkTransferService.failTheBulkTransfer(bulkUpload,  failureReason);
						return;
					}
				}
				// No Response from Back end after confirming the transaction. Leave the Bulk transfer in processing state.
				else {
					log.info("No Response from Back end -- > " + result.getMessage());
					bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Pending);
					bulkUpload.setDeliveryDate(new Timestamp());
					bulkUploadService.save(bulkUpload);
					return;
				}

			}
			else {
				log.info("Failing the Bulk Transfer " + bulkUpload.getID() + " as the Inquiry failed");
				String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : result.getNotificationCode()+"";
				bulkTransferService.failTheBulkTransfer(bulkUpload, failureReason);
				return;
			}
		}

		else {
			log.info(" Failing the Bulk Transfer " + bulkUpload.getID() + " as the Inquiry result is null");
			String failureReason = "Inquiry Result is null";
			bulkTransferService.failTheBulkTransfer(bulkUpload,  failureReason);
			return;
		}


	}


	/**
	 * Send SMS Notification
	 * @param bulkupload
	 * @param notificationCode
	 */
	private void sendNotification(BulkUpload bulkupload, Integer notificationCode) {

		String mdn = bulkupload.getMDN();
		NotificationWrapper notification = new NotificationWrapper();
		notification.setLanguage(CmFinoFIX.Language_English);
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
		smsService.asyncSendSMS();
	}
}
