package com.mfino.service;

import java.math.BigDecimal;

import com.mfino.domain.FundDefinition;
import com.mfino.domain.FundDistributionInfo;
import com.mfino.domain.UnregisteredTxnInfo;
import com.mfino.fix.CmFinoFIX.CMFundWithdrawalInquiry;
import com.mfino.result.XMLResult;

public interface FundValidationService {
	
	/**
	 * Checks if the present transaction date is past the expiryTime stored in the record
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public boolean checkExpiry(UnregisteredTxnInfo unRegisteredTxnInfo);

	
	/**
	 * returns true if available amt is greater or equalTo than trxn amount
	 * @param trxnAmount
	 * @param availableAmt
	 * @return
	 */
	public boolean checkAvaliableAmount(BigDecimal trxnAmount,BigDecimal availableAmt);
	
	/**
	 * 
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public int getMaxFailAttempts(UnregisteredTxnInfo unRegisteredTxnInfo);
	
	
	/**
	 * Updates the failure attempts when the fundWithdrawal fails and when max no of of failures is reached checks for the
	 * event set to happen in the fundDefinition and returns corresponding notification code for processing
	 * @param unRegisteredTxnInfo
	 * @param fundDefinition
	 * @return
	 */
	public Integer updateFailureAttempts(UnregisteredTxnInfo unRegisteredTxnInfo,FundDefinition fundDefinition);
	



	/**
	 * Regenerates the fund access code and stores its digested value in the table
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public String regenerateFAC(UnregisteredTxnInfo unRegisteredTxnInfo);
	
	/**
	 * Retruns true if the digested fac created from the present transaction is same as the one stored in the 
	 * unRegistered trxn Info record
	 * @param digestedFAC
	 * @param unRegisteredTxnInfo
	 * @return
	 */
	public boolean isValidFAC(String digestedFAC,UnregisteredTxnInfo unRegisteredTxnInfo);
	
	/**
	 * Creates a new fundDistributionInfo record for fundWithdrawal inquiry request when isDebit is set to true and does appropriate changes 
	 * to the status of the fund stored in the unregistered trxnInfo table.On failure of fund withdrawal this method is called with isDebit false
	 * so that the amount debitted is credited back to unregTrxnInfo table and status is also changed.
	 * @param unRegisteredTxnInfo
	 * @param fundWithdrawalInquiry
	 * @param isDebit
	 * @param amount
	 */
	public void updateAvailableAmount(UnregisteredTxnInfo unRegisteredTxnInfo,CMFundWithdrawalInquiry fundWithdrawalInquiry,boolean isDebit,BigDecimal amount);

	/**
	 * gets the unregistered trxn ifno row corresponding to our transaction.Query can be executed with only sctlID also
	 * @param withdrawalMDN
	 * @param fac
	 * @param sctlID
	 * @return UnRegisteredTxnInfo
	 */
	public UnregisteredTxnInfo queryUnRegisteredTxnInfo(String withdrawalMDN, String fac,Long sctlID,String enteredPartnerCode);
	
	/**
	 * Valdiates entered fac,available amount,fund expiry time and the partner and return false for failure and true for success
	 * @param subscriberMDN
	 * @param result
	 * @param amount
	 * @param partnerCode 
	 * @param fac
	 * @return
	 */
	public Integer validate(UnregisteredTxnInfo unRegisteredTxnInfo, XMLResult result, BigDecimal amount, String OTP,FundDefinition fundDefinition, String partnerCode);
	
	/**
	 * Checks if allowed partner code and entered code are the same.If the allowed partner is "any" then all partners are allowed. 
	 * @param allowedPartnerCode
	 * @param enteredPartnerCode
	 * @return
	 */
	public boolean checkValidPartner(String allowedPartnerCode, String enteredPartnerCode);
	
	/**
	 * Gets the FundDistributionInfo record with the matching sctlID
	 * @param sctlid
	 * @return
	 */
	public FundDistributionInfo queryFundDistributionInfo(Long sctlid);

	/**
	 * Validates whether the Purpose and fund definition defined for given partner code 
	 * @param partnerCode
	 * @return
	 */
	public boolean validatePurposeAndFundDefinition(String partnerCode);

}
