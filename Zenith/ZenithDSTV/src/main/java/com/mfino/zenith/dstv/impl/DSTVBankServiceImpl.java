package com.mfino.zenith.dstv.impl;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferReversalToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMDSTVPayment;
import com.mfino.fix.CmFinoFIX.CMDSTVPaymentInquiry;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMDSTVTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.impl.BankServiceDefaultImpl;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.zenith.dstv.DSTVBackendResponse;
import com.mfino.zenith.dstv.DSTVBankService;

/**
 * @author Sasi
 *
 */
public class DSTVBankServiceImpl extends BankServiceDefaultImpl implements DSTVBankService {
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onDSTVTransferInquiryToBank(CMDSTVPaymentInquiry requestFix){
		log.info("DSTVBankServiceImpl :: onDSTVTransferInquiryToBank BEGIN");
		CFIXMsg  returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryToBank(requestFix);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(returnFix instanceof BackendResponse){
			//do nothing, this condition will occur only if there is an error.
			//As the request has to go to Bank since the transaction is either Emoney to Bank or Bank to Bank
		}
		else{
			CMDSTVTransferInquiryToBank toBank = new CMDSTVTransferInquiryToBank();
			toBank.copy((CMTransferInquiryToBank)returnFix);
			toBank.setBillerCode(requestFix.getBillerCode());
			toBank.setInvoiceNumber(requestFix.getInvoiceNumber());
			returnFix = toBank;
		}
		
		log.info("DSTVBankServiceImpl :: onDSTVTransferInquiryToBank END");
		return returnFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onDSTVTransferInquiryFromBank(CMDSTVTransferInquiryToBank toBank, CMDSTVTransferInquiryFromBank fromBank){
		log.info("DSTVBankServiceImpl :: onDSTVTransferInquiryFromBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferInquiryFromBank(toBank, fromBank);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		BackendResponse backendResponse = new BackendResponse();
		
		if(returnFix instanceof BackendResponse){
			backendResponse.copy((BackendResponse)returnFix);
			
			BigDecimal totalAmount = new BigDecimal(0);
			BigDecimal amount = ((BackendResponse)returnFix).getAmount();
			BigDecimal charges = ((BackendResponse)returnFix).getCharges();
			if(amount != null){
				totalAmount = totalAmount.add(amount);
			}
			if(charges != null){
				totalAmount = totalAmount.add(charges);
			}
			
			backendResponse.setAmount(totalAmount);
			backendResponse.setCharges(new BigDecimal(0));
			
			backendResponse.setSenderMDN(((BackendResponse) returnFix).getSenderMDN());
			backendResponse.setSourceMDN(((BackendResponse) returnFix).getSenderMDN());
			backendResponse.setReceiverMDN(((BackendResponse) returnFix).getReceiverMDN());
		}
		
		log.info("DSTVBankServiceImpl :: onDSTVTransferInquiryFromBank END");
		return backendResponse;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onDSTVTransferConfirmationToBank(CMDSTVPayment requestFix){
		log.info("DSTVBankServiceImpl :: onDSTVTransferConfirmagtionToBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try{
		returnFix = onTransferConfirmationToBank(requestFix);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if(returnFix instanceof BackendResponse){
			//do nothing, this condition will occur only if there is an error.
			//As the request has to go to Bank since the transaction is either Emoney to Bank or Bank to Bank
		}
		else{
			CMDSTVMoneyTransferToBank toBank = new CMDSTVMoneyTransferToBank();
			toBank.copy((CMMoneyTransferToBank)returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			toBank.setBillerCode(requestFix.getBillerCode());
			toBank.setInvoiceNumber(requestFix.getInvoiceNumber());
			returnFix = toBank;
		}
		log.info("DSTVBankServiceImpl :: onDSTVTransferConfirmagtionToBank END");
		return returnFix;
	}
	
	public CFIXMsg onDSTVTransferConfirmationFromBank(CMDSTVMoneyTransferToBank toBank, CMDSTVMoneyTransferFromBank fromBank){
		log.info("DSTVBankServiceImpl :: onDSTVTransferConfirmationFromBank BEGIN");
		
		log.debug("DSTVBankServiceImpl :: onDSTVTransferConfirmationFromBank BEGIN fromBank.getResponseCode()="+fromBank.getResponseCode());
		BackendResponse returnFix=createResponseObject();
		try{
		returnFix = (BackendResponse)onTransferConfirmationFromBank(toBank, fromBank);
		}catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(toBank);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);	
		}
		returnFix.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());
		
		if(!(fromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success))){
			BackendResponse backendResponse = new BackendResponse();
			backendResponse.copy(returnFix);
			returnFix = backendResponse;
		}
		
		if(returnFix instanceof BackendResponse){
			BigDecimal totalAmount = new BigDecimal(0);
			BigDecimal amount = ((BackendResponse)returnFix).getAmount();
			BigDecimal charges = ((BackendResponse)returnFix).getCharges();
			if(amount != null){
				totalAmount = totalAmount.add(amount);
			}
			if(charges != null){
				totalAmount = totalAmount.add(charges);
			}
			
			((BackendResponse)returnFix).setAmount(totalAmount);
			((BackendResponse)returnFix).setCharges(new BigDecimal(0));
		}
		
		log.info("DSTVBankServiceImpl :: onDSTVTransferConfirmationFromBank END");
		return returnFix;
	}

	/* (non-Javadoc)
	 * @see com.mfino.mce.backend.impl.BankServiceDefaultImpl#handlePCTonSuccess(com.mfino.domain.PendingCommodityTransfer)
	 */
	@Override
	protected void handlePCTonSuccess(PendingCommodityTransfer pct) {
		//Do not move the PCT to CT as still there are additional steps in the transaction.
		//Will move PCT to CT only after successful response from webservice
	}
	
	@Override
	
	public BackendResponse createResponseObject()
	{
		return new DSTVBackendResponse();
	}
 
	@Override
	public CFIXMsg onDSTVTransferReversalToBank(
			CMBankAccountToBankAccountConfirmation requestFix,
			CMDSTVMoneyTransferReversalToBank responseFix) 
	{
	    log.info("DSTVBankServiceImpl :: onDSTVTransferReversalToBank BEGIN");
		
		DSTVBackendResponse returnFix = (DSTVBackendResponse)onTransferReversalToBank	(requestFix, responseFix);
		
		log.info("DSTVBankServiceImpl :: onDSTVTransferReversalToBank END");
		return returnFix;
	}
		

	@Override
	public CFIXMsg onDSTVTransferReversalFromBank(
			CMDSTVMoneyTransferToBank requestFix,
			CMDSTVMoneyTransferReversalFromBank responseFix) {
		log.info("DSTVBankServiceImpl :: onDSTVTransferReversalFromBank BEGIN");
		
		DSTVBackendResponse returnFix = (DSTVBackendResponse)onDSTVTransferReversalFromBank(requestFix, responseFix);
		
		log.info("DSTVBankServiceImpl :: onDSTVTransferReversalFromBank END");
		return returnFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onResolveCompleteOfTransfer(PendingCommodityTransfer pendingTransfer){
		log.info("DSTVBankServiceImpl :: onResolveCompleteOfTransfer BEGIN");
		BackendResponse backendResponse = (BackendResponse)super.onResolveCompleteOfTransfer(pendingTransfer);
		
		log.info("DSTVBankServiceImpl :: onResolveCompleteOfTransfer backendResponse instanceof DSTVBackendResponse="+(backendResponse instanceof DSTVBackendResponse));
		log.info("DSTVBankServiceImpl :: onResolveCompleteOfTransfer END");
		return backendResponse;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onRevertOfTransferConfirmation(PendingCommodityTransfer pendingTransfer, boolean updatePCT) {
		log.info("DSTVBankServiceImpl :: onRevertOfTransferConfirmation BEGIN");
		BackendResponse backendResponse = (BackendResponse)super.onRevertOfTransferConfirmation(pendingTransfer, updatePCT);
		
		/*
		 * on resolve, this should go to Notification, so creating instance of BackendResponse.
		 */
		BackendResponse newResponse = new BackendResponse();
		newResponse.copy(backendResponse);
		
		newResponse.setResult(CmFinoFIX.ResponseCode_Failure);
		
		log.info("DSTVBankServiceImpl :: onResolveCompleteOfTransfer backendResponse instanceof DSTVBackendResponse="+(backendResponse instanceof DSTVBackendResponse));
		log.info("DSTVBankServiceImpl :: onResolveCompleteOfTransfer END");
		return newResponse;
	}
	
	//FIXME: Duplicating code from DSTVServiceImpl, need to refactor.
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onResolveOfIntegrationServiceStatus(PendingCommodityTransfer pct){
		log.debug("DSTVBankServiceImpl :: onResolveOfIntegrationServiceStatus BEGIN");
		/*
		 * Though Web Service did not respond/rejected request from mFino platform, we now assume, things are settled outside.
		 * Do the stuff that needs to be done when webservice returns success response and send notification.
		 */
		BackendResponse backendResponse = new BackendResponse();
		
		pct.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
		pct.setTransferFailureReason(CmFinoFIX.TransferFailureReason_CompletedSuccessfuly);
		pct.setNotificationCode(NotificationCodes.BillPayCompletedToSender.getNotificationCode());
		pct.setEndTime(new Timestamp());
		getCommodityTransferService().movePctToCt(pct);
		
		((BackendResponse)backendResponse).setInternalErrorCode(NotificationCodes.BillPayCompletedToSender.getInternalErrorCode());
		((BackendResponse)backendResponse).setTransferID(pct.getID());
//		((BackendResponse)backendResponse).setBillerCode(billPayments.getBillerCode());
//		((BackendResponse)backendResponse).setInvoiceNumber(billPayments.getInvoiceNumber());
		((BackendResponse)backendResponse).setAmount(pct.getAmount());
		((BackendResponse)backendResponse).setCharges(pct.getCharges());
		((BackendResponse)backendResponse).setCurrency(pct.getCurrency());
		
		backendResponse.setResult(CmFinoFIX.ResponseCode_Success);
		log.debug("DSTVBankServiceImpl :: onResolveOfIntegrationServiceStatus END");
		return backendResponse;
	}
}
