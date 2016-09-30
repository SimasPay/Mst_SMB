package com.mfino.mce.backend.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundDefinitionDAO;
import com.mfino.dao.PurposeDAO;
import com.mfino.dao.query.FundDefinitionQuery;
import com.mfino.dao.query.PurposeQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.Purpose;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFundAllocationConfirm;
import com.mfino.fix.CmFinoFIX.CMFundAllocationInquiry;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalConfirm;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.backend.FundService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.FundStorageService;
import com.mfino.service.FundValidationService;

public class FundServiceImpl extends BaseServiceImpl implements FundService{
	
	protected FundValidationService fundValidationService;


	public FundValidationService getFundValidationService() {
		return fundValidationService;
	}
	public void setFundValidationService(FundValidationService fundValidationService) {
		this.fundValidationService = fundValidationService;
	}
	protected FundStorageService fundStorageService;
	
	public FundStorageService getFundStorageService() {
		return fundStorageService;
	}
	public void setFundStorageService(FundStorageService fundStorageService) {
		this.fundStorageService = fundStorageService;
	}
	protected BankService bankService;
	public static Long DEFAULT_EXPIRY_SECONDS = 86400L;
	public static String ANY_PARTNER = "any";

	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onFundAllocationInquiry(CMFundAllocationInquiry fundAllocationInquiry) {
		log.info("BankServiceDefaultImpl :: onFundAllocationInquiry BEGIN");
		fundAllocationInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Fund_Allocation);
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = bankService.onTransferInquiryToBank(fundAllocationInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if(returnFix instanceof BackendResponse){
			if ( CmFinoFIX.ResponseCode_Success.equals(((BackendResponse) returnFix).getResult()) ) { 
				((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundAllocationConfirmationPrompt.getInternalErrorCode());
				((BackendResponse) returnFix).setOnBehalfOfMDN(fundAllocationInquiry.getWithdrawalMDN());
			}
		}
		return returnFix;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg handleFundAllocationConfirm(CMFundAllocationConfirm fundAllocationConfirm) {
		CFIXMsg returnFix;
		returnFix = onFundAllocationConfirm(fundAllocationConfirm);
		if(returnFix instanceof BackendResponse){
			BackendResponse response = (BackendResponse) returnFix;
			if ( CmFinoFIX.ResponseCode_Success.equals(response.getResult())) { 
				log.debug("Creating the UnRegisteredTxn with transfer Complete status for fund allocation for MDN --> " + fundAllocationConfirm.getWithdrawalMDN());
				//Create UnRegisteredTxnRecord
				Long sctlid = fundAllocationConfirm.getServiceChargeTransactionLogID();
				Long transferID = response.getTransferID();
				
				PurposeDAO purposeDAO = DAOFactory.getInstance().getPurposeDAO();
				PurposeQuery purposeQuery = new PurposeQuery();
				String[] multiCode = new String[2];
				multiCode[0] = ANY_PARTNER;
				multiCode[1] = fundAllocationConfirm.getPartnerCode();
				purposeQuery.setMultiCode(multiCode);
				List<Purpose> lstPurposes = purposeDAO.get(purposeQuery);
				Purpose purpose = null;
				
				//lstPurposes.size() will be either 1 or 2 .For all other cases it is an error which is being checked in the frontend
				//handler.The below piece of if..else sets the purpose to specific partnerCode purpose if it exists else the default any purpose is set
				if(lstPurposes.size()>1){
					if(!ANY_PARTNER.equalsIgnoreCase(lstPurposes.get(0).getCode())){
						purpose = lstPurposes.get(0);
					}
					else{
						purpose = lstPurposes.get(1);
					}
				}
				else{
					purpose = lstPurposes.get(0);
				}
				
				log.info("Using the purpose with code as: "+purpose.getCode());
				
				FundDefinitionDAO fundDefinitionDAO = DAOFactory.getInstance().getFundDefinitionDAO();
				FundDefinitionQuery fundDefinitionQuery = new FundDefinitionQuery();
				fundDefinitionQuery.setPurposeID(purpose.getId().longValue());
				List<FundDefinition> lstFundDefinitions = fundDefinitionDAO.get(fundDefinitionQuery);
				FundDefinition fundDefinition = lstFundDefinitions.get(0);
				
				log.info("using the FundDefinition with id: "+fundDefinition.getId());
				
				UnregisteredTxnInfo unRegTxnInfo = new UnregisteredTxnInfo();
				unRegTxnInfo.setTransferctid(new BigDecimal(transferID));
				unRegTxnInfo.setTransferSCTLId(sctlid);

				log.info("unregistered trxn info logging"+fundAllocationConfirm.getWithdrawalMDN());
				unRegTxnInfo.setWithdrawalmdn(fundAllocationConfirm.getWithdrawalMDN());
				unRegTxnInfo.setFailurereasoncode(Long.valueOf(0));
				unRegTxnInfo.setExpirytime(getExpiryTime(fundDefinition.getExpirationType()));
				unRegTxnInfo.setFundDefinition(fundDefinition);
				unRegTxnInfo.setWithdrawalfailureattempt(Long.valueOf(0));
				unRegTxnInfo.setAvailableamount(response.getAmount());
				unRegTxnInfo.setPartnercode(fundAllocationConfirm.getPartnerCode());
				unRegTxnInfo.setTransactionname(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION);
				unRegTxnInfo.setAmount(response.getAmount());
				
				String code = fundStorageService.generateFundAccessCode(fundDefinition);
				String digestedCode = fundStorageService.generateDigestedFAC(fundAllocationConfirm.getWithdrawalMDN(), code);
				unRegTxnInfo.setDigestedpin(digestedCode);
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE));
				
				coreDataWrapper.save(unRegTxnInfo);
				((BackendResponse) returnFix).setOneTimePin(code);
				((BackendResponse)returnFix).setOnBehalfOfMDN(fundAllocationConfirm.getWithdrawalMDN());
				((BackendResponse) returnFix).setPartnerCode(fundAllocationConfirm.getPartnerCode());

			}
		}
		return returnFix;
	}
	
	private Timestamp getExpiryTime(ExpirationType expirationType) {
		if(expirationType.getExpirytype().equals(CmFinoFIX.ExpiryType_Fund)){
			if(expirationType.getExpirymode().equals(CmFinoFIX.ExpiryMode_DurationInSecs)){
				return new Timestamp(System.currentTimeMillis() + expirationType.getExpiryvalue().intValue() * 1000);
			}else if(expirationType.getExpirymode().equals(CmFinoFIX.ExpiryMode_CutOffTime)){
			}
		}
		log.debug("Could not find Fund related expiry time.setting a deafult of 1 days");
		return new Timestamp(System.currentTimeMillis() +  DEFAULT_EXPIRY_SECONDS* 1000);		
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onFundAllocationConfirm(CMFundAllocationConfirm fundAllocationConfirm) {
		log.info("BankServiceDefaultImpl :: onFundAllocationConfirm BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = bankService.onTransferConfirmationToBank(fundAllocationConfirm);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if(returnFix instanceof BackendResponse){
			if ( CmFinoFIX.ResponseCode_Success.equals(((BackendResponse) returnFix).getResult())) { 
				((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundAllocationConfirmedToSender.getInternalErrorCode());
			}
		}

		return returnFix;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onFundWithdrawalInquiry(CMFundWithdrawalInquiry fundWithdrawalInquiry) {
		log.info("BankServiceDefaultImpl :: onFundWithdrawalInquiry BEGIN");
		fundWithdrawalInquiry.setUICategory(CmFinoFIX.TransactionUICategory_Fund_Withdrawal);
		CFIXMsg returnFix=createResponseObject();
		try {
			returnFix = bankService.onTransferInquiryToBank(fundWithdrawalInquiry);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if(returnFix instanceof BackendResponse){
			if(CmFinoFIX.DistributionType_Withdrawal.equals(fundWithdrawalInquiry.getDistributionType())){
				if ( CmFinoFIX.ResponseCode_Success.equals(((BackendResponse) returnFix).getResult()) ) { 
					((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundWithdrawalConfirmationPrompt.getInternalErrorCode());
				}
			}
		}
		return returnFix;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg handleFundWithdrawalConfirm(CMFundWithdrawalConfirm fundWithdrawalConfirm) {
		CFIXMsg returnFix;
		returnFix = onFundWithdrawalConfirm(fundWithdrawalConfirm);
		if(returnFix instanceof BackendResponse){
			BackendResponse response = (BackendResponse) returnFix;
			if ( CmFinoFIX.ResponseCode_Success.equals(response.getResult())) { 
				log.debug("Creating the fundDistributionInfoRecord with transfer Complete status for fund Withdrawal for MDN --> " + fundWithdrawalConfirm.getSourceMDN());
				//update UnRegisteredTxnRecord and fundDistributionInfoRecord
				Long sctlid = fundWithdrawalConfirm.getServiceChargeTransactionLogID();
				Long transferID = response.getTransferID();
				
				FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(sctlid);
				UnregisteredTxnInfo unRegTxnInfo = fundDistributionInfo.getUnregisteredTxnInfo();
				FundDefinition fundDefinition=unRegTxnInfo.getFundDefinition();
				
				if(CmFinoFIX.DistributionType_Withdrawal.equals(fundWithdrawalConfirm.getDistributionType())){
					if(unRegTxnInfo.getAvailableamount().compareTo(BigDecimal.ZERO)==0){
						unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUND_COMPLETELY_WITHDRAWN));
						((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundCompleteWithdrawalConfirmedToMerchant.getInternalErrorCode());
					}else{
						if(fundDefinition.getIsmultiplewithdrawalallowed()){
							String code = fundStorageService.generateFundAccessCode(fundDefinition);
							String digestedCode = fundStorageService.generateDigestedFAC(unRegTxnInfo.getWithdrawalmdn(), code);
							unRegTxnInfo.setDigestedpin(digestedCode);
							unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN));
							((BackendResponse) returnFix).setOneTimePin(code);
							((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundPartialWithdrawalConfirmedToMerchant.getInternalErrorCode());
						}else{
							unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED));
							unRegTxnInfo.setReversalreason("Partial Withdrawal complete.Multiple withdrawal not allowed.");
						}
					}
					((BackendResponse) returnFix).setOnBehalfOfMDN(fundWithdrawalConfirm.getWithdrawalMDN());
					((BackendResponse) returnFix).setReceiverMDN(fundWithdrawalConfirm.getDestMDN());
					((BackendResponse) returnFix).setSourceMDN(null);
				}else{
					unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_COMPLETED));
					((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundAllocationReversalToSender.getInternalErrorCode());
					((BackendResponse) returnFix).setReceiverMDN(fundWithdrawalConfirm.getDestMDN());
					((BackendResponse) returnFix).setOnBehalfOfMDN(fundWithdrawalConfirm.getWithdrawalMDN());
					((BackendResponse) returnFix).setParentTransactionID(unRegTxnInfo.getTransferSCTLId());
				}
				fundDistributionInfo.setDistributionstatus(Long.valueOf(CmFinoFIX.DistributionStatus_TRANSFER_COMPLETED));
				coreDataWrapper.save(fundDistributionInfo);
				coreDataWrapper.save(unRegTxnInfo);

			}
		}
		return returnFix;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg onFundWithdrawalConfirm(CMFundWithdrawalConfirm fundWithdrawalConfirm) {
		log.info("BankServiceDefaultImpl :: onFundWithdrawalConfirm BEGIN");
		CFIXMsg returnFix=createResponseObject();
		try {
		returnFix = bankService.onTransferConfirmationToBank(fundWithdrawalConfirm);
		} catch(Exception e){
			log.error(e.getMessage());
			((BackendResponse) returnFix).setResult(CmFinoFIX.ResponseCode_Failure);
			((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.DBCommitTransactionFailed.getInternalErrorCode());
			return returnFix;
		}
		if(returnFix instanceof BackendResponse){
			if ( CmFinoFIX.ResponseCode_Success.equals(((BackendResponse) returnFix).getResult())) { 
				//change here
				((BackendResponse) returnFix).setInternalErrorCode(NotificationCodes.FundWithdrawalConfirmedToMerchant.getInternalErrorCode());
			}
		}

		return returnFix;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg handleFundWithdrawalInquiry(CMFundWithdrawalInquiry fundWithdrawalInquiry) {
		CFIXMsg returnFix;
		returnFix = onFundWithdrawalInquiry(fundWithdrawalInquiry);
		if(returnFix instanceof BackendResponse){
			BackendResponse response = (BackendResponse) returnFix;
			if ( CmFinoFIX.ResponseCode_Success.equals(response.getResult())) { 
			}
		}
		return returnFix;
	}


	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BankService getBankService() {
		return bankService;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Integer getBankNotificationCode(BackendResponse returnFix) {
		FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(returnFix.getServiceChargeTransactionLogID());
		UnregisteredTxnInfo unRegTxnInfo = fundDistributionInfo.getUnregisteredTxnInfo();
		FundDefinition fundDefinition=unRegTxnInfo.getFundDefinition();;
		int notificationCode = NotificationCodes.FundWithdrawalConfirmedToMerchant.getNotificationCode();

		
		if(unRegTxnInfo.getAvailableamount().compareTo(BigDecimal.ZERO)==0){
			unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUND_COMPLETELY_WITHDRAWN));
			notificationCode = NotificationCodes.FundCompleteWithdrawalConfirmedToMerchant.getInternalErrorCode();
		}else{
			if(fundDefinition.getIsmultiplewithdrawalallowed()){
				String code = fundStorageService.generateFundAccessCode(fundDefinition);
				String digestedCode = fundStorageService.generateDigestedFAC(unRegTxnInfo.getWithdrawalmdn(), code);
				unRegTxnInfo.setDigestedpin(digestedCode);
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN));
				((BackendResponse) returnFix).setOneTimePin(code);
				notificationCode = NotificationCodes.FundPartialWithdrawalConfirmedToMerchant.getNotificationCode();
			}else{
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED));
				unRegTxnInfo.setReversalreason("Partial Withdrawal complete.Multiple withdrawal not allowed.");
			}
		}
		fundDistributionInfo.setDistributionstatus(Long.valueOf(CmFinoFIX.DistributionStatus_TRANSFER_COMPLETED));
		coreDataWrapper.save(fundDistributionInfo);
		coreDataWrapper.save(unRegTxnInfo);
		
		((BackendResponse) returnFix).setOnBehalfOfMDN(unRegTxnInfo.getWithdrawalmdn());
		((BackendResponse) returnFix).setReceiverMDN(returnFix.getReceiverMDN());
		((BackendResponse) returnFix).setSourceMDN(null);
		return notificationCode;
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void resolvePendingFunds(Integer csrAction, BackendResponse returnFix) {
		if(csrAction.equals(CmFinoFIX.CSRAction_Cancel))
		{
			FundDistributionInfo fundDistributionInfo = fundValidationService.queryFundDistributionInfo(returnFix.getServiceChargeTransactionLogID());
			UnregisteredTxnInfo unRegTxnInfo = fundDistributionInfo.getUnregisteredTxnInfo();
			FundDefinition fundDefinition=unRegTxnInfo.getFundDefinition();
			fundValidationService.updateAvailableAmount(unRegTxnInfo, null, false, fundDistributionInfo.getDistributedamount());
			if(unRegTxnInfo.getAvailableamount().compareTo(unRegTxnInfo.getAmount())==0){
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE));
			}else{
				unRegTxnInfo.setUnregisteredtxnstatus(Long.valueOf(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN));
			}
			int notificationCode = fundValidationService.updateFailureAttempts(unRegTxnInfo, fundDefinition);
			if(fundDefinition.getMaxfailattemptsallowed()!=-1){
				returnFix.setNumberOfTrailsLeft((fundDefinition.getMaxfailattemptsallowed().intValue()-unRegTxnInfo.getWithdrawalfailureattempt().intValue()));
			}else{
				returnFix.setNumberOfTrailsLeft(99999999);
			}
			
			if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(notificationCode)){
				returnFix.setOneTimePin(fundValidationService.regenerateFAC(unRegTxnInfo));
			}
			returnFix.setInternalErrorCode(NotificationCodes.getInternalErrorCodeFromNotificationCode(notificationCode));
			fundDistributionInfo.setDistributionstatus(Long.valueOf(CmFinoFIX.DistributionStatus_TRANSFER_FAILED));
			fundDistributionInfo.setFailurereason("Pending transaction resolved as failed");
			coreDataWrapper.save(fundDistributionInfo);
			coreDataWrapper.save(unRegTxnInfo);
			((BackendResponse) returnFix).setOnBehalfOfMDN(unRegTxnInfo.getWithdrawalmdn());
			((BackendResponse) returnFix).setSourceMDN(null);
			
			
		}
		else if(csrAction.equals(CmFinoFIX.CSRAction_Complete))
		{
			returnFix.setInternalErrorCode(NotificationCodes.getInternalErrorCodeFromNotificationCode(getBankNotificationCode(returnFix)));
		}
	}
		
	}


