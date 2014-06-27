package com.mfino.transactionapi.service;

import java.util.Date;

import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.InvalidDataException;

/**
 * 
 * @author Sreenath
 *
 */
public interface TransactionApiValidationService {

	/**
	 * Validates whether the the subscriberMDN is allowed to perform funds transfer as source
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberAsSource(SubscriberMDN subscriberMDN);
	
	/**
	 * validates whether the subscriberMDN is allowed to receive funds as destination
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberAsDestination(SubscriberMDN subscriberMDN);
	
	/**
	 * validates if a partner is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validatePartnerMDN(SubscriberMDN subscriberMDN);
	
	/**
	 * validates if an agent is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateAgentMDN(SubscriberMDN subscriberMDN);
	
	/**
	 * validates if a teller is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateTellerMDN(SubscriberMDN subscriberMDN);
	
	/**
	 * validates if a merchant is allowed to transfer or receive funds
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateMerchantMDN(SubscriberMDN subscriberMDN);
	
	/**
	 * Validates the sourcePocket for not null and checks if it is active and returns source related  notification codes
	 * @param srcPocket
	 * @return
	 */
	public Integer validateSourcePocket(Pocket srcPocket);
	
	/**
	 * validates the destination pocket for not null and checks if it is active and returns destination pocket related notification codes
	 * @param destPocket
	 * @return
	 */
	public Integer validateDestinationPocket(Pocket destPocket);
	
	/**
	 * Validates the input partner for not null and calls the validatePartnerMDN method to validate partner details
	 * by extracting the subscriberMDN from the not null partner
	 * @param partner
	 * @return
	 */
	public Integer validatePartnerByPartnerType(Partner partner);
	
	/**
	 * Validates the input agent for not null and calls the validateAgentMDN method to validate agent details
	 * by extracting the subscriberMDN from the not null agent
	 * @param agent
	 * @return
	 */
	public Integer validateAgentByPartnerType(Partner agent);
	
	/**
	 * Validates the input teller for not null and calls the validateTellerMDN method to validate teller details
	 * by extracting the subscriberMDN from the not null teller
	 * @param teller
	 * @return
	 */
	public Integer validateTellerByPartnerType(Partner teller);
	
	/**
	 * Validates the input merchant for not null and calls the validateMerchantMDN method to validate Merchant details
	 * by extracting the subscriberMDN from the not null merchant
	 * @param merchant
	 * @return
	 */
	public Integer validateMerchantByPartnerType(Partner merchant);
	
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
	public Integer validatePin(SubscriberMDN subscriberMDN,String pin);
	

	/**
	 * Validates the unregistered destination pocket for not null and checks if its status is OneTimeActive
	 * and returns the corresponding notification code
	 * @param destPocket
	 * @return
	 */
	public Integer validateDestinationPocketForUnregistered(Pocket destPocket);

	/**
	 * Validates the unregistered source pocket for not null and checks if its status is OneTimeActive
	 * and returns the corresponding notification code
	 * @param srcSubscriberPocket
	 * @return
	 */
	public Integer validateSourcePocketForUnregistered(
			Pocket srcSubscriberPocket);
	
	/**
	 * validates destinationPocket for not null and checks if its status is inactive and returns the corresponding notification code
	 * for ATM Reversal
	 * @param destSubscriberPocket
	 * @return
	 */
	public Integer validateDestinationPocketForATMReversal(Pocket destSubscriberPocket);

	/**
	 * validates srcSubscriberPocket for not null and checks if its status is inactive and returns the corresponding notification code
	 * for ATM Withdrawal
	 * @param srcSubscriberPocket
	 * @return
	 */
	public Integer validateSourcePocketForATMWithdrawal(Pocket srcSubscriberPocket);
	
	/**
	 * Changes the status of an inactived subscriber due to no fund movement to active when there has been a fund movement
	 * @param subscriberMDN
	 */
	public void checkAndChangeStatus(SubscriberMDN subscriberMDN);

	/**
	 *  converts the date in string format to simpleDateFormat ddMMyyyy
	 *  @param dateStr
	 *  @throws InvalidDataException 
	 */
	public Date getDate(String dateOfExpiry) throws InvalidDataException;

	public Integer validateSubscriberAsDestinationString(String mdn);
	
	/**
	 * Validates the given subscriber mdn details for reset pin request. It returns failure result only in case of
	 * a. MDN not found
	 * b. Mdn is absolutely locked(Absloute restriction)
	 * c. MDN in Not registered or Retired or Garve status
	 * @param subscriberMDN
	 * @return
	 */
	public Integer validateSubscriberForResetPinRequest(SubscriberMDN subscriberMDN);
	
	public Integer validateSubscriberForResetPinInquiryRequest(SubscriberMDN subscriberMDN);

}
