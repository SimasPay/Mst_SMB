package com.mfino.zenith.airtime.visafone.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AirtimePurchaseDAO;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.AirtimePurchaseQuery;
import com.mfino.domain.AirtimePurchase;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransactionReversal;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchase;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimePurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMVisafoneAirtimeTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.BankServiceDefaultImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.zenith.airtime.visafone.VisafoneAirtimeBankService;

/**
 * @author Sasi
 *
 */
public class VisafoneAirtimeBankServiceImpl extends BankServiceDefaultImpl implements VisafoneAirtimeBankService {

	@Override
	public CFIXMsg onVisafoneAirtimeTransferInquiryToBank(CMVisafoneAirtimePurchaseInquiry requestFix) {
		log.info("VisafoneAirtimeBankServiceImpl :: VisafoneAirtimeBankServiceImpl BEGIN requestFix="+requestFix);
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(requestFix);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		log.debug("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferInquiryToBank returnFix.getclass="+returnFix.getClass());
		
		if(returnFix instanceof BackendResponse){
			if(((BackendResponse) returnFix).getInternalErrorCode().equals(NotificationCodes.BankAccountToBankAccountConfirmationPrompt.getInternalErrorCode())){
				BackendResponse bResponse = (BackendResponse)returnFix;
				/*Set inquiry success notification for airtime purchase.*/
				((BackendResponse) returnFix).setReceiverMDN(requestFix.getRechargeMDN());
				((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.AirtimePurchaseInquiry.getInternalErrorCode());
				
				BigDecimal amount = bResponse.getAmount().add(bResponse.getCharges());
				bResponse.setAmount(amount);
				bResponse.setCharges(BigDecimal.valueOf(0));
			}
		}
		/*
		 * For E-Money to E-Money do nothing, as inquiry is complete return backend response, for bank transfers, return ToBank FIX message.
		 */
		if(returnFix instanceof CMTransferInquiryToBank){
			CMVisafoneAirtimeTransferInquiryToBank toBank = new CMVisafoneAirtimeTransferInquiryToBank();
			toBank.copy((CMTransferInquiryToBank)returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			returnFix = toBank;
		}
		
		log.info("VisafoneAirtimeBankServiceImpl :: onTransferInquiryToBank END");
		return returnFix;
	}

	@Override
	public CFIXMsg onVisafoneAirtimeTransferInquiryFromBank(CMVisafoneAirtimeTransferInquiryToBank toBank,CMVisafoneAirtimeTransferInquiryFromBank fromBank) {
		log.info("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferInquiryFromBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryFromBank(toBank, fromBank);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		log.info("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferInquiryFromBank END");
		return returnFix;
	}
	
	@Override
	public CFIXMsg onVisafoneAirtimeTransferConfirmationToBank(CMVisafoneAirtimePurchase requestFix) {
		log.debug("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferConfirmationToBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(requestFix);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(returnFix instanceof BackendResponse){
			log.debug("VisafoneAirtimeBankServiceImpl :: returnFix instanceof BackendResponse DEBUG1="+((BackendResponse)returnFix).getTransferID());
			/*For E-Money to E-Money, this block will be hit*/
			
			if(((BackendResponse)returnFix).getResult() != CmFinoFIX.ResponseCode_Success){
				//do nothing as it should go to notification now.
			}
			else{
				VisafoneAirtimeBackendResponse vBackendResponse = new VisafoneAirtimeBackendResponse();
				vBackendResponse.copy((BackendResponse)returnFix);
				returnFix = vBackendResponse;
			}
		}
		else{
			log.debug("VisafoneAirtimeBankServiceImpl :: else block");
			/*If bank is involved, else block is invoked*/
			CMVisafoneAirtimeMoneyTransferToBank toBank = new CMVisafoneAirtimeMoneyTransferToBank();
			toBank.copy((CMMoneyTransferToBank)returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			returnFix = toBank;
		}
		
		log.debug("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferConfirmationToBank END returnFix="+returnFix);
		return returnFix;
	}

	@Override
	public CFIXMsg onVisafoneAirtimeTransferConfirmationFromBank(CMVisafoneAirtimeMoneyTransferToBank toBank,CMVisafoneAirtimeMoneyTransferFromBank fromBank) {
		log.debug("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferConfirmationFromBank BEGIN");
		VisafoneAirtimeBackendResponse vBackendResponse=(VisafoneAirtimeBackendResponse) createResponseObject();
		try{
		vBackendResponse = (VisafoneAirtimeBackendResponse)onTransferConfirmationFromBank(toBank, fromBank);
		}catch(Exception e){
			log.error(e.getMessage());

			((BackendResponse) vBackendResponse).copy(toBank);
			((BackendResponse) vBackendResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) vBackendResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		vBackendResponse.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());
		
		log.debug("VisafoneAirtimeBankServiceImpl :: onVisafoneAirtimeTransferConfirmationFromBank END");
		return vBackendResponse;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onResolveOfIntegrationService(PendingCommodityTransfer pct){
		log.debug("VisafoneAirtimeBankServiceImpl :: onResolveOfIntegrationServiceStatus BEGIN");

		BackendResponse backendResponse = new BackendResponse();
		
		pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
		pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
		pct.setNotificationCode(NotificationCodes.BillPayCompletedToSender.getNotificationCode());
		pct.setEndTime(new Timestamp());
		getCommodityTransferService().movePctToCt(pct);
		
		backendResponse.setInternalErrorCode(NotificationCodes.AirtimePurchaseConfirmation.getInternalErrorCode());
		backendResponse.setTransferID(pct.getID());
		backendResponse.setAmount(pct.getAmount().add(pct.getCharges()));
		backendResponse.setCharges(BigDecimal.valueOf(0));
		backendResponse.setCurrency(pct.getCurrency());
		backendResponse.setSourceMDN(pct.getSourceMDN());
		backendResponse.setReceiverMDN(pct.getDestMDN());
		backendResponse.setRechargeMdn(pct.getDestMDN());
		backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
//		backendResponse.setServiceChargeTransactionLogID(sctlID);
		log.debug("VisafoneAirtimeBankServiceImpl :: onResolveOfIntegrationServiceStatus END");
		return backendResponse;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onRevertOfIntegrationService(PendingCommodityTransfer pct){
		log.debug("VisafoneAirtimeBankServiceImpl :: onRevertOfIntegrationService BEGIN");
		
		ChannelCodeDAO ccDao = DAOFactory.getInstance().getChannelCodeDao();
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTransactionLog sctl = sctlDao.getByTransactionLogId(pct.getTransactionsLogByTransactionID().getID());
		AirtimePurchase airtimePurchase = getAirtimePurchase(sctl.getID());
		
		CMTransactionReversal transactionReversal = new CMTransactionReversal();
		
		transactionReversal.setSourcePocketID(pct.getDestPocketID());
		transactionReversal.setDestPocketID(pct.getPocketBySourcePocketID().getID());
		transactionReversal.setTransferID(pct.getID());
		transactionReversal.setSourceMDN(pct.getDestMDN());
		transactionReversal.setDestMDN(pct.getSourceMDN());
		transactionReversal.setServiceChargeTransactionLogID(sctl.getID());
		transactionReversal.setParentTransactionID(pct.getTransactionsLogByTransactionID().getID());
		transactionReversal.setUICategory(CmFinoFIX.TransactionUICategory_TransactionReversal);
		
		BackendResponse backendResponse = (BackendResponse)onTransactionReversal(transactionReversal);
		
		if(CmFinoFIX.ResponseCode_Success.equals(backendResponse.getResult())){
			airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_REVERTED);
			((BackendResponse)backendResponse).setInternalErrorCode(NotificationCodes.AirtimePurchaseReverted.getInternalErrorCode());
		}
		else{
			airtimePurchase.setAirtimePurchaseStatus(CmFinoFIX.AirtimePurchaseStatus_REVERT_FAILED);
			((BackendResponse)backendResponse).setInternalErrorCode(NotificationCodes.AirtimePurchaseRevertFailed.getInternalErrorCode());
		}
		
		AirtimePurchaseDAO airtimePurchaseDAO = DAOFactory.getInstance().getAirtimePurchaseDao();
		airtimePurchaseDAO.save(airtimePurchase);
		
		((BackendResponse)backendResponse).setTransferID(sctl.getID());
		((BackendResponse)backendResponse).setAmount(pct.getAmount());
		((BackendResponse)backendResponse).setCharges(pct.getCharges());
		((BackendResponse)backendResponse).setCurrency(pct.getCurrency());
		
		log.debug("VisafoneAirtimeBankServiceImpl :: onRevertOfIntegrationService END");
		return backendResponse;
	}
	
	@Override
	public CFIXMsg onVisafoneAirtimeTransferReversalToBank(CMVisafoneAirtimePurchase requestFix,CMVisafoneAirtimeMoneyTransferReversalToBank responseFix) {
		return null;
	}

	@Override
	public CFIXMsg onVisafoneAirtimeTransferReversalFromBank(CMVisafoneAirtimeMoneyTransferReversalToBank requestFix,CMVisafoneAirtimeMoneyTransferReversalFromBank responseFix) {
		return null;
	}

	@Override
	protected void handlePCTonSuccess(PendingCommodityTransfer pct) {
		//Do not move the PCT to CT as still there are additional steps in the transaction.
		//Will move PCT to CT only after successful response from webservice.
	}
	
	@Override
	public BackendResponse createResponseObject()
	{
		return new VisafoneAirtimeBackendResponse();
	}
	
	protected AirtimePurchase getAirtimePurchase(Long sctlID)
	{
		AirtimePurchaseDAO airtimePurchaseDao = DAOFactory.getInstance().getAirtimePurchaseDao();
		AirtimePurchaseQuery query = new AirtimePurchaseQuery();
		query.setSctlID(sctlID);
		List<AirtimePurchase> airtimePurchaseList = airtimePurchaseDao.get(query);
		
		if(airtimePurchaseList!=null && !airtimePurchaseList.isEmpty())
		{
			//Only there should be one record for a given sctld
			return airtimePurchaseList.get(0);
		}
		
		return null;
	}
}
