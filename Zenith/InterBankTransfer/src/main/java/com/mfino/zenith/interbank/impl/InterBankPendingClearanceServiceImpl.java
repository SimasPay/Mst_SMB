package com.mfino.zenith.interbank.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.InterBankTransfersDao;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.InterBankTransfersQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.InterbankTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMInterBankFundsTransferStatus;
import com.mfino.fix.CmFinoFIX.CMInterBankPendingCommodityTransferRequest;
import com.mfino.mce.backend.impl.PendingClearanceServiceDefaultImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.TransactionPendingSummaryService;
import com.mfino.zenith.interbank.IBTBankService;
import com.mfino.zenith.interbank.InterBankPendingClearanceService;

/**
 * @author Sasi
 *
 */
public class InterBankPendingClearanceServiceImpl extends PendingClearanceServiceDefaultImpl implements InterBankPendingClearanceService {
	
	private IBTBankService ibtBankService;
	
	private TransactionPendingSummaryService transactionPendingSummaryService ;

	public TransactionPendingSummaryService getTransactionPendingSummaryService() {
		return transactionPendingSummaryService;
	}

	public void setTransactionPendingSummaryService(
			TransactionPendingSummaryService transactionPendingSummaryService) {
		this.transactionPendingSummaryService = transactionPendingSummaryService;
	}

	@Override
	public CFIXMsg processMessage(CMInterBankPendingCommodityTransferRequest fixPendingRequest) {
		log.debug("InterBankPendingClearanceServiceImpl :: processMessage() BEGIN");
		CFIXMsg returnFix = null;
		
		Long sctlID = fixPendingRequest.getTransferID();
		ServiceChargeTransactionLog sctl = coreDataWrapper.getSCTLById(sctlID);
		
		if(sctl==null||!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())){
			BackendResponse bResponse = new BackendResponse();
			//Should not reach here as this request is only sent if the record is in PendingCommodityTransfer table.
			String message = "No PendingSctl record found for ID: "+sctlID;
			bResponse.setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
			bResponse.setDescription(message);
			log.warn(message);
			return bResponse;
		}
		
		List<ChargeTxnCommodityTransferMap> ctxnMap = coreDataWrapper.getBySctlID(sctlID);
		
		if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete)){
			try{
				transactionPendingSummaryService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			CMInterBankFundsTransferStatus txnStatus = new CMInterBankFundsTransferStatus();
			InterbankTransfer ibt = getIBT(sctl.getID());			
			txnStatus.setDestBankCode(ibt.getDestBankCode());
			txnStatus.setTerminalID(ibt.getTerminalID());
			txnStatus.setPaymentReference(ibt.getPaymentReference());
			txnStatus.setParentTransactionID(ibt.getTransferID());
			txnStatus.setServiceChargeTransactionLogID(ibt.getSctlId());
			//txnStatus.setTransactionID(pendingCT.getTransactionsLogByTransactionID().getID());
			returnFix = txnStatus;
		}
		else if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel))
		{
			try{
				transactionPendingSummaryService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			BackendResponse backendResponse = new BackendResponse();
			PendingCommodityTransfer pct = null;
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				if(pct!=null){
				backendResponse.setTransferID(pct.getID());
				backendResponse.setInternalErrorCode(NotificationCodes.TransactionCannotBeCancelled.getInternalErrorCode());
				break;
				}
			}
			backendResponse.setServiceChargeTransactionLogID(sctlID);
			returnFix = backendResponse;
		}
		
		log.debug("InterBankPendingClearanceServiceImpl :: processMessage() END");
		return returnFix;
	}

	public IBTBankService getIbtBankService() {
		return ibtBankService;
	}

	public void setIbtBankService(IBTBankService ibtBankService) {
		this.ibtBankService = ibtBankService;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public InterbankTransfer getIBT(Long sctlId){
		
		if(sctlId == null) return null;
		
		InterBankTransfersDao interBankTransferDao = DAOFactory.getInstance().getInterBankTransferDao();
		InterBankTransfersQuery query = new InterBankTransfersQuery();
		query.setSctlId(sctlId);
		
		List<InterbankTransfer> ibtList = interBankTransferDao.get(query);
		
		if(ibtList!=null && !ibtList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return ibtList.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public ServiceChargeTransactionLog getSctlByTransactionLogId(Long transactionLogId){
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getByTransactionLogId(transactionLogId);
		
		return sctl;
	}
}
