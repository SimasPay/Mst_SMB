/**
 * 
 */
package com.mfino.transactionapi.constants;

import com.mfino.constants.ServiceAndTransactionConstants;

/**
 * @author Chaitanya
 * 
 */
public class ApiConstants extends ServiceAndTransactionConstants{

	//webapi specific transactions

	public static final String	TRANSACTION_LOGIN	                = "Login";

	public static final String	TRANSACTION_LOGOUT	                = "Logout";
	
	//parameters
	public static final String	PARAMETER_SERVICE_NAME	            = "service";

	public static final String	PARAMETER_TRANSACTIONNAME	        = "txnName";

	public static final String	PARAMETER_SOURCE_MDN	            = "sourceMDN";

	public static final String	PARAMETER_NEW_PIN	                = "newPIN";

	public static final String	PARAMETER_OTP	                    = "otp";

	public static final String	PARAMETER_CHANNEL_ID	            = "channelID";

	public static final String	PARAMETER_CONFIRM_PIN	            = "confirmPIN";

	public static final String	PARAMETER_SOURCE_PIN	            = "sourcePIN";

	public static final String	PARAMETER_DEST_MDN	                = "destMDN";

	public static final String	PARAMETER_AMOUNT	                = "amount";

	public static final String	PARAMETER_OLD_PIN	                = "oldPIN";

	public static final String	PARAMETER_SECRET_ANSWER	            = "secretAnswer";

	public static final String	PARAMETER_CONTACT_NO	            = "contactNumber";

	public static final String	PARAMETER_EMAIL	                    = "email";

	public static final String	PARAMETER_NEW_EMAIL	   		         = "newEmail";
	
	public static final String	PARAMETER_CONFIRM_EMAIL	            = "confirmEmail";
	
	public static final String	PARAMETER_LANG	                    = "language";

	public static final String	PARAMETER_NOTIFICATION_METHOD	    = "notificationMethod";

	public static final String	PARAMETER_PARTNER_CODE	            = "partnerCode";

	public static final String	PARAMETER_AGENT_CODE	            = "agentCode";
	
	public static final String	PARAMETER_MERCHANT_CODE	            = "merchantCode";

	public static final String	PARAMETER_BUCKET_TYPE	            = "bucketType";

	public static final String	PARAMETER_SRC_POCKET_CODE	        = "sourcePocketCode";

	public static final String	PARAMETER_CARDPAN_SUFFIX	        = "cardPANSuffix";

	public static final String	PARAMETER_SRC_MESSAGE	            = "sourceMessage";

	public static final String	PARAMETER_DEST_POCKET_CODE	        = "destPocketCode";

	public static final String	PARAMETER_TRANSFER_ID	            = "transferID";

	public static final String	PARAMETER_CONFIRMED	                = "confirmed";

	public static final String	PARAMETER_PARENTTXN_ID	            = "parentTxnID";

	public static final String	PARAMETER_ISDEFAULT	                = "isDefault";

	public static final String	PARAMETER_BANK_ID	                = "bankID";

	public static final String	PARAMETER_BILLER_NAME	            = "billerName";

	public static final String	PARAMETER_CUSTOMER_ID	            = "customerID";

	public static final String	PARAMETER_BILL_DETAILS	            = "billDetails";

	public static final String	PARAMETER_DATA	                    = "data";

	public static final String	PARAMETER_MFS_BILLER_CODE	        = "mfsBillerCode";

	public static final String	PARAMETER_INVOICE_NO	            = "invoiceNo";

	public static final String	PARAMETER_SUB_MDN	                = "subMDN";

	public static final String	PARAMETER_SUB_FIRSTNAME	            = "subFirstName";

	public static final String	PARAMETER_SUB_LASTNAME	            = "subLastName";
	
	public static final String	PARAMETER_SUB_MothersMaidenName	    = "subMothersMaidenName";
	
	public static final String	PARAMETER_ACCOUNT_TYPE	            = "accountType";

	public static final String	PARAMETER_APPLICATION_ID	        = "appId";

	public static final String	PARAMETER_DOB	                    = "dob";

	public static final String	PARAMETER_SALT	                    = "salt";

	public static final String	PARAMETER_ACTIVATION_NEWPIN	        = "activationNewPin";
	
	public static final String	PARAMETER_ACTIVATION_CONFIRMPIN	    = "activationConfirmPin";
	
	public static final String	PARAMETER_AUTHENTICATION_STRING	    = "authenticationString";

	public static final String	PARAMETER_SUBSCRIBER_MDN	        = "subscriberMDN";

	public static final String PARAMETER_BILLER_CODE				= "billerCode";
	
	public static final String PARAMETER_BILL_NO					= "billNo";
	
	public static final String PARAMETER_KYC_TYPE = "kycType";
	
	public static final String PARAMETER_CITY = "city";

	public static final String	PARAMETER_SUB_NICKNAME= "nickname";
	
	public static final String	PARAMETER_FAVORITE_CATEGORY_ID = "favoriteCategoryID";
	
	public static final String	PARAMETER_SUBSCRIBER_FAVORITE_ID = "subscriberFavoriteID";
	
	public static final String	PARAMETER_FAVORITE_LABEL = "favoriteLabel";
	
	public static final String	PARAMETER_FAVORITE_VALUE = "favoriteValue";
	
	public static final String PARAMETER_FAVORITE_CODE = "favoriteCode";
	
	public static final String PARAMETER_NEXT_OF_KIN = "nextOfKin";
	
	public static final String PARAMETER_NEXT_OF_KIN_NO = "nextOfKinNo";
	
	public static final String PARAMETER_PLOT_NO = "plotNo";
	
	public static final String PARAMETER_STREET_ADDRESS = "streetAddress";
	
	public static final String PARAMETER_REGION_NAME = "regionName";
	
	public static final String PARAMETER_COUNTRY = "country";
	
	public static final String PARAMETER_ID_TYPE = "idType";
	
	public static final String PARAMETER_ID_NUMBER = "idNumber";
	
	public static final String PARAMETER_DATE_OF_EXPIRY = "dateOfExpiry";
	
	public static final String PARAMETER_ADDRESS_PROOF = "addressProof";
	
	public static final String PARAMETER_BIRTH_PLACE = "birthPlace";
	
	public static final String PARAMETER_NATIONALITY = "nationality";
	
	public static final String PARAMETER_COMPANY_NAME = "companyName";
	
	public static final String PARAMETER_SUBSCRIBER_MOBILE_COMPANY  = "subscriberMobileCompany";
	
	public static final String PARAMETER_CERT_OF_INCORP = "certOfIncorp";
	
	public static final String PARAMETER_DEST_BANK_CODE				= "destBankCode";
	
	public static final String PARAMETER_DEST_ACCOUNT_NO			= "destAccountNo";
	
	public static final String PARAMETER_DESTINATION_BANK_ACCOUNT_NO = "destBankAccount";
	
	public static final String PARAMETER_ON_BEHALF_OF_MDN            = "onBehalfOfMDN";
	
	public static final String	PARAMETER_SECRETE_CODE	            = "secreteCode";
	
	public static final String	PARAMETER_APPTYPE					= "apptype";
	
	public static final String	PARAMETER_APPVERSION				= "appversion";
	
	public static final String	PARAMETER_APPOS						= "appos";
	
	public static final String  PARAMETER_INSTITUTION_ID 			= "institutionID";
	
	public static final String	PARAMETER_APPROVAL_REQUIRED         = "approvalRequired";
	
	public static final String	PARAMETER_BANK_ACCOUNT_TYPE         = "bankAccountType";
	
	public static final String	PARAMETER_CARD_PAN		            = "cardPan";
	
	public static final String	PARAMETER_CARD_ALIAS		            = "cardAlias";
	
	public static final String	PARAMETER_AUTHORIZING_FIRSTNAME         = "authorizingFirstName";
	
	public static final String	PARAMETER_AUTHORIZING_LASTNAME         = "authorizingLastName";
	
	public static final String	PARAMETER_AUTHORIZING_IDNUMBER        = "authorizingIdNumber";
	
	public static final String	PARAMETER_APPROVAL_COMMENTS         = "approvalComments";
	
	public static final String PARAMETER_PAGE_NUMBER = "pageNumber";
	
	public static final String PARAMETER_NUM_RECORDS = "numRecords";
	
	public static final String PARAMETER_OTHER_MDN = "otherMDN";
	
	public static final String  PARAMETER_INTEGRATION_NAME 			= "integrationName";
	
	public static final String  PARAMETER_IPADRESS					= "ipAddress";
	
	public static final String PARAMETER_TRANSID = "transID";
	
	public static final String  PARAMETER_AUTHENTICATION_KEY		= "authenticationKey";
	
	public static final String PARAMETER_CATEGORY                   ="category";
	
	public static final String PARAMETER_VERSION                   ="version";

	public static final String PARAMETER_NARRATION = "narration";
	
	public static final String PARAMETER_BEN_OP_CODE = "benOpCode";

	
	public static final String PARAMETER_DESCRIPTION 				= "description";
	
	public static final String PARAMETER_MFA_TRANSACTION 				= "mfaTransaction";
	
	public static final String	PARAMETER_MFA_OTP	                    = "mfaOtp";
	
	public static final String	PARAMETER_PARENT_TRANSID = "parentTransID";
	
    public static final String	PARAMETER_PARTNER_TYPE 		=  "partnerType";
	
	public static final String	PARAMETER_TRADE_NAME 		=  "tradeName";
	
	public static final String	PARAMETER_POSTAL_CODE 		=  "postalCode";
	
	public static final String	PARAMETER_USER_NAME 		=  "userName";
	
	public static final String	PARAMETER_OUTLET_CLASIFICATION 		=  "outletClasification";
	
	public static final String	PARAMETER_FRANCHISE_NUMBER 		=  "franchisePhoneNumber";
	
	public static final String	PARAMETER_FAX_NUBER 		=  "faxNumber";
	
	public static final String	PARAMETER_TYPEOF_ORGANIZATION 		=  "typeOfOrganization";
	
	public static final String	PARAMETER_WEBSITE 		=  "webSite";
	
	public static final String	PARAMETER_INDUSTRY_CLASIFICATION 		=  "industryClassification";
	
	public static final String	PARAMETER_NOOF_OUTLETS		=  "numberOfOutlets";
	
	public static final String	PARAMETER_YEAR_ESTABLISHED 		=  "yearEstablished";
	
	public static final String	PARAMETER_OUTLET_LINE1 		=  "outletAddressLine1";
	
	public static final String	PARAMETER_OUTLET_LINE2 		=  "outletAddressLine2";
	
	public static final String	PARAMETER_OUTLET_CITY 		=  "outletAddressCity";
	
	public static final String	PARAMETER_OUTLET_STATE 		=  "outletAddressState";
	
	public static final String	PARAMETER_OUTLET_ZIPCODE 		=  "outletAddressZipcode";
	
	public static final String	PARAMETER_OUTLET_COUNTRY 		=  "outletAddressCountry";
	
	public static final String	PARAMETER_AUTHORIZED_REPRESENTATIVE		=  "authorizedRepresentative";
	
	public static final String	PARAMETER_REPRESENTATIVE_NAME 		=  "representativeName";
	
	public static final String	PARAMETER_DESIGNATION 		=  "designation";
	
	public static final String	PARAMETER_AUTHORIZED_FAX_NUMBER 		=  "authorizedFaxNumber";
	
	public static final String	PARAMETER_FROM_DATE 		=  "fromDate";
	
	public static final String	PARAMETER_TO_DATE 		=  "toDate";
	
	public static final String PARAMETER_PAYMENT_MODE = "paymentMode";
	
	public static final String PARAMETER_ADDRESS_LINE_1 = "addressLine1";
	
	public static final String PARAMETER_ZIP_CODE = "zipCode";
	
	public static final String PARAMETER_STATE = "state";
	
	public static final String	DUMMY_BANK_ID	                    = "Not Yet";

	public static final String	CONSTANT_VALUE_ZERO	                = "0";

	public static final String	CONSTANT_VALUE_TRUE	                = "true";
	
	public static final String	CONSTANT_VALUE_FALSE	                = "false";

	public static final String	SERVICE_PROVIDER_NAME	            = "serviceProviderName";

	public static final String	ENV_VARIABLE_ENABLE_WEBAPI_SECURITY	= "ENABLEWEBAPISECURITY";
	
	public static final String	COMPANY_ID	= "companyID";

	public static final String	UTF_8	                            = "UTF-8";

	public static final String	US_ASCII	                        = "US-ASCII";

	public static final String SCHEME_HTTPS = "https";
	
	public static final String SCHEME_HTTP = "http";
	
	public static final String POCKET_CODE_SVA = "1";
	
	public static final String POCKET_CODE_BANK = "2";
	
	public static final String PROFILE_GTBANK = "gtbank";
	
	public static final String PROFILE_ZENITHBANK = "zenithbank";

	public static final String BILL_INQUIRY = "billInquiry";
	
	public static final String PARAMETER_MERCHANT_DATA = "merchantData";
	
	public static final String PARAMETER_DENOM_CODE = "denomCode";
	
	public static final String PARAMETER_NOMINAL_AMOUNT = "nominalAmount";
	
	public static final String PARAMETER_USER_API_KEY = "userAPIKey";
	
	public static final String PARAMETER_SCTL_ID = "sctlId";
	
	public static boolean isSecuredParameter(String parameterName){
		boolean isSecure = false;
		if(PARAMETER_ACTIVATION_CONFIRMPIN.equals(parameterName) ||
				(PARAMETER_ACTIVATION_NEWPIN.equals(parameterName)) ||
				(PARAMETER_AUTHENTICATION_STRING.equals(parameterName)) ||
				(PARAMETER_CARDPAN_SUFFIX.equals(parameterName)) ||
				(PARAMETER_CONFIRM_PIN.equals(parameterName)) ||
				(PARAMETER_NEW_PIN.equals(parameterName)) ||
				(PARAMETER_OLD_PIN.equals(parameterName)) ||
				(PARAMETER_OTP.equals(parameterName)) ||
				(PARAMETER_SALT.equals(parameterName)) ||
				(PARAMETER_SECRET_ANSWER.equals(parameterName)) ||
				(PARAMETER_SOURCE_PIN.equals(parameterName))||
				(PARAMETER_SECRETE_CODE.equals(parameterName))){
			isSecure = true;
		}
		return isSecure;
	}
}
