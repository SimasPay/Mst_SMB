package com.mfino.zenith.dstv.impl;

import java.util.List;

import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMDSTVPendingCommodityTransferRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.PendingClearanceServiceDefaultImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MessageTypes;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionPendingSummaryServiceImpl;
import com.mfino.zenith.dstv.DSTVBankService;
import com.mfino.zenith.dstv.DSTVPendingClearanceService;

public class DSTVPendingClearanceServiceImpl extends PendingClearanceServiceDefaultImpl implements DSTVPendingClearanceService {
	
	private DSTVBankService dstvBankService;

	private TransactionChargingService transactionChargingService;

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	@Override
	public CFIXMsg processMessage(CMDSTVPendingCommodityTransferRequest fixPendingRequest) {
		log.info("DSTVPendingClearanceServiceImpl got the request in pending clearance service");
		//get the pending commodity transfer record
		CFIXMsg returnFix = createResponseObject();
		returnFix = takeAction(fixPendingRequest);
		
		
	
		log.info("DSTVPendingClearanceServiceImpl Completed processMessage, response: "+returnFix);
		
		return returnFix;
	}
	
	private CFIXMsg takeAction(CMDSTVPendingCommodityTransferRequest fixPendingRequest) 
	{
		BackendResponse returnFix = createResponseObject();
		returnFix.copy(fixPendingRequest);
		
		
		Long sctlID = fixPendingRequest.getTransferID();
		ServiceChargeTransactionLog sctl = coreDataWrapper.getSCTLById(sctlID);
		
		if(sctl==null||!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())){
			//Should not reach here as this request is only sent if the record is in PendingCommodityTransfer table.
			String message = "No PendingSctl record found for ID: "+sctlID;
			returnFix.setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
			returnFix.setDescription(message);
			log.warn(message);
			return returnFix;
		}
		
		TransactionPendingSummaryServiceImpl tpsService = new TransactionPendingSummaryServiceImpl();
    	
    	
		List<ChargeTxnCommodityTransferMap> ctxnMap = coreDataWrapper.getBySctlID(sctlID);

		if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel))
		{
			try{
				tpsService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				PendingCommodityTransfer pct = null;
				CommodityTransfer ct = null;
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				ct = coreDataWrapper.getCommodityTransferDao().getById(ctxn.getCommodityTransferID());
				//Before Correcting errors reported by Findbugs:
					//Integer transferFailureReason = pct.getTransferFailureReason();
					//Integer transferStatus = pct.getTransferStatus();
			
			    //After Correcting the errors reported by Findbugs:pct was being used without performing null check
				if(pct!=null){
					Integer transferFailureReason = pct.getTransferFailureReason();
					Integer transferStatus = pct.getTransferStatus();
					if (((pct.getMsgType().equals(MessageTypes.DSTVPaymentInquiry.getMessageCode())) || 
							(pct.getMsgType().equals(MessageTypes.DSTVPayment.getMessageCode()))) && 
							(CmFinoFIX.TransferStatus_Pending.equals(transferStatus))) {		
							
							if((CmFinoFIX.TransferFailureReason_RejectedByIntegration.equals(transferFailureReason)) ||
							   (CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable.equals(transferFailureReason))){
								
									BackendResponse backendResponse = new BackendResponse();
									Timestamp timeStamp = new Timestamp();
									returnFix.setSourceMDN(pct.getSourceMDN());
									returnFix.setSenderMDN(pct.getSourceMDN());
									returnFix.setLanguage(pct.getSubscriberBySourceSubscriberID().getLanguage());
									//returnFix.setReceiverMDN(objDestSubMdn.getMDN());
									returnFix.setTransactionID(pct.getTransactionsLogByTransactionID().getID());
									returnFix.setParentTransactionID(pct.getTransactionsLogByTransactionID().getID());
									returnFix.setTransferID(pct.getID());
									returnFix.setReceiveTime(timeStamp);
									returnFix.setInternalErrorCode(NotificationCodes.DSTVTransactionCannotBeCancelled.getInternalErrorCode());
									returnFix.setMessageType(pct.getMsgType());
									backendResponse.copy(returnFix);						
									returnFix = backendResponse;
									break;
							}
							else if(CmFinoFIX.TransferFailureReason_MoneyTransferToBankExpired.equals(transferFailureReason))
							{
									pct.setCSRAction(fixPendingRequest.getCSRAction());
									pct.setCSRActionTime(new Timestamp());
									pct.setCSRComment(fixPendingRequest.getCSRComment());
									pct.setCSRUserID(fixPendingRequest.getCSRUserID());
									pct.setCSRUserName(fixPendingRequest.getCSRUserName());
									returnFix = (BackendResponse)dstvBankService.onRevertOfTransferConfirmation(pct, true);
									if(sctl.getCommodityTransferID()==null){
										transactionChargingService.addTransferID(sctl, pct.getID());
									}
									String failureReason = "resolve as failed";
									transactionChargingService.failTheTransaction(sctl, failureReason);	
							}
					}
				}
				else if(ct!=null&&ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
					//FIXME revert transaction
				}
			}		
		}
		else if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete))
		{
			try{
				tpsService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			
			//transaction need to be completed
			//identify the transaction and complete the transaction			
			PendingCommodityTransfer pct = null;
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				//Before Correcting errors reported by Findbugs:
					//Integer transferFailureReason = pct.getTransferFailureReason();
					//Integer transferStatus = pct.getTransferStatus();
		
				//After Correcting the errors reported by Findbugs:pct was being used without performing null check
				if(pct!=null){
					Integer transferFailureReason = pct.getTransferFailureReason();
					Integer transferStatus = pct.getTransferStatus();
					if (((pct.getMsgType().equals(MessageTypes.DSTVPaymentInquiry.getMessageCode())) || 
							(pct.getMsgType().equals(MessageTypes.DSTVPayment.getMessageCode()))) && 
							(CmFinoFIX.TransferStatus_Pending.equals(transferStatus))) {
					
							if(CmFinoFIX.TransferFailureReason_MoneyTransferToBankExpired.equals(transferFailureReason)){
								pct.setCSRAction(fixPendingRequest.getCSRAction());
								pct.setCSRActionTime(new Timestamp());
								pct.setCSRComment(fixPendingRequest.getCSRComment());
								pct.setCSRUserID(fixPendingRequest.getCSRUserID());
								pct.setCSRUserName(fixPendingRequest.getCSRUserName());
								returnFix = (BackendResponse)dstvBankService.onResolveCompleteOfTransfer(pct);						
							}
							else if((CmFinoFIX.TransferFailureReason_RejectedByIntegration.equals(transferFailureReason)) ||
									(CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable.equals(transferFailureReason))){
								
								returnFix = (BackendResponse)dstvBankService.onResolveOfIntegrationServiceStatus(pct);
							}
					}
					
					break;
				}
			}

/*			tcs.confirmTheTransaction(sctl);
			if(sctl.getCommodityTransferID()==null&&pct!=null){
				tcs.addTransferID(sctl, pct.getID());
			}						
*/			
		}
		returnFix.setServiceChargeTransactionLogID(sctlID);
		return returnFix;
	}

	public DSTVBankService getDstvBankService() {
		return dstvBankService;
	}

	public void setDstvBankService(DSTVBankService dstvBankService) {
		this.dstvBankService = dstvBankService;
	}
}
