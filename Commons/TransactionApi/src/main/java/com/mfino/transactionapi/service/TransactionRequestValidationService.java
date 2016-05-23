/**
 * 
 */
package com.mfino.transactionapi.service;

import com.mfino.exceptions.InvalidDataException;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * @author Shashank
 *
 */
public interface TransactionRequestValidationService {


	public void validateConfirmPin(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateNewPin(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSourcePin(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateOTP(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAmount(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDesttinationMDN(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDestinationAccountNo(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDestinationBankCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransferId(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateParentTxnId(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateconfirmString(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validatePartnerCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateBillerCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateBillNo(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateFirstName(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateLastName(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateMothersMaidenName(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateApplicationId(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDateOfBirth(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCity(TransactionDetails transactionDetails)  throws InvalidDataException;
	
	public void validateKycType(TransactionDetails transactionDetails)  throws InvalidDataException;
	
	public void validateKinName(TransactionDetails transactionDetails)  throws InvalidDataException;
	
	public void validateKinNo(TransactionDetails transactionDetails)  throws InvalidDataException;
	
	public void validateAccountType(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSalt(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAuthenticationString(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSecreteCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateInstitutionID(TransactionDetails transactionDetails)  throws InvalidDataException;
	
	public void validateBankAccountType(TransactionDetails transactionDetails) throws InvalidDataException;
			
	public void validateCardPAN(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAuthorizingFirstName(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAuthorizingLastName(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAuthorizingIdNumber(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateApprovalComments(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNarration(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateBenOpCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validatechannelCode(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSubscriberActivationDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateResetPinByOTPDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateForgotPinDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransactionStatusDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateChangePinDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCheckBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransactionHistoryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransactionHistoryDetailedStmtDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateEmailTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDownloadTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransactionPendingSettlementsForPartner(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashOutInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashOutConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashOutAtATMInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashOutAtATMConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateInterEmoneyTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateInterEmoneyTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAgentToAgentTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validatAgentToAgentTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashInInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateCashInConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSubscriberRegistrationByAgentDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateSubscriberRegistration(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSubscriberRegistrationWithActivation(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSubscriberRegistrationThroughWebDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateGenerateOTPDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateOTPValidationDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateSubscriberRegistrationWithActivationForHub(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAutoApproverDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validatePurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validatePurchaseConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateLoginDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateLogoutDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateBillPayInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAgentBillPayInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAgentBillPayConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateBillPayConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAirtimePurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAirtimePurchaseDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateInterBankTransferInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateInterBankTransferConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateUnregisteredCashOutInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateUnregisteredCashOutConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateRegisteringMDN(String mdn) throws InvalidDataException;
	
	public void validateIdType(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateIdNumber(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateBillInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateChangeSettingDetails(TransactionDetails transactionDetails) throws InvalidDataException;
		
	public void validateFundAllocationInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateFundAllocationConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateFundWithdrawalInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateFundWithdrawalConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateOnBehalfOfMDN(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateEmail(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateSubscriberReactivationDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateSubscriberStatusDetails(TransactionDetails transactionDetails);

	public void validateFRSCPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateFRSCPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateAirtimePinPurchaseInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	public void validateAirtimePinPurchaseConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateChangeEmailDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateChangeNicknameDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateChangeOtherMDNDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateNFCPocketBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCCardUnlinkDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCCardTopup(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCCardTopupReversal(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateNFCCardLinkDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCTransactionHistoryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCTxnHistoryDetailedStmtDetails(TransactionDetails transactionDetails) throws InvalidDataException;
		
	public void validateEmailNFCTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDownloadNFCTxnHistoryAsPDFDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCCardBalanceDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateModifyNFCCardAliasDetails(TransactionDetails transactionDetails) throws InvalidDataException;	
	
	public void validateNFCPocketTopupInquiry(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateNFCPocketTopup(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateKYCUpgrade(TransactionDetails transactionDetails)throws InvalidDataException;
	
	public void validateFavoriteDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	

	public void validatePartnerRegistrationDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateStarTimesPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateStarTimesPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDonationInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateDonationConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateQrPaymentInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateQrPaymentConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
	
	public void validateTransferToUangkuInquiryDetails(TransactionDetails transactionDetails) throws InvalidDataException;

	public void validateTransferToUangkuConfirmDetails(TransactionDetails transactionDetails) throws InvalidDataException;
}