package com.mfino.transactionapi.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.MfinoUtilService;
import com.mfino.service.PartnerService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.service.TransactionApiValidationService;
/**
 * This class is used for all source and destination pocket ,mdn and partner validations for
 * the all the handlers.There should be no db calls in this class
 * @author Sreenath
 *
 */
@Service("TransactionApiValidationServiceImpl")
public class TransactionApiValidationServiceImpl implements TransactionApiValidationService{
	
	private static Logger log = LoggerFactory.getLogger(TransactionApiValidationServiceImpl.class);
	
	@Autowired
	@Qualifier("MfinoUtilServiceImpl")
	private MfinoUtilService mfinoUtilService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;

	/**
	 * Validates whether the the subscriberMDN is allowed to perform funds transfer as source
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberAsSource(SubscriberMDN subscriberMDN){
		if(subscriberMDN==null){
			log.error("SourceMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()))
		{
			log.error("Source Subscriber with mdn: "+subscriberMDN.getMDN()+"is not registered");
			return CmFinoFIX.NotificationCode_SubscriberNotRegistered;
		}
		
		// First Restrictions should be checked as we are allowing InActive Subscribers(of no activity) to login
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions())) &&
				!(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()))) {
			log.error("Source Subscriber with mdn: "+subscriberMDN.getMDN()+"is restricted");
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		
		if( !(CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())) &&
				!(CmFinoFIX.MDNStatus_InActive.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())) ){
			log.error("Source Subscriber with mdn: "+subscriberMDN.getMDN()+"is not active");
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		
		return CmFinoFIX.ResponseCode_Success;
		
	}
	
	/**
	 * validates whether the subscriberMDN is allowed to receive funds as destination
	 * @param string mdn
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public Integer validateSubscriberAsDestinationString(String mdn){
	    SubscriberMDN subscriberMDN = subscriberMdnService.getByMDN(mdn);

		Integer result = validateSubscriberAsDestination(subscriberMDN);
		return result;
		
	}

	/**
	 * validates whether the subscriberMDN is allowed to receive funds as destination
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberAsDestination(SubscriberMDN subscriberMDN){
		if(subscriberMDN==null){
			return CmFinoFIX.NotificationCode_DestinationMDNNotFound;
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriber.getStatus()))
		{
			return CmFinoFIX.NotificationCode_SubscriberNotRegistered;
		}
		if (!CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())
				&&!CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())) {
			if(!(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()))){
				return CmFinoFIX.NotificationCode_DestinationMDNIsNotActive;
			}
		} 

		if (CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriberMDN.getRestrictions())) {
			return CmFinoFIX.NotificationCode_DestinationMDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * validates if a partner is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validatePartnerMDN(SubscriberMDN subscriberMDN) {
		if(subscriberMDN==null){
			log.error("Partner SubscriberMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;

		}
		Partner partner = partnerService.getPartner(subscriberMDN);
		
		if(partner==null){
			log.error("Partner with mdn: "+subscriberMDN.getMDN()+"not found");
			return CmFinoFIX.NotificationCode_PartnerNotFound;
		}
		Subscriber subscriber = partner.getSubscriber();	

		if (!(CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())&&CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus()))) {
			log.error("Partner with mdn: "+subscriberMDN.getMDN()+"is not active");
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriber.getRestrictions())&&CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			log.error("Partner with mdn: "+subscriberMDN.getMDN()+"is restricted");
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
		
	}
	
	/**
	 * validates if an agent is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateAgentMDN(SubscriberMDN subscriberMDN) {
		if(subscriberMDN==null){
			log.error("Agent SubscriberMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;

		}
		boolean isAgent=true;
		Partner partner=null;
		Subscriber agentsubscriber=subscriberMDN.getSubscriber();

		if(!(agentsubscriber.getType().equals(CmFinoFIX.SubscriberType_Partner))){
			if(isAgent)
				return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found			
		}
		if(partner==null){
			Set<Partner> agentPartner = agentsubscriber.getPartnerFromSubscriberID();
			if(!agentPartner.isEmpty()){
				partner=agentPartner.iterator().next();
			}

		}
		if(partner==null){
			if(isAgent)
				return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
			return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found			
		}
		if(isAgent && !partnerService.isAgentType(partner.getBusinessPartnerType()) && !(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice))){
			//return CmFinoFIX.NotificationCode_PartnerNotFound;//agent Not found
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		if (!(CmFinoFIX.SubscriberStatus_Active.equals(agentsubscriber.getStatus())&&CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus()))) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(agentsubscriber.getRestrictions())&&CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
 		
	}
	
	/**
	 * validates if a teller is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateTellerMDN(SubscriberMDN subscriberMDN){
		if(subscriberMDN==null){
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		Partner partner = partnerService.getPartner(subscriberMDN);
		if(partner==null){
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		if(!(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_BranchOffice))){
			return CmFinoFIX.NotificationCode_PartnerNotFound;//teller Not found
		}
		
		Subscriber subscriber = partner.getSubscriber();	
		if (!(CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())&&CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus()))) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriber.getRestrictions())&&CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * validates if a merchant is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateMerchantMDN(SubscriberMDN subscriberMDN){
		if(subscriberMDN==null){
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		Partner partner = partnerService.getPartner(subscriberMDN);
		if(partner==null){
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		if(!(partner.getBusinessPartnerType().equals(CmFinoFIX.BusinessPartnerType_Merchant))){
			return CmFinoFIX.NotificationCode_PartnerNotFound;//Merchant Not found
		}
		
		Subscriber subscriber = partner.getSubscriber();	
		if (!(CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())&&CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus()))) {
			return CmFinoFIX.NotificationCode_MDNIsNotActive;
		}
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriber.getRestrictions())&&CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
		
	}
	
	/**
	 * Validates the input partner for not null and calls the validatePartnerMDN method to validate partner details
	 * by extracting the subscriberMDN from the not null partner
	 * @param partner
	 * @return
	 */
	public Integer validatePartnerByPartnerType(Partner partner){
		if(partner==null){
			log.error("Partner obtained null");
			return CmFinoFIX.NotificationCode_PartnerNotFound;
		}
		else{
			SubscriberMDN subscriberMDN = partner.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
			return validatePartnerMDN(subscriberMDN);
		}
	}
	
	/**
	 * Validates the input agent for not null and calls the validateAgentMDN method to validate agent details
	 * by extracting the subscriberMDN from the not null agent
	 * @param agent
	 * @return
	 */
	public Integer validateAgentByPartnerType(Partner agent){
		if(agent==null){
			log.error("Agent obtained null");
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		else{
			SubscriberMDN subscriberMDN = agent.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
			return validateAgentMDN(subscriberMDN);
		}
	}
	
	/**
	 * Validates the input teller for not null and calls the validateTellerMDN method to validate teller details
	 * by extracting the subscriberMDN from the not null teller
	 * @param teller
	 * @return
	 */
	public Integer validateTellerByPartnerType(Partner teller){
		if(teller==null){
			log.error("Teller partner obtained null");
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		else{
			SubscriberMDN subscriberMDN = teller.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
			return validateTellerMDN(subscriberMDN);
		}
	}
	
	/**
	 * Validates the input merchant for not null and calls the validateMerchantMDN method to validate Merchant details
	 * by extracting the subscriberMDN from the not null merchant
	 * @param merchant
	 * @return
	 */
	public Integer validateMerchantByPartnerType(Partner merchant){
		if(merchant==null){
			log.error("Merchant obtained null");
			return CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		}
		else{
			SubscriberMDN subscriberMDN = merchant.getSubscriber().getSubscriberMDNFromSubscriberID().iterator().next();
			return validateMerchantMDN(subscriberMDN);
		}
	}
	
	/**
	 * Validates the sourcePocket for not null and checks if it is active and returns source related  notification codes
	 * @param srcPocket
	 * @return
	 */
	public Integer validateSourcePocket(Pocket srcPocket) {
		if(srcPocket==null){
			log.error("Source pocket obtained null");
			return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
		}
		else if(!srcPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)){
			log.error("Source pocket is not active");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * validates the destination pocket for not null and checks if it is active and returns destination pocket related notification codes
	 * @param destPocket
	 * @return
	 */
	public Integer validateDestinationPocket(Pocket destPocket) {
		if(destPocket==null){
			log.error("Destination pocket obtained null");
			return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound;
		}
		else if(!destPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)){
			log.error("Destination pocket is not active");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * validates the input pin by digesting it and comparing with that in the subscriberMDN object sent.
	 * For wrong pin entered the max count is checked and corresponding changes to restrictions and pin count are done in the 
	 * subscriberMDn,subscriber and inactiving partner if necessary in partner table
	 * For correct pin the wrong pin count is reset.
	 * 
	 * @param subscriberMDN
	 * @param pin
	 * @return validation related notifcation code
	 */
	public Integer validatePin(SubscriberMDN subscriberMDN,String pin){
		if (subscriberMDN == null) {
				return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		Subscriber subscriber = subscriberMDN.getSubscriber();
		if (!StringUtils.isNotBlank(pin)) { // || pin.length() < ConfigurationUtil.getMinPINLength()
			return CmFinoFIX.NotificationCode_InvalidSMSCommand;
		}

		// we will not check length when the pin check happens, because PIN length might have been changed in config file
		// after the PIN has been set by the subscriber. We take care of PIN length in case of changing PIN.
		
	//	String calcPIN = MfinoUtil.calculateDigestPin(subscriberMDN.getMDN(), pin);
		String digestedPin = null;

		digestedPin = subscriberMDN.getDigestedPIN();

		if(StringUtils.isBlank(digestedPin)){
			return CmFinoFIX.NotificationCode_PINResetRequired;
		}
		String storedPin = digestedPin;
		if (mfinoUtilService.validatePin(subscriberMDN.getMDN(), pin, storedPin).equals(GeneralConstants.LOGIN_RESPONSE_FAILED)) {
			log.error("Invalid PIN entered MDN="+subscriberMDN.getMDN());
			int wrongPINCount = subscriberMDN.getWrongPINCount();
			subscriberMDN.setWrongPINCount(wrongPINCount + 1);
			recalculateMDNRestrictions(subscriberMDN);
			subscriberMdnService.saveSubscriberMDN(subscriberMDN);

			subscriberService.saveSubscriber(subscriber);
			
			return CmFinoFIX.NotificationCode_WrongPINSpecified;
		}
		else if(mfinoUtilService.validatePin(subscriberMDN.getMDN(), pin, storedPin).equals(GeneralConstants.LOGIN_RESPONSE_SUCCESS)) {
			// reset wrong pin count and allow them to login
			if (subscriberMDN.getWrongPINCount() > 0) {
				log.info("Setting wrong pin count to zero");
				subscriberMDN.setWrongPINCount(0);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				return CmFinoFIX.ResponseCode_Success;
			}
		}else{
			//internal failure reason . May have failed due to HSM error . Dont reset pin count
				return CmFinoFIX.NotificationCode_InternalLoginError;
		}
		
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * recalculates the subscriber restrictions if the max pin invalid pin count is reached and makes appropriate changes to the
	 * subscriber restrictions
	 * @param subscriberMDN
	 */
	private void recalculateMDNRestrictions(SubscriberMDN subscriberMDN) {
		Subscriber subscriber = subscriberMDN.getSubscriber();
		log.info("recalculating subscriber restriction for the wrong pin entered");
		if ((subscriberMDN.getRestrictions() & CmFinoFIX.SubscriberRestrictions_SecurityLocked) != 0) {
			return;
		}
		if (subscriberMDN.getWrongPINCount() >= systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)) {
			Timestamp now = new Timestamp();
			subscriberMDN.setRestrictions(subscriberMDN.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			subscriberMDN .setStatus(CmFinoFIX.SubscriberStatus_InActive);
			subscriberMDN.setStatusTime(now);
			subscriber.setRestrictions(subscriber.getRestrictions() | CmFinoFIX.SubscriberRestrictions_SecurityLocked);
			subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
			subscriber.setStatusTime(now);
			subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);
			
			// Check if the Subscriber is of Partner type
			if (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType())) {
				Set<Partner> setPartners = subscriber.getPartnerFromSubscriberID();
				if (CollectionUtils.isNotEmpty(setPartners)) {
					Partner partner = setPartners.iterator().next();
					partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
					partnerService.savePartner(partner);
				}
			}
		}
	}

	
	public long getAccountType(String accontTypeStr) throws InvalidDataException {
		long accountType = -1L;
		try{
			accountType = Long.parseLong(accontTypeStr);
		}catch (NumberFormatException e) {
			log.error("Exception in Registration: Invalid Account Type (KYC Level)",e);
			throw new InvalidDataException("Invalid Account type", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_ACCOUNT_TYPE);
		}
		return accountType;
	}
	
	public Date getDate(String dateStr) throws InvalidDataException {
		Date dateOfBirth;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			dateFormat.setLenient(false);
			dateOfBirth = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			log.error("Exception in Registration: Invalid Date",e);
			throw new InvalidDataException("Invalid Date", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_DOB);
		}		
		return dateOfBirth;
	}

	/**
	 * Validates the unregistered destination pocket for not null and checks if its status is OneTimeActive
	 * and returns the corresponding notification code
	 * @param destPocket
	 * @return
	 */
	public Integer validateDestinationPocketForUnregistered(Pocket destPocket) {
		if(destPocket==null){
			log.error("The destination pocket for unregistered is null");
			return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound;
		}
		if (!destPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive)) {
			log.error("The destination pocket status for unregistered is not OneTimeActive");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * Validates the unregistered source pocket for not null and checks if its status is OneTimeActive
	 * and returns the corresponding notification code
	 * @param srcSubscriberPocket
	 * @return
	 */
	public Integer validateSourcePocketForUnregistered(
			Pocket srcSubscriberPocket) {
		if(srcSubscriberPocket==null){
			log.error("The source pocket for unregistered is null");
			return CmFinoFIX.NotificationCode_DestinationEMoneyPocketNotFound;
		}
		if (!srcSubscriberPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive)) {
			log.error("The destination pocket status for unregistered is not OneTimeActive");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * validates destinationPocket for not null and checks if its status is inactive and returns the corresponding notification code
	 * for ATM Reversal
	 * @param destSubscriberPocket
	 * @return
	 */
	public Integer validateDestinationPocketForATMReversal(Pocket destSubscriberPocket){
		if(destSubscriberPocket==null){
			log.error("Destination Money Pocket not Found");
			return CmFinoFIX.NotificationCode_DestinationMoneyPocketNotFound;
		}
		if (! ((destSubscriberPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) ||
			  (destSubscriberPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive))) ){
			log.error("Destination Money Pocket not Active");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * validates srcSubscriberPocket for not null and checks if its status is inactive and returns the corresponding notification code
	 * for ATM Withdrawal
	 * @param srcSubscriberPocket
	 * @return
	 */
	public Integer validateSourcePocketForATMWithdrawal(Pocket srcSubscriberPocket) {
		if(srcSubscriberPocket==null){
			log.error("Source Money Pocket not Found for atmWithdrawal");
			return CmFinoFIX.NotificationCode_SourceMoneyPocketNotFound;
		}
		if (! ((srcSubscriberPocket.getStatus().equals(CmFinoFIX.PocketStatus_Active)) ||
			  (srcSubscriberPocket.getStatus().equals(CmFinoFIX.PocketStatus_OneTimeActive))) ){
			log.error("Source Money Pocket not Active for atmWithdrawal");
			return CmFinoFIX.NotificationCode_MoneyPocketNotActive;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
	
	/**
	 * Changes the status of an inactived subscriber due to no fund movement to active when there is a fund movement
	 * @param subscriberMDN
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void checkAndChangeStatus(SubscriberMDN subscriberMDN) {
		if(subscriberMDN != null){
			if(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()) &&
					CmFinoFIX.SubscriberStatus_InActive.equals(subscriberMDN.getStatus())){
				Timestamp now = new Timestamp();
				subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Active);
				subscriberMDN.setStatusTime(now);
				Subscriber subscriber= subscriberMDN.getSubscriber();
				subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_Active);
				subscriber.setStatusTime(now);
				subscriberStatusEventService.upsertNextPickupDateForStatusChange(subscriber,true);

				subscriberService.saveSubscriber(subscriber);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				
			}
		}
	}
	
	/**
	 * Validates the given subscriber mdn details for reset pin request. It returns failure result only in case of
	 * a. MDN not found
	 * b. Mdn is absolutely locked(Absloute restriction)
	 * c. MDN in Not registered or Retired or Garve status
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberForResetPinInquiryRequest(SubscriberMDN subscriberMDN) {
		if(subscriberMDN == null){
			log.error("SourceMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getStatus()) ||
				CmFinoFIX.SubscriberStatus_PendingRetirement.equals(subscriberMDN.getStatus()) ||
				CmFinoFIX.SubscriberStatus_Retired.equals(subscriberMDN.getStatus()) )  {
			log.error("Source Subscriber status : " + subscriberMDN.getStatus());
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
	}

	/**
	 * Validates the given subscriber mdn details for reset pin request. It returns failure result only in case of
	 * a. MDN not found
	 * b. Mdn is absolutely locked(Absloute restriction)
	 * c. MDN in Not registered or Retired or Garve status
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberForResetPinRequest(SubscriberMDN subscriberMDN) {
		if(subscriberMDN == null){
			log.error("SourceMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		if(CmFinoFIX.SubscriberStatus_NotRegistered.equals(subscriberMDN.getStatus()) ||
				CmFinoFIX.SubscriberStatus_PendingRetirement.equals(subscriberMDN.getStatus()) ||
				CmFinoFIX.SubscriberStatus_Retired.equals(subscriberMDN.getStatus()) ||
				CmFinoFIX.SubscriberRestrictions_AbsoluteLocked.equals(subscriberMDN.getRestrictions()))  {
			log.error("Source Subscriber status : " + subscriberMDN.getStatus());
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		return CmFinoFIX.ResponseCode_Success;
	}
}
