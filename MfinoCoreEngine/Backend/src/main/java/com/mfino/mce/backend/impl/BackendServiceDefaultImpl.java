package com.mfino.mce.backend.impl;

import static com.mfino.fix.CmFinoFIX.ServletPath_BankAccount;
import static com.mfino.fix.CmFinoFIX.ServletPath_Subscribers;
import static com.mfino.fix.CmFinoFIX.ServletPath_WebAppFEForSubscribers;
import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.NoISOResponseMsg;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionsLog;
import com.mfino.domain.UnRegisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransfer;
import com.mfino.fix.CmFinoFIX.CMAgentToAgentTransferInquiry;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccount;
import com.mfino.fix.CmFinoFIX.CMBankAccountToBankAccountConfirmation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOut;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATM;
import com.mfino.fix.CmFinoFIX.CMCashOutAtATMInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiry;
import com.mfino.fix.CmFinoFIX.CMCashOutInquiryForNonRegistered;
import com.mfino.fix.CmFinoFIX.CMChargeDistribution;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivation;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationFromBank;
import com.mfino.fix.CmFinoFIX.CMExistingSubscriberReactivationToBank;
import com.mfino.fix.CmFinoFIX.CMFundAllocationConfirm;
import com.mfino.fix.CmFinoFIX.CMFundAllocationInquiry;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.fix.CmFinoFIX.CMGetBankAccountTransactions;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsToBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsToBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferReversalFromBank;
import com.mfino.fix.CmFinoFIX.CMMoneyTransferToBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationFromBank;
import com.mfino.fix.CmFinoFIX.CMNewSubscriberActivationToBank;
import com.mfino.fix.CmFinoFIX.CMPendingCommodityTransferRequest;
import com.mfino.fix.CmFinoFIX.CMPinLessInquiryLessTransfer;
import com.mfino.fix.CmFinoFIX.CMPurchase;
import com.mfino.fix.CmFinoFIX.CMPurchaseInquiry;
import com.mfino.fix.CmFinoFIX.CMReverseTransaction;
import com.mfino.fix.CmFinoFIX.CMReverseTransactionInquiry;
import com.mfino.fix.CmFinoFIX.CMSettlementOfCharge;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistrationCashIn;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToBank;
import com.mfino.fix.CmFinoFIX.CMTransferInquiryToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMTransferToNonRegistered;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATM;
import com.mfino.fix.CmFinoFIX.CMWithdrawFromATMInquiry;
import com.mfino.mce.backend.BackendService;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.FundService;
import com.mfino.mce.backend.PendingClearanceService;
import com.mfino.mce.backend.TransactionLogService;
import com.mfino.mce.backend.ValidationService;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.MessageTypes;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.UnRegisteredTxnInfoService;
import com.mfino.service.impl.UnRegisteredTxnInfoServiceImpl;
import com.mfino.util.MfinoUtil;

public class BackendServiceDefaultImpl extends BaseServiceImpl implements BackendService
{
	protected BankService bankService;
	protected ValidationService validationService;
	protected TransactionLogService transactionLogService;
	protected PendingClearanceService pendingClearanceService;
	private FundService defaultFundServiceImpl;
	protected boolean isOfflineBank;
	protected UnRegisteredTxnInfoService unRegisteredTxnInfoService ;
	
	public UnRegisteredTxnInfoService getUnRegisteredTxnInfoService() {
		return unRegisteredTxnInfoService;
	}

	public void setUnRegisteredTxnInfoService(
			UnRegisteredTxnInfoService unRegisteredTxnInfoService) {
		this.unRegisteredTxnInfoService = unRegisteredTxnInfoService;
	}

	public boolean getIsOfflineBank() {
		return isOfflineBank;
	}

	public void setIsOfflineBank(boolean isOfflineBank) {
		this.isOfflineBank = isOfflineBank;
	}
	
	

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse preProcess(MCEMessage mceMessage) throws BackendRuntimeException
	{	
		log.info("BackendServiceDefaultImpl :: preProcess() BEGIN");
		BackendResponse backendResponse = createResponseObject();
		CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
		CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();
		CMBase baseMessage = getBaseMessage(mceMessage);
		
		/**
		 * HACK HACK
		 * transaction id of fix message that lands here is changed but for the case of reversal it should not be changed   
		 * This piece of code is written only to take care of reversal.
		 * This code is written since we are unsure if changing the code would cause other issues 
		 */
		//HACK BEGIN
		TransactionsLog transactionLog;
		if(responseFix instanceof NoISOResponseMsg)
		{
			/**
			 * Ideal code
			 *  // observe the change in using baseMesage instead of requestFix
			 *   TransactionsLog transactionLog = transactionLogService.createTransactionLog(baseMessage);
			 */
			((CMBase) responseFix).copy((CMBase)requestFix);
			transactionLog = transactionLogService.createTransactionLog(baseMessage);
		}
		else
		{
			transactionLog = transactionLogService.createTransactionLog(requestFix);
		}
		//HACK END
		
		// original code 
		// TransactionsLog transactionLog = transactionLogService.createTransactionLog(requestFix);
		
		if(transactionLog == null){
			backendResponse.setDescription("Error writing TransactionLog to database messageCode");
			backendResponse.setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());
		}
		else{
			backendResponse = validationService.validateFixMessage(MessageTypes.getMessageCode(baseMessage), baseMessage);
			
			if(isNullorZero(((BackendResponse)backendResponse).getInternalErrorCode())){

					if(!baseMessage.checkRequiredFields())
					{
						log.warn("required feilds are missing in fix message, possibly a bug in code, stopping processing for this request baseMessage="+baseMessage);
						backendResponse.setInternalErrorCode(NotificationCodes.RequiredParametersMissing.getInternalErrorCode());
					}
			}
			
			if(!isNullOrEmpty(baseMessage.getServletPath())){
				if ((baseMessage.getServletPath().equals(ServletPath_Subscribers)) || 
					(baseMessage.getServletPath().equals(ServletPath_BankAccount)) || 
					(baseMessage.getServletPath().equals(ServletPath_WebAppFEForSubscribers))) {
						
					baseMessage.setMessageType(MessageTypes.getMessageCode(baseMessage)); 
				}
			}
		}
		
		log.info("BackendServiceDefaultImpl :: preProcess() END");
		return backendResponse;
	}

	public CMBase getBaseMessage(MCEMessage mceMessage) throws BackendRuntimeException
	{
		log.info("BackendServiceDefaultImpl :: getBaseMessage() BEGIN");
		CMBase baseMessage;
		
		CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
		CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();
		
		if(mceMessage.getResponse() != null){
			baseMessage = (CMBase)responseFix;		 
		}
		else{
			baseMessage = (CMBase)requestFix;
		}
		
		baseMessage.setMessageType(MessageTypes.getMessageCode(baseMessage));
		
		log.info("BackendServiceDefaultImpl :: getBaseMessage() END baseMessage="+baseMessage);
		
		return baseMessage;
	}
	
	public MCEMessage getResponse(MCEMessage mceMessage, CFIXMsg returnFix){
		log.info("BackendServiceDefaultImpl :: getResponse() BEGIN");
		//if we get a req/res pair, actual req for is is the response from third party, ignore the req
		if(mceMessage.getResponse() != null){
			mceMessage.setRequest(mceMessage.getResponse());
			mceMessage.setResponse(returnFix);
		}
		else{
			mceMessage.setResponse(returnFix);
		}
		
		log.info("BackendServiceDefaultImpl :: getResponse() END");
		return mceMessage;
	}
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public MCEMessage processMessage(MCEMessage mceMessage) throws BackendRuntimeException
	{
		log.info("BackendServiceDefaultImpl :: processMessage() BEGIN # "+mceMessage);
		CFIXMsg returnFix = null;
		try
		{
			CMBase baseMessage =  getBaseMessage(mceMessage);
			CFIXMsg requestFix = (CFIXMsg)mceMessage.getRequest();
			CFIXMsg responseFix = (CFIXMsg)mceMessage.getResponse();
	
			log.debug("BackendServiceDefaultImpl :: processMessage baseMessage.DumpFields():"+baseMessage.DumpFields());
			/**
			 * HACK HACK
			 * transaction id of fix message that lands here is changed but for the case of reversal it should not be changed   
			 * This piece of code is written only to take care of reversal.
			 * This code is written since we are unsure if changing the code would cause other issues 
			 */
			// do not call this code for the case of reversal message
			returnFix= preProcess(mceMessage);
			if((null == returnFix) || isNullorZero(((BackendResponse)returnFix).getInternalErrorCode())){
				
				if(baseMessage instanceof CMTransferInquiryToNonRegistered){
					returnFix = handleTransferInquiryForUnRegistered((CMTransferInquiryToNonRegistered)baseMessage);
				}
				else if(baseMessage instanceof CMTransferToNonRegistered){
					CMTransferToNonRegistered transferToNonRegistered = (CMTransferToNonRegistered)baseMessage;
					returnFix = handleTransferConfirmationForUnregistered(transferToNonRegistered);
				}
				else if(baseMessage instanceof CMCashOutInquiryForNonRegistered){
					CMCashOutInquiryForNonRegistered cashoutInquiryForNonRegistered = (CMCashOutInquiryForNonRegistered)baseMessage;
					returnFix = handleCashoutInquiryForUnRegistered(cashoutInquiryForNonRegistered);
				}
				else if(baseMessage instanceof CMCashOutForNonRegistered){
					CMCashOutForNonRegistered cashoutForNonRegistered = (CMCashOutForNonRegistered)baseMessage;
					returnFix = handleCashoutForUnRegistered(cashoutForNonRegistered);
				}
				else if(baseMessage instanceof CMCashInInquiry){
					returnFix = bankService.onCashInInquiry((CMCashInInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMCashIn){
					returnFix = bankService.onCashIn((CMCashIn)baseMessage);
				}
				else if(baseMessage instanceof CMCashOutInquiry){
					returnFix = bankService.onCashOutInquiry((CMCashOutInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMCashOut){
					returnFix = bankService.onCashOut((CMCashOut)baseMessage);
				}
				else if(baseMessage instanceof CMCashOutAtATMInquiry){
					returnFix = bankService.onCashOutAtATMInquiry((CMCashOutAtATMInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMCashOutAtATM){
					returnFix = handleCashOutAtATMConfirmation((CMCashOutAtATM)baseMessage);
				}
				else if(baseMessage instanceof CMWithdrawFromATMInquiry){
					returnFix = bankService.onwithdrawFromATMInquiry((CMWithdrawFromATMInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMWithdrawFromATM){
					returnFix = bankService.onwithdrawFromATM((CMWithdrawFromATM)baseMessage);
				}			
				else if(baseMessage instanceof CMFundAllocationInquiry){
					returnFix = defaultFundServiceImpl.onFundAllocationInquiry((CMFundAllocationInquiry)baseMessage);

				}
				else if(baseMessage instanceof CMFundAllocationConfirm){
					returnFix = defaultFundServiceImpl.handleFundAllocationConfirm((CMFundAllocationConfirm)baseMessage);
				}
				else if(baseMessage instanceof CMFundWithdrawalInquiry){
					returnFix = defaultFundServiceImpl.handleFundWithdrawalInquiry((CMFundWithdrawalInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMFundWithdrawalConfirm){
					returnFix = defaultFundServiceImpl.handleFundWithdrawalConfirm((CMFundWithdrawalConfirm)baseMessage);
				}
				else if(baseMessage instanceof CMAgentToAgentTransferInquiry){
					returnFix = bankService.onAgentToAgentTransferInquiry((CMAgentToAgentTransferInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMAgentToAgentTransfer){
					returnFix = bankService.onAgentToAgentTransfer((CMAgentToAgentTransfer)baseMessage);
				}
				else if(baseMessage instanceof CMPurchaseInquiry){
					returnFix = bankService.onPurchaseInquiry((CMPurchaseInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMPurchase){
					returnFix = bankService.onPurchase((CMPurchase)baseMessage);
				}
				else if(baseMessage instanceof CMReverseTransactionInquiry){
					returnFix = bankService.onReverseTransactionInquiry((CMReverseTransactionInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMReverseTransaction){
					returnFix = bankService.onReverseTransaction((CMReverseTransaction)baseMessage);
				}				
				else if(baseMessage instanceof CMBankAccountBalanceInquiry)
				{
					returnFix = bankService.onBalanceInquiry((CMBankAccountBalanceInquiry)baseMessage);
				}
				else if(baseMessage instanceof CMBalanceInquiryFromBank){
					returnFix = bankService.onBalanceInquiryFromBank((CMBalanceInquiryToBank)requestFix, (CMBalanceInquiryFromBank)responseFix);
				}
				else if(baseMessage instanceof CMBankAccountToBankAccount){
					returnFix = bankService.onTransferInquiryToBank((CMBankAccountToBankAccount)baseMessage);
				}
				else if(baseMessage instanceof CMTransferInquiryFromBank){
					returnFix = bankService.onTransferInquiryFromBank((CMTransferInquiryToBank)requestFix, (CMTransferInquiryFromBank)responseFix);
				}
				else if(baseMessage instanceof CMBankAccountToBankAccountConfirmation){
					returnFix = bankService.onTransferConfirmationToBank((CMBankAccountToBankAccountConfirmation)baseMessage);
				}
				else if(baseMessage instanceof NoISOResponseMsg && (!getIsOfflineBank()))
				{
					if(requestFix instanceof CMMoneyTransferToBank)
						returnFix = bankService.onTransferReversalToBank((CMMoneyTransferToBank)requestFix, (NoISOResponseMsg)responseFix);
				}
				else if(baseMessage instanceof CMMoneyTransferReversalFromBank)
				{
					returnFix = bankService.onTransferReversalFromBank((CMMoneyTransferToBank)requestFix, (CMMoneyTransferReversalFromBank)responseFix);
				}
				else if(baseMessage instanceof CMMoneyTransferFromBank){
					returnFix = bankService.onTransferConfirmationFromBank((CMMoneyTransferToBank)requestFix, (CMMoneyTransferFromBank)responseFix);
				}
				else if(baseMessage instanceof CMSubscriberRegistrationCashIn){
					returnFix = bankService.onSubscriberRegistrationCashIn((CMSubscriberRegistrationCashIn)baseMessage);
				}
				else if(baseMessage instanceof CMChargeDistribution){
					returnFix = bankService.onChargeDistribution((CMChargeDistribution)baseMessage);
				}	
				else if(baseMessage instanceof CMSettlementOfCharge){
					returnFix = bankService.onSettlementOfCharge((CMSettlementOfCharge)baseMessage);
				}
				else if(baseMessage instanceof CMGetBankAccountTransactions) {
					returnFix = bankService.onGetBankHistoryToBank((CMGetBankAccountTransactions)baseMessage);
				}
				else if(baseMessage instanceof CMGetLastTransactionsFromBank) {
					returnFix = bankService.onGetBankHistoryFromBank((CMGetLastTransactionsToBank)requestFix, (CMGetLastTransactionsFromBank)responseFix);
				}
				else if (baseMessage instanceof CMPinLessInquiryLessTransfer) {
					returnFix = bankService.onPinLessInquiryLessTransfer((CMPinLessInquiryLessTransfer)baseMessage);
				}
				else if(baseMessage instanceof CMPendingCommodityTransferRequest){
					if(pendingClearanceService!=null){
						returnFix = pendingClearanceService.processMessage((CMPendingCommodityTransferRequest)baseMessage);
					}else{
						log.error("Invalid initialization PendingClearanceService missing so aborted request for PCT clearance");
					}
				}
				else if(baseMessage instanceof CMNewSubscriberActivation) {
					returnFix = bankService.onNewSubscriberActivation((CMNewSubscriberActivation)baseMessage);
				}
				else if(baseMessage instanceof CMExistingSubscriberReactivation) {
					returnFix = bankService.onExistingSubscriberReactivation((CMExistingSubscriberReactivation)baseMessage);
				}
				else if(baseMessage instanceof CMNewSubscriberActivationFromBank) {
					returnFix = bankService.onNewSubscriberActivationFromBank((CMNewSubscriberActivationToBank)requestFix, (CMNewSubscriberActivationFromBank)responseFix);
				}
				else if(baseMessage instanceof CMExistingSubscriberReactivationFromBank) {
					returnFix = bankService.onExistingSubscriberReactivationFromBank((CMExistingSubscriberReactivationToBank)requestFix, (CMExistingSubscriberReactivationFromBank)responseFix);
				}
				else if(baseMessage instanceof CMGetSubscriberDetailsFromBank)
				{
					returnFix = bankService.onGetSubscriberDetailsFromBank((CMGetSubscriberDetailsToBank)requestFix, (CMGetSubscriberDetailsFromBank)responseFix);
				}
				else
				{
					log.error("got an invalid message "+requestFix.DumpFields());
					((BackendResponse)returnFix).setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());  
				}
			}
			
			
		}
		catch(Exception e){
			if(returnFix instanceof BackendResponse){
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			}
			log.error("Error in BackendService ", e);
		}
		mceMessage = getResponse(mceMessage, returnFix);
		
		log.info("completed backend service "+mceMessage.getResponse());
		
		if(mceMessage.getResponse() != null){
			log.debug("Return FIX "+mceMessage.getResponse().DumpFields());
		}
		
		return mceMessage;
	}

	private CFIXMsg handleCashoutForUnRegistered(
			CMCashOutForNonRegistered cashoutForNonRegistered) {
		CFIXMsg returnFix;
		
		returnFix = bankService.onCashOutForUnRegistered(cashoutForNonRegistered);
		
		if (returnFix instanceof BackendResponse) {
			UnRegisteredTxnInfoDAO urtDAO = coreDataWrapper.getUnRegisteredTxnInfoDAO();
			UnRegisteredTxnInfoQuery query = new UnRegisteredTxnInfoQuery();
			query.setSubscriberMDNID(cashoutForNonRegistered.getSourceMDNID());
			query.setCashoutSCTLId(cashoutForNonRegistered.getServiceChargeTransactionLogID());
			List<UnRegisteredTxnInfo> lstUnRegTxnInfo = urtDAO.get(query);
			UnRegisteredTxnInfo unRegTxnInfo = lstUnRegTxnInfo.iterator().next();
			unRegTxnInfo.setCashoutCTId(cashoutForNonRegistered.getTransferID());			
			if (CmFinoFIX.ResponseCode_Success.equals(((BackendResponse) returnFix).getResult())) {
				log.debug("Updating the Status to CashOut complete for the UnRegisteredTxn -->"+ cashoutForNonRegistered.getSourceMDN());
				// Update the UnRegisteredTxnInfo
				unRegTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_COMPLETED);
			}else{
				log.debug("Updating the Status to CashOut failed for the UnRegisteredTxn -->"+ cashoutForNonRegistered.getSourceMDN());
				
				unRegTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_CASHOUT_FAILED);
			}
			coreDataWrapper.save(unRegTxnInfo);
		}
		
		return returnFix;
	}

	private CFIXMsg handleCashoutInquiryForUnRegistered(
			CMCashOutInquiryForNonRegistered cashoutInquiryForNonRegistered) {
		CFIXMsg returnFix;
		returnFix = bankService.onCashOutInquiryForUnRegistered(cashoutInquiryForNonRegistered);
		return returnFix;
	}

	private CFIXMsg handleTransferConfirmationForUnregistered(
			CMTransferToNonRegistered transferToNonRegistered) {
		CFIXMsg returnFix;
		returnFix = bankService.onTransferConfirmationForUnRegistered(transferToNonRegistered);
		if(returnFix instanceof BackendResponse){
			BackendResponse response = (BackendResponse) returnFix;
			if ( CmFinoFIX.ResponseCode_Success.equals(response.getResult())) { 
				log.debug("Creating the UnRegisteredTxn with Complete status --> " + transferToNonRegistered.getDestMDN());
				//Create UnRegisteredTxnRecord
				SubscriberMDN objDestSubMdn = coreDataWrapper.getSubscriberMdn(transferToNonRegistered.getDestMDN());
				Long sctlid = transferToNonRegistered.getServiceChargeTransactionLogID();
				Long transferID = response.getTransferID();
				
				UnRegisteredTxnInfo unRegTxnInfo = new UnRegisteredTxnInfo();
				unRegTxnInfo.setTransferCTId(transferID);
				unRegTxnInfo.setTransferSCTLId(sctlid);
				unRegTxnInfo.setSubscriberMDNByMDNID(objDestSubMdn);
				unRegTxnInfo.setAmount(response.getAmount());


				String code = unRegisteredTxnInfoService.generateFundAccessCode();
				String digestedCode = MfinoUtil.calculateDigestPin(objDestSubMdn.getMDN(), code);
				unRegTxnInfo.setDigestedPIN(digestedCode);
				unRegTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED);
				coreDataWrapper.save(unRegTxnInfo);
				((BackendResponse) returnFix).setOneTimePin(code);
			}
		}
		return returnFix;
	}
	

	private CFIXMsg handleCashOutAtATMConfirmation(CMCashOutAtATM cashOut) {
		CFIXMsg returnFix;
		returnFix = bankService.onCashOutAtATM(cashOut);
		if(returnFix instanceof BackendResponse){
			BackendResponse response = (BackendResponse) returnFix;
			if ( CmFinoFIX.ResponseCode_Success.equals(response.getResult())) { 
				log.debug("Creating the UnRegisteredTxn with transfer Complete status for cash out at ATM for MDN --> " + cashOut.getSourceMDN());
				//Create UnRegisteredTxnRecord
				SubscriberMDN objSubMdn = coreDataWrapper.getSubscriberMdn(cashOut.getSourceMDN());
				Long sctlid = cashOut.getServiceChargeTransactionLogID();
				Long transferID = response.getTransferID();
				
				UnRegisteredTxnInfo unRegTxnInfo = new UnRegisteredTxnInfo();
				unRegTxnInfo.setTransferCTId(transferID);
				unRegTxnInfo.setTransferSCTLId(sctlid);
				unRegTxnInfo.setSubscriberMDNByMDNID(objSubMdn);
				unRegTxnInfo.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_AT_ATM);
				unRegTxnInfo.setAmount(response.getAmount());
				String code = unRegisteredTxnInfoService.generateFundAccessCode();
				String digestedCode = MfinoUtil.calculateDigestPin(objSubMdn.getMDN(), code);
				unRegTxnInfo.setDigestedPIN(digestedCode);
				unRegTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_TRANSFER_COMPLETED);
				coreDataWrapper.save(unRegTxnInfo);
				((BackendResponse) returnFix).setOneTimePin(code);
			}
		}
		return returnFix;
	}

	private CFIXMsg handleTransferInquiryForUnRegistered(CMTransferInquiryToNonRegistered baseMessage) {
		CFIXMsg returnFix;
		returnFix = bankService.onTransferInquiryForUnRegistered(baseMessage);
		return returnFix;
	}

	public ValidationService getValidationService() {
		return validationService;
	}

	public void setValidationService(ValidationService validationService) {
		this.validationService = validationService;
	}

	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}

	public BankService getBankService() {
		return bankService;
	}

	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	
	public PendingClearanceService getPendingClearanceService(){
		return pendingClearanceService;
	}
	
	public void setPendingClearanceService(PendingClearanceService pendingClearanceService){
		this.pendingClearanceService = pendingClearanceService;
	}

	public FundService getDefaultFundServiceImpl() {
		return defaultFundServiceImpl;
	}

	public void setDefaultFundServiceImpl(FundService defaultFundServiceImpl) {
		this.defaultFundServiceImpl = defaultFundServiceImpl;
	}
}
