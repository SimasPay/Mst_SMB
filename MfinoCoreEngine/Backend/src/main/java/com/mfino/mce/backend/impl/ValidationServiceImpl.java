package com.mfino.mce.backend.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.isNullorZero;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.domain.Partner;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SystemParameters;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.backend.LedgerService;
import com.mfino.mce.backend.ValidationService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;

/**
 * @author sasidhar
 * All validations required for backend.
 */
public class ValidationServiceImpl extends BaseServiceImpl implements ValidationService{
	
	
	private MfinoUtilService mfinoUtilService;
	
	private SubscriberStatusEventService subscriberStatusEventService;
	
	private SystemParametersService systemParametersService;
	
	protected LedgerService ledgerService;
	
	public LedgerService getLedgerService() {
		return ledgerService;
	}

	public void setLedgerService(LedgerService ledgerService) {
		this.ledgerService = ledgerService;
	}

	public SubscriberStatusEventService getSubscriberStatusEventService() {
		return subscriberStatusEventService;
	}

	public void setSubscriberStatusEventService(
			SubscriberStatusEventService subscriberStatusEventService) {
		this.subscriberStatusEventService = subscriberStatusEventService;
	}

	public MfinoUtilService getMfinoUtilService() {
		return mfinoUtilService;
	}

	public void setMfinoUtilService(MfinoUtilService mfinoUtilService) {
		this.mfinoUtilService = mfinoUtilService;
	}

	public SystemParametersService getSystemParametersService() {
		return systemParametersService;
	}

	public void setSystemParametersService(
			SystemParametersService systemParametersService) {
		this.systemParametersService = systemParametersService;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateFixMessage(Integer messageCode, CFIXMsg requestFix){
		log.info("ValidationServiceImpl : checkRequiredFields :: validate()");
		
		CMBase fixMessage = (CMBase)requestFix;
		BackendResponse responseFix = createResponseObject();
		
		if(fixMessage == null){
			log.info("ValidationServiceImpl : validateFixMessage() : Invalid FIX message");
			responseFix.setDescription("Fix Message is null");
			responseFix.setInternalErrorCode(NotificationCodes.InternalSystemError.getInternalErrorCode());
			return responseFix;
		}
		
		log.debug(fixMessage.DumpFields());
		
		if(fixMessage.getParentTransactionID() == null){
			fixMessage.setParentTransactionID(0L);
		}
		
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateBankAccountSubscriber(Subscriber subscriber, SubscriberMdn subscriberMdn, Pocket pocket, String rPin, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean allowInitialized){
		return validateBankAccountSubscriber(subscriber, subscriberMdn, pocket, rPin, isSource, isMerchant, isValidateBobPocket, allowInitialized, false);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateBankAccountSubscriber(Subscriber subscriber, SubscriberMdn subscriberMdn, Pocket pocket, String rPin, boolean isSource, boolean isMerchant, 
			boolean isValidateBobPocket, boolean allowInitialized, boolean isSystemInitiatedTransaction){
		
		log.info("ValidationServiceImpl :: validateBankAccountSubscriber "+subscriberMdn.getMdn());
		BackendResponse returnFix = createResponseObject();
		
		returnFix = validateSubscriber(subscriber, subscriberMdn, isSource, isMerchant, isValidateBobPocket, allowInitialized, isSystemInitiatedTransaction);
		
		if(!isNullorZero(returnFix.getInternalErrorCode())) {
			return returnFix;
		}
		
		if(pocket == null){
			log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() pocket not found");
			
			if(isSource){
				returnFix.setInternalErrorCode(NotificationCodes.DefaultBankAccountPocketNotFound.getInternalErrorCode());
			}
			else{
				returnFix.setInternalErrorCode(NotificationCodes.DestinationEMoneyPocketNotFound.getInternalErrorCode()); 
			}

			return returnFix;
		}
		
		returnFix = validateSubscriberPin(subscriber, subscriberMdn, rPin, isSource, isSystemInitiatedTransaction);
		if(!isNullorZero(returnFix.getInternalErrorCode())) {
			return returnFix;
		}
				
		if((CmFinoFIX.PocketType_SVA.intValue() == pocket.getPocketTemplateByPockettemplateid().getType()) ||
				(CmFinoFIX.PocketType_NFC.intValue() == pocket.getPocketTemplateByPockettemplateid().getType()) ||
				(CmFinoFIX.PocketType_LakuPandai.intValue() == pocket.getPocketTemplateByPockettemplateid().getType())){
			log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() SVA Pocket");
			
			if(!isValidSVAPocketStatus(subscriber, pocket, isSource))
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() EMoneyPocketNotActive");
				if(isSource){
					returnFix.setInternalErrorCode(NotificationCodes.BankAccountPocketNotActive.getInternalErrorCode()); 
				}
				else{
					returnFix.setInternalErrorCode(NotificationCodes.DestinationBankAccountPocketNotActive.getInternalErrorCode()); 
				}

				return returnFix;
			} 
			else if(isSource && pocket.getRestrictions()	!=	0)
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() SenderEMoneyPocketIsRestricted");
				returnFix.setInternalErrorCode(NotificationCodes.SenderEMoneyPocketIsRestricted.getInternalErrorCode()); 
				return returnFix;
			}
			else if(!isSource && ((pocket.getRestrictions() & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == 1))
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() Dest EMoneyPocketIsRestricted");
				returnFix.setInternalErrorCode(NotificationCodes.DestinationEMoneyPocketIsRestricted.getInternalErrorCode());
				return returnFix;
			}
			
			return returnFix;
		}
		else if(CmFinoFIX.PocketType_BankAccount.intValue() == pocket.getPocketTemplateByPockettemplateid().getType()){

			log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() Bank Pocket#");

			if((pocket.getStatus() != CmFinoFIX.PocketStatus_Active) && !(isSource && pocket.getStatus() == CmFinoFIX.PocketStatus_PendingRetirement))
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() Bank Pocket Not active#");
				if(isSource){
					returnFix.setInternalErrorCode(NotificationCodes.BankAccountPocketNotActive.getInternalErrorCode());
				}
				else{
					returnFix.setInternalErrorCode(NotificationCodes.DestinationBankAccountPocketNotActive.getInternalErrorCode());
				}
			}
			else if(pocket.getRestrictions() != 0)
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() BankAccountIsSuspended");
				if(isSource){
					returnFix.setInternalErrorCode(NotificationCodes.SenderBankPocketIsRestricted.getInternalErrorCode());
				}
				
			}
			else if(pocket.getCardpan()	== null)
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() BankAccountCardPANMissing");
				if(isSource){
					returnFix.setInternalErrorCode(NotificationCodes.BankAccountCardPANMissing.getInternalErrorCode());
				}
				else{
					returnFix.setInternalErrorCode(NotificationCodes.DestinationBankAccountCardPANMissing.getInternalErrorCode());
				}
			}	
			else if(pocket.getPocketTemplateByPockettemplateid().getBankcode() == null)
			{
				log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() PocketTemplateBankCodeMissing");
				if(isSource){
					returnFix.setInternalErrorCode(NotificationCodes.PocketTemplateBankCodeMissing.getInternalErrorCode());
				}
				else{
					returnFix.setInternalErrorCode(NotificationCodes.DestinationPocketTemplateBankCodeMissing.getInternalErrorCode());
				}
			}	
			else
			{
				return	returnFix;
			}
		}
		else{
			log.debug("ValidationServiceImpl :: validateBankAccountSubscriber() Invalid Pocket");
			if(isSource){
				returnFix.setInternalErrorCode(NotificationCodes.DefaultBankAccountPocketNotFound.getInternalErrorCode()); 
			}
			else{
				returnFix.setInternalErrorCode(NotificationCodes.DestinationEMoneyPocketNotFound.getInternalErrorCode()); 
			}

			return returnFix;
		}
		
		return returnFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)

	public BackendResponse validateSubscriberPin(Subscriber subscriber, SubscriberMdn subscriberMdn, String rPin, boolean isSource, boolean isSystemInitiatedTransaction){
		BackendResponse returnFix = createResponseObject();

		// Modified to do the mPin validation for all the transactions irrespective of source pocket type.
		// Skip the mPin validation for System Initiated Transactions
		if((isSource) && !("mFino260".equals(rPin)) && !(isSystemInitiatedTransaction)){
			SystemParameters pinLengthParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.PIN_LENGTH);
			int pinlength = Integer.parseInt(pinLengthParam.getParametervalue());
			String pinValid = mfinoUtilService.validatePin( subscriberMdn.getMdn(), rPin, subscriberMdn.getDigestedpin(), pinlength);

			if(pinValid.equals(GeneralConstants.LOGIN_RESPONSE_SUCCESS)){
				if(subscriberMdn.getWrongpincount() > 0){
					subscriberMdn.setWrongpincount(0);
					coreDataWrapper.save(subscriberMdn);
				}
			}
			else if(pinValid.equals(GeneralConstants.LOGIN_RESPONSE_FAILED)){
				log.error("Invalid PIN entered MDN="+subscriberMdn.getMdn());
				subscriberMdn.setWrongpincount(subscriberMdn.getWrongpincount() + 1);
				SystemParameters wrongPinCountParam = coreDataWrapper.getSystemParameterByName(SystemParameterKeys.MAX_WRONGPIN_COUNT);
				int maxWrongPinCount = (wrongPinCountParam.getParametervalue() != null) ? Integer.parseInt(wrongPinCountParam.getParametervalue()) : 3; 
				if (subscriberMdn.getWrongpincount() >= maxWrongPinCount) {
					Timestamp now = new Timestamp();
					subscriberMdn.setRestrictions(subscriberMdn.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
					subscriberMdn.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriberMdn.setStatustime(now);
					subscriber.setRestrictions(subscriber.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriber.setStatustime(now);
					subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);

					// Check if the Subscriber is of Partner type
					if (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType())) {
						Set<Partner> setPartners = subscriber.getPartners();
						if (CollectionUtils.isNotEmpty(setPartners)) {
							PartnerDAO partnerDAO = DAOFactory.getInstance().getPartnerDAO();
							Partner partner = setPartners.iterator().next();
							partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
							partnerDAO.save(partner);
						}
					}
				}
				coreDataWrapper.save(subscriberMdn);
				coreDataWrapper.save(subscriber);
				returnFix.setNumberOfTrailsLeft((int)(maxWrongPinCount - subscriberMdn.getWrongpincount()));
				returnFix.setInternalErrorCode(NotificationCodes.WrongPINSpecified.getInternalErrorCode());
				//return returnFix;
			}else {
				//Pin error due to internal Reasons
				returnFix.setInternalErrorCode(NotificationCodes.InternalLoginError.getInternalErrorCode());
			}
		}
		return returnFix;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateSubscriber(Subscriber subscriber, SubscriberMdn subscriberMdn, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean isAllowInitialized){
		return validateSubscriber(subscriber, subscriberMdn, isSource, isMerchant, isValidateBobPocket, isAllowInitialized, false);
	}

	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateSubscriber(Subscriber subscriber, SubscriberMdn subscriberMdn, boolean isSource, boolean isMerchant, boolean isValidateBobPocket, boolean isAllowInitialized, boolean isSystemInitiatedTransaction){
		
		log.info("ValidationServiceImpl :: validateSubscriber() "+ (subscriberMdn != null ? subscriberMdn.getMdn() : "") + ", isSource="+isSource);
		
		BackendResponse responseFix = createResponseObject();
		
		if(subscriberMdn == null)
		{
			log.debug("Subscriber Not found");
			if(isSource){
				responseFix.setInternalErrorCode(NotificationCodes.MDNNotFound.getInternalErrorCode()); 
			}
			else{
				responseFix.setInternalErrorCode(NotificationCodes.DestinationMDNNotFound.getInternalErrorCode()); 
			}
		}
		else if(!isValidSubscriberAndMDNStatus(subscriber, subscriberMdn, isSource))
		{
			log.debug("ValidationServiceImpl :: validateSubscriber() MDNNotFound");
			if(isSource){
				responseFix.setInternalErrorCode(NotificationCodes.MDNIsNotActive.getInternalErrorCode()); 
			}
			else{
				responseFix.setInternalErrorCode(NotificationCodes.DestinationMDNIsNotActive.getInternalErrorCode()); 
			}
		}
		else if((isSource) && (isNullOrEmpty(subscriberMdn.getDigestedpin())) && !(isSystemInitiatedTransaction)){
			log.debug("ValidationServiceImpl :: validateSubscriber() PIN Empty");
			
			responseFix.setInternalErrorCode(NotificationCodes.PINResetRequired.getInternalErrorCode()); 
		}
		else if((isSource) && !((subscriber.getRestrictions() == 0) && (subscriberMdn.getRestrictions() == 0))){
			if((subscriber.getRestrictions() != CmFinoFIX.SubscriberRestrictions_NoFundMovement.intValue())
					&& (subscriberMdn.getRestrictions() != CmFinoFIX.SubscriberRestrictions_NoFundMovement.intValue())){
				log.debug("ValidationServiceImpl :: validateSubscriber() MDN restricted");
				responseFix.setInternalErrorCode(NotificationCodes.MDNIsRestricted.getInternalErrorCode());
			}
		}
		else if((!isSource) && (((subscriberMdn.getRestrictions() & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == 1) && 
				(subscriberMdn.getRestrictions() & CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == 1)){
			
			log.debug("ValidationServiceImpl :: validateSubscriber() Destination MDN restricted");
			responseFix.setInternalErrorCode(NotificationCodes.DestinationMDNIsRestricted.getInternalErrorCode()); 
		}
		else
		{
			if(isValidateBobPocket){
				
				Pocket defaultBobPocket = coreDataWrapper.getDefaultBobPocketByMdnId(subscriberMdn.getId().longValue());
				if(defaultBobPocket == null){
					log.debug("ValidationServiceImpl :: validateSubscriber() BOB Pocket Not found");
					if(isSource){
						responseFix.setInternalErrorCode(NotificationCodes.BOBPocketNotFound.getInternalErrorCode());
					}
					else{
						responseFix.setInternalErrorCode(NotificationCodes.DestinationBOBPocketNotFound.getInternalErrorCode());
					}
				}
				else{
					if((defaultBobPocket.getStatus()	==	CmFinoFIX.PocketStatus_Retired) || 
						(!isAllowInitialized && defaultBobPocket.getStatus() == CmFinoFIX.PocketStatus_Initialized))
						{
							log.debug("ValidationServiceImpl :: validateSubscriber() BOB Pocket Not Active");
							
							if(isSource)
								responseFix.setInternalErrorCode(NotificationCodes.BOBPocketNotActive.getInternalErrorCode());
							else
								responseFix.setInternalErrorCode(NotificationCodes.DestinationBOBPocketNotActive.getInternalErrorCode());
						} 
						else
						{
							if(isSource	&& defaultBobPocket.getRestrictions() != 0)
							{
								log.debug("ValidationServiceImpl :: validateSubscriber() BOB Pocket restricted");
								responseFix.setInternalErrorCode(NotificationCodes.BOBPocketIsRestricted.getInternalErrorCode());
							}	
							else if(!isSource && ((defaultBobPocket.getRestrictions() &	CmFinoFIX.SubscriberRestrictions_AbsoluteLocked) == 1))
							{
								log.debug("ValidationServiceImpl :: validateSubscriber() Dest bob pocket restricted");
								responseFix.setInternalErrorCode(NotificationCodes.DestinationBOBPocketIsRestricted.getInternalErrorCode());
							}	
							else
							{
								if(isSource	&& defaultBobPocket.getPocketTemplateByPockettemplateid().getOperatorcode() == null)
								{
									log.debug("ValidationServiceImpl :: validateSubscriber() Operator code missing");
									responseFix.setInternalErrorCode(NotificationCodes.PocketTemplateOperatorCodeMissing.getInternalErrorCode());
								}
							}
						}
					}
				}
		}
		responseFix.setSourceMDN(subscriberMdn.getMdn());
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validateRisksAndLimits(Pocket sourcePocket, Pocket destinationPocket, BigDecimal debitAmount, BigDecimal creditAmount,
			SubscriberMdn srcSubscriberMdn, SubscriberMdn destSubscriberMdn){

		BackendResponse responseFix = createResponseObject();
		if ((CmFinoFIX.SubscriberType_Partner.intValue() == srcSubscriberMdn.getSubscriber().getType()) && 
				(CmFinoFIX.PocketType_BankAccount.intValue() == sourcePocket.getPocketTemplateByPockettemplateid().getType()) ) {
			responseFix.setInternalErrorCode(null);
		} else {
			responseFix = validateRisksAndLimits(sourcePocket, debitAmount, true);
		}
			
		return responseFix;
	}
	
	/**
	 * The pocket limits are checked for velocity rules and transaction limits.
	 * If isSource is true then returns notifications for source else the notification for receiver are provided.
	 * 
	 * Returns null if pocket or amount is null.
	 *  
	 * @param pocket
	 * @param amount
	 * @param isSource
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	protected BackendResponse validateRisksAndLimits(Pocket pocket, BigDecimal amount, boolean isSource){
		BackendResponse responseFix = createResponseObject();
		Timestamp now = new Timestamp();
		if(pocket==null || amount==null){
			return null;
		}
		
		if(pocket.getPocketTemplateByPockettemplateid().getIscollectorpocket()){
			responseFix.setInternalErrorCode(null);
//			pocket.setLastTransactionTime(now);
			
			return responseFix;
		}
		
		if(pocket.getPocketTemplateByPockettemplateid().getIssuspencepocket()){
			responseFix.setInternalErrorCode(null);
//			pocket.setLastTransactionTime(now);
			
			return responseFix;
		}
		
		if(pocket.getPocketTemplateByPockettemplateid().getIssystempocket()){
			responseFix.setInternalErrorCode(null);
//			pocket.setLastTransactionTime(now);
			
			return responseFix;
		}
		/*
		 * Initializations
		 */
		if(pocket.getLasttransactiontime() == null){
			
			pocket.setLasttransactiontime(now);
			pocket.setCurrentdailyexpenditure(BigDecimal.valueOf(0));
			pocket.setCurrentdailytxnscount(0);
			pocket.setCurrentmonthlyexpenditure(BigDecimal.valueOf(0));
			pocket.setCurrentmonthlytxnscount(0);
			pocket.setCurrentweeklyexpenditure(BigDecimal.valueOf(0));
			pocket.setCurrentweeklytxnscount(0);
		}else{
			// Reset the pocket counters based on the last transaction date 
			Calendar calendarNow = Calendar.getInstance();
			calendarNow.setTimeInMillis(now.getTime());
			Calendar lastTransationTime = Calendar.getInstance();
			lastTransationTime.setTimeInMillis(pocket.getLasttransactiontime().getTime()); 
			if (calendarNow.get(Calendar.DATE) != lastTransationTime.get(Calendar.DATE)) {
				pocket.setCurrentdailytxnscount(0);
				pocket.setCurrentdailyexpenditure(BigDecimal.valueOf(0));
			} 
			
			if (lastTransationTime.get(Calendar.DAY_OF_WEEK)>calendarNow.get(Calendar.DAY_OF_WEEK) || 
					(calendarNow.getTimeInMillis() - lastTransationTime.getTimeInMillis()) > 7  * 24 * 60 * 60 * 1000) {
				pocket.setCurrentweeklytxnscount(0);
				pocket.setCurrentweeklyexpenditure(BigDecimal.valueOf(0));
			} 
			
			if (calendarNow.get(Calendar.MONTH) != lastTransationTime.get(Calendar.MONTH)) {
				pocket.setCurrentmonthlytxnscount(0);
				pocket.setCurrentmonthlyexpenditure(BigDecimal.valueOf(0));
			} 
		}
		
		if(pocket.getPocketTemplateByPockettemplateid().getType() == CmFinoFIX.PocketType_SVA){
			if(null == pocket.getCurrentbalance()){
				pocket.setCurrentbalance(new BigDecimal(0));
			}
		}
		
		
		
//		/*Was there any transaction in last one day*/
//		if((now.getTime() - pocket.getLastTransactionTime().getTime()) > 24 * 60 * 60 * 1000){
//			pocket.setCurrentDailyTransactionsCount(0);
//			pocket.setCurrentDailyExpenditure(BigDecimal.valueOf(0));
//		}
//		
//		if((now.getTime() - pocket.getLastTransactionTime().getTime()) > 7  * 24 * 60 * 60 * 1000){
//			pocket.setCurrentWeeklyTransactionsCount(0);
//			pocket.setCurrentWeeklyExpenditure(BigDecimal.valueOf(0));
//		}
//		
//		if((now.getTime() - pocket.getLastTransactionTime().getTime()) > 31  * 24 * 60 * 60 * 1000){
//			pocket.setCurrentMonthlyTransactionsCount(0);
//			pocket.setCurrentMonthlyExpenditure(BigDecimal.valueOf(0));
//		}
		Integer notificationCode = null;
		
		if(amount.compareTo(BigDecimal.valueOf(0)) != 1){
			notificationCode = NotificationCodes.TransactionFailedDueToInvalidAmount.getInternalErrorCode();
		}
		else if(pocket.getRestrictions() != 0){
			if(isSource){
				notificationCode = NotificationCodes.SenderSVAPocketRestricted.getInternalErrorCode();
			}else{
				notificationCode = NotificationCodes.ReceiverSVAPocketRestricted.getInternalErrorCode();
			}
		}
		else if(isSource && (pocket.getPocketTemplateByPockettemplateid().getMintimebetweentransactions() > 0) && 
				(pocket.getPocketTemplateByPockettemplateid().getMintimebetweentransactions()*1000 > (now.getTime() - pocket.getLasttransactiontime().getTime()))){
			notificationCode = NotificationCodes.TransactionFailedDueToTimeLimitTransactionReached.getInternalErrorCode();
		}
		else if((pocket.getPocketTemplateByPockettemplateid().getType() ==	CmFinoFIX.PocketType_SVA || 
				 pocket.getPocketTemplateByPockettemplateid().getType() ==	CmFinoFIX.PocketType_LakuPandai)
				&& ledgerService.isImmediateUpdateRequiredForPocket(pocket)){
			if(isSource){
				if(((pocket.getCurrentbalance().subtract(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMinimumstoredvalue())) == -1)){
					notificationCode = NotificationCodes.BalanceTooLow.getInternalErrorCode();
				}
			}
			else if(((pocket.getCurrentbalance().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaximumstoredvalue())) == 1)){
				notificationCode = NotificationCodes.BalanceTooHigh.getInternalErrorCode();
			}
		}
		if(amount.compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountpertransaction()) == 1){
			notificationCode = NotificationCodes.TransferAmountAboveMaximumAllowed.getInternalErrorCode();
			responseFix.setMaxTransactionLimit(pocket.getPocketTemplateByPockettemplateid().getMaxamountpertransaction());
		}
		else if(amount.compareTo(pocket.getPocketTemplateByPockettemplateid().getMinamountpertransaction()) == -1){ 
			notificationCode = NotificationCodes.TransferAmountBelowMinimumAllowed.getInternalErrorCode();
			responseFix.setMinTransactionLimit(pocket.getPocketTemplateByPockettemplateid().getMinamountpertransaction());
		}
		else if(pocket.getCurrentdailytxnscount()	>=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionsperday()){
			notificationCode = NotificationCodes.AboveDailyTransactionsCountLimit.getInternalErrorCode();
		}
		else if(pocket.getCurrentweeklytxnscount()	>=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionsperweek()){
			notificationCode = NotificationCodes.AboveWeeklyTransactionsCountLimit.getInternalErrorCode();
		}
		else if(pocket.getCurrentmonthlytxnscount()	>=	pocket.getPocketTemplateByPockettemplateid().getMaxtransactionspermonth()){
			notificationCode = NotificationCodes.AboveMonthlyTransactionsCountLimit.getInternalErrorCode();
		}
		else if(pocket.getCurrentdailyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountperday()) == 1){
			notificationCode = NotificationCodes.AboveDailyExpenditureLimit.getInternalErrorCode();
		}
		else if(pocket.getCurrentweeklyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountperweek()) == 1){
			notificationCode = NotificationCodes.AboveWeeklyExpenditureLimit.getInternalErrorCode();
		}
		else if(pocket.getCurrentmonthlyexpenditure().add(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMaxamountpermonth()) == 1){
			notificationCode = NotificationCodes.AboveMonthlyExpenditureLimit.getInternalErrorCode();
		}
	
		responseFix.setInternalErrorCode(notificationCode);
		pocket.setLasttransactiontime(now);
		
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validatePocketsForBulkTransfer(Pocket srcPocket, Pocket destPocket, BigDecimal amount) {
		Integer notificationCode = null;
		BackendResponse responseFix = createResponseObject();
		Timestamp now = new Timestamp();
		
		if( srcPocket==null || amount==null || destPocket==null){
			return null;
		}
		
		if(amount.compareTo(BigDecimal.valueOf(0)) != 1) {
			notificationCode = NotificationCodes.TransactionFailedDueToInvalidAmount.getInternalErrorCode();
		}
		else if(srcPocket.getRestrictions() != 0) {
			notificationCode = NotificationCodes.SenderSVAPocketRestricted.getInternalErrorCode();
		}
		else if (destPocket.getRestrictions() != 0) {
			notificationCode = NotificationCodes.ReceiverSVAPocketRestricted.getInternalErrorCode();
		}
		else if((srcPocket.getPocketTemplateByPockettemplateid().getType()	==	CmFinoFIX.PocketType_SVA ||
				 srcPocket.getPocketTemplateByPockettemplateid().getType()==	CmFinoFIX.PocketType_LakuPandai) 
				&& ledgerService.isImmediateUpdateRequiredForPocket(srcPocket)) {
			if(((srcPocket.getCurrentbalance().subtract(amount).compareTo(srcPocket.getPocketTemplateByPockettemplateid().getMinimumstoredvalue())) == -1)) {
				notificationCode = NotificationCodes.BalanceTooLow.getInternalErrorCode();
			}
		}
	
		responseFix.setInternalErrorCode(notificationCode);
//		srcPocket.setLastTransactionTime(now);
//		destPocket.setLastTransactionTime(now);
		
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validatePocketsForChargeDistribution(Pocket pocket, BigDecimal amount, boolean isSource) {
		Integer notificationCode = null;
		BackendResponse responseFix = createResponseObject();
		Timestamp now = new Timestamp();
		
		if( pocket==null || amount==null ){
			return null;
		}
		
		if(pocket.getPocketTemplateByPockettemplateid().getIscollectorpocket()){
			responseFix.setInternalErrorCode(null);
			pocket.setLasttransactiontime(now);
			return responseFix;
		}
		
		if(amount.compareTo(BigDecimal.valueOf(0)) != 1) {
			notificationCode = NotificationCodes.TransactionFailedDueToInvalidAmount.getInternalErrorCode();
		}
		else if(pocket.getRestrictions() != 0) {
			if(isSource)
			{
				notificationCode = NotificationCodes.SenderSVAPocketRestricted.getInternalErrorCode();
			}
			else
			{
				notificationCode = NotificationCodes.ReceiverSVAPocketRestricted.getInternalErrorCode();
			}
		}
		else if(isSource && (pocket.getPocketTemplateByPockettemplateid().getType() ==	CmFinoFIX.PocketType_SVA || 
				             pocket.getPocketTemplateByPockettemplateid().getType() ==	CmFinoFIX.PocketType_LakuPandai)
				&& ledgerService.isImmediateUpdateRequiredForPocket(pocket)) 
		{
			if(((pocket.getCurrentbalance().subtract(amount).compareTo(pocket.getPocketTemplateByPockettemplateid().getMinimumstoredvalue())) == -1)) 
			{
				notificationCode = NotificationCodes.BalanceTooLow.getInternalErrorCode();
			}
		}
	
		responseFix.setInternalErrorCode(notificationCode);
		pocket.setLasttransactiontime(now);		
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validatePct(PendingCommodityTransfer pct,Pocket sourcePocket, Pocket destinationPocket, SubscriberMdn sourceMdn, SubscriberMdn destMdn){
		
		BackendResponse responseFix = createResponseObject();
		
		if((sourcePocket != null) && !(pct.getSourcemdn().equals(sourceMdn.getMdn())
				&&sourcePocket.equals(pct.getPocket())))	{
			log.info("pct with ID="+pct.getId()+" invalidated.Supplied sourcepocket with ID="+sourcePocket.getId()+" pct sourcepocketid="+pct.getPocket().getId() +" do not match");
			responseFix.setInternalErrorCode(NotificationCodes.TransferIDDoesNotBelongToSourceMDN.getInternalErrorCode());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_Expired);
			return responseFix;
		}else if(destMdn!=null
				&&pct.getDestmdn()!=null
				&&(!pct.getDestmdn().equals(destMdn.getMdn()))){
			log.info("pct with ID="+pct.getId()+" invalidated.Supplied destMDN with="+destMdn.getMdn()+" pct destmdn="+pct.getDestmdn()+" do not match");
			responseFix.setInternalErrorCode(NotificationCodes.TransferRecordNotFound.getInternalErrorCode());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_Expired);
		}else if(destinationPocket!=null
				&&pct.getDestpocketid()!=null
				&&(!pct.getDestpocketid().equals(destinationPocket.getId()))){
			log.info("pct with ID="+pct.getId()+" invalidated.Supplied destpocket with ID="+destinationPocket.getId()+" pct destpocketid="+pct.getDestpocketid() +" do not match");
			responseFix.setInternalErrorCode(NotificationCodes.TransferRecordNotFound.getInternalErrorCode());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_Expired);
		}else if((sourcePocket != null) && (CmFinoFIX.PocketType_SVA.equals(sourcePocket.getPocketTemplateByPockettemplateid().getType()) || 
				CmFinoFIX.PocketType_LakuPandai.equals(sourcePocket.getPocketTemplateByPockettemplateid().getType())) 
				&& ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)
				&&((sourcePocket.getCurrentbalance().subtract(pct.getAmount().add(pct.getCharges())).compareTo(sourcePocket.getPocketTemplateByPockettemplateid().getMinimumstoredvalue())) == -1)){
			log.info("pct with ID="+pct.getId()+" invalidated. Balance will be below the limit if the trxn is allowed");
			responseFix.setInternalErrorCode(NotificationCodes.BalanceTooLow.getInternalErrorCode());
					pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_EMoneySourcePocketLimits);
					pct.setNotificationcode(CmFinoFIX.NotificationCode_BalanceTooLow);
		}else if((destinationPocket != null) && (CmFinoFIX.PocketType_SVA.equals(destinationPocket.getPocketTemplateByPockettemplateid().getType()) ||
				CmFinoFIX.PocketType_LakuPandai.equals(destinationPocket.getPocketTemplateByPockettemplateid().getType()))
				&&((destinationPocket.getCurrentbalance().add(pct.getAmount()).compareTo(destinationPocket.getPocketTemplateByPockettemplateid().getMaximumstoredvalue())) == 1)){
			log.info("pct with ID="+pct.getId()+" invalidated. Balance will be above the limit if the trxn is allowed");
			responseFix.setInternalErrorCode(NotificationCodes.BalanceTooHigh.getInternalErrorCode());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_EMoneySourcePocketLimits);//change it destination pocket limits
			pct.setNotificationcode(CmFinoFIX.NotificationCode_BalanceTooHigh);			
		}else{				
		responseFix.setInternalErrorCode(NotificationCodes.Success.getInternalErrorCode());
		}
		return responseFix;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public BackendResponse validatePctForSourcePocketBalance(PendingCommodityTransfer pct,Pocket sourcePocket) {
		BackendResponse responseFix = createResponseObject();
		
		if((CmFinoFIX.PocketType_SVA.equals(sourcePocket.getPocketTemplateByPockettemplateid().getType()) || 
				CmFinoFIX.PocketType_LakuPandai.equals(sourcePocket.getPocketTemplateByPockettemplateid().getType()))
			&& ledgerService.isImmediateUpdateRequiredForPocket(sourcePocket)
			&&((sourcePocket.getCurrentbalance().subtract(pct.getAmount().add(pct.getCharges())).compareTo(sourcePocket.getPocketTemplateByPockettemplateid().getMinimumstoredvalue())) == -1)){
			log.info("pct with ID="+pct.getId()+" invalidated. Balance will be below the limit if the trxn is allowed");
			responseFix.setInternalErrorCode(NotificationCodes.BalanceTooLow.getInternalErrorCode());
			pct.setTransferfailurereason(CmFinoFIX.TransferFailureReason_EMoneySourcePocketLimits);
			pct.setNotificationcode(CmFinoFIX.NotificationCode_BalanceTooLow);
		} 
		else {				
			responseFix.setInternalErrorCode(NotificationCodes.Success.getInternalErrorCode());
		}
		return responseFix;
	}
	
	protected boolean isValidSVAPocketStatus(Subscriber subscriber, Pocket pocket, boolean isSource)
	{
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()) &&
				(pocket.getStatus() == CmFinoFIX.PocketStatus_OneTimeActive))
		{
			//This is only allowed for Not Registered
			return true;
		}		
		
		if((pocket.getStatus() != CmFinoFIX.PocketStatus_Active) && 
		!(isSource && pocket.getStatus() == CmFinoFIX.PocketStatus_PendingRetirement))
		{
			return false;
		}
		return true;
	}
	
	protected boolean isValidSubscriberAndMDNStatus(Subscriber subscriber, SubscriberMdn subscriberMdn, boolean isSource)
	{
		if((CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()) &&
				CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMdn.getStatus())) ||
				(CmFinoFIX.SubscriberStatus_InActive.equals(subscriberMdn.getStatus()) &&
						CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMdn.getRestrictions())))
		{
			return true;
		}
		if((CmFinoFIX.SubscriberStatus_Active.intValue() != subscriber.getStatus()) &&
		(CmFinoFIX.SubscriberStatus_PendingRetirement.intValue() != subscriber.getStatus()) &&
		(CmFinoFIX.MDNStatus_Active.intValue() != subscriberMdn.getStatus()) &&
		(CmFinoFIX.MDNStatus_PendingRetirement.intValue() != subscriberMdn.getStatus()))
		{
			return false;
		}
		return true;
	}
}


