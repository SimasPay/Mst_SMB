package com.mfino.billpayments.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.billpayments.service.BillPayMoneyTransferService;
import com.mfino.billpayments.service.BillPayPendingResolveService;
import com.mfino.billpayments.service.BillPaymentsBaseServiceImpl;
import com.mfino.billpayments.service.BillPaymentsService;
import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBillPayPendingRequest;
import com.mfino.mce.backend.AutoReversalPendingResolveService;
import com.mfino.mce.backend.AutoReversalService;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.TransactionPendingSummaryService;

/**
 * 
 * @author Sasi
 *
 */
public class BillPayPendingResolveServiceImpl extends BillPaymentsBaseServiceImpl implements BillPayPendingResolveService{
	
	private BillPaymentsService billPaymentsService;
	
	private BillPayMoneyTransferService billPayMoneyTransferService;
	
	private AutoReversalService autoReversalService;
	
	private AutoReversalPendingResolveService autoReversalPendingResolveService;
	
	private BankService bankService;
	
	private TransactionPendingSummaryService transactionPendingSummaryService;

	public TransactionPendingSummaryService getTransactionPendingSummaryService() {
		return transactionPendingSummaryService;
	}

	public void setTransactionPendingSummaryService(
			TransactionPendingSummaryService transactionPendingSummaryService) {
		this.transactionPendingSummaryService = transactionPendingSummaryService;
	}

	@Override
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage resolvePendingTransaction(MCEMessage mceMessage) {
		log.info("BillPayPendingResolveServiceImpl :: resolvePendingTransaction BEGIN mceMessage="+mceMessage);
		
		CFIXMsg response = null;
		CMBillPayPendingRequest pendingRequest = (CMBillPayPendingRequest)mceMessage.getRequest();
		log.info("PendingRequest: "+ pendingRequest.getServiceChargeTransactionLogID() + " CSR Action: " + pendingRequest.getCSRAction());
		
		Long sctlId = pendingRequest.getServiceChargeTransactionLogID();
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);

		PendingCommodityTransfer pct = getPendingCommodityTransfer(sctl);
		
		if((sctl==null) || ((!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())) && (pct != null))){
			log.error("sctl is null or sctl status is not pending @@"+sctlId);

			response = new BackendResponse();
			String message = "No PendingTransaction record found for ID: "+sctlId;
			((BackendResponse)response).setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
			((BackendResponse)response).setDescription(message);
		}
		else
		{
			BillPayments billPayments = billPaymentsService.getBillPaymentsRecord(sctlId);
			AutoReversals autoReversals = autoReversalService.getAutoReversalBySctlId(sctlId);
			
			log.info("BillPayPendingResolveServiceImpl :: billPayments="+billPayments+", autoReversals="+autoReversals);
			
			if(billPayments != null){
				if(autoReversals != null){
					log.info("BillPayPendingResolveServiceImpl :: This is a reversal pending case autoReversals status = "+autoReversals.getAutorevstatus());
					mceMessage = autoReversalPendingResolveService.resolvePendingTransaction(mceMessage);
					
					try{
						transactionPendingSummaryService.saveTransactionPendingSummary(pendingRequest);
					}catch(Exception e){
						log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getId()+" while Resolving",e);
					}
					//reversal means sctl should be failed.
					sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
					sctlDao.save(sctl);
				}
				else
				{
					log.info("BillPayPendingResolveServiceImpl :: BillPayStatus: "+ billPayments.getBillpaystatus());
					if((null != billPayments.getBillpaystatus()) && 
							(CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_PENDING.equals(billPayments.getBillpaystatus()))){
						
						if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel)){
							response = bankService.onRevertOfTransferConfirmation(pct, true);
							((BackendResponse)response).setInternalErrorCode(NotificationCodes.BillpaymentFailed.getInternalErrorCode());
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_FAILED);
						}
						else if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete)){
							//here reversal is required, so set reversal queue as destination queue
							response = bankService.onResolveCompleteOfTransfer(pct);
							((BackendResponse)response).setInternalErrorCode(NotificationCodes.BillpaymentFailed.getInternalErrorCode());
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_MT_SRC_TO_SUSPENSE_COMPLETED);
							/*
							 * after reversal is done message will be routed to suspenseAndChargesRRQueue (bill pay reversal response)
							 * and notification will be sent only if reversal is positive, Otherwise, batch will take care of reinitiating the reversal.
							 */
							mceMessage.setDestinationQueue("jms:suspenseAndChargesToSourceReversal");
						}
						
						try{
							transactionPendingSummaryService.saveTransactionPendingSummary(pendingRequest);
						}catch(Exception e){
							log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getId()+" while Resolving",e);
						}
						sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
						sctlDao.save(sctl);
					}
					else if((null != billPayments.getBillpaystatus()) && 
							(CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_PENDING.equals(billPayments.getBillpaystatus()))){

						//if notification not required, set internalError code to null here.
						if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel)){
							response = bankService.onRevertOfTransferConfirmation(pct, true);
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_FAILED);
						}
						else if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete)){
							response = bankService.onResolveCompleteOfTransfer(pct);
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_MT_SUSPENSE_TO_DEST_COMPLETED);
						}
						
						try{
							transactionPendingSummaryService.saveTransactionPendingSummary(pendingRequest);
						}catch(Exception e){
							log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getId()+" while Resolving",e);
						}
						sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
						sctlDao.save(sctl);
					}
					else if((null != billPayments.getBillpaystatus()) && 
							(CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_PENDING.equals(billPayments.getBillpaystatus()))){

						//if notification not required, set internalError code to null here.
						if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel)){
							response = new BackendResponse();
							((BackendResponse)response).setInternalErrorCode(NotificationCodes.BillpaymentFailed.getInternalErrorCode());
							((BackendResponse)response).setServiceChargeTransactionLogID(pendingRequest.getServiceChargeTransactionLogID());
							((BackendResponse)response).setResult(CmFinoFIX.ResponseCode_Failure);
							((BackendResponse)response).setUICategory(pendingRequest.getUICategory());
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_FAILED);
							/*
							 * after reversal is done message will be routed to suspenseAndChargesRRQueue (bill pay reversal response)
							 * and notification will be sent only if reversal is positive, Otherwise, batch will take care of reinitiating the reversal.
							 */
							mceMessage.setDestinationQueue("jms:suspenseAndChargesToSourceReversal");
							try{
								transactionPendingSummaryService.saveTransactionPendingSummary(pendingRequest);
							}catch(Exception e){
								log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getId()+" while Resolving",e);
							}
							sctl.setStatus(CmFinoFIX.SCTLStatus_Failed);
						}
						else if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete)){
							response = new BackendResponse();
							((BackendResponse)response).setInternalErrorCode(NotificationCodes.BillpaymentConfirmationSuccessful.getInternalErrorCode());
							((BackendResponse)response).setServiceChargeTransactionLogID(pendingRequest.getServiceChargeTransactionLogID());
							((BackendResponse)response).setResult(CmFinoFIX.ResponseCode_Success);
							((BackendResponse)response).setUICategory(pendingRequest.getUICategory());
							billPaymentsService.updateBillPayStatus(sctlId, CmFinoFIX.BillPayStatus_BILLER_CONFIRMATION_COMPLETED);
							/*
							 * transaction completed so initiate suspense to destination transfer
							 * direct:moneyTransferInquirySuspenseToDestination
							 */
							mceMessage.setDestinationQueue("jms:moneyTransferInquirySuspenseToDestination");
							try{
								transactionPendingSummaryService.saveTransactionPendingSummary(pendingRequest);
							}catch(Exception e){
								log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getId()+" while Resolving",e);
							}
							sctl.setStatus(CmFinoFIX.SCTLStatus_Confirmed);
						}
						sctlDao.save(sctl);
					}
					else{
						log.error("BillPayPendingResolveServiceImpl :: ************ bug in code, should not come here ************");
					}
				}
			}
			else{
				log.error("No Billpayments record found for sctlid="+sctlId);
				response = new BackendResponse();
				String message = "No Billpayments record found for sctlId="+sctlId;
				((BackendResponse)response).setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
				((BackendResponse)response).setDescription(message);				
			}
		}
		
		mceMessage.setResponse(response);
		
		log.info("BillPayPendingResolveServiceImpl :: resolvePendingTransaction END");
		return mceMessage;
	}
	
	private PendingCommodityTransfer getPendingCommodityTransfer(ServiceChargeTxnLog sctl){
		PendingCommodityTransfer pct = null;
		
		ChargeTxnCommodityTransferMapDAO chargeTxnCommodityTransferMapDao = DAOFactory.getInstance().getTxnTransferMap();
		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		
		if(sctl != null)
		{
	        ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getId().longValue());
			List<ChargeTxnCommodityTransferMap> ctxnList = chargeTxnCommodityTransferMapDao.get(query);
			
			//Assuming there will be only one pending transaction for this sctlid
			for(ChargeTxnCommodityTransferMap ctxnMap : ctxnList){
				pct = pctDao.getById(ctxnMap.getCommoditytransferid().longValue());
				if(pct != null){
					break;
				}
			}
		}
		
		return pct;
	}

	public BillPaymentsService getBillPaymentsService() {
		return billPaymentsService;
	}

	public void setBillPaymentsService(BillPaymentsService billPaymentsService) {
		this.billPaymentsService = billPaymentsService;
	}

	public AutoReversalService getAutoReversalService() {
		return autoReversalService;
	}

	public void setAutoReversalService(AutoReversalService autoReversalService) {
		this.autoReversalService = autoReversalService;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	public AutoReversalPendingResolveService getAutoReversalPendingResolveService() {
		return autoReversalPendingResolveService;
	}

	public void setAutoReversalPendingResolveService(AutoReversalPendingResolveService autoReversalPendingResolveService) {
		this.autoReversalPendingResolveService = autoReversalPendingResolveService;
	}

	public BillPayMoneyTransferService getBillPayMoneyTransferService() {
		return billPayMoneyTransferService;
	}

	public void setBillPayMoneyTransferService(
			BillPayMoneyTransferService billPayMoneyTransferService) {
		this.billPayMoneyTransferService = billPayMoneyTransferService;
	}
	
	
}
