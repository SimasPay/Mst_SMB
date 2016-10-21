package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSApproveRejectBulkTranfer;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.XMLResult;
import com.mfino.service.BulkUploadService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PocketService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.handlers.money.BulkTransferHandler;
import com.mfino.transactionapi.handlers.money.BulkTransferInquiryHandler;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.ApproveRejectBulkTransferProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

@Service("ApproveRejectBulkTransferProcessorImpl")
public class ApproveRejectBulkTransferProcessorImpl extends BaseFixProcessor implements ApproveRejectBulkTransferProcessor{

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
	@Qualifier("BulkTransferServiceImpl")
	private BulkTransferService bulkTransferService;

	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;	
	
	@Override
 	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSApproveRejectBulkTranfer realMsg = (CMJSApproveRejectBulkTranfer) msg;
		CMJSError err=new CMJSError();

		log.info("Approve / Reject Bulk Transfer --> " + realMsg.getBulkUploadID() );
		if (realMsg.getBulkUploadID() != null) {
			BulkUpload bu =  bulkUploadService.getById(realMsg.getBulkUploadID());

			if (bu != null && CmFinoFIX.BulkUploadDeliveryStatus_Uploaded.equals(bu.getDeliverystatus()) ) {

				bu.setApprovercomments(realMsg.getAdminComment());
				if (CmFinoFIX.AdminAction_Approve.equals(realMsg.getAdminAction())) {
					log.info("Bulk Transfer Request is Approved.");
					err = transferFromSourcePocketToDestinationPocket(realMsg.getBulkUploadID(), err);
					if (CmFinoFIX.ErrorCode_NoError.equals(err.getErrorCode())) {
						bu.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
						bu.setDeliverydate(new Timestamp());
						bulkUploadService.save(bu);
						err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
						err.setErrorDescription(MessageText._("Successfully Approved the Bulk Transfer."));
					}
					else {
						return err;
					}
				}
				else if (CmFinoFIX.AdminAction_Reject.equals(realMsg.getAdminAction())) {
					log.info("Bulk transfer Request is Rejected.");
					bu.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Rejected);
					bu.setDeliverydate(new Timestamp());
					bulkUploadService.save(bu);
					err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
					err.setErrorDescription(MessageText._("Rejected the Bulk Transfer."));
					bulkTransferService.sendNotification(bu, "Bulk transfer rejected", CmFinoFIX.NotificationCode_BulkTransferRequestRejectedToPartner);
				}
				else {
					log.info("Approve / Reject failed because of invalid action");
		            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		            err.setErrorDescription(MessageText._("Inavlid Admin Action"));
		            return err;
				}
			} else {
				log.info("Approve / Reject failed because of wrong status ");
	            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed because of wrong status."));
			}

		} else {
			log.info("Approve / Reject failed because Bulk trasnfer Id is null");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Approve / Reject of the Transaction is failed because Bulk trasnfer Id is null"));
		}
		return err;
	}

	private CMJSError transferFromSourcePocketToDestinationPocket(Long bulkTransferId, CMJSError err)
	{
		log.info("Transfering the money from Source pocket to Suspense pocket as part of Bulk transfer id: " + bulkTransferId);
 		BulkUpload bulkUpload = bulkUploadService.getById(bulkTransferId);

		Pocket sourcePocket = bulkUpload.getPocket();
		Integer validationResult = transactionApiValidationService.validateSourcePocket(sourcePocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.error("Source pocket with id "+(sourcePocket!=null? sourcePocket.getId():null)+" has failed validations");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Source pocket has failed validations."));
            return err;
		}
		
		SubscriberMdn sourceSubscriberMDN = sourcePocket.getSubscriberMdn();
		validationResult=transactionApiValidationService.validateSubscriberAsSource(sourceSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+sourceSubscriberMDN.getMdn()+" has failed validations");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Source subscriber has failed validations."));
            return err;
		}
		
        long destPocketId = systemParametersService.getLong(SystemParameterKeys.INTEREST_COMMISSION_FUNDING_POCKET_ID);
        if (destPocketId == -1) {
        	log.info("Interest / commission funding pocket id not configured");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Interest / commission funding pocket id not configured."));
            return err;
        }
        Pocket destPocket = pocketService.getById(destPocketId);
		validationResult = transactionApiValidationService.validateDestinationPocket(destPocket);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("Destination pocket with id "+(destPocket!=null? destPocket.getId():null)+" has failed validations");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Destination pocket has failed validations."));
            return err;
		}		
		
		log.info("Got the Destination pocket for the bulk transfer --> " + destPocket.getId());
		
		SubscriberMdn destSubscriberMDN = destPocket.getSubscriberMdn();
		validationResult=transactionApiValidationService.validateSubscriberAsDestination(destSubscriberMDN);
		if(!CmFinoFIX.ResponseCode_Success.equals(validationResult)){
			log.error("Destination subscriber with mdn : "+destSubscriberMDN.getMdn()+" has failed validations");
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Destination subscriber has failed validations."));
            return err;
		}
		
 		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);

		log.info("Creating the Bulk Transfer Inquiry object");
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setSourceMDN(bulkUpload.getMdn());
		transactionDetails.setDestMDN(destSubscriberMDN.getMdn());
		transactionDetails.setAmount(bulkUpload.getTotalamount());
		transactionDetails.setSourcePIN("mFino260");
		transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_BULK_TRANSFER);
		transactionDetails.setChannelCode(channelCode.getChannelcode());
		transactionDetails.setSrcPocketId(sourcePocket.getId().longValue());
		transactionDetails.setDestinationPocketId(destPocket.getId().longValue());
		transactionDetails.setCc(channelCode);
		transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_BULK_TRANSFER);

		XMLResult result = (XMLResult)bulkTransferInquiryHandler.handle(transactionDetails);

		if (result != null) {
			bulkUpload.setServicechargetransactionlogid(result.getSctlID());
			bulkUploadService.save(bulkUpload);
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode())) {
				log.info("Creating the Bulk Transfer Confirmation object");
				
				transactionDetails.setParentTxnId(result.getParentTransactionID());
				transactionDetails.setTransferId(result.getTransferID());
				transactionDetails.setConfirmString("true");
				
				result = (XMLResult)bulkTransferHandler.handle(transactionDetails);

				if (result != null && result.getDetailsOfPresentTransaction()!= null) {
					CommodityTransfer ct = result.getDetailsOfPresentTransaction();
					if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferstatus())) {
						log.info("Trnasfer from source Pocket to Destination suspense pocket completed");
						err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
						return err;
					}
					else {
						log.info("Failing the Bulk Transfer " + bulkUpload.getId() + "  as the Transaction is failed.");
						String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : result.getNotificationCode()+"";
						bulkTransferService.failTheBulkTransfer(bulkUpload,  failureReason);
			            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			            err.setErrorDescription(MessageText._("Sorry, Failing the Bulk Transfer " + bulkUpload.getId() + "  as the Transaction is failed."));
						return err;
					}
				}
				// No Response from Back end after confirming the transaction. Leave the Bulk transfer in processing state.
				else {
					log.info("No Response from Back end -- > " + result.getMessage());
					bulkUpload.setDeliverystatus(CmFinoFIX.BulkUploadDeliveryStatus_Pending);
					bulkUpload.setDeliverydate(new Timestamp());
					bulkUploadService.save(bulkUpload);
		            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		            err.setErrorDescription(MessageText._("Sorry, The transaction is in Pending as there is no response from Backend."));
					return err;
				}
			}
			else {
				log.info("Failing the Bulk Transfer " + bulkUpload.getId() + " as the Inquiry failed");
				String failureReason = StringUtils.isNotBlank(result.getMessage()) ? result.getMessage() : result.getNotificationCode()+"";
				bulkTransferService.failTheBulkTransfer(bulkUpload, failureReason);
	            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
	            err.setErrorDescription(MessageText._("Sorry, Failing the Bulk Transfer " + bulkUpload.getId() + " as the Inquiry failed"));
				return err;
			}
		}
		else {
			log.info(" Failing the Bulk Transfer " + bulkUpload.getId() + " as the Inquiry result is null");
			String failureReason = "Inquiry Result is null";
			bulkTransferService.failTheBulkTransfer(bulkUpload,  failureReason);
            err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
            err.setErrorDescription(MessageText._("Sorry, Failing the Bulk Transfer " + bulkUpload.getId() + " as the Inquiry result is null"));
			return err;
		}
	}
}
