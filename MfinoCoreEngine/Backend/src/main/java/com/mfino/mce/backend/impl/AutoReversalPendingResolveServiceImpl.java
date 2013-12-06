package com.mfino.mce.backend.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChargeTxnCommodityTransferMapDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.ChargeTxnCommodityTransferMapQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.mce.backend.AutoReversalPendingResolveService;
import com.mfino.mce.backend.AutoReversalService;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.MCEMessage;

/**
 * @author Sasi
 *
 */
public class AutoReversalPendingResolveServiceImpl extends BaseServiceImpl implements AutoReversalPendingResolveService{
	
	private AutoReversalService autoReversalService;
	
	private BankService bankService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage resolvePendingTransaction(MCEMessage mceMessage) {
		log.info("AutoReversalPendingResolveService :: resolvePendingTransaction mceMessage="+mceMessage);
		
		CFIXMsg response = null;
		CMPendingCommodityTransferRequest pendingRequest = (CMPendingCommodityTransferRequest)mceMessage.getRequest();
		
		Long sctlId = pendingRequest.getServiceChargeTransactionLogID();
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getById(sctlId);
		
		PendingCommodityTransfer pct = getPendingCommodityTransfer(sctl);
		
		AutoReversals autoReversals = autoReversalService.getAutoReversalBySctlId(sctlId);
		
		if((null != autoReversals.getAutoRevStatus()) && (CmFinoFIX.AutoRevStatus_DEST_TRANSIT_PENDING.equals(autoReversals.getAutoRevStatus()))){
			log.info("BillPayPendingResolveServiceImpl :: resolvePendingTransaction() AutoRevStatus_DEST_TRANSIT_PENDING");
			if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel)){
				response = bankService.onRevertOfTransferConfirmation(pct, true);
				autoReversalService.updateAutoReversalStatus(sctlId, CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED);
				//batch will take care.
			}
			else{
				response = bankService.onResolveCompleteOfTransfer(pct);
				autoReversalService.updateAutoReversalStatus(sctlId, CmFinoFIX.AutoRevStatus_DEST_TRANSIT_COMPLETED);
				//transit to source is pending, so set approriate queue name.
				mceMessage.setDestinationQueue("jms:transitToSourceInquiry?disableReplyTo=true");
			}
		}
		else if((null != autoReversals.getAutoRevStatus()) && (CmFinoFIX.AutoRevStatus_TRANSIT_SRC_PENDING.equals(autoReversals.getAutoRevStatus()))){
			log.info("BillPayPendingResolveServiceImpl :: resolvePendingTransaction() AutoRevStatus_TRANSIT_SRC_PENDING");
			if(pendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel)){
				response = bankService.onRevertOfTransferConfirmation(pct, true);
				autoReversalService.updateAutoReversalStatus(sctlId, CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED);
			}
			else{
				response = bankService.onResolveCompleteOfTransfer(pct);
				autoReversalService.updateAutoReversalStatus(sctlId, CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED);
			}
		}
		else{
			log.error("BillPayPendingResolveServiceImpl :: resolvePendingTransaction, BUG in the code, should not come here.");
		}		
		
		mceMessage.setResponse(response);
		
		return mceMessage;
	}
	
	private PendingCommodityTransfer getPendingCommodityTransfer(ServiceChargeTransactionLog sctl){
		PendingCommodityTransfer pct = null;
		
		ChargeTxnCommodityTransferMapDAO chargeTxnCommodityTransferMapDao = DAOFactory.getInstance().getTxnTransferMap();
		PendingCommodityTransferDAO pctDao = DAOFactory.getInstance().getPendingCommodityTransferDAO();
		
		if(sctl != null)
		{
	        ChargeTxnCommodityTransferMapQuery query = new ChargeTxnCommodityTransferMapQuery();
			query.setSctlID(sctl.getID());
			List<ChargeTxnCommodityTransferMap> ctxnList = chargeTxnCommodityTransferMapDao.get(query);
			
			//Assuming there will be only one pending transaction for this sctlid
			for(ChargeTxnCommodityTransferMap ctxnMap : ctxnList){
				pct = pctDao.getById(ctxnMap.getCommodityTransferID());
				if(pct != null){
					break;
				}
			}
		}
		
		return pct;
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
}
