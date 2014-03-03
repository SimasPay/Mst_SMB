/**
 * 
 */
package com.mfino.webapi.utilities;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.Result.ResultType;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;

/**
 * This class converts, user data to transaction details required for processing transaction.
 * 
 * @author Chaitanya
 *
 */
public class UserDataToTxnDetailsConverter {
	
	private static final Logger	log = LoggerFactory.getLogger(UserDataToTxnDetailsConverter.class);

	public TransactionDetails getTransactionDetails(IUserDataContainer userDataContainer) throws InvalidDataException{
		TransactionDetails txnDetails = new TransactionDetails();
		
		String confirmPin = userDataContainer.getConfirmPin();
		String newPin = userDataContainer.getNewPin();
		String sourcePin = userDataContainer.getSourcePin();
		String activationConfirmPin = userDataContainer.getActivationConfirmPin();
		String activationNewPin = userDataContainer.getActivationNewPin();
		
		if(ConfigurationUtil.getuseHSM() && ConfigurationUtil.getuseHashedPIN())
		{
			//hashed pin is set to true only when HSM is enabled and hashed Pin is enabled
			txnDetails.setHashedPin(true);
		}
		txnDetails.setAccountType(userDataContainer.getAccountType());
		txnDetails.setActivationOTP(userDataContainer.getOTP());
		if(StringUtils.isNotBlank(userDataContainer.getAmount())){
			txnDetails.setAmount(getAmount(userDataContainer.getAmount()));
		}
		txnDetails.setApplicationId(userDataContainer.getApplicationID());
		txnDetails.setAuthenticationString(userDataContainer.getAuthenticationString());
		txnDetails.setBillerCode(userDataContainer.getBillerCode());
		txnDetails.setBillNum(userDataContainer.getBillNo());
		txnDetails.setChannelCode(userDataContainer.getChannelId());
		txnDetails.setConfirmPIN(confirmPin);
		txnDetails.setSecreteCode(userDataContainer.getSecreteCode());
		if(activationConfirmPin!=null)
		{
			txnDetails.setConfirmPIN(activationConfirmPin);
		}
		txnDetails.setConfirmString(userDataContainer.getConfirmed());
		if(StringUtils.isNotBlank(userDataContainer.getDateOfBirth())){
			txnDetails.setDateOfBirth(getDate(userDataContainer.getDateOfBirth(), ApiConstants.PARAMETER_DOB));
		}
		txnDetails.setDestMDN(userDataContainer.getDestinationMdn());
		if(userDataContainer.getSubscriberMDN()!=null){
			txnDetails.setDestMDN(userDataContainer.getSubscriberMDN());
		}
		txnDetails.setDestPocketCode(userDataContainer.getDestinationPocketCode());
		txnDetails.setIsHttps(userDataContainer.getIsHttps());
		txnDetails.setFirstName(userDataContainer.getFirstName());
		txnDetails.setLastName(userDataContainer.getLastName());
		txnDetails.setMothersMaidenName(userDataContainer.getMothersMaidenName());
		txnDetails.setNewPIN(newPin);
		if(activationNewPin!=null)
		{
			txnDetails.setNewPIN(activationNewPin);
		}
		if(StringUtils.isNotBlank(userDataContainer.getParentTxnId())){
			txnDetails.setParentTxnId(getParentTxnId(userDataContainer.getParentTxnId()));
		}
		txnDetails.setPartnerCode(userDataContainer.getPartnerCode());
		if(userDataContainer.getAgentCode()!=null){
			txnDetails.setPartnerCode(userDataContainer.getAgentCode());
		}
		txnDetails.setResultType(ResultType.XML);
		txnDetails.setSalt(userDataContainer.getSalt());
		txnDetails.setServiceName(userDataContainer.getServiceName());
		txnDetails.setSourceMDN(userDataContainer.getSourceMdn());
		txnDetails.setSourceMessage(userDataContainer.getSourceMessage());
		txnDetails.setSourcePIN(sourcePin);
		txnDetails.setSourcePocketCode(userDataContainer.getSourcePocketCode());
		txnDetails.setTransactionCode(userDataContainer.getTransactionID());
		txnDetails.setTransactionName(userDataContainer.getTransactionName());
		if(StringUtils.isNotBlank(userDataContainer.getTransferId())){
			txnDetails.setTransferId(getTransferId(userDataContainer.getTransferId()));
		}
		txnDetails.setCompanyID(userDataContainer.getCompanyID());
		
		txnDetails.setDestAccountNumber(userDataContainer.getDestAccountNumber());
		txnDetails.setDestinationBankAccountNo(userDataContainer.getDestinationBankAccountNo());
		txnDetails.setDestBankCode(userDataContainer.getDestBankCode());
		txnDetails.setAppOS(userDataContainer.getAppOS());
		txnDetails.setAppVersion(userDataContainer.getAppVersion());
		txnDetails.setAppType(userDataContainer.getAppType());
		
		txnDetails.setKycType(userDataContainer.getKycType());
		txnDetails.setCity(userDataContainer.getCity());
		txnDetails.setNextOfKin(userDataContainer.getNextOfKin());
		txnDetails.setNextOfKinNo(userDataContainer.getNextOfKinNo());
		txnDetails.setEmail(userDataContainer.getEmail());
		txnDetails.setPlotNo(userDataContainer.getPlotNo());
		txnDetails.setStreetAddress(userDataContainer.getStreetAddress());
		txnDetails.setRegionName(userDataContainer.getRegionName());
		txnDetails.setCountry(userDataContainer.getCountry());
		txnDetails.setIdType(userDataContainer.getIdType());
		txnDetails.setIdNumber(userDataContainer.getIdNumber());
		txnDetails.setDateOfExpiry(userDataContainer.getDateOfExpiry());
		txnDetails.setAddressProof(userDataContainer.getAddressProof());
		txnDetails.setBirthPlace(userDataContainer.getBirthPlace());
		txnDetails.setNationality(userDataContainer.getNationality());
		txnDetails.setCompanyName(userDataContainer.getCompanyName());
		txnDetails.setSubscriberMobileCompany(userDataContainer.getSubscriberMobileCompany());
		txnDetails.setCertOfIncorp(userDataContainer.getCertOfIncorp());
		txnDetails.setLanguage(userDataContainer.getLanguage());
		txnDetails.setNotificationMethod(userDataContainer.getNotificationMethod());
		txnDetails.setInstitutionID(userDataContainer.getInstitutionID());
		txnDetails.setOnBehalfOfMDN(userDataContainer.getOnBehalfOfMDN());
		txnDetails.setCategory(userDataContainer.getCategory());
		txnDetails.setVersion(userDataContainer.getVersion());
		//tarun
		txnDetails.setApprovalRequired(userDataContainer.getApprovalRequired());
		txnDetails.setBankAccountType(userDataContainer.getBankAccountType());
		txnDetails.setCardPAN(userDataContainer.getCardPAN());
		txnDetails.setCardAlias(userDataContainer.getCardAlias());
		txnDetails.setAuthorizingFirstName(userDataContainer.getAuthorizingFirstName());
		txnDetails.setAuthorizingLastName(userDataContainer.getAuthorizingLastName());
		txnDetails.setAuthorizingIdNumber(userDataContainer.getAuthorizingIdNumber());
		txnDetails.setApprovalComments(userDataContainer.getApprovalComments());
		txnDetails.setNarration(userDataContainer.getNarration());
		txnDetails.setBenOpCode(userDataContainer.getBenOpCode());
		txnDetails.setDescription(userDataContainer.getDescription());
		txnDetails.setTransactionOTP(userDataContainer.getMfaOtp());
		txnDetails.setMfaTransaction(userDataContainer.getMFATransaction());
		txnDetails.setNewEmail(userDataContainer.getNewEmail());
		txnDetails.setConfirmEmail(userDataContainer.getConfirmEmail());
		txnDetails.setNickname(userDataContainer.getNickname());
		txnDetails.setTransID(userDataContainer.getTransID());
		
		txnDetails.setPageNumber(userDataContainer.getPageNumber());
		txnDetails.setNumRecords(userDataContainer.getNumRecords());
		txnDetails.setOtherMdn(userDataContainer.getOtherMdn());
		txnDetails.setParentTransID(userDataContainer.getParentTransID());
		txnDetails.setFavoriteCategoryID(userDataContainer.getFavoriteCategoryID());
		txnDetails.setFavoriteLabel(userDataContainer.getFavoriteLabel());
		txnDetails.setFavoriteValue(userDataContainer.getFavoriteValue());
		txnDetails.setFavoriteCode(userDataContainer.getFavoriteCode());
		if(StringUtils.isNotBlank(userDataContainer.getPartnerType())){
			try{
				txnDetails.setPartnerType(Integer.parseInt(userDataContainer.getPartnerType()));
			}catch (NumberFormatException ex) {
				log.error("Error parsing partnerType string", ex);
				throw new InvalidDataException("Invalid partnerType", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_PARTNER_TYPE);
			
			}
		}
		txnDetails.setTradeName(userDataContainer.getTradeName());
		txnDetails.setPostalCode(userDataContainer.getPostalCode());
		txnDetails.setUserName(userDataContainer.getUserName());
		txnDetails.setOutletClasification(userDataContainer.getOutletClasification());
		txnDetails.setFranchisePhoneNumber(userDataContainer.getFranchisePhoneNumber());
		txnDetails.setFaxNumber(userDataContainer.getFaxNumber());
		txnDetails.setTypeOfOrganization(userDataContainer.getTypeOfOrganization());
		txnDetails.setWebSite(userDataContainer.getWebSite());
		txnDetails.setIndustryClassification(userDataContainer.getIndustryClassification());
		if(StringUtils.isNotBlank(userDataContainer.getNumberOfOutlets())){
			try{
				txnDetails.setNumberOfOutlets(Integer.parseInt(userDataContainer.getNumberOfOutlets()));
			}catch (NumberFormatException ex) {
				log.error("Error parsing numberOfOutlets string", ex);
				throw new InvalidDataException("Invalid partnerType", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_NOOF_OUTLETS);
			
			}
		}
		if(StringUtils.isNotBlank(userDataContainer.getYearEstablished())){
			if(userDataContainer.getYearEstablished().length()!=4)
				throw new InvalidDataException("Invalid yearEstablished", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_YEAR_ESTABLISHED);
			try{
				txnDetails.setYearEstablished(Integer.parseInt(userDataContainer.getYearEstablished()));
			}catch (NumberFormatException ex) {
				log.error("Error parsing yearEstablished string", ex);
				throw new InvalidDataException("Invalid yearEstablished", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_YEAR_ESTABLISHED);
			}
		}
		txnDetails.setOutletAddressLine1(userDataContainer.getOutletAddressLine1());
		txnDetails.setOutletAddressLine2(userDataContainer.getOutletAddressLine2());
		txnDetails.setOutletAddressCity(userDataContainer.getOutletAddressCity());
		txnDetails.setOutletAddressState(userDataContainer.getOutletAddressState());
		txnDetails.setOutletAddressZipcode(userDataContainer.getOutletAddressZipcode());
		txnDetails.setOutletAddressCountry(userDataContainer.getOutletAddressCountry());
		txnDetails.setAuthorizedRepresentative(userDataContainer.getAuthorizedRepresentative());
		txnDetails.setRepresentativeName(userDataContainer.getRepresentativeName());
		txnDetails.setDesignation(userDataContainer.getDesignation());
		txnDetails.setAuthorizedFaxNumber(userDataContainer.getAuthorizedFaxNumber());
		txnDetails.setPaymentMode(userDataContainer.getPaymentMode());
		if(StringUtils.isNotBlank(userDataContainer.getFromDate())){
			txnDetails.setFromDate(getDate(userDataContainer.getFromDate(), ApiConstants.PARAMETER_FROM_DATE));
		}
		if(StringUtils.isNotBlank(userDataContainer.getToDate())){
			txnDetails.setToDate(getDate(userDataContainer.getToDate(), ApiConstants.PARAMETER_TO_DATE));
		}
		txnDetails.setAddressLine1(userDataContainer.getAddressLine1());
		txnDetails.setZipCode(userDataContainer.getZipCode());
		txnDetails.setState(userDataContainer.getState());
		return txnDetails;
	}
	
	
	public BigDecimal getAmount(String amountStr1) throws InvalidDataException{
		BigDecimal amount = new BigDecimal(-1);
		try {
			amount = new BigDecimal(amountStr1);
		}
		catch (NumberFormatException ex) {
			log.error("Invalid Amount: " + amountStr1, ex);
			throw new InvalidDataException("Invalid Amount", CmFinoFIX.NotificationCode_TransactionFailedDueToInvalidAmount, 
					ApiConstants.PARAMETER_AMOUNT);
		}
		return amount;
	}
	
	public long getTransferId(String transferIdStr) throws InvalidDataException { 
		long transferId = -1L;
		try {
			transferId = Long.parseLong(transferIdStr);
		}
		catch (NumberFormatException ex) {
			log.error("Error parsing transfer id string", ex);
			throw new InvalidDataException("Invalid Amount", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_TRANSFER_ID);
		}
		return transferId;
	}
	
	public long getParentTxnId(String parentTxnIdStr) throws InvalidDataException { 
		long parentTrxnId = -1L;
		try {
			parentTrxnId = Long.parseLong(parentTxnIdStr);
		}
		catch (NumberFormatException ex) {
			log.error("Error parsing parent transaction id string", ex);
			throw new InvalidDataException("Invalid Amount", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_PARENTTXN_ID);
		}		
		return parentTrxnId;
	}
	
	public Date getDate(String dateStr, String parameterName) throws InvalidDataException {
		Date dateOfBirth;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
			dateFormat.setLenient(false);
			dateOfBirth = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			log.error("Exception in Registration: Invalid Date",e);
			throw new InvalidDataException("Invalid Date", CmFinoFIX.NotificationCode_InvalidData, parameterName);
		}		
		return dateOfBirth;
	}
}
