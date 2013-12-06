package com.mfino.mce.bankteller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.domain.ChargeTxnCommodityTransferMap;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMTellerPendingCommodityTransferRequest;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.PendingClearanceServiceDefaultImpl;
import com.mfino.mce.bankteller.TellerBankService;
import com.mfino.mce.bankteller.TellerPendingClearanceService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.result.Result;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.impl.TransactionChargingServiceImpl;
import com.mfino.transactionapi.handlers.money.impl.AutoReverseHandlerImpl;
import com.mfino.transactionapi.vo.TransactionDetails;

@Service("TellerPendingClearanceServiceImpl")
public class TellerPendingClearanceServiceImpl extends PendingClearanceServiceDefaultImpl implements TellerPendingClearanceService {
	
	private TellerBankService tellerBankService;
	private TransactionChargingService transactionChargingService ;

	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	private @Autowired AutowireCapableBeanFactory beanFactory;

	
	@Override
	public CFIXMsg processMessage(CMTellerPendingCommodityTransferRequest fixPendingRequest) 
	{
		log.info("CMTellerPendingCommodityTransferRequest got the request in pending clearance service");
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
			//FIXME for cashout revesolve revert amount to subscriber
			Integer uicatageory =0;
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
					returnFix = (BackendResponse)tellerBankService.onRevertOfTransferConfirmation(pct, true);
					if(sctl.getCommodityTransferID()==null){
						transactionChargingService.addTransferID(sctl, pct.getID());
					}	
					uicatageory= pct.getUICategory();
				}
				if(ct!=null&&ct.getTransferStatus().equals(CmFinoFIX.TransferStatus_Completed)){
					//FIXME revert transaction
				}
			}
			String failureReason = "resolve as failed";
			if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(uicatageory)){

				TransactionDetails transactionDetails = new TransactionDetails();
				transactionDetails.setSctlId(sctl.getID());
				transactionDetails.setChargeReverseAlso(true);
				
				AutoReverseHandlerImpl autoReverseHandler= new AutoReverseHandlerImpl();
				beanFactory.autowireBean(autoReverseHandler);
				
				Result result = autoReverseHandler.handle(transactionDetails);
				if (CmFinoFIX.NotificationCode_AutoReverseSuccess.equals(result.getNotificationCode())) {
					transactionChargingService.failTheTransaction(sctl, failureReason);
				}
			}
			else if(CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer.equals(uicatageory)){
				transactionChargingService.failTheTransaction(sctl, failureReason);
			}
			else if(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf.equals(uicatageory)){
				handleTellerEMoneyClearancePendingResolve(sctl,fixPendingRequest.getCSRAction());
			}
		}
		else if(fixPendingRequest.getCSRAction().equals(CmFinoFIX.CSRAction_Complete))
		{
			//transaction need to be completed
			//identify the transaction and complete the transaction	
			
			PendingCommodityTransfer pct = null;
			Integer uicatageory =0;
			for(ChargeTxnCommodityTransferMap ctxn : ctxnMap){
				pct = coreDataWrapper.getPCTById(ctxn.getCommodityTransferID());
				if(pct!=null){
					pct.setCSRAction(fixPendingRequest.getCSRAction());
					pct.setCSRActionTime(new Timestamp());
					pct.setCSRComment(fixPendingRequest.getCSRComment());
					pct.setCSRUserID(fixPendingRequest.getCSRUserID());
					pct.setCSRUserName(fixPendingRequest.getCSRUserName());
					returnFix = (BackendResponse)tellerBankService.onResolveCompleteOfTransfer(pct);
					uicatageory = pct.getUICategory();
//					only one pending transaction exists for resolve as complete transactions 
					break;
				}
			}
			
			if(sctl.getCommodityTransferID()==null&&pct!=null){
				transactionChargingService.addTransferID(sctl, pct.getID());
			}
			
			if(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank.equals(uicatageory)){
				transactionChargingService.confirmTheTransaction(sctl);
			}
			else if(CmFinoFIX.TransactionUICategory_Teller_Cashin_SelfTransfer.equals(uicatageory)){
				transactionChargingService.changeStatusToPendingResolved(sctl);
			}
			else if(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf.equals(uicatageory)){
				handleTellerEMoneyClearancePendingResolve(sctl,fixPendingRequest.getCSRAction());
			}
		}
		returnFix.setServiceChargeTransactionLogID(sctlID);
		return returnFix;
	}
	
	public TellerBankService getTellerBankService() {
		return tellerBankService;
	}

	public void setTellerBankService(TellerBankService tellerBankService) {
		this.tellerBankService = tellerBankService;
	}
	
	private void handleTellerEMoneyClearancePendingResolve(ServiceChargeTransactionLog tellerClearanceSctl, Integer csrAction){
		log.debug("TellerPendingClearanceServiceImpl :: handleTellerEMoneyClearancePendingResolve BEGIN sctlId="+tellerClearanceSctl.getID()+", csrAction="+csrAction);
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
		TransactionType cashInTxnType = transactionTypeDao.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
//		TransactionType cashOutTxnType = transactionTypeDao.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
//		TransactionType cashOutUnRegTxnType = transactionTypeDao.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
		
		List<ServiceChargeTransactionLog> childSctlList = sctlDao.getByParentSctlId(tellerClearanceSctl.getID());
		
		log.debug("TellerPendingClearanceServiceImpl :: handleTellerEmoneyClearancePendingResolve");
		
		if((childSctlList != null) && (childSctlList.size() > 0)){
			if(CmFinoFIX.CSRAction_Complete.equals(csrAction)){
				transactionChargingService.confirmTheTransaction(tellerClearanceSctl);
				for(ServiceChargeTransactionLog sctl: childSctlList){
					if(cashInTxnType.getID().equals(sctl.getTransactionTypeID())){
						transactionChargingService.failTheTransaction(sctl, "TellerPendingClearance-FAIL");
					}
					else{
						transactionChargingService.confirmTheTransaction(sctl);
					}
				}
			}
			else{
				transactionChargingService.failTheTransaction(tellerClearanceSctl, "TellerPendingClearance-Failed");
				for(ServiceChargeTransactionLog sctl: childSctlList){
					transactionChargingService.changeStatusToPendingResolved(sctl);
				}
			}
		}
		log.debug("TellerPendingClearanceServiceImpl :: handleTellerEMoneyClearancePendingResolve END");
	}
}
