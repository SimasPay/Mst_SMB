package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.FundDistributionInfoDAO;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.dao.PurposeDAO;
import com.mfino.dao.UnRegisteredTxnInfoDAO;
import com.mfino.dao.query.FundDistributionInfoQuery;
import com.mfino.dao.query.PurposeQuery;
import com.mfino.dao.query.UnRegisteredTxnInfoQuery;
import com.mfino.domain.ExpirationType;
import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.Purpose;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.FundStorageService;
import com.mfino.service.FundValidationService;
import com.mfino.service.SubscriberService;
import com.mfino.util.MfinoUtil;
/**
 * THis validation service class is used for all validations regarding Fund allocation and wihtdrawal flows
 * @author Sreenath
 *
 */
@Service("FundValidationServiceImpl")
public class FundValidationServiceImpl implements FundValidationService {

	private static Logger log = LoggerFactory.getLogger(FundValidationServiceImpl.class);
	@Autowired
	@Qualifier("FundStorageServiceImpl")
	private FundStorageService fundStorageService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	public static final String ANY_PARTNER="any";
	
	/**
	 * Checks if the present transaction date is past the expiryTime stored in the record
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	@Override
	public boolean checkExpiry(UnregisteredTxnInfo unRegisteredTxnInfo) {
		Timestamp currentDate = new Timestamp();
		Timestamp fundExpiryTime = unRegisteredTxnInfo.getExpiryTime();
		boolean isTxnExpired = false;

		if (currentDate.after(fundExpiryTime)) {
			isTxnExpired = true;
		}
		
		return isTxnExpired;
	}

	
	/**
	 * returns true if available amt is greater or equalTo than trxn amount
	 * @param trxnAmount
	 * @param availableAmt
	 * @return
	 */
	@Override
	public boolean checkAvaliableAmount(BigDecimal trxnAmount,BigDecimal availableAmt) {
		if(availableAmt.compareTo(trxnAmount)!=-1){
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public int getMaxFailAttempts(UnregisteredTxnInfo unRegisteredTxnInfo){
		//query the fundDefID in the FundDef table and get the max fail attempts allowed.
		FundDefinition fundDefinition=unRegisteredTxnInfo.getFundDefinition();
		Long temp = fundDefinition.getMaxfailattemptsallowed();
		return temp.intValue();
	}
	
	
	/**
	 * Updates the failure attempts when the fundWithdrawal fails and when max no of of failures is reached checks for the
	 * event set to happen in the fundDefinition and returns corresponding notification code for processing
	 * @param unRegisteredTxnInfo
	 * @param fundDefinition
	 * @return
	 */
	@Override
	public Integer updateFailureAttempts(UnregisteredTxnInfo unRegisteredTxnInfo,FundDefinition fundDefinition) {
		
		int currentFailAttempts = unRegisteredTxnInfo.getWithdrawalFailureAttempt();
		currentFailAttempts = currentFailAttempts + 1;
			//increment value  change here
		log.info("Transaction Failed.Updating failure attempt:"+currentFailAttempts);
			unRegisteredTxnInfo.setWithdrawalFailureAttempt(currentFailAttempts);
			fundStorageService.allocateFunds(unRegisteredTxnInfo);
			if(fundDefinition.getMaxfailattemptsallowed()<=currentFailAttempts && fundDefinition.getMaxfailattemptsallowed()!=-1){
				log.info("max fail attempts reached");
				//query the definition table for fund event id and proceed accordingly
				if(fundDefinition.getFundEventsByOnfailedattemptsexceeded().getFundeventtype().equals(CmFinoFIX.FundEventType_Reversal)){//reversal
					log.info("Reversing allocated fund.Reversing transaction with sctlID as:"+unRegisteredTxnInfo.getTransferSCTLId());
					unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED);
					unRegisteredTxnInfo.setReversalReason("Max fail attempts reached");
					fundStorageService.allocateFunds(unRegisteredTxnInfo);
					return CmFinoFIX.NotificationCode_ReverseFundRequestInitaited;
				}else if(fundDefinition.getFundEventsByOnfailedattemptsexceeded().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACAuto)){//auto fac regen
					log.info("regerating fac.....");
					return CmFinoFIX.NotificationCode_RegenFACAuto;
					//regenerateFAC(unRegisteredTxnInfo);
				}else if(fundDefinition.getFundEventsByOnfailedattemptsexceeded().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACManual)){//manual fac regen
					log.info("regenerate fac manually");
					return CmFinoFIX.NotificationCode_RegenFACManual;
				}

			}
			else{
				log.info("Regenerating FAC.......");
				if(fundDefinition.getFundEventsByGenerationofotponfailure().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACAuto)){//auto fac regen
					log.info("regerating fac.....");
					return CmFinoFIX.NotificationCode_RegenFACAuto;
					//regenerateFAC(unRegisteredTxnInfo);
				}else if(fundDefinition.getFundEventsByGenerationofotponfailure().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACManual)){//manual fac regen
					log.info("regenerate fac manually");
					return CmFinoFIX.NotificationCode_RegenFACManual;
				}	
			}
			return -1;
	}
	



	/**
	 * Regenerates the fund access code and stores its digested value in the table
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	@Override
	public String regenerateFAC(UnregisteredTxnInfo unRegisteredTxnInfo) {
	
		String code = fundStorageService.generateFundAccessCode(unRegisteredTxnInfo.getFundDefinition());
		String digestedCode = fundStorageService.generateDigestedFAC(unRegisteredTxnInfo.getWithdrawalMDN(), code);
		unRegisteredTxnInfo.setDigestedPIN(digestedCode);
		
		fundStorageService.allocateFunds(unRegisteredTxnInfo);
		return code;
	}
	
	@Override
	/**
	 * Retruns true if the digested fac created from the present transaction is same as the one stored in the 
	 * unRegistered trxn Info record
	 * @param digestedFAC
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public boolean isValidFAC(String digestedFAC,UnregisteredTxnInfo unRegisteredTxnInfo) {
		//if both are equal return true
		if(digestedFAC.equals(unRegisteredTxnInfo.getDigestedPIN())){
			return true;
		}
		return false;
	}
	
	/**
	 * Creates a new fundDistributionInfo record for fundWithdrawal inquiry request when isDebit is set to true and does appropriate changes 
	 * to the status of the fund stored in the unregistered trxnInfo table.On failure of fund withdrawal this method is called with isDebit false
	 * so that the amount debitted is credited back to unregTrxnInfo table and status is also changed.
	 * @param unRegisteredTxnInfo
	 * @param fundWithdrawalInquiry
	 * @param isDebit
	 * @param amount
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public void updateAvailableAmount(UnregisteredTxnInfo unRegisteredTxnInfo,CMFundWithdrawalInquiry fundWithdrawalInquiry,boolean isDebit,BigDecimal amount){
		
		BigDecimal availableAmount;
		log.info("Updating amount:is debit="+isDebit);
		if(isDebit){
			if(CmFinoFIX.DistributionType_Withdrawal.equals(fundWithdrawalInquiry.getDistributionType())){
				availableAmount = unRegisteredTxnInfo.getAvailableAmount().subtract(fundWithdrawalInquiry.getAmount());
				unRegisteredTxnInfo.setAvailableAmount(availableAmount);
				if(BigDecimal.ZERO.compareTo(availableAmount)==0){
					unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_FUND_COMPLETELY_WITHDRAWN);
				}
			}
			log.info("creating FundDistributionInfo entry......for is debit="+isDebit);
			FundDistributionInfo fundDistributionInfo = new FundDistributionInfo();
			fundDistributionInfo.setDistributedamount(amount);
			fundDistributionInfo.setUnRegisteredTxnInfoByFundAllocationId(unRegisteredTxnInfo);
			fundDistributionInfo.setTransferctid(new BigDecimal(fundWithdrawalInquiry.getTransactionID()));
			fundDistributionInfo.setTransfersctlid(new BigDecimal(fundWithdrawalInquiry.getServiceChargeTransactionLogID()));
			fundDistributionInfo.setDistributiontype(fundWithdrawalInquiry.getDistributionType().longValue());
			fundDistributionInfo.setDistributionstatus(CmFinoFIX.DistributionStatus_INITIALIZED.longValue());
			MfinoServiceProviderDAO mfsDAO= DAOFactory.getInstance().getMfinoServiceProviderDAO();
			MfinoServiceProvider mfsProvider = mfsDAO.getById(1L);
			fundDistributionInfo.setmFinoServiceProviderByMSPID(mfsProvider);
			fundStorageService.allocateFunds(unRegisteredTxnInfo);
			fundStorageService.withdrawFunds(fundDistributionInfo);
			
		}else {
			availableAmount = unRegisteredTxnInfo.getAvailableAmount().add(amount);
			unRegisteredTxnInfo.setAvailableAmount(availableAmount);
			if(availableAmount.equals(unRegisteredTxnInfo.getAmount())){
				unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE);
			}else if(unRegisteredTxnInfo.getUnRegisteredTxnStatus().equals(CmFinoFIX.UnRegisteredTxnStatus_FUND_COMPLETELY_WITHDRAWN)){
				unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN);
			}
			fundStorageService.allocateFunds(unRegisteredTxnInfo);
		}
		log.info("Entry Successful for is debit="+isDebit);
	}

	/**
	 * gets the unregistered trxn ifno row corresponding to our transaction.Query can be executed with only sctlID also
	 * @param withdrawalMDN
	 * @param fac
	 * @param sctlID
	 * @return UnRegisteredTxnInfo
	 */
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public UnregisteredTxnInfo queryUnRegisteredTxnInfo(String withdrawalMDN, String fac,Long sctlID,String enteredPartnerCode){
		
		String mdn = subscriberService.normalizeMDN(withdrawalMDN);
		String digestedFAC = MfinoUtil.calculateDigestPin(mdn, fac);
		log.info("Getting the unregisteredTrxnInfo record...");
		UnRegisteredTxnInfoDAO urtiDAO = DAOFactory.getInstance().getUnRegisteredTxnInfoDAO();
		String[] multiPartnerCode =new String[2];
		multiPartnerCode[0] = ANY_PARTNER;
		multiPartnerCode[1] = enteredPartnerCode;
		Integer[] status = new Integer[2]; 
		status[0]=CmFinoFIX.UnRegisteredTxnStatus_FUNDALLOCATION_COMPLETE;
		status[1]=CmFinoFIX.UnRegisteredTxnStatus_FUND_PARTIALLY_WITHDRAWN;
		UnRegisteredTxnInfoQuery urtiQuery = new UnRegisteredTxnInfoQuery();
		if(sctlID!=null){
			urtiQuery.setTransferSctlId(sctlID);
		}else{
			//if sctl id is not null add to query
			urtiQuery.setMultiStatus(status);
			urtiQuery.setWithdrawalMDN(withdrawalMDN);
			urtiQuery.setMultiPartnerCode(multiPartnerCode);
			urtiQuery.setSortString(CmFinoFIX.CRUnRegisteredTxnInfo.FieldName_CreateTime+":desc");
			urtiQuery.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_FUND_ALLOCATION);
		}
		
		//getting the list of fundAllocations with query on the given withdrawalMDN, status and partnerCode and sorting in descending order of createTime 
		List<UnregisteredTxnInfo> lstUnRegisteredTxnInfos = urtiDAO.get(urtiQuery);
		
		//getting a matching record with the entered fac.If no record is found with given fac returning the oldest record to update failure attempts
		//in priority order of "specific" merchant fund allocation to "Any" merchant fund allocation
		if (CollectionUtils.isNotEmpty(lstUnRegisteredTxnInfos)) {
			UnregisteredTxnInfo unregTxnInfo = lstUnRegisteredTxnInfos.get(0);
			int index = 0;
			
			for(int iter=0;iter<lstUnRegisteredTxnInfos.size();iter++){
				unregTxnInfo = lstUnRegisteredTxnInfos.get(iter);
				
				if(digestedFAC.equals(unregTxnInfo.getDigestedPIN())){
					log.info("Obatined fund with matching fac.The fundAllocation ID is: "+unregTxnInfo.getID());
					return unregTxnInfo;
				}
				if(!(ANY_PARTNER.equalsIgnoreCase(unregTxnInfo.getPartnerCode()))){
					index=iter;
				}
				
			}
			log.info("Matching fund allocation record not found.Updating fund allocation record with id: "+lstUnRegisteredTxnInfos.get(index).getID());
			return lstUnRegisteredTxnInfos.get(index) ;
		}
		log.error("No fund allocation record found");
		return null;
	}
	
	/**
	 * Valdiates entered fac,available amount,fund expiry time and the partner and return false for failure and true for success
	 * @param subscriberMDN
	 * @param result
	 * @param amount
	 * @param partnerCode 
	 * @param fac
	 * @return
	 */
	@Override
	public Integer validate(UnregisteredTxnInfo unRegisteredTxnInfo, XMLResult result, BigDecimal amount, String OTP,FundDefinition fundDefinition, String partnerCode){
			log.info("Validating the Withdraw request ...");
			int notificationCode=-1;
			int validationResult;
			String digestedFac = fundStorageService.generateDigestedFAC(unRegisteredTxnInfo.getWithdrawalMDN(), OTP);
			
			log.info("Validating fac...");
			if(!isValidFAC(digestedFac,unRegisteredTxnInfo)){
				//if entered fac is invalid
				log.error("The FAC entered is invalid.Checking No of failure attempts");
				validationResult=updateFailureAttempts(unRegisteredTxnInfo,fundDefinition);
				if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(validationResult)){
					result.setOneTimePin(regenerateFAC(unRegisteredTxnInfo));
					if(ANY_PARTNER.equalsIgnoreCase(unRegisteredTxnInfo.getPartnerCode())){
						notificationCode = CmFinoFIX.NotificationCode_InvalidFundAccessCodeNewFac;
					}
					else{
						notificationCode = CmFinoFIX.NotificationCode_InvalidFundAccessCodeNewFacSpecificMerchant;
					}
				}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(validationResult)){
					notificationCode = CmFinoFIX.NotificationCode_InvalidFundAccessCode;
				}else{
					notificationCode = CmFinoFIX.NotificationCode_InvalidFundAccessCodeReversal;
				}
				return notificationCode;	
			}
			log.info("Checking fund expiry time");
			if (checkExpiry(unRegisteredTxnInfo)) {
				log.info("Allocated Fund expired");

				if(fundDefinition.getFundEventsByOnfundallocationtimeexpiry().getFundeventtype().equals(CmFinoFIX.FundEventType_Reversal)){//reversal
					log.info("Reversing allocated fund.Reversing transaction with sctlID as:"+unRegisteredTxnInfo.getTransferSCTLId());
					unRegisteredTxnInfo.setUnRegisteredTxnStatus(CmFinoFIX.UnRegisteredTxnStatus_REVERSAL_INITIALIZED);
					unRegisteredTxnInfo.setReversalReason("Fund Expired");
					fundStorageService.allocateFunds(unRegisteredTxnInfo);
					notificationCode=CmFinoFIX.NotificationCode_FundAllocatedExpiredReversal;
				}else if(fundDefinition.getFundEventsByOnfundallocationtimeexpiry().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACAuto)){//auto fac regen
					log.info("regerating fac.....");
					unRegisteredTxnInfo.setExpiryTime(getNewExpiryTime(fundDefinition.getExpirationType()));
					fundStorageService.allocateFunds(unRegisteredTxnInfo);
					result.setOneTimePin(regenerateFAC(unRegisteredTxnInfo));
					notificationCode=CmFinoFIX.NotificationCode_FundAllocatedExpiredNewFac;
				}else if(fundDefinition.getFundEventsByOnfundallocationtimeexpiry().getFundeventtype().equals(CmFinoFIX.FundEventType_RegenerateFACManual)){//manual fac regen
					log.info("regenerate fac manually");
					unRegisteredTxnInfo.setExpiryTime(getNewExpiryTime(fundDefinition.getExpirationType()));
					fundStorageService.allocateFunds(unRegisteredTxnInfo);
					notificationCode=CmFinoFIX.NotificationCode_FundAllocatedExpired;
				}
				return notificationCode;
			}
			log.info("checking available amount...");
			if(!checkAvaliableAmount(amount,unRegisteredTxnInfo.getAvailableAmount())){
				log.error("Requested amount is greater than the fund allocated.Checking No of failure attempts....");
				validationResult=updateFailureAttempts(unRegisteredTxnInfo,fundDefinition);
				if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(validationResult)){
					regenerateFAC(unRegisteredTxnInfo);
					result.setOneTimePin(regenerateFAC(unRegisteredTxnInfo));
					notificationCode = CmFinoFIX.NotificationCode_InsufficientFundsNewFac;
				}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(validationResult)){
					notificationCode = CmFinoFIX.NotificationCode_InsufficientFunds;
				}else{
					notificationCode = CmFinoFIX.NotificationCode_InsufficientFundsReversal;
				}

				return notificationCode;
			}
			log.info("validating entered partner...");
			if(!checkValidPartner(unRegisteredTxnInfo.getPartnerCode(),partnerCode)){
				log.info("Invalid merchant.entered merchant is not eligible for fund transfer");
				validationResult=updateFailureAttempts(unRegisteredTxnInfo,fundDefinition);
				if(CmFinoFIX.NotificationCode_RegenFACAuto.equals(validationResult)){
					regenerateFAC(unRegisteredTxnInfo);
					result.setOneTimePin(regenerateFAC(unRegisteredTxnInfo));
					notificationCode = CmFinoFIX.NotificationCode_NotEligibleMerchantNewFac;
				}else if(CmFinoFIX.NotificationCode_RegenFACManual.equals(validationResult)){
					notificationCode = CmFinoFIX.NotificationCode_NotEligibleMerchant;
				}else{
					notificationCode = CmFinoFIX.NotificationCode_NotEligibleMerchantReversal;
				}
				return notificationCode;
			}
			log.info("Fund withdrawal inquiry validations successful");
				return CmFinoFIX.ResponseCode_Success;

		
	}
	
	/**
	 * Checks if allowed partner code and entered code are the same.If the allowed partner is "any" then all partners are allowed. 
	 * @param allowedPartnerCode
	 * @param enteredPartnerCode
	 * @return
	 */
	@Override
	public boolean checkValidPartner(String allowedPartnerCode, String enteredPartnerCode) {
		if(allowedPartnerCode.equalsIgnoreCase(enteredPartnerCode) || ANY_PARTNER.equalsIgnoreCase(allowedPartnerCode)){
			return true;
		}
		return false;
	}
	
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	/**
	 * Gets the FundDistributionInfo record with the matching sctlID
	 * @param sctlid
	 * @return
	 */
	public FundDistributionInfo queryFundDistributionInfo(Long sctlid) {
		if(sctlid!=null){
		FundDistributionInfoDAO fundDistributionInfoDAO = DAOFactory.getInstance().getFundDistributionInfoDAO();
		FundDistributionInfoQuery fundDistributionInfoQuery = new FundDistributionInfoQuery();
		fundDistributionInfoQuery.setTransferSCTLId(sctlid);
		List<FundDistributionInfo> lstFundDistributionInfos = fundDistributionInfoDAO.get(fundDistributionInfoQuery);
		return lstFundDistributionInfos.get(0);
		}
		return null;
	}
	
	/**
	 * Creates the new expiry time based on data in expirationType table
	 * @param expirationType
	 * @return
	 */
	private Timestamp getNewExpiryTime(ExpirationType expirationType) {
		Long defaultExpirySeconds = 86400L;
		if(expirationType.getExpirytype().equals(CmFinoFIX.ExpiryType_Fund)){
			if(expirationType.getExpirymode().equals(CmFinoFIX.ExpiryMode_DurationInSecs)){
				return new Timestamp(System.currentTimeMillis() + expirationType.getExpiryvalue().intValue() * 1000);
			}else if(expirationType.getExpirymode().equals(CmFinoFIX.ExpiryMode_CutOffTime)){
			}
		}
		log.debug("Could not find Fund related expiry time.setting a deafult of 1 days");
		return new Timestamp(System.currentTimeMillis() +  defaultExpirySeconds* 1000);		
	}

	/**
	 * Validates whether the Purpose and fund definition defined for given partner code 
	 * @param partnerCode
	 * @return
	 */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public boolean validatePurposeAndFundDefinition(String partnerCode) {
		PurposeDAO purposeDAO = DAOFactory.getInstance().getPurposeDAO();
		PurposeQuery purposeQuery = new PurposeQuery();
		String[] multiCode = new String[2];
		multiCode[0] = ANY_PARTNER;
		multiCode[1] = partnerCode;
		purposeQuery.setMultiCode(multiCode);
		List<Purpose> lstPurposes = purposeDAO.get(purposeQuery);
		Purpose purpose = null;
		Purpose defaultAnyPurpose = null;
		Integer purposeCount = 0;
		
		if(CollectionUtils.isNotEmpty(lstPurposes)){
			if(lstPurposes.size()<=2){
				for(int i =0;i<lstPurposes.size();i++){
					if(!ANY_PARTNER.equalsIgnoreCase(lstPurposes.get(i).getCode())){
						purpose = lstPurposes.get(i);
						purposeCount++;
					}
					else{
						defaultAnyPurpose = lstPurposes.get(i);
						purposeCount--;
					}
				}
				if(purposeCount==2){
					log.error("Multiple purposes found for same partner code: "+purpose.getCode());
					return false;
				}
				else if(purposeCount==-2){
					log.error("Multiple purposes found for same partner code: "+ANY_PARTNER);
					return false;
				}
			}
			else{
				log.error("Multiple purposes found for same partner code");
				return false;
			}
		}
		else{
			log.error("No purpose found with given partner code.Default purpose is not set");
			return false;
		}
		
		if(purpose==null){
			purpose = defaultAnyPurpose;
		}
		
		Set<FundDefinition> lstFundDefinitions = purpose.getFundDefinitionFromPurposeID();
		if(CollectionUtils.isNotEmpty(lstFundDefinitions)){
			if(lstFundDefinitions.size() > 1){
				log.error("Multiple fundDefinition found for same purpose ID: "+ purpose.getId());
				return false;
			}
		}
		else{
			log.error("No fundDefinition found with given purpose ID: "+purpose.getId());
			return false;
		}
		return true;
	}

}
