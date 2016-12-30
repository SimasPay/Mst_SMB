package com.mfino.mce.backend.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.AutoReversalsDao;
import com.mfino.dao.BillPaymentsDAO;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.ServiceChargeTransactionLogDAO;
import com.mfino.dao.query.BillPaymentsQuery;
import com.mfino.domain.AutoReversals;
import com.mfino.domain.BillPayments;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAutoReversal;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.backend.AutoReversalService;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.exception.DuplicateAutoReversalException;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.TransactionLogService;
import com.mfino.service.impl.SystemParametersServiceImpl;

/**
 * @author Sasi
 *
 */
public class AutoReversalServiceImpl extends BaseServiceImpl implements AutoReversalService{
	
	private BankService bankService;

	private TransactionLogService transactionLogService;
	
	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage doReversal(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: doReversal BEGIN mceMessage = "+mceMessage);
		MCEMessage returnMessage = null;
		
		CMAutoReversal autoReversalRequest = (CMAutoReversal)mceMessage.getRequest();
		
		log.info("AutoReversalRequest sctlId="+autoReversalRequest.getServiceChargeTransactionLogID()+", amount="+autoReversalRequest.getAmount()+", charges="+autoReversalRequest.getCharges());
		
		if(!isValidReversal(autoReversalRequest)){
			log.info("doReversal::Invalid reversal request as BillPay status is not "+CmFinoFIX.BillPayStatus_BILLPAY_FAILED);
			throw new DuplicateAutoReversalException("Invalid reversal request for sctlID:"+autoReversalRequest.getServiceChargeTransactionLogID());
		}
		
		Long sctlId = autoReversalRequest.getServiceChargeTransactionLogID();
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		
		if(autoReversal == null){
			autoReversal = initializeAutoReversal((CMAutoReversal)mceMessage.getRequest());
		}else if((CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED.intValue() != autoReversal.getAutorevstatus())){
			log.info("doReversal::Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID());
		}		
		if(BigDecimal.ZERO.compareTo(autoReversal.getCharges()) == -1){
			returnMessage = chargesToTransitInquiry(mceMessage); //charges to transit inquiry	
			
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult())){
				returnMessage = chargesToTransitConfirmation(mceMessage); //charges to transit confirmation
			}
		}
		
		if((null == returnMessage) || (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult()))){
			
			if(returnMessage == null){
				returnMessage = mceMessage;
			}
			
			returnMessage = destinationToTransitInquiry(returnMessage); //destination to transit inquiry
			
			if((returnMessage.getResponse() instanceof BackendResponse) && (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult()))){
				returnMessage = destinationToTransitConfirmation(returnMessage); //destination to transit confirmation.
				
				if((returnMessage.getResponse() instanceof BackendResponse) && (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult()))){
					returnMessage = transitToSourceInquiry(returnMessage); //transit to source inquiry
					
					if((returnMessage.getResponse() instanceof BackendResponse) && (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult()))){
						returnMessage = transitToSourceConfirmation(returnMessage); //transit to source confirmation
						
						if((returnMessage.getResponse() instanceof BackendResponse) && (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)returnMessage.getResponse()).getResult()))){
							setReversalSuccessNotification(returnMessage);
						}
					}
				}
			}
		}
		
		log.info("AutoReversalServiceImpl :: doReversal END mceMessage = "+mceMessage);
		return returnMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage chargesToTransitInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: chargesToTransitInquiry mceMessage="+mceMessage);
		
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_PENDING;
		
		CMAutoReversal autoReversalRequest = (CMAutoReversal)mceMessage.getRequest();
		Long sctlId = autoReversalRequest.getServiceChargeTransactionLogID();
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		if ((CmFinoFIX.AutoRevStatus_INITIALIZED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED.intValue() != autoReversal.getAutorevstatus())){
			log.info("chargesToTransitInquiry::Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID());
		}
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		CMBankAccountToBankAccount moneyTransferInquiry = getMoneyTransferInquiryFix(sctl, getChargesPocket(autoReversalRequest), getTransitPocket(), autoReversal.getCharges());
		
		TransactionLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AutoReversal, moneyTransferInquiry.DumpFields());
		
		moneyTransferInquiry.setTransactionID(tLog.getId().longValue());
		CFIXMsg retFix=createResponseObject();
		try {
		retFix = bankService.onTransferInquiryToBank(moneyTransferInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_COMPLETED;
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_FAILED;
			}
		}
		
		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage chargesToTransitConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: chargesToTransitConfirmation mceMessage="+mceMessage);
		
		CMAutoReversal autoReversalRequest = (CMAutoReversal)mceMessage.getRequest();
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_PENDING;
				 
		BackendResponse inquiryResponse = (BackendResponse)mceMessage.getResponse();
		Long sctlId = inquiryResponse.getServiceChargeTransactionLogID();
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		if(CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_INQ_COMPLETED.intValue() != autoReversal.getAutorevstatus()){
			log.info("chargesToTransitConfirmation::Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException(
					"Duplicate reversal request for sctlID:"+ autoReversalRequest.getServiceChargeTransactionLogID());
		}		
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		CMBankAccountToBankAccountConfirmation moneyTransferConfirmation = getMoneyTransferConfirmationFix(sctl, getChargesPocket(autoReversalRequest), getTransitPocket(), inquiryResponse);
		CFIXMsg retFix=createResponseObject();
		try {
		retFix = bankService.onTransferConfirmationToBank(moneyTransferConfirmation);
		} catch(Exception e){
			log.error(e.getMessage());
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_COMPLETED;
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_FAILED;
			}
		}
		
		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage destinationToTransitInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: destinationToTransitInquiry mceMessage="+mceMessage);
		
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_PENDING;
		
		CMBase cmBase = (CMBase)mceMessage.getRequest();
		Long sctlId = cmBase.getServiceChargeTransactionLogID();
		
		AutoReversals autoReversal = getAutoReversal(sctlId);
		if(BigDecimal.ZERO.compareTo(autoReversal.getCharges()) == -1){
			if (CmFinoFIX.AutoRevStatus_CHARGES_TRANSIT_COMPLETED.intValue() != autoReversal.getAutorevstatus()&&
					(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED.intValue() != autoReversal.getAutorevstatus())&&
					(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED.intValue() != autoReversal.getAutorevstatus())) {
				log.info("destinationToTransitInquiry::Duplicate reversal request for sctlID:"+ autoReversal.getSctlid()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
				throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversal.getSctlid());
			}
		}else if((CmFinoFIX.AutoRevStatus_INITIALIZED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED.intValue() != autoReversal.getAutorevstatus())){
			log.info("destinationToTransitInquiry::Duplicate reversal request for sctlID:"+ autoReversal.getSctlid()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversal.getSctlid());
		}		
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		CMBankAccountToBankAccount moneyTransferInquiry = getMoneyTransferInquiryFix(sctl, getDestPocket(sctlId), getTransitPocket(), autoReversal.getAmount());
		
		TransactionLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AutoReversal, moneyTransferInquiry.DumpFields());
		
		moneyTransferInquiry.setTransactionID(tLog.getId().longValue());
		CFIXMsg retFix = createResponseObject();
		try {
		retFix  = bankService.onTransferInquiryToBank(moneyTransferInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_COMPLETED;
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED;
			}
		}
		else{
			mceMessage.setDestinationQueue("jms:destinationToTransitBRQueue?disableReplyTo=false");
		}
		
		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage destinationToTransitConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: destinationToTransitConfirmation mceMessage="+mceMessage);
		
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_PENDING;
		BackendResponse inquiryResponse = (BackendResponse)mceMessage.getResponse();
		Long sctlId = inquiryResponse.getServiceChargeTransactionLogID();
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		if(CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_COMPLETED.intValue() != autoReversal.getAutorevstatus()){
			log.info("destinationToTransitConfirmation::Duplicate reversal request for sctlID:"+ autoReversal.getSctlid()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversal.getSctlid());
		}	
				
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		CMBankAccountToBankAccountConfirmation moneyTransferConfirmation = getMoneyTransferConfirmationFix(sctl, getDestPocket(sctlId), getTransitPocket(), inquiryResponse);
		CFIXMsg retFix=createResponseObject();
		try {
		retFix = bankService.onTransferConfirmationToBank(moneyTransferConfirmation);
		} catch(Exception e){
			log.error(e.getMessage());
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_COMPLETED;
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED;
			}
		}
		else{
			mceMessage.setDestinationQueue("jms:destinationToTransitBRQueue?disableReplyTo=false");
		}

		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage transitToSourceInquiry(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: transitToSourceInquiry mceMessage="+mceMessage);
		
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_PENDING;
		
		CMBase cmBase = (CMBase)mceMessage.getRequest();
		Long sctlId = cmBase.getServiceChargeTransactionLogID();
		
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		if((CmFinoFIX.AutoRevStatus_DEST_TRANSIT_COMPLETED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED.intValue() != autoReversal.getAutorevstatus())&&
				(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED.intValue() != autoReversal.getAutorevstatus())){
			log.info("transitToSourceInquiry::Duplicate reversal request for sctlID:"+ autoReversal.getSctlid()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException(
					"Duplicate reversal request for sctlID:"+ autoReversal.getSctlid());
		}	
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		BigDecimal amount = autoReversal.getAmount().add(autoReversal.getCharges());
		
		CMBankAccountToBankAccount moneyTransferInquiry = getMoneyTransferInquiryFix(sctl, getTransitPocket(), getSourcePocket(sctlId), amount);
		
		CFIXMsg retFix =createResponseObject();
		try {		
		retFix = bankService.onTransferInquiryToBank(moneyTransferInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_COMPLETED;
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED;
			}
		}
		else{
			mceMessage.setDestinationQueue("jms:transitToSourceBRQueue?disableReplyTo=false");
		}

		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage transitToSourceConfirmation(MCEMessage mceMessage) throws DuplicateAutoReversalException{
		log.info("AutoReversalServiceImpl :: transitToSourceConfirmation mceMessage="+mceMessage);
		Integer autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_PENDING;
		BackendResponse inquiryResponse = (BackendResponse)mceMessage.getResponse();
		Long sctlId = inquiryResponse.getServiceChargeTransactionLogID();
		AutoReversals autoReversal = getAutoReversalInTxn(sctlId);
		if(CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_COMPLETED.intValue() != autoReversal.getAutorevstatus()){
			log.info("transitToSourceConfirmation::Duplicate reversal request for sctlID:"+ autoReversal.getSctlid()+" AutoreversalStatus:"+ autoReversal.getAutorevstatus());
			throw new DuplicateAutoReversalException("Duplicate reversal request for sctlID:"+ autoReversal.getSctlid());
		}	
		
		ServiceChargeTransactionLogDAO sctlDao = DAOFactory.getInstance().getServiceChargeTransactionLogDAO();
		ServiceChargeTxnLog sctl = sctlDao.getById(sctlId);
		
		CMBankAccountToBankAccountConfirmation moneyTransferConfirmation = getMoneyTransferConfirmationFix(sctl, getTransitPocket(), getSourcePocket(sctlId), inquiryResponse);
		CFIXMsg retFix=createResponseObject();
		try {
			retFix = bankService.onTransferConfirmationToBank(moneyTransferConfirmation);
		} catch(Exception e){
			if(retFix instanceof BackendResponse){
			((BackendResponse) retFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) retFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		((CMBase)retFix).setServiceChargeTransactionLogID(sctlId);
		
		if(retFix instanceof BackendResponse){
			if(CmFinoFIX.ResponseCode_Success.equals(((BackendResponse)retFix).getResult())){
				autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED;
				setReversalSuccessNotification(mceMessage);
			}
			else{
				autoRevStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED;
			}
		}
		else{
			mceMessage.setDestinationQueue("jms:transitToSourceBRQueue?disableReplyTo=false");
		}
		
		updateAutoReversalStatus(sctlId, autoRevStatus);
		
		mceMessage.setResponse(retFix);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage destinationToTransitInquiryFromBank(MCEMessage mceMessage){
		log.info("AutoReversalServiceImpl : destinationToTrasnferInquiryFromBank");
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)mceMessage.getRequest();
		CMTransferInquiryFromBank fromBank = (CMTransferInquiryFromBank)mceMessage.getResponse();
		
		Long sctlId = toBank.getServiceChargeTransactionLogID();
		BackendResponse inquiryResponse=createResponseObject();
		try {
		inquiryResponse = (BackendResponse)bankService.onTransferInquiryFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			if(inquiryResponse instanceof BackendResponse){
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		Integer autoReversalStatus = null;
		if(CmFinoFIX.ResponseCode_Success.equals(inquiryResponse.getResult())){
			autoReversalStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_COMPLETED;
		}
		else{
			autoReversalStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_INQ_FAILED;
		}
		
		updateAutoReversalStatus(sctlId, autoReversalStatus);
		mceMessage.setResponse(inquiryResponse);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage destinationToTransitConfirmationFromBank(MCEMessage mceMessage){
		log.info("AutoReversalServiceImpl : destinationToTransitConfirmationFromBank");
		
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank)mceMessage.getRequest();
		CMMoneyTransferFromBank fromBank = (CMMoneyTransferFromBank)mceMessage.getResponse();
		
		Long sctlId = toBank.getServiceChargeTransactionLogID();
		BackendResponse confirmationResponse=createResponseObject();
		try {
		confirmationResponse = (BackendResponse)bankService.onTransferConfirmationFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			if(confirmationResponse instanceof BackendResponse){
			((BackendResponse) confirmationResponse).copy(toBank);
			((BackendResponse) confirmationResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) confirmationResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		Integer autoReversalStatus = null;
		if(CmFinoFIX.ResponseCode_Success.equals(confirmationResponse.getResult())){
			autoReversalStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_COMPLETED;
		}
		else{
			autoReversalStatus = CmFinoFIX.AutoRevStatus_DEST_TRANSIT_FAILED;
		}
		
		updateAutoReversalStatus(sctlId, autoReversalStatus);
		mceMessage.setResponse(confirmationResponse);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage transitToSourceInquiryFromBank(MCEMessage mceMessage){
		log.info("AutoReversalServiceImpl : transitToSourceInquiryFromBank");
		
		CMTransferInquiryToBank toBank = (CMTransferInquiryToBank)mceMessage.getRequest();
		CMTransferInquiryFromBank fromBank = (CMTransferInquiryFromBank)mceMessage.getResponse();
		
		Long sctlId = toBank.getServiceChargeTransactionLogID();
		BackendResponse inquiryResponse=createResponseObject();
		try {
		inquiryResponse = (BackendResponse)bankService.onTransferInquiryFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			if(inquiryResponse instanceof BackendResponse){
			((BackendResponse) inquiryResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) inquiryResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		mceMessage.setResponse(inquiryResponse);
		
		Integer autoReversalStatus = null;
		if(CmFinoFIX.ResponseCode_Success.equals(inquiryResponse.getResult())){
			autoReversalStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_COMPLETED;
		}
		else{
			autoReversalStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_INQ_FAILED;
		}
		
		updateAutoReversalStatus(sctlId, autoReversalStatus);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage transitToSourceConfirmationFromBank(MCEMessage mceMessage){
		log.info("AutoReversalServiceImpl : transitToSourceConfirmationFromBank");
		
		CMMoneyTransferToBank toBank = (CMMoneyTransferToBank)mceMessage.getRequest();
		CMMoneyTransferFromBank fromBank = (CMMoneyTransferFromBank)mceMessage.getResponse();
		
		Long sctlId = toBank.getServiceChargeTransactionLogID();
		
		BackendResponse confirmationResponse=createResponseObject();
		try {
		confirmationResponse = (BackendResponse)bankService.onTransferConfirmationFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			if(confirmationResponse instanceof BackendResponse){
			((BackendResponse) confirmationResponse).copy(toBank);
			((BackendResponse) confirmationResponse).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) confirmationResponse).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
		}
		mceMessage.setResponse(confirmationResponse);
		
		Integer autoReversalStatus = null;
		if(CmFinoFIX.ResponseCode_Success.equals(confirmationResponse.getResult())){
			autoReversalStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_COMPLETED;
			setReversalSuccessNotification(mceMessage);
		}
		else{
			autoReversalStatus = CmFinoFIX.AutoRevStatus_TRANSIT_SRC_FAILED;
		}
		
		updateAutoReversalStatus(sctlId, autoReversalStatus);
		mceMessage.setResponse(confirmationResponse);
		
		return mceMessage;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public AutoReversals initializeAutoReversal(CMAutoReversal autoReversalRequest){
		log.info("AutoReversalServiceImpl initializeAutoReversal BEGIN");
		AutoReversalsDao autoReversalsDao = DAOFactory.getInstance().getAutoReversalsDao();
		
		AutoReversals autoReversal = new AutoReversals();
		autoReversal.setSourcepocketid(autoReversalRequest.getSourcePocketID());
		autoReversal.setDestpocketid(autoReversalRequest.getDestPocketID());
		autoReversal.setSctlid(autoReversalRequest.getServiceChargeTransactionLogID());
		autoReversal.setAmount(autoReversalRequest.getAmount());
		autoReversal.setCharges(autoReversalRequest.getCharges());
		autoReversal.setAutorevstatus(CmFinoFIX.AutoRevStatus_INITIALIZED);
		
		autoReversalsDao.save(autoReversal);
		
		return autoReversal;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void updateAutoReversalStatus(Long sctlId, Integer status){
		AutoReversalsDao autoReversalsDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = autoReversalsDao.getBySctlId(sctlId);
		autoReversal.setAutorevstatus(status);
		autoReversalsDao.save(autoReversal);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public AutoReversals getAutoReversal(Long sctlId){
		AutoReversalsDao autoReversalsDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = autoReversalsDao.getBySctlId(sctlId);
		
		return autoReversal;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public AutoReversals getAutoReversalInTxn(Long sctlId){
		AutoReversalsDao autoReversalsDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = autoReversalsDao.getBySctlId(sctlId);
		
		return autoReversal;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getSourcePocket(Long sctlId){
		AutoReversalsDao autoRevDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = autoRevDao.getBySctlId(sctlId);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket sourcePocket = pocketDAO.getById(autoReversal.getSourcepocketid().longValue());
		return sourcePocket;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getDestPocket(Long sctlId){
		AutoReversalsDao autoRevDao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = autoRevDao.getBySctlId(sctlId);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket destPocket = pocketDAO.getById(autoReversal.getDestpocketid().longValue());
		return destPocket;
	}
	
	public Pocket getTransitPocket(){
		SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
		long reversePocketId = systemParametersServiceImpl.getLong(SystemParameterKeys.REVERSE_POCKET_ID);
		PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
		Pocket transitPocket = pocketDAO.getById(reversePocketId);
		return transitPocket;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public String getDefaultChannelCode(){
		ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao();
		ChannelCode cc = channelCodeDAO.getByChannelSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		return cc.getChannelcode();
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Pocket getChargesPocket(CMAutoReversal autoReversal){
		Long chargesPocketId = null;
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		if (autoReversal.getChargesPocketID() == null) {
			SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
			chargesPocketId = systemParametersServiceImpl.getLong(SystemParameterKeys.CHARGES_POCKET_ID_KEY);
		}
		else {
			chargesPocketId = autoReversal.getChargesPocketID();
		}
		Pocket chargesPocket = pocketDao.getById(chargesPocketId);
		return chargesPocket;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage getBankRequest(MCEMessage mceMessage){
		mceMessage.setRequest(mceMessage.getResponse());
		mceMessage.setResponse(null);
		return mceMessage;
	}
	
/*	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	protected TransactionsLog saveTransactionsLog(Integer messageCode, String data) {
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
        MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
        mFinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	protected TransactionsLog saveTransactionsLog(Integer messageCode, String data,Long parentTxnID)
	{
		TransactionsLogDAO transactionsLogDAO = DAOFactory.getInstance().getTransactionsLogDAO();
		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(messageCode);
		transactionsLog.setMessageData(data);
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		mFinoServiceProvider msp = mspDao.getById(1);
		transactionsLog.setmFinoServiceProviderByMSPID(msp);
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		if(parentTxnID!=null)
			transactionsLog.setParentTransactionID(parentTxnID);
		transactionsLogDAO.save(transactionsLog);
		return transactionsLog;
	}
*/	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Collection<AutoReversals> getAutoReversalsWithStatus(Collection<Integer> statuses){
		log.info("AutoReversalServiceImpl :: getAutoReversalsWithStatus BEGIN");
		AutoReversalsDao autoReversalsDao = DAOFactory.getInstance().getAutoReversalsDao();
		
		Collection<AutoReversals> autoReversals = autoReversalsDao.getAutoReversalsWithStatus(statuses);
		
		return autoReversals;
	}
	
	@Transactional(readOnly=true, propagation = Propagation.REQUIRED)
	public AutoReversals getAutoReversalBySctlId(Long sctlId){
		AutoReversalsDao dao = DAOFactory.getInstance().getAutoReversalsDao();
		AutoReversals autoReversal = dao.getBySctlId(sctlId);
		
		return autoReversal;
	}
	
	public CMBankAccountToBankAccount getMoneyTransferInquiryFix(ServiceChargeTxnLog sctl, Pocket sourcePocket, Pocket destPocket, BigDecimal amount){
		CMBankAccountToBankAccount moneyTransferInquiry = new CMBankAccountToBankAccount();
		
		moneyTransferInquiry.setSourcePocketID(sourcePocket.getId().longValue());
		moneyTransferInquiry.setDestPocketID(destPocket.getId().longValue());
		moneyTransferInquiry.setSourceMDN(sourcePocket.getSubscriberMdn().getMdn());
		moneyTransferInquiry.setDestMDN(destPocket.getSubscriberMdn().getMdn());
		moneyTransferInquiry.setAmount(amount);
		moneyTransferInquiry.setCharges(BigDecimal.ZERO);
		moneyTransferInquiry.setIsSystemIntiatedTransaction(Boolean.TRUE);
		moneyTransferInquiry.setMessageType(moneyTransferInquiry.header().getMsgType());
		moneyTransferInquiry.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		moneyTransferInquiry.setChannelCode(getDefaultChannelCode());
		moneyTransferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		moneyTransferInquiry.setServiceChargeTransactionLogID(sctl.getId().longValue());
		moneyTransferInquiry.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		moneyTransferInquiry.setParentTransactionID(0L);
		moneyTransferInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Auto_Reverse);
		moneyTransferInquiry.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AUTO_REVERSE);
		
		TransactionLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AutoReversal, moneyTransferInquiry.DumpFields());
		
		moneyTransferInquiry.setTransactionID(tLog.getId().longValue());
		
		return moneyTransferInquiry;
	}
	
	public CMBankAccountToBankAccountConfirmation getMoneyTransferConfirmationFix(ServiceChargeTxnLog sctl, Pocket sourcePocket, Pocket destPocket, BackendResponse inquiryResponse){
		CMBankAccountToBankAccountConfirmation moneyTransferConfirmation = new CMBankAccountToBankAccountConfirmation();

		moneyTransferConfirmation.setSourcePocketID(sourcePocket.getId().longValue());
		moneyTransferConfirmation.setDestPocketID(destPocket.getId().longValue());
		moneyTransferConfirmation.setSourceMDN(sourcePocket.getSubscriberMdn().getMdn());
		moneyTransferConfirmation.setDestMDN(destPocket.getSubscriberMdn().getMdn());
		moneyTransferConfirmation.setIsSystemIntiatedTransaction(Boolean.TRUE);
		moneyTransferConfirmation.setMessageType(moneyTransferConfirmation.header().getMsgType());
		moneyTransferConfirmation.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
		moneyTransferConfirmation.setChannelCode(getDefaultChannelCode());
		moneyTransferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		moneyTransferConfirmation.setServiceChargeTransactionLogID(sctl.getId().longValue());
		moneyTransferConfirmation.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
		moneyTransferConfirmation.setParentTransactionID(inquiryResponse.getParentTransactionID());
		moneyTransferConfirmation.setTransferID(inquiryResponse.getTransferID());
		
		TransactionLog tLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_AutoReversal, moneyTransferConfirmation.DumpFields(), inquiryResponse.getParentTransactionID());

		moneyTransferConfirmation.setTransactionID(tLog.getId().longValue());
		moneyTransferConfirmation.setConfirmed(Boolean.TRUE);

		return moneyTransferConfirmation;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	
	private void setReversalSuccessNotification(MCEMessage mceMessage){
		if((mceMessage != null)  && (mceMessage.getResponse() instanceof BackendResponse)){
			((BackendResponse)mceMessage.getResponse()).setInternalErrorCode(NotificationCodes.AutoReverseSuccessToSource.getInternalErrorCode());
		}
	}
	
	private boolean isValidReversal(CMAutoReversal reversal){
		BillPaymentsDAO billPaymentsDAO = DAOFactory.getInstance().getBillPaymentDAO();
		BillPaymentsQuery query = new BillPaymentsQuery();
		query.setSctlID(reversal.getServiceChargeTransactionLogID());
		List<BillPayments> billPayments = billPaymentsDAO.get(query);
		if(billPayments!=null
				&&!billPayments.isEmpty()
				&&!CmFinoFIX.BillPayStatus_BILLPAY_FAILED.equals(billPayments.get(0).getBillpaystatus())){
			return false;
		}
		return true;
	}
	
}
