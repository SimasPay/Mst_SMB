package com.mfino.mce.backend.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.PendingClearanceService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionPendingSummaryService;
import com.mfino.service.impl.TransactionChargingServiceImpl;
import com.mfino.service.impl.TransactionPendingSummaryServiceImpl;

/**
 * Pending clearance service would do all to make sure that pending transactions are cleared 
 * from pending state
 * It is utmost important to get the transactions to the consistent state
 * including the ledger entries. 
 * @author POCHADRI
 *
 */
public class PendingClearanceServiceDefaultImpl extends BaseServiceImpl implements 	PendingClearanceService{
	
	private BankService bankService;
	private TransactionChargingService transactionChargingService ;


	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(
			TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	private TransactionPendingSummaryService transactionPendingSummaryService;

	public TransactionPendingSummaryService getTransactionPendingSummaryService() {
		return transactionPendingSummaryService;
	}

	public void setTransactionPendingSummaryService(
			TransactionPendingSummaryService transactionPendingSummaryService) {
		this.transactionPendingSummaryService = transactionPendingSummaryService;
	}

	@Override
	public CFIXMsg processMessage(CMPendingCommodityTransferRequest fixPendingRequest) 
	{
		log.info("got the request in pending clearance service");
		//get the pending commodity transfer record
		CFIXMsg returnFix = createResponseObject();
		returnFix = takeAction(fixPendingRequest);
		
		log.info("Completed processMessage, response: "+returnFix);
		
		return returnFix;
	}
	
	private CFIXMsg takeAction(CMPendingCommodityTransferRequest fixPendingRequest) 
	{
		log.info(":: takeAction() BEGIN #"+fixPendingRequest);
		BackendResponse returnFix = createResponseObject();
		returnFix.copy(fixPendingRequest);
		
		Long sctlID = fixPendingRequest.getTransferID();
		ServiceChargeTransactionLog sctl = coreDataWrapper.getSCTLById(sctlID);
		TransactionType trxnType = DAOFactory.getInstance().getTransactionTypeDAO().getById(sctl.getTransactionTypeID());
		
		if(sctl==null||!CmFinoFIX.SCTLStatus_Pending.equals(sctl.getStatus())){
			//Should not reach here as this request is only sent if the record is in PendingCommodityTransfer table.
			String message = "No PendingSctl record found for ID: "+sctlID;
			returnFix.setInternalErrorCode(CmFinoFIX.ErrorCode_Generic);
			returnFix.setDescription(message);
			log.warn(message);
			return returnFix;
		}
		
		
		List<ChargeTxnCommodityTransferMap> ctxnMap = coreDataWrapper.getBySctlID(sctlID);
		if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Cancel))
		{
			try{
				transactionPendingSummaryService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				PendingCommodityTransfer pct = null;
				CommodityTransfer ct = null;
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				ct = coreDataWrapper.getCommodityTransferDao().getById(ctxn.getCommodityTransferID());
				if(pct!=null){
					pct.setCSRAction(fixPendingRequest.getCSRAction());
					pct.setCSRActionTime(new Timestamp());
					pct.setCSRComment(fixPendingRequest.getCSRComment());
					pct.setCSRUserID(fixPendingRequest.getCSRUserID());
					pct.setCSRUserName(fixPendingRequest.getCSRUserName());
					returnFix = (BackendResponse)bankService.onRevertOfTransferConfirmation(pct, true);
					if(sctl.getCommodityTransferID()==null){
						transactionChargingService.addTransferID(sctl, pct.getID());
					}		
					
					// As the money reversed from suspense pocket to Third Party Suspense pocket, we need to change the status of SCTL to processing so that the scheduler
					// will pickup for expiring the transaction and money will be reversed to source.					
					if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName()) &&
							CmFinoFIX.TransactionUICategory_Withdraw_From_ATM.equals(pct.getUICategory())) {
						log.info("Changing the status of the SCTL : " + sctl.getID() + " to Processing and URTI to Cashout failed");
						transactionChargingService.chnageStatusToProcessing(sctl);
						UnRegisteredTxnInfoDAO urtiDAO = coreDataWrapper.getUnRegisteredTxnInfoDAO();
						UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
						urtiQuery.setTransferSctlId(sctl.getID());
						List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = urtiDAO.get(urtiQuery);
						UnRegisteredTxnInfo urti = null;
						if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
							urti = lstUnRegisteredTxnInfos.get(0);
							urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
							urtiDAO.save(urti);
						}
					}
					else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName()) &&
							CmFinoFIX.TransactionUICategory_Reverse_From_ATM.equals(pct.getUICategory())) {
						//TODO
						// As Reversal from ATM is failed money struck in bank only so need to transfer money from Bank to source again, 
						// for this ATM has to send Reverse again. If it is not possible the bank has to settle subscriber manually by debit the Bank pocket of ATM 
						// and credit Emoney pocket of the source subscriber.
						transactionChargingService.confirmTheTransaction(sctl);
					}
					
				}
				if(ct!=null&&ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
					//FIXME revert transaction
				}
			}
			if (! ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName())) {
				String failureReason = "resolve as failed";
				transactionChargingService.failTheTransaction(sctl, failureReason);
			}
		}
		else if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete))
		{
			try{
				transactionPendingSummaryService.saveTransactionPendingSummary(fixPendingRequest);
			}catch(Exception e){
				log.error("Exception occured in saving TransactionPendingSummary Domaina Object corresponding to SCTL with id:"+sctl.getID()+" while Resolving",e);
			}
			
			//transaction need to be completed
			//identify the transaction and complete the transaction			
			PendingCommodityTransfer pct = null;
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				if(pct!=null){
					pct.setCSRAction(fixPendingRequest.getCSRAction());
					pct.setCSRActionTime(new Timestamp());
					pct.setCSRComment(fixPendingRequest.getCSRComment());
					pct.setCSRUserID(fixPendingRequest.getCSRUserID());
					pct.setCSRUserName(fixPendingRequest.getCSRUserName());
					returnFix = (BackendResponse)bankService.onResolveCompleteOfTransfer(pct);
					
					if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName()) &&
							CmFinoFIX.TransactionUICategory_Withdraw_From_ATM.equals(pct.getUICategory())) {
						//TODO 
						// As the money transfered to Bank pocket of ATM, Bank needs to give money to Subscriber (Physical cash).
						transactionChargingService.confirmTheTransaction(sctl);
					}
					// As the money moved from suspense pocket to Third Party Suspense pocket, we need to change the status of SCTL to processing so that the scheduler
					// will pickup for expiring the transaction and money will be reversed to source.					
					else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName()) &&
							CmFinoFIX.TransactionUICategory_Reverse_From_ATM.equals(pct.getUICategory())) {
						log.info("Changing the status of the SCTL : " + sctl.getID() + " to Processing and URTI to Cashout failed");
						transactionChargingService.chnageStatusToProcessing(sctl);
						UnRegisteredTxnInfoDAO urtiDAO = coreDataWrapper.getUnRegisteredTxnInfoDAO();
						UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
						urtiQuery.setTransferSctlId(sctl.getID());
						List<UnRegisteredTxnInfo> lstUnRegisteredTxnInfos = urtiDAO.get(urtiQuery);
						UnRegisteredTxnInfo urti = null;
						if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
							urti = lstUnRegisteredTxnInfos.get(0);
							urti.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
							urtiDAO.save(urti);
						}
					}					
//					only one pending transaction exists for resolve as complete transactions 
					break;
				}
			}
			if (! ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM.equals(trxnType.getTransactionName())) {
			    transactionChargingService.confirmTheTransaction(sctl);
			}
			if(sctl.getCommodityTransferID()==null&&pct!=null){
				transactionChargingService.addTransferID(sctl, pct.getID());
			}						
		}
		returnFix.setServiceChargeTransactionLogID(sctlID);
		FundServiceImpl fundServiceImpl = new FundServiceImpl();
		fundServiceImpl.setCoreDataWrapper(getCoreDataWrapper());
		if(trxnType.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_FUND_WITHDRAWAL)){
			fundServiceImpl.resolvePendingFunds(fixPendingRequest.getCSRAction(),returnFix);
		}
		return returnFix;
	}
	
	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
}
