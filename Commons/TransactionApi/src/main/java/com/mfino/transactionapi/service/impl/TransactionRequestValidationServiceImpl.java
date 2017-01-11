/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.GeneralConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.DateUtil;

/**
 * @author Bala Sunku
 *
 */
@Service("TransactionRequestValidationServiceImpl")
public class TransactionRequestValidationServiceImpl implements TransactionRequestValidationService{
	public static Logger log = LoggerFactory.getLogger(TransactionRequestValidationServiceImpl.class);
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;

	public void validateConfirmPin(TransactionDetails transactionDetails) throws InvalidDataException {
		if (!ConfigurationUtil.getuseRSA() && StringUtils.isNotBlank(transactionDetails.getConfirmPIN()) 
				&& !transactionDetails.getConfirmPIN().equals(transactionDetails.getNewPIN())) {
			throw new InvalidDataException("New Pin and Confirm Pin not match", CmFinoFIX.NotificationCode_NewPINConfirmPINDoNotMatch, 
					ApiConstants.PARAMETER_CONFIRM_PIN);
		}
	}

	public void validateNewPin(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNewPIN())) {
			throw new InvalidDataException("Invalid New Pin", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NEW_PIN);
		}
		
		if (!ConfigurationUtil.getuseRSA() && systemParametersService.getPinLength() != transactionDetails.getNewPIN().length() ) {
			throw new InvalidDataException("Invalid New Pin", CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidPINLength, 
					ApiConstants.PARAMETER_NEW_PIN);
		}
	}
	
	public void validateSourcePin(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getSourcePIN())) {
			throw new InvalidDataException("Invalid Source Pin", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SOURCE_PIN);
		}
	}

	public void validateOTP(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getActivationOTP())) {
			throw new InvalidDataException("Invalid Activation Key", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_OTP);
		}
	}

	public void validateSctlId(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isNotBlank(transactionDetails.getSctlId().toString()) && !StringUtils.isNumeric(transactionDetails.getSctlId().toString())) {
			throw new InvalidDataException("Invalid Activation Key", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SCTL_ID);
		}
	}	
	
	public void validateAmount(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getAmount()==null) {
			throw new InvalidDataException("Invalid Amount", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_AMOUNT);
		}
	}
	
	public void validateDesttinationMDN(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getDestMDN())) {
			throw new InvalidDataException("Invalid Destination MDN", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DEST_MDN);
		}
	}
	
	public void validateDestinationAccountNo(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getDestAccountNumber())) {
			throw new InvalidDataException("Invalid Destination Account Number", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DEST_ACCOUNT_NO);
		}
	}
	
	public void validateDestinationBankCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getDestBankCode())) {
			throw new InvalidDataException("Invalid Destination Bank Code", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DEST_BANK_CODE);
		}
	}
	
	public void validateTransferId(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getTransferId()==null) {
			throw new InvalidDataException("Invalid Transfer Id", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_TRANSFER_ID);
		}
	}
	
	public void validateParentTxnId(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getParentTxnId()==null) {
			throw new InvalidDataException("Invalid parent transaction id", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_PARENTTXN_ID);
		}
	}
	
	public void validateconfirmString(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getConfirmString())) {
			throw new InvalidDataException("Invalid confrim string", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_CONFIRMED);
		}
	}
	
	public void validatePartnerCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getPartnerCode())) {
			throw new InvalidDataException("Invalid Partner code", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_PARTNER_CODE);
		}
	}
	
	public void validateBillerCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getBillerCode())) {
			throw new InvalidDataException("Invalid Biller code", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_BILLER_CODE);
		}
	}
	
	public void validateBillNo(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getBillNum())) {
			throw new InvalidDataException("Invalid Bill No.", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_BILL_NO);
		}
	}
	
	public void validateUserAPIKey(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getUserAPIKey())) {
			throw new InvalidDataException("Invalid UserAPIKey", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_USER_API_KEY);
		}
	}
	
	public void validateFirstName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getFirstName())) {
			throw new InvalidDataException("Invalid First name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SUB_FIRSTNAME);
		}
	}
	
	public void validateKtpId(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getKtpId())) {
			throw new InvalidDataException("Invalid KTP Id", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_KTPID);
		}
	}
	
	public void validateLastName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getLastName())) {
			throw new InvalidDataException("Invalid Last name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SUB_LASTNAME);
		}
	}
	public void validateMothersMaidenName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getMothersMaidenName())) {
			throw new InvalidDataException("Invalid Mothers_Maiden Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SUB_MothersMaidenName);
		}
	}
	
	public void validateApplicationId(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getApplicationId())) {
			throw new InvalidDataException("Invalid Application Id", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_APPLICATION_ID);
		}
	}
	
	public void validateDateOfBirth(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getDateOfBirth()==null) {
			throw new InvalidDataException("Invalid Date Of Birth", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DOB);
		}
		Date dateOfBirth=transactionDetails.getDateOfBirth();
//		try {
//			SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
//			dateFormat.setLenient(false);
//			dateOfBirth = dateFormat.parse(transactionDetails.getDateOfBirth());
//		} catch (ParseException e) {
//			throw new InvalidDataException("Invalid Date", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_DOB);
//		}
		//Min age  validation
		Date today = DateUtil.addYears(new Date(), -(systemParametersService.getInteger(SystemParameterKeys.MIN_REGISTRATION_AGE)));
		if(today.before(dateOfBirth)){
			throw new InvalidDataException("Invalid Date", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_DOB);
		}
	}
	
	public void validateCity(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getCity())) {
			throw new InvalidDataException("Invalid City", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_CITY);
		}		
	}

	public void validateAddressLine1(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAddressLine1())) {
			throw new InvalidDataException("Invalid Address Line1", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ADDRESS_LINE_1);
		}		
	}
	
	public void validateZipCode(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getZipCode())) {
			throw new InvalidDataException("Invalid Zip Code", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ZIP_CODE);
		}		
	}
	public void validateState(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getState())) {
			throw new InvalidDataException("Invalid State", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_STATE);
		}		
	}
	
	public void validateKycType(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getKycType())) {
			throw new InvalidDataException("Invalid Kyc Level", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_KYC_TYPE);
		}		
	}
	
	public void validateKinName(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNextOfKin())) {
			throw new InvalidDataException("Invalid kin Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NEXT_OF_KIN);
		}		
	}
	
	public void validateKinNo(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNextOfKinNo())) {
			throw new InvalidDataException("Invalid Kin No", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NEXT_OF_KIN_NO);
		}		
	}
	
	public void validateAccountType(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAccountType())) {
			throw new InvalidDataException("Invalid Account type", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ACCOUNT_TYPE);
		}
	}
	
	public void validateSalt(TransactionDetails transactionDetails) throws InvalidDataException {
		if(!transactionDetails.isHttps()){
			if (StringUtils.isBlank(transactionDetails.getSalt())) {
				throw new InvalidDataException("Invalid salt", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
						ApiConstants.PARAMETER_SALT);
			}
		}
	}
	
	public void validateAuthenticationString(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAuthenticationString())) {
			throw new InvalidDataException("Invalid authentication string", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_AUTHENTICATION_STRING);
		}
	}
	
	public void validateSourcePocketCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getSourcePocketCode())) {
			throw new InvalidDataException("Invalid authentication string", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SRC_POCKET_CODE);
		}
	}
	
	public void validateSourceAndDestinationPocketCodes(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getSourcePocketCode())) {
			throw new InvalidDataException("Invalid authentication string", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SRC_POCKET_CODE);
		}
		if (StringUtils.isBlank(transactionDetails.getDestPocketCode())) {
			throw new InvalidDataException("Invalid authentication string", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DEST_POCKET_CODE);
		}
		
	}
	
	public void validateSecreteCode(TransactionDetails transactionDetails) throws InvalidDataException{
		if (StringUtils.isBlank(transactionDetails.getSecreteCode())) {
			throw new InvalidDataException("Invalid secreteCode", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SECRETE_CODE);
		}
		
	}
	
	public void validateInstitutionID(TransactionDetails transactionDetails)  throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getInstitutionID())) {
			throw new InvalidDataException("Invalid InstitutionID", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_INSTITUTION_ID);
		}		
	}
	
	public void validateBankAccountType(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getBankAccountType())) {
			throw new InvalidDataException("Invalid Bank Account Type", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_BANK_ACCOUNT_TYPE);
		}
	}
			
	public void validateCardPAN(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getCardPAN())) {
			throw new InvalidDataException("Invalid CardPan", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_CARD_PAN);
		}
		if(ServiceAndTransactionConstants.SERVICE_NFC.equals(transactionDetails.getServiceName()))
		{
			if(transactionDetails.getCardPAN().length() != ServiceAndTransactionConstants.NFC_CARDPAN_LENGTH || !StringUtils.isNumeric(transactionDetails.getCardPAN()))
			{
				throw new InvalidDataException("Invalid CardPan", CmFinoFIX.NotificationCode_Invalid_CardPan, 
						ApiConstants.PARAMETER_CARD_PAN);
			}
		}
	}
	
	private void validateCardPanAndAlias(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getCardPAN()) && StringUtils.isBlank(transactionDetails.getCardAlias())) {
			throw new InvalidDataException("Invalid CardPan", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_CARD_PAN);
		}
	}
	
	public void validateAuthorizingFirstName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAuthorizingFirstName())) {
			throw new InvalidDataException("Invalid Authorizing First Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_AUTHORIZING_FIRSTNAME);
		}
	}
	
	public void validateAuthorizingLastName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAuthorizingLastName())) {
			throw new InvalidDataException("Invalid Authorizing Last Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_AUTHORIZING_LASTNAME);
		}
	}
	
	public void validateAuthorizingIdNumber(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAuthorizingIdNumber())) {
			throw new InvalidDataException("Invalid Authorizing IdNumber", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_AUTHORIZING_IDNUMBER);
		}
	}
	
	public void validateApprovalComments(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getApprovalComments())) {
			throw new InvalidDataException("Invalid Approval Comments Field", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_APPROVAL_COMMENTS);
		}
	}
	
	public void validateNarration(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNarration())) {
			throw new InvalidDataException("Invalid Narration", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NARRATION);
		}
	}
	
	public void validateBenOpCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getBenOpCode())) {
			throw new InvalidDataException("Invalid BenOpCode", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_BEN_OP_CODE);
		}
	}
	
	public void validatechannelCode(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getChannelCode())) {
			throw new InvalidDataException("Invalid Channel Code", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_CHANNEL_ID);
		}
	}
	
	public void validateFromAndToDates(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getFromDate() == null) {
			throw new InvalidDataException("Invalid From Date", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_FROM_DATE);
		}
		if (transactionDetails.getToDate() == null) {
			throw new InvalidDataException("Invalid To Date", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_TO_DATE);
		}
	}
	
	public void validateAppOSAndVersion(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getAppOS())) {
			throw new InvalidDataException("Invalid App OS", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_APPOS);
		}
		if (StringUtils.isBlank(transactionDetails.getAppVersion())) {
			throw new InvalidDataException("Invalid App Version", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_APPVERSION);
		}
	}
	
	public void validateSubscriberActivationDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateOTP(transactionDetails);
				
		if(!transactionDetails.isSimpaspayActivity()){
			validateNewPin(transactionDetails);
			validateConfirmPin(transactionDetails);	
		}
		if (transactionDetails.getDateOfBirth()!=null) {
			validateDateOfBirth(transactionDetails);
		}
	}
	public void validateResetPinByOTPDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateOTP(transactionDetails);
		validateNewPin(transactionDetails);
		validateConfirmPin(transactionDetails);
	}
	
	public void validateForgotPinDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateOTP(transactionDetails);
		validateNewPin(transactionDetails);
		validateConfirmPin(transactionDetails);
		validateSctlId(transactionDetails);
	}
	
	public void validateTransactionStatusDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateTransferId(transactionDetails);
	}
	
	public void validateChangePinDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateNewPin(transactionDetails);
		validateConfirmPin(transactionDetails);
	}
	
	public void validateCheckBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
	}
	
	public void validateResendMFAOTPDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		
		validateSourcePin(transactionDetails);
		validateSctlId(transactionDetails);
	}
	
	public void validateTransactionHistoryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
	}
	
	public void validateTransactionHistoryDetailedStmtDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFromAndToDates(transactionDetails);
	}
	
	public void validateEmailTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateEmail(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFromAndToDates(transactionDetails);
	}
	
	public void validateDownloadTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFromAndToDates(transactionDetails);
	}
	
	public void validateTransactionPendingSettlementsForPartner(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
	}
	
	public void validateTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDesttinationMDN(transactionDetails);
		validateSourcePocketCode(transactionDetails);
	}

	public void validateTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateDesttinationMDN(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateCashOutInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		//validatePartnerCode(transactionDetails);
	}
	
	public void validateCashOutConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		//validatePartnerCode(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateCashOutAtATMInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
	}
	
	public void validateCashOutAtATMConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateInterEmoneyTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDesttinationMDN(transactionDetails);
		validateNarration(transactionDetails);
		validateBenOpCode(transactionDetails);
		validatechannelCode(transactionDetails);
	}
	
	public void validateInterEmoneyTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	public void validateFRSCPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateNarration(transactionDetails);
		validateBillNo(transactionDetails);
		validatechannelCode(transactionDetails);
		validateOnBehalfOfMDN(transactionDetails);
	}
	
	public void validateFRSCPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
		validateBillNo(transactionDetails);
	}
	
	public void validateStarTimesPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateBillNo(transactionDetails);
		//validateNarration(transactionDetails);
		validateOnBehalfOfMDN(transactionDetails);
	}
	
	public void validateStarTimesPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
		validateBillNo(transactionDetails);
		//validateNarration(transactionDetails);
	}
	
	public void validateAgentToAgentTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validatePartnerCode(transactionDetails);
	}
	
	public void validatAgentToAgentTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validatePartnerCode(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateCashInInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDesttinationMDN(transactionDetails);
	}
	
	public void validateCashInConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateDesttinationMDN(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateSubscriberKtpDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateDesttinationMDN(transactionDetails);
		validateFirstName(transactionDetails);
		//validateDateOfBirth(transactionDetails);
		validateKtpId(transactionDetails);
	}
	
	public void validateSubscriberRegistrationByAgentDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateDesttinationMDN(transactionDetails);
		validateFirstName(transactionDetails);
		//validateLastName(transactionDetails);
		//validateDateOfBirth(transactionDetails);
		if(transactionDetails.getMothersMaidenName()!=null) {
			
			validateMothersMaidenName(transactionDetails);	
		}
        
		/*validateAccountType(transactionDetails);
		
        if(transactionDetails.getApplicationId()!=null){
		
        	validateApplicationId(transactionDetails);
		}
        
		validateSourcePin(transactionDetails);*/
	}

	public void validateSubscriberRegistration(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFirstName(transactionDetails);
		validateLastName(transactionDetails);
		if(transactionDetails.getMothersMaidenName()!=null)
		{
			validateMothersMaidenName(transactionDetails);
		}
		if (transactionDetails.getDateOfBirth()!=null) {
			validateDateOfBirth(transactionDetails);
		}
		if(StringUtils.isNotBlank(transactionDetails.getEmail()))
		{
			validateEmail(transactionDetails);
		}
	}
	
	public void validateSubscriberRegistrationForNonKyc(TransactionDetails transactionDetails) throws InvalidDataException {
		
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFirstName(transactionDetails);
		
		if(StringUtils.isNotBlank(transactionDetails.getEmail())) {
			
			validateEmail(transactionDetails);
		}
	}
	
	public void validateSubscriberRegistrationWithActivation(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateFirstName(transactionDetails);
		validateLastName(transactionDetails);
		validateIdType(transactionDetails);
		validateIdNumber(transactionDetails);
		validateDateOfBirth(transactionDetails);
		validateNewPin(transactionDetails);
		validateConfirmPin(transactionDetails);
	}
	
	public void validateSubscriberRegistrationThroughWebDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateInstitutionID(transactionDetails);
		validateRegisteringMDN(transactionDetails.getDestMDN());
		validateFirstName(transactionDetails);
		validateLastName(transactionDetails);
		validateDateOfBirth(transactionDetails);
		validateCity(transactionDetails);
		validateKycType(transactionDetails);
		if(!CmFinoFIX.RecordType_SubscriberUnBanked.toString().equals(transactionDetails.getKycType()))
		{
			validateKinName(transactionDetails);
			validateKinNo(transactionDetails);
			validateAutoApproverDetails(transactionDetails);
		}		
	}

	public void validateAutoApproverDetails(
			TransactionDetails transactionDetails) throws InvalidDataException{
		if(!transactionDetails.getApprovalRequired()){
		validateBankAccountType(transactionDetails);
		validateCardPAN(transactionDetails);
		validateAuthorizingFirstName(transactionDetails);
		validateAuthorizingLastName(transactionDetails);
		validateAuthorizingIdNumber(transactionDetails);
		validateApprovalComments(transactionDetails);
		}
	}

	public void validatePurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validatePartnerCode(transactionDetails);
	}
	
	public void validatePurchaseConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validatePartnerCode(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateLoginDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSalt(transactionDetails);
		validateAuthenticationString(transactionDetails);
	}
	
	public void validateLogoutDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateAuthenticationString(transactionDetails);
	}
	
	public void validateBillPayInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		//validateAmount(transactionDetails);
		validateBillerCode(transactionDetails);
		validateBillNo(transactionDetails);
	}
	public void validateAgentBillPayInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateBillPayInquiryDetails(transactionDetails);
		validateDesttinationMDN(transactionDetails);
	}
	
	public void validateAgentBillPayConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateBillPayConfirmDetails(transactionDetails);
	}
	public void validateBillPayConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateBillNo(transactionDetails);
		validateBillerCode(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateQrPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateBillerCode(transactionDetails);
		validateBillNo(transactionDetails);
		validateUserAPIKey(transactionDetails);
	}
	
	public void validateQrPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateBillNo(transactionDetails);
		validateBillerCode(transactionDetails);
		validateUserAPIKey(transactionDetails);
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	@SuppressWarnings("unused")
	private void validateDiscountAmount(TransactionDetails transactionDetails) throws InvalidDataException {
		if (transactionDetails.getDiscountAmount()==null) {
			throw new InvalidDataException("Invalid Discount Amount", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DISCOUNT_AMOUNT);
		}
	}

	@SuppressWarnings("unused")
	private void validateNumberOfCoupons(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNumberOfCoupons())) {
			throw new InvalidDataException("Invalid Number Of Coupons", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NOOF_COUPONS);
		}
	}

	@SuppressWarnings("unused")
	private void validateDiscountType(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getDiscountType())) {
			throw new InvalidDataException("Invalid Discount Type", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_DISCOUNT_TYPE);
		}
	}

	@SuppressWarnings("unused")
	private void validateLoyaltyName(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getLoyalityName())) {
			throw new InvalidDataException("Invalid Loyalty Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_LOYALITY_NAME);
		}
	}
	
	public void validateAirtimePurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateAmount(transactionDetails);
		//validateAmountForAirtimePurchase(transactionDetails);
		//validateDesttinationMDN(transactionDetails);
		validateSourcePin(transactionDetails);
		//channelCode, sourceMessage, sourceMDN, destMDN, sourcePIN, amount, srcPocketCode, partnerCode, inCode
//		validatePartnerCode(transactionDetails);
	}
	
	public void validateAirtimePurchaseDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateInterBankTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDestinationAccountNo(transactionDetails);
		validateDestinationBankCode(transactionDetails);
	}
	
	public void validateInterBankTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	public void validateTransferToUangkuInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDestinationAccountNo(transactionDetails);
	}
	
	public void validateTransferToUangkuConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}

	public void validateUnregisteredCashOutInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateTransferId(transactionDetails);
		validateDesttinationMDN(transactionDetails);
		validateSecreteCode(transactionDetails);
		
	}
	
	public void validateUnregisteredCashOutConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
		
	}
	
	public void validateRegisteringMDN(String mdn) throws InvalidDataException {
		if (StringUtils.isBlank(mdn)) {
			throw new InvalidDataException("Invalid MDN", CmFinoFIX.NotificationCode_InvalidMDNLength, ApiConstants.PARAMETER_SOURCE_MDN);
		}
		mdn = subscriberService.normalizeMDN(mdn);
		if((mdn.length() < systemParametersService.getInteger(SystemParameterKeys.MIN_MDN_LENGTH_WITH_COUNTRYCODE)) ||
				(mdn.length() > systemParametersService.getInteger(SystemParameterKeys.MAX_MDN_LENGTH_WITH_COUNTRYCODE)) ){
			throw new InvalidDataException("Invalid MDN", CmFinoFIX.NotificationCode_InvalidMDNLength, ApiConstants.PARAMETER_SOURCE_MDN);
		}
	}
	
	public void validateIdType(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getIdType())) {
			throw new InvalidDataException("Invalid Id Type", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ID_TYPE);
		}
	}
	
	public void validateIdNumber(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getIdNumber())) {
			throw new InvalidDataException("Invalid Id Number", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ID_NUMBER);
		}
	}

	public void validateBillInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateBillerCode(transactionDetails);
		validateBillNo(transactionDetails);		
	}

	public void validateChangeSettingDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getEmail())
				&&StringUtils.isBlank(transactionDetails.getNotificationMethod())
				&&StringUtils.isBlank(transactionDetails.getLanguage())){
			throw new InvalidDataException("Invalid Request", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_LANG);
		}
		
	}
	
	
	
	public void validateFundAllocationInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
	}
	
	public void validateFundAllocationConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);		
	}

	public void validateFundWithdrawalInquiryDetails(
			TransactionDetails transactionDetails) throws InvalidDataException {
		validateOTP(transactionDetails);
		validateSourcePin(transactionDetails);
		validateOnBehalfOfMDN(transactionDetails);
		validateAmount(transactionDetails);	
	}

	public void validateFundWithdrawalConfirmDetails(
			TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
		
	}
	
	public void validateOnBehalfOfMDN(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getOnBehalfOfMDN())) {
			throw new InvalidDataException("Invalid OnBehalfOfMDN", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_ON_BEHALF_OF_MDN);
		}
	}
	
	public void validateEmail(TransactionDetails transactionDetails) throws InvalidDataException {
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(transactionDetails.getEmail())) {
			throw new InvalidDataException("Invalid Email", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_EMAIL);
		}
	}
	
	
	public void validateSubscriberReactivationDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateConfirmPin(transactionDetails);
	}

	public void validateAirtimePinPurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
		validateDesttinationMDN(transactionDetails);
		validateBenOpCode(transactionDetails);
		validatechannelCode(transactionDetails);
	}
	public void validateAirtimePinPurchaseConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	private void validateNickname(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getNickname())) {
			throw new InvalidDataException("Invalid Nickname", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_SUB_NICKNAME);
		}
	}
	public void validateChangeEmailDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateNewEmail(transactionDetails);
		validateConfirmEmail(transactionDetails);
		validateSourcePin(transactionDetails);
	}	
	
	public void validateChangeNicknameDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateNickname(transactionDetails);		
		validateSourcePin(transactionDetails);
	}
	
	public void validateChangeOtherMDNDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateOtherMDN(transactionDetails);
	}
	
	private void validateNewEmail(TransactionDetails transactionDetails) throws InvalidDataException {
		EmailValidator validator = EmailValidator.getInstance();
		if (!validator.isValid(transactionDetails.getNewEmail())) {
			throw new InvalidDataException("Invalid New Email", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_NEW_EMAIL);
		}
	}
	
	private void validateConfirmEmail(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isNotBlank(transactionDetails.getConfirmEmail()) 
				&& !transactionDetails.getConfirmEmail().equalsIgnoreCase(transactionDetails.getNewEmail())) {
			throw new InvalidDataException("New Email and Confirm Email not match", CmFinoFIX.NotificationCode_NewEmailConfirmEmailDoNotMatch, 
					ApiConstants.PARAMETER_CONFIRM_EMAIL);
		}
	}
	public void validateSubscriberRegistrationWithActivationForHub(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateNewPin(transactionDetails);
		validateConfirmPin(transactionDetails);
		validateOtherMDN(transactionDetails);
	}
	public void validateGenerateOTPDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getSourceMDN());
	}
	
	public void validateOTPValidationDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validateOTP(transactionDetails);
	}
	private void validateOtherMDN(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getOtherMdn())) {
			throw new InvalidDataException("Invalid Other MDN", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_OTHER_MDN); 
		}
	}
	public void validateNFCPocketBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validateTransID(transactionDetails);
		validatechannelCode(transactionDetails);
	}
	
	public void validateNFCTransactionHistoryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPanAndAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validatechannelCode(transactionDetails);
	}
	
	public void validateNFCTxnHistoryDetailedStmtDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPanAndAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
		validatechannelCode(transactionDetails);
		validateFromAndToDates(transactionDetails);
	}
	
	public void validateEmailNFCTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPanAndAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validateEmail(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
//		validateFromAndToDates(transactionDetails);
	}
	
	public void validateDownloadNFCTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPanAndAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validateRegisteringMDN(transactionDetails.getSourceMDN());
//		validateFromAndToDates(transactionDetails);
	}
	
	public void validateNFCCardBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		//validateCardPAN(transactionDetails);
		//validateCardAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validatechannelCode(transactionDetails);
	}
	
	public void validateModifyNFCCardAliasDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validateCardAlias(transactionDetails);
		validateSourcePin(transactionDetails);
		validatechannelCode(transactionDetails);
	}
	
	public void validateNFCCardUnlinkDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validatechannelCode(transactionDetails);
		validateSourcePin(transactionDetails);
		if(transactionDetails.getChannelCode().equals(CmFinoFIX.SourceApplication_CMS.toString())) { //for CMS
			validateTransID(transactionDetails);
		}
	}
	
	private void validateTransID(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getTransID())) {
			throw new InvalidDataException("Invalid Transaction ID", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_TRANSID);
		}
	}
	public void validateNFCCardTopup(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validateTransID(transactionDetails);
		validatechannelCode(transactionDetails);
		validateAmount(transactionDetails);
	}
	
	public void validateNFCCardTopupReversal(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validateTransID(transactionDetails);
		validatechannelCode(transactionDetails);
		validateParentTransID(transactionDetails);		
	}
	
	public void validateDonationInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException{
		validateSourcePin(transactionDetails);
		validateAmount(transactionDetails);
	}
	
	public void validateDonationConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateconfirmString(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	private void validateParentTransID(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getParentTransID())) {
			throw new InvalidDataException("Invalid Parent Transaction ID", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PARAMETER_PARENT_TRANSID);
		}
	}
	
	public void validateSubscriberStatusDetails(TransactionDetails transactionDetails)
	{
		
	}
	private void validateCardAlias(TransactionDetails transactionDetails) throws InvalidDataException {
		if (StringUtils.isBlank(transactionDetails.getCardAlias())) {
			throw new InvalidDataException("Invalid Card Alias", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing,
					ApiConstants.PARAMETER_CARD_ALIAS);
		}
	}
 	
	public void validateNFCCardLinkDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		//validation based on channel code 
		validatechannelCode(transactionDetails);
		if(CmFinoFIX.SourceApplication_WebAPI.toString().equals(transactionDetails.getChannelCode()))
		{
		log.info("TransactionRequestValidationServiceImpl :: validateNFCCardLinkDetails Request received from webapi channel");
		validateCardPAN(transactionDetails);
		validateSourcePin(transactionDetails);
		validateCardAlias(transactionDetails);
		}else if (CmFinoFIX.SourceApplication_CMS.toString().equals(transactionDetails.getChannelCode())){
			log.info("TransactionRequestValidationServiceImpl :: validateNFCCardLinkDetails Request received from CMS channel");
			validateCardPAN(transactionDetails);
			validateTransID(transactionDetails);
		}
		}

	@Override
	public void validateNFCPocketTopupInquiry(TransactionDetails transactionDetails) throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validateSourcePin(transactionDetails);
		validatechannelCode(transactionDetails);
		validateAmount(transactionDetails);
	}

	@Override
	public void validateNFCPocketTopup(TransactionDetails transactionDetails)throws InvalidDataException {
		validateCardPAN(transactionDetails);
		validatechannelCode(transactionDetails);
		validateTransferId(transactionDetails);
		validateParentTxnId(transactionDetails);
	}
	
	@Override
	public void validateKYCUpgrade(TransactionDetails transactionDetails)throws InvalidDataException {
		validateIdType(transactionDetails);
		validateIdNumber(transactionDetails);
		validateFirstName(transactionDetails);
		validateLastName(transactionDetails);
		validateAddressLine1(transactionDetails);
		validateCity(transactionDetails);
		validateState(transactionDetails);
		validateZipCode(transactionDetails);
		validateDateOfBirth(transactionDetails);
		validateKycType(transactionDetails);
		validatechannelCode(transactionDetails);
	}

	@Override
	public void validateFavoriteDetails(TransactionDetails transactionDetails)
			throws InvalidDataException {		
		String invalidParam = null;
		if (StringUtils.isBlank(transactionDetails.getSourcePIN())) {
			invalidParam = ApiConstants.PARAMETER_SOURCE_PIN;
		} else if(StringUtils.isBlank(transactionDetails.getChannelCode())) {
			invalidParam = ApiConstants.PARAMETER_CHANNEL_ID;
		} else if(StringUtils.isBlank(transactionDetails.getFavoriteCategoryID())) {
			invalidParam = ApiConstants.PARAMETER_FAVORITE_CATEGORY_ID;
		} else if(!transactionDetails.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_GENERATE_FAVORITE_JSON)
				&& StringUtils.isBlank(transactionDetails.getFavoriteValue())) {
			invalidParam = ApiConstants.PARAMETER_FAVORITE_VALUE;
		} else if(transactionDetails.getTransactionName().equals(ServiceAndTransactionConstants.TRANSACTION_EDIT_FAVORITE) &&
					StringUtils.isBlank(transactionDetails.getFavoriteLabel())) {
			invalidParam = ApiConstants.PARAMETER_FAVORITE_LABEL;
		}
		if(invalidParam != null) {
			throw new InvalidDataException("Invalid " + invalidParam, CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					invalidParam);
		}
	}
	@Override
	public void validatePartnerRegistrationDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		validateRegisteringMDN(transactionDetails.getDestMDN());
		validatePartnerCode(transactionDetails);
		validatePartnerTypeForRegistration(transactionDetails);
		validateTradeName(transactionDetails);
		validateAddress(transactionDetails);
		validateFranchisePhoneNumber(transactionDetails);
		validaYearEstablished(transactionDetails);
		validateRepresentativName(transactionDetails);
		validateEmail(transactionDetails);
		validateLineOFBussiness(transactionDetails);
	}
	
	public void validateSubscriberClosingInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		
		validateDesttinationMDN(transactionDetails);
	}
	
	public void validateSubscriberClosingDetails(TransactionDetails transactionDetails) throws InvalidDataException {
		
		validateDesttinationMDN(transactionDetails);
		validateOTP(transactionDetails);
	}

	private void validateLineOFBussiness(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getIndustryClassification()))
			throw new InvalidDataException("Invalid industryClassification", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_INDUSTRY_CLASIFICATION);
	}

	private void validateRepresentativName(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getRepresentativeName()))
			throw new InvalidDataException("Invalid representativeName", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_REPRESENTATIVE_NAME);
	}

	private void validaYearEstablished(TransactionDetails transactionDetails) throws InvalidDataException {
		if(transactionDetails.getYearEstablished()==null)
			throw new InvalidDataException("Invalid yearEstablished", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_YEAR_ESTABLISHED);
	}

	private void validateFranchisePhoneNumber(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getFranchisePhoneNumber())
				||(!StringUtils.isNumeric(transactionDetails.getFranchisePhoneNumber())))
			throw new InvalidDataException("Invalid franchisePhoneNumber", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_FRANCHISE_NUMBER);
	}

	private void validateAddress(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getPlotNo()))
			throw new InvalidDataException("Invalid plotNo", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_PLOT_NO);		
		if(StringUtils.isBlank(transactionDetails.getCity()))
			throw new InvalidDataException("Invalid city", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_CITY);		
		if(StringUtils.isBlank(transactionDetails.getRegionName()))
			throw new InvalidDataException("Invalid state or region", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_REGION_NAME);		
		if(StringUtils.isBlank(transactionDetails.getCountry()))
			throw new InvalidDataException("Invalid Country", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_COUNTRY);		
		if(StringUtils.isBlank(transactionDetails.getPostalCode()))
			throw new InvalidDataException("Invalid postalCode", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_POSTAL_CODE);		
	}

	private void validateTradeName(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getTradeName()))
			throw new InvalidDataException("Invalid tradeName", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_TRADE_NAME);		
	}

	private void validatePartnerTypeForRegistration(TransactionDetails transactionDetails) throws InvalidDataException {
		try{
			String partnertype = transactionDetails.getPartnerType().toString();
			String[] partnerTypes = systemParametersService.getString(SystemParameterKeys.ALLOWED_PARTNERS_TOREGISTER_THROUGHAPI).split(GeneralConstants.COMMA_STRING);
			for(String type:partnerTypes){
					if(partnertype.equals(type))
						return;
				}
			log.info("Partner Type :"+partnertype+" not allowed to register through API");
			throw new InvalidDataException("Partner Type :"+partnertype+" not allowed to register through API", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_PARTNER_TYPE);
		}catch (Exception e) {
			log.error("Error:",e);
			throw new InvalidDataException("Invalid partnerType", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, ApiConstants.PARAMETER_PARTNER_TYPE);
		}		
	}
	
	private void validateprofileImageString(TransactionDetails transactionDetails) throws InvalidDataException {
		if(StringUtils.isBlank(transactionDetails.getProfileImageString()))
			throw new InvalidDataException("Invalid Profile Image data", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
					ApiConstants.PROFILE_IMAGE_STRING);		
	}

	public void validateUpdateProfile(TransactionDetails transactionDetails) throws InvalidDataException {
		validateSourcePin(transactionDetails);
		validateprofileImageString(transactionDetails);
	}


public void validateProductReferralDetails(TransactionDetails transactionDetails) throws InvalidDataException {	
	validateFullName(transactionDetails);
	validateDesttinationMDN(transactionDetails);
	validateProductDesired(transactionDetails);
}

private void validateProductDesired(TransactionDetails transactionDetails)throws InvalidDataException {
	if (StringUtils.isBlank(transactionDetails.getProductDesired())) {
		throw new InvalidDataException("Invalid Product Desired", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
				ApiConstants.PARAMETER_PRODUCT_DESIRED);
	}
	
}

private void validateFullName(TransactionDetails transactionDetails)throws InvalidDataException {
	if (StringUtils.isBlank(transactionDetails.getFullName())) {
		throw new InvalidDataException("Invalid Full Name", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
				ApiConstants.PARAMETER_FULL_NAME);
	}
	
}
	
	
	
	
	
}
