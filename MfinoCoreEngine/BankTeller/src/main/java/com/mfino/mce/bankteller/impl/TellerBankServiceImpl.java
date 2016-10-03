package com.mfino.mce.bankteller.impl;

import java.math.BigDecimal;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashIn;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashInConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOut;
import com.mfino.fix.CmFinoFIX.CMBankTellerCashOutConfirm;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBankTellerTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.mce.backend.impl.BankServiceDefaultImpl;
import com.mfino.mce.bankteller.TellerBackendResponse;
import com.mfino.mce.bankteller.TellerBankService;
import com.mfino.mce.bankteller.TellerEmoneyTransferService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;

/**
 * @author Maruthi
 * 
 */
public class TellerBankServiceImpl extends BankServiceDefaultImpl implements TellerBankService {

	private TellerEmoneyTransferService tellerEmoneyTransferService;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerCashIn(CMBankTellerCashIn requestFix) {
		log.info("TellerBankServiceImpl :: onTellerCashIn BEGIN");
		CMBankTellerCashIn toInquiry = requestFix;
		if(requestFix.getIsInDirectCashIn()){
			toInquiry.setDestMDN(requestFix.getSourceMDN());
			toInquiry.setAmount(requestFix.getAmount().add(requestFix.getCharges()));
			toInquiry.setCharges(BigDecimal.ZERO);
		}
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferInquiryToBank(toInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof BackendResponse) {
			if (((BackendResponse) returnFix).getInternalErrorCode().equals(NotificationCodes.BankAccountToBankAccountConfirmationPrompt.getInternalErrorCode())) {
				
				TellerBackendResponse response = (TellerBackendResponse)createResponseObject(); 
				response.copy((BackendResponse)returnFix);
				response.setSourcePocketID(requestFix.getSourcePocketID());
				response.setDestPocketID(requestFix.getDestPocketID());
				response.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
				response.setSourceApplication(requestFix.getSourceApplication());
				returnFix = tellerEmoneyTransferService.generateCashInConfirm(response);
			}
		} else {
			CMBankTellerTransferInquiryToBank toBank = new CMBankTellerTransferInquiryToBank();
			toBank.copy((CMTransferInquiryToBank) returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			toBank.setIsInDirectCashIn(requestFix.getIsInDirectCashIn());
			toBank.setEndDestPocketID(requestFix.getEndDestPocketID());
			returnFix = toBank;
		}
		log.info("TellerBankServiceImpl :: onTellerCashIn END");
		return returnFix;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerTransferInquiryFromBank(CMBankTellerTransferInquiryToBank toBank,CMBankTellerTransferInquiryFromBank fromBank) {
		log.info("TellerBankServiceImpl :: onTellerTransferInquiryFromBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferInquiryFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof BackendResponse) {
			if(toBank.getIsInDirectCashIn()){
				ServiceChargeTxnLog sctl = coreDataWrapper.getSCTLById(toBank.getServiceChargeTransactionLogID());
				((BackendResponse) returnFix).setAmount(sctl.getTransactionamount());
				((BackendResponse) returnFix).setCharges(toBank.getAmount().subtract(sctl.getTransactionamount()));
				((BackendResponse) returnFix).setReceiverMDN(sctl.getDestmdn());				
				((BackendResponse) returnFix).setServiceChargeTransactionLogID(sctl.getId().longValue());
			}
			if (((BackendResponse) returnFix).getInternalErrorCode().equals(NotificationCodes.BankAccountToBankAccountConfirmationPrompt.getInternalErrorCode())) {
			
			}
		}
		log.info("TellerBankServiceImpl :: onTellerTransferInquiryFromBank END");
		return returnFix;
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerCashInConfirm(CMBankTellerCashInConfirm cashinConfirm) {
		log.info("TellerBankServiceImpl :: onTellerCashInConfirm BEGIN");
		CMBankTellerCashInConfirm toConfirm = cashinConfirm;
		if(cashinConfirm.getIsInDirectCashIn()){
			toConfirm.setDestMDN(cashinConfirm.getSourceMDN());
		}
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferConfirmationToBank(cashinConfirm);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof BackendResponse) {
			//return response to user as transaction failed or is Emoney to Emoney
		} else {
			CMBankTellerMoneyTransferToBank toBank = new CMBankTellerMoneyTransferToBank();
			toBank.copy((CMMoneyTransferToBank) returnFix);
			toBank.setServiceChargeTransactionLogID(cashinConfirm.getServiceChargeTransactionLogID());
			toBank.setIsInDirectCashIn(cashinConfirm.getIsInDirectCashIn());
			toBank.setEndDestPocketID(cashinConfirm.getEndDestPocketID());
			toBank.setPin(cashinConfirm.getPin());
			returnFix = toBank;
		}

		log.info("TellerBankServiceImpl :: onTellerCashInConfirm END");
		return returnFix;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerTransferConfirmationFromBank(CMBankTellerMoneyTransferToBank toBank,CMBankTellerMoneyTransferFromBank fromBank) {
		log.info("TellerBankServiceImpl :: onTellerTransferConfirmationFromBank BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferConfirmationFromBank(toBank, fromBank);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).copy(toBank);
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof TellerBackendResponse) {
			TellerBackendResponse tellerResponse = (TellerBackendResponse)returnFix;
			if (tellerResponse.getInternalErrorCode().equals(NotificationCodes.BankAccountToEMoneyCompletedToSender.getInternalErrorCode())) {
				tellerResponse.setPin(toBank.getPin());
				tellerResponse.setServiceChargeTransactionLogID(toBank.getServiceChargeTransactionLogID());
				tellerResponse.setDestPocketID(toBank.getDestPocketID());
				tellerResponse.setEndDestPocketID(toBank.getEndDestPocketID());
			    returnFix = tellerEmoneyTransferService.generateCashInInquiry((TellerBackendResponse)returnFix);
			}
		}
		log.info("TellerBankServiceImpl :: onTransferConfirmationFromBank END");
		return returnFix;
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerCashOut(CMBankTellerCashOut requestFix) {
		log.info("TellerBankServiceImpl :: onTellerCashOut BEGIN");
		requestFix.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank);
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferInquiryToBank(requestFix);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof BackendResponse) {
			
		} else {
			CMBankTellerTransferInquiryToBank toBank = new CMBankTellerTransferInquiryToBank();
			toBank.copy((CMTransferInquiryToBank) returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			toBank.setIsInDirectCashIn(false);
			returnFix = toBank;
		}
		log.info("TellerBankServiceImpl :: onTellerCashOut END");
		return returnFix;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public CFIXMsg onTellerCashOutConfirmation(CMBankTellerCashOutConfirm requestFix) {
		log.info("TellerBankServiceImpl :: onTellerCashOutConfirmation BEGIN");
		
		requestFix.setUICategory(CmFinoFIX.TransactionUICategory_Teller_Cashout_TransferToBank);
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = onTransferConfirmationToBank(requestFix);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
		}
		if (returnFix instanceof BackendResponse) {
			//return response to user as transaction failed or is Emoney to Emoney
		} else {
			CMBankTellerMoneyTransferToBank toBank = new CMBankTellerMoneyTransferToBank();
			toBank.copy((CMMoneyTransferToBank) returnFix);
			toBank.setServiceChargeTransactionLogID(requestFix.getServiceChargeTransactionLogID());
			toBank.setIsInDirectCashIn(false);
			returnFix = toBank;
		}

		log.info("TellerBankServiceImpl :: onTellerCashOutConfirmation END");
		return returnFix;
	}

	public TellerEmoneyTransferService getTellerEmoneyTransferService() {
		return tellerEmoneyTransferService;
	}

	public void setTellerEmoneyTransferService(
			TellerEmoneyTransferService tellerEmoneyTransferService) {
		this.tellerEmoneyTransferService = tellerEmoneyTransferService;
	}

	@Override
	public BackendResponse createResponseObject() 
	{
		return new TellerBackendResponse();
	}

	


}
