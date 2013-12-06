/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.Date;

import org.hibernate.exception.DataException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionResponse;
import com.mfino.domain.TransactionsLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSMoneyTransfer;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.NotificationService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.MoneyTransferProcessor;

/**
 * 
 * @author Maruthi
 */
@Service("MoneyTransferProcessorImpl")
public class MoneyTransferProcessorImpl extends MultixCommunicationHandler
		implements MoneyTransferProcessor {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	private ChannelCode cc;

	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;

	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;

	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;

	public CFIXMsg process(CFIXMsg msg) {
		
		log.info("TransferRequest from user:"+getLoggedUserName());

		CMJSMoneyTransfer realMsg = (CMJSMoneyTransfer) msg;
		CMJSError error = new CMJSError();

		TransactionsLog transactionsLog = new TransactionsLog();
		transactionsLog.setMessageCode(CmFinoFIX.MsgType_JSMoneyTransfer);
		transactionsLog.setMessageData(realMsg.DumpFields());
		transactionsLog.setTransactionTime(new Timestamp(new Date()));
		transactionLogService.save(transactionsLog);

		cc = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
		Transaction transDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setChannelCodeId(cc.getID());
		sc.setDestMDN(realMsg.getDestMDN());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_BANK);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		sc.setSourceMDN(realMsg.getSourceMDN());
		sc.setTransactionAmount(realMsg.getAmount());
		sc.setTransactionLogId(transactionsLog.getID());
		try {
			transDetails = transactionChargingService.getCharge(sc);
		} catch (InvalidServiceException e) {
			log.error("Exception occured in getting charges", e);
			error.setCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			error.setErrorDescription("BankService is not avaialbe. Please contact Admin to add BankService");
			return error;
		} catch (InvalidChargeDefinitionException e) {
			log.error(e.getMessage());
			error.setCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			error.setErrorDescription("Invalid ChargeDefinition contact Admin");
			return error;
		} catch (DataException e) {
			log.error(e.getMessage());
			error.setCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			error.setErrorDescription("Internal System error please try again");
			return error;
		}
		ServiceChargeTransactionLog sctl = transDetails.getServiceChargeTransactionLog();
		realMsg.setServiceChargeTransactionLogID(sctl.getID());

		error = (CMJSError) handleInquiry(realMsg);
		if (CmFinoFIX.ErrorCode_NoError.equals(error.getErrorCode())) {
			sctl = serviceChargeTransactionLogService.getById(sctl.getID());
			if(CmFinoFIX.SCTLStatus_Inquiry.equals(sctl.getStatus())) {
					transactionChargingService.chnageStatusToProcessing(sctl);
				} 
			error = handleConfirmation(realMsg);
		} /*else {
			error = new CMJSError();
			error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			error.setErrorDescription("TransactionFailed:"+ notificationService.getNotificationText(Integer.parseInt(response.getCode()),
							CmFinoFIX.Language_English));
		}*/
		return error;

	}

	private CMJSError handleInquiry(CMJSMoneyTransfer realMsg) {

		CMBankAccountToBankAccount transferInquiry = new CMBankAccountToBankAccount();
		transferInquiry.setDestMDN(realMsg.getDestMDN());
		transferInquiry.setAmount(realMsg.getAmount());
		if (realMsg.getPin() != null)
			transferInquiry.setPin(realMsg.getPin());
		else{
			transferInquiry.setIsSystemIntiatedTransaction(true);
			transferInquiry.setPin("mFino260");// dummy
		}
		transferInquiry.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferInquiry.setSourceMDN(realMsg.getSourceMDN());
		transferInquiry.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		transferInquiry.setSourceApplication(cc.getChannelSourceApplication());
		transferInquiry.setChannelCode(cc.getChannelCode());
		transferInquiry.setSourcePocketID(realMsg.getSourcePocketID());
		transferInquiry.setDestPocketID(realMsg.getDestPocketID());
		transferInquiry.setServiceChargeTransactionLogID(realMsg.getServiceChargeTransactionLogID());
		log.info("Sending trnaferinquiry request From source:"+realMsg.getSourceMDN()+" PocketID:"+realMsg.getSourcePocketID()+" To:"+realMsg.getDestMDN()
				+" PocketID:"+realMsg.getDestPocketID()+ " Amount"+realMsg.getAmount());
		CMJSError response = (CMJSError) super.handleRequestResponse(transferInquiry);
		// Saves the Transaction Id returned from Back End
		TransactionResponse transactionResponse = checkBackEndResponse(response);
		ServiceChargeTransactionLog sctl = serviceChargeTransactionLogService.getById(realMsg.getServiceChargeTransactionLogID());

		if (transactionResponse.getTransactionId() != null) {
			sctl.setTransactionID(transactionResponse.getTransactionId());
			transactionChargingService.saveServiceTransactionLog(sctl);
		}
		transactionChargingService.updateTransactionStatus(transactionResponse,sctl);
		if (transactionResponse.isResult()) {
			realMsg.setTransferID(response.getTransferID());
			realMsg.setParentTransactionID(response.getParentTransactionID());
		}
		return response;

	}

	private CMJSError handleConfirmation(CMJSMoneyTransfer realMsg) {

		CMBankAccountToBankAccountConfirmation transferConfirmation = new CMBankAccountToBankAccountConfirmation();
		transferConfirmation.setSourceMDN(realMsg.getSourceMDN());
		transferConfirmation.setDestMDN(realMsg.getDestMDN());
		transferConfirmation.setServletPath(CmFinoFIX.ServletPath_Subscribers);
		transferConfirmation.setTransferID(realMsg.getTransferID());
		transferConfirmation.setConfirmed(true);
		transferConfirmation.setSourceApplication(cc.getChannelSourceApplication());
		transferConfirmation.setChannelCode(cc.getChannelCode());
		transferConfirmation.setParentTransactionID(realMsg.getParentTransactionID());
		transferConfirmation.setSourcePocketID(realMsg.getSourcePocketID());
		transferConfirmation.setDestPocketID(realMsg.getDestPocketID());
		transferConfirmation.setServiceChargeTransactionLogID(realMsg.getServiceChargeTransactionLogID());

		TransactionsLog transactionsLog = transactionLogService.saveTransactionsLog(
						CmFinoFIX.MessageType_BankAccountToBankAccountConfirmation,
						transferConfirmation.DumpFields(),
						transferConfirmation.getParentTransactionID());
		transferConfirmation.setTransactionID(transactionsLog.getID());
		log.info("Sending trnaferConfirmation request From source:"+realMsg.getSourceMDN()+" PocketID:"+realMsg.getSourcePocketID()+" To:"+realMsg.getDestMDN()
				+" PocketID:"+realMsg.getDestPocketID()+ " Amount"+realMsg.getAmount()+" TransferID:"+realMsg.getTransferID());
		
		CMJSError errorMsg = (CMJSError) handleRequestResponse(transferConfirmation);
		ServiceChargeTransactionLog sctl = serviceChargeTransactionLogService.getById(realMsg.getServiceChargeTransactionLogID());

		if (CmFinoFIX.ErrorCode_NoError.equals(errorMsg.getErrorCode())) {
			transactionChargingService.confirmTheTransaction(sctl);
			realMsg.setsuccess(Boolean.FALSE);
		} else if (CmFinoFIX.ErrorCode_LinkDisconnected.equals(errorMsg.getErrorCode())) {
			log.info("No response from backend.");
		} else {
			log.info(" confirmation failed");
			transactionChargingService.failTheTransaction(sctl,errorMsg.getErrorDescription());
		}
		return errorMsg;
	}

	
	@Override
	public CFIXMsg handleResponse(CFIXMsg pMsg) {
		if (pMsg != null) {
			return super.handleResponse(pMsg);
		} else {
			CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
			log.info(MessageText._("No response from backend server"));
			errorMsg.setErrorCode(CmFinoFIX.ErrorCode_LinkDisconnected);
			errorMsg.setErrorDescription("Your request is queued. Please check after sometime.");
			return errorMsg;
		}

	}
}
