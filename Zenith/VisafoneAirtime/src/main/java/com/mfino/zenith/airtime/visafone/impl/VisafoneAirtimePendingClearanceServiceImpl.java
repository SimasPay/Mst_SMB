package com.mfino.zenith.airtime.visafone.impl;

import java.util.List;

import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePendingCommodityTransferRequest;
import com.mfino.mce.backend.impl.BaseServiceImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MessageTypes;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimeBankService;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimePendingClearanceService;

/**
 * @author Sasi
 *
 */
public class VisafoneAirtimePendingClearanceServiceImpl extends BaseServiceImpl implements VisafoneAirtimePendingClearanceService {
	
	private VisafoneAirtimeBankService visafoneAirtimeBankService;
	
	@Override
	public CFIXMsg processMessage(CMVisafoneAirtimePendingCommodityTransferRequest fixPendingRequest) {
		log.debug("VisafoneAirtimePendingClearanceServiceImpl :: processMessage() BEGIN");
		CFIXMsg returnFix = null;
	
		BackendResponse backendResponse = createResponseObject();
		backendResponse.copy(fixPendingRequest);
		
		//transaction is cancelled
		Long sctlID = fixPendingRequest.getTransferID();
		ServiceChargeTransactionLog sctl = coreDataWrapper.getSCTLById(sctlID);
		
		if(sctl==null||!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())){
			//Should not reach here as this request is only sent if the record is in PendingCommodityTransfer table.
			String message = "No PendingTransaction record found for ID: "+sctlID;
			backendResponse.setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
			backendResponse.setDescription(message);
			log.warn(message);
			return backendResponse;
		}
		List<ChargeTxnCommodityTransferMap> ctxnMap = coreDataWrapper.getBySctlID(sctlID);		
		
		if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel))
		{
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				PendingCommodityTransfer pct = null;
				CommodityTransfer ct = null;
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				ct = coreDataWrapper.getCommodityTransferDao().getById(ctxn.getCommodityTransferID());
				if(pct!=null){
					Integer transferStatus = pct.getTransferStatus();
					Integer transferFailureReason = pct.getTransferFailureReason();
					
					if (((pct.getMsgType().equals(MessageTypes.VisafoneAirtimePurchaseInquiry.getMessageCode())) || 
						(pct.getMsgType().equals(MessageTypes.VisafoneAirtimePurchase.getMessageCode()))) && 
						(CmFinoFIX.TransferStatus_Pending.equals(transferStatus)) && 
						(((CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable.equals(transferFailureReason)))) ||
						((CmFinoFIX.TransferFailureReason_RejectedByIntegration.equals(transferFailureReason))))
					{
						
						returnFix = visafoneAirtimeBankService.onRevertOfIntegrationService(pct);
					}
					else{
						log.error("VisafoneAirtimePendingClearanceServiceImpl :: CMVisafoneAirtimePendingCommodityTransferRequest has invalid transfer status, This transaction cannot be resolved");
					}
					break;
				}
				else if(ct!=null){
					//revert transaction
				}
			}
		}
		else if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete))
		{
			/*
			 * Only one case to handle here, payment is always successful, as this is E-Money to E-Money transfer, 
			 * So just move pct to ct.
			 */
			PendingCommodityTransfer pct = null;
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				if(pct!=null){
					Integer transferStatus = pct.getTransferStatus();
					Integer transferFailureReason = pct.getTransferFailureReason();
					
					if (((pct.getMsgType().equals(MessageTypes.VisafoneAirtimePurchaseInquiry.getMessageCode())) || 
							(pct.getMsgType().equals(MessageTypes.VisafoneAirtimePurchase.getMessageCode()))) && 
							(CmFinoFIX.TransferStatus_Pending.equals(transferStatus)) && 
							(((CmFinoFIX.TransferFailureReason_IntegrationServiceUnavailable.equals(transferFailureReason)))) ||
							((CmFinoFIX.TransferFailureReason_RejectedByIntegration.equals(transferFailureReason))))
						{
				
							returnFix = visafoneAirtimeBankService.onResolveOfIntegrationService(pct);
						}
						else
						{
							log.error("VisafoneAirtimePendingClearanceServiceImpl :: CMVisafoneAirtimePendingCommodityTransferRequest has invalid transfer status, This transaction cannot be resolved");
						}
					
					break;
				}
			}
		}
		
		log.debug("VisafoneAirtimePendingClearanceServiceImpl :: processMessage() END");
		return returnFix;
	}

	public VisafoneAirtimeBankService getVisafoneAirtimeBankService() {
		return visafoneAirtimeBankService;
	}

	public void setVisafoneAirtimeBankService(VisafoneAirtimeBankService visafoneAirtimeBankService) {
		this.visafoneAirtimeBankService = visafoneAirtimeBankService;
	}
}
