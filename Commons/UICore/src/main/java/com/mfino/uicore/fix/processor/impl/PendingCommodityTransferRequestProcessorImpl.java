/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.dao.query.UserQuery;
import com.mfino.domain.BulkUpload;
import com.mfino.domain.BulkUploadEntry;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.fix.CmFinoFIX.CMJSPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMQRPaymentPendingRequest;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.BulkUploadEntryService;
import com.mfino.service.BulkUploadService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.MfinoService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionTypeService;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.service.UserService;
import com.mfino.transactionapi.service.BulkTransferService;
import com.mfino.uicore.fix.processor.PendingCommodityTransferRequestProcessor;
 
/**
 *
 * @author sunil
 */
@org.springframework.stereotype.Service("PendingCommodityTransferRequestProcessorImpl")
public class PendingCommodityTransferRequestProcessorImpl extends MultixCommunicationHandler implements PendingCommodityTransferRequestProcessor{
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("BulkTransferServiceImpl")
	private BulkTransferService bulkTransferService;
	
	@Autowired
	@Qualifier("BulkUploadServiceImpl")
	private BulkUploadService bulkUploadService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("BulkUploadEntryServiceImpl")
	private BulkUploadEntryService bulkUploadEntryService;
	
	@Autowired
	@Qualifier("UnRegisteredTxnInfoServiceImpl")
	private UnRegisteredTxnInfoService unRegisteredTxnInfoService;
	
	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;


	 public CFIXMsg process(CFIXMsg msg) {

	        CMJSPendingCommodityTransferRequest oldMsg = (CMJSPendingCommodityTransferRequest)msg;
	        CMPendingCommodityTransferRequest newMsg = new CMPendingCommodityTransferRequest();
	        
	        //Check for smart #407
	        Long sctlID = oldMsg.getTransferID();
	        ServiceChargeTransactionLog sctl = serviceChargeTransactionLogService.getById(sctlID);
	        
	        if(null == sctl) {
	          CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
	          errorMsg.setErrorDescription(MessageText._("This transfer is no longer pending. Please refresh."));
	          errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);	          
	          return errorMsg;
	        }
	        if(!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())) {
		          CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		          errorMsg.setErrorDescription(MessageText._("This transfer is no longer pending. Please refresh."));
		          errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);	          
		          return errorMsg;
		        }
	        newMsg.setServiceChargeTransactionLogID(sctlID);
	        updateEntity(newMsg, oldMsg);
	        CFIXMsg response = handleRequestResponse( buildMsg(newMsg,sctl));
	        
	     // Check if the Transaction is related to Bulk Transfer then do the changes in BulkUpload table also
/*	Commented as this methos not required in Dimo / Hub         
			updateBulkTransfer(sctl, response);
			*/
	        return response;
	    }


	/**
	 * Update the Bulk Transfer status
	 * @param sctl
	 * @param response
	 */
	private void updateBulkTransfer(ServiceChargeTransactionLog sctl, CFIXMsg response) {
		
 		TransactionType tt = transactionTypeService.getTransactionTypeById(sctl.getTransactionTypeID());
		
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		
		// If the transaction is Bulk Transfer then change the status of bulk upload 
		if (ServiceAndTransactionConstants.TRANSACTION_BULK_TRANSFER.equals(tt.getTransactionName())) {

 			BulkUpload bulkUpload = bulkUploadService.getBySCTLId(sctl.getID());
			if (bulkUpload != null) {
				// If the Pending Transaction resolved as Success
				if (transactionResponse.isResult() && CmFinoFIX.NotificationCode_Resovle_Transaction_To_Success.toString().equals(transactionResponse.getCode())) {
					log.info("Changing the Status of the Bulk Transfer from Pending to Approved.");
					bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Approved);
					bulkUpload.setDeliveryDate(new Timestamp());
					bulkUploadService.save(bulkUpload);
				} 
				// If the Pending transaction resolved as Failure
				else if (CmFinoFIX.NotificationCode_Resovle_Transaction_To_Fail.toString().equals(transactionResponse.getCode())){
					bulkTransferService.failTheBulkTransfer(bulkUpload, "Resolved Transaction as Failed");
				}
			}
		}
		// If the transaction is Sub Bulk Transfer then change the status of bulk upload entry
		else if (ServiceAndTransactionConstants.TRANSACTION_SUB_BULK_TRANSFER.equals(tt.getTransactionName())) {

			BulkUploadEntry bue = bulkUploadEntryService.getBulkUploadEntryBySctlID(sctl.getID());
			if (bue != null) {
				if (transactionResponse.isResult()) {
					log.info("Changing the staus of the Bulk Upload Entry for SCTL id " + sctl.getID() + " to complete");
					bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Completed);
				} else {
					log.info("Changing the staus of the Bulk Upload Entry for SCTL id " + sctl.getID() + " to Failed");
					bue.setStatus(CmFinoFIX.TransactionsTransferStatus_Failed);
				}
				bulkUploadEntryService.saveBulkUploadEntry(bue);
			}
		}
		// If the transaction is Reverse Bulk Transfer for non transfered amount then change the status of bulk upload
		else if (ServiceAndTransactionConstants.TRANSACTION_SETTLE_BULK_TRANSFER.equals(tt.getTransactionName())) {

			BulkUpload bulkUpload = bulkUploadService.getByReverseSCTLId(sctl.getID());
			if (bulkUpload != null) {
				if (transactionResponse.isResult()) {
					log.info("Setting the Revert Amount for Bulk Transfer: " + bulkUpload.getID() + " Zero.");
					bulkUpload.setRevertAmount(BigDecimal.ZERO);
				} else {
					log.info("As the Transaction is failed for Bulk Transfer: " + bulkUpload.getID() + " the Revert Amount is unchanged.");
				}
				bulkUpload.setDeliveryStatus(CmFinoFIX.BulkUploadDeliveryStatus_Complete);
				bulkUpload.setDeliveryDate(new Timestamp());
				bulkUploadService.save(bulkUpload);
			}
		}
		// If the transaction is Cash out at ATM then change the status in UnRegistered_txn_info table
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(tt.getTransactionName())) {

			UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
			UnRegisteredTxnInfo urti = null;
			urtiQuery.setTransferSctlId(sctl.getID());
			urtiQuery.setStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_REQUESTED);
			List<UnRegisteredTxnInfo> lstUrti = unRegisteredTxnInfoService.getUnRegisteredTxnInfoListByQuery(urtiQuery);
			if (CollectionUtils.isNotEmpty(lstUrti)) {
				urti = lstUrti.get(0);
				if (transactionResponse.isResult()) {
					urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
				}
				else {
					urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
					log.info("Changing the SCTL status to processing as the withdraw from ATM is failed..." + sctl.getID());

					sctl.setFailureReason("");
					transactionChargingService.chnageStatusToProcessing(sctl);
				}
				unRegisteredTxnInfoService.save(urti);
			}
		}
	}

  
    public CMBase buildMsg(CMPendingCommodityTransferRequest newMsg,ServiceChargeTransactionLog sctl) {
    	
    	Service service=mfinoService.getByServiceID(sctl.getServiceID());
        TransactionType transactionType = transactionTypeService.getTransactionTypeById(sctl.getTransactionTypeID());

        if (ServiceAndTransactionConstants.SERVICE_PAYMENT.equals(service.getServiceName())
        		&&ServiceAndTransactionConstants.TRANSACTION_BILL_PAY.equals(transactionType.getTransactionName())) {
    		CMBillPayPendingRequest billPayPendingRequest = new CMBillPayPendingRequest();
    		billPayPendingRequest.copy(newMsg);
			billPayPendingRequest.setIntegrationCode(sctl.getIntegrationCode());
        	newMsg = billPayPendingRequest;
        }
        else if (ServiceAndTransactionConstants.SERVICE_BUY.equals(service.getServiceName())
                &&ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equals(transactionType.getTransactionName())) {
    		CMBillPayPendingRequest billPayPendingRequest = new CMBillPayPendingRequest();
    		billPayPendingRequest.copy(newMsg);
			billPayPendingRequest.setIntegrationCode(sctl.getIntegrationCode());
			CommodityTransfer ct = commodityTransferService.getCommodityTransferById(sctl.getCommodityTransferID());
			if (ct != null) {
				billPayPendingRequest.setUICategory(ct.getUICategory());
			}
        	newMsg = billPayPendingRequest;
        }                
        else if (ServiceAndTransactionConstants.SERVICE_PAYMENT.equals(service.getServiceName())
        		&&ServiceAndTransactionConstants.TRANSACTION_QR_PAYMENT.equals(transactionType.getTransactionName())) {
        	CMQRPaymentPendingRequest billPayPendingRequest = new CMQRPaymentPendingRequest();
        	billPayPendingRequest.copy(newMsg);
        	billPayPendingRequest.setIntegrationCode(sctl.getIntegrationCode());
        	newMsg = billPayPendingRequest;
        }
//        else if(ServiceAndTransactionConstants.SERVICE_WALLET.equals(service.getServiceName())
//        		&&ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER.equals(transactionType.getTransactionName())){
//        	
//        	CMBillPayPendingRequest billPayPendingRequest = new CMBillPayPendingRequest();
//        	billPayPendingRequest.copy(newMsg);
//        	billPayPendingRequest.setIntegrationCode(sctl.getIntegrationCode());
//        	newMsg = billPayPendingRequest;
//        }        
//        else if(ServiceAndTransactionConstants.SERVICE_BANK.equals(service.getServiceName())
//        		&&ServiceAndTransactionConstants.TRANSACTION_INTERBANK_TRANSFER.equals(transactionType.getTransactionName())){
//        	CMInterBankPendingCommodityTransferRequest interBankPendingRequest = new CMInterBankPendingCommodityTransferRequest();
//        	interBankPendingRequest.copy(newMsg);
//        	newMsg = interBankPendingRequest;
//        }
//        else if(ServiceAndTransactionConstants.SERVICE_TELLER.equals(service.getServiceName())){
//        	CMTellerPendingCommodityTransferRequest tellerPendingRequest = new CMTellerPendingCommodityTransferRequest();
//        	tellerPendingRequest.copy(newMsg);
//        	newMsg = tellerPendingRequest;
//        }
		
        return newMsg;
		
	}


	public void updateEntity(CMPendingCommodityTransferRequest newMsg,CMJSPendingCommodityTransferRequest oldMsg) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName =(auth != null) ? auth.getName() : " ";

        UserQuery query = new UserQuery();
        query.setUserName(userName);
        List<User> results = (List<User>) userService.get(query);
        if (results.size() > 0) {
             User userObj = results.get(0);
             newMsg.setCSRUserID(userObj.getID());
        }
        newMsg.setLoginName(userName);
        if(oldMsg.getMSPID()!=null){
            newMsg.setMSPID(oldMsg.getMSPID());
        }
        if(oldMsg.getParentTransactionID()!=null){
            newMsg.setParentTransactionID(oldMsg.getParentTransactionID());
        }
        if(oldMsg.getSourceMDN()!=null){
            newMsg.setSourceMDN(oldMsg.getSourceMDN());
        }
        if(oldMsg.getTransactionID()!=null){
            newMsg.setTransactionID(oldMsg.getTransactionID());
        }
        if(oldMsg.getCSRComment()!=null){
            newMsg.setCSRComment(oldMsg.getCSRComment());
        }
        if(oldMsg.getTransferID()!=null){
            newMsg.setTransferID(oldMsg.getTransferID());
        }
        newMsg.setCSRAction(oldMsg.getCSRAction());
        newMsg.setCSRUserName(userName);
        newMsg.setServletPath(CmFinoFIX.ServletPath_BankAccount);
    }
}