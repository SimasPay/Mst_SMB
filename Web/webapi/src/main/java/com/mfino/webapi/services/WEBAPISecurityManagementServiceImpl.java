package com.mfino.webapi.services;

import static com.mfino.fix.CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SecurityConstants;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.crypto.CryptographyService;
import com.mfino.domain.ChannelSessionManagement;
import com.mfino.domain.SubscriberMDN;
import com.mfino.exceptions.InvalidMDNException;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.ChannelSessionManagementService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.webapi.utilities.IUserDataContainer;
import com.mfino.webapi.utilities.InvalidWeabpiSessionException;
import com.mfino.webapi.utilities.ReceivedUserDataContainer;
import com.mfino.webapi.utilities.SecurityDisabledUserDataContainer;
import com.mfino.webapi.utilities.SecurityEnabledUserDataContainer;

@Service("WEBAPISecurityManagementServiceImpl")
public class WEBAPISecurityManagementServiceImpl implements WEBAPISecurityManagementService{
	private static Logger	log	= LoggerFactory.getLogger(WEBAPISecurityManagementServiceImpl.class);

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("WebAPIUtilsServiceImpl")
	private WebAPIUtilsService webAPIUtilsService;
	
	@Autowired
	@Qualifier("ChannelSessionManagementServiceImpl")
	private ChannelSessionManagementService channelSessionManagementService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private  boolean isUserSessionValid(String mdn) {
//		SubscriberMDNDAO sdao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN smdn = subscriberMdnService.getByMDN(mdn);
//		ChannelSessionManagementDAO dao = DAOFactory.getInstance().getChannelSessionManagementDAO();
		try {
			ChannelSessionManagement csm = channelSessionManagementService.getChannelSessionManagemebtByMDNID(smdn.getID());
			return isCSMStatusValid(csm);
		}
		catch (Exception ex) {
			log.error("Error during channel session validation", ex);
		}
		return false;
	}

	
	private  boolean isCSMStatusValid(ChannelSessionManagement csm) {

		Timestamp lastRequestTime = csm.getLastRequestTime();
		Timestamp presentTime = new Timestamp();
		long timeDiff = presentTime.getTime() - lastRequestTime.getTime();

		if (timeDiff >= SecurityConstants.TIME_SINCE_LAST_LOGIN_MILLIS)
			return false;

		return true;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	private  boolean isLoginStatusValid(String transactionName, String sourceMDN) {
		if (ApiConstants.TRANSACTION_LOGIN.equals(transactionName) || ApiConstants.TRANSACTION_LOGOUT.equals(transactionName) || ApiConstants.TRANSACTION_RESEND_MFAOTP.equals(transactionName))
			return true;
		return isUserSessionValid(sourceMDN);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public boolean isWebApiUserSessionValid(HttpServletRequest request, ServletOutputStream writer) {

		String sourceMDN = request.getParameter(ApiConstants.PARAMETER_SOURCE_MDN);
		if (!StringUtils.isBlank(sourceMDN)) {

			String transactionName = request.getParameter(ApiConstants.PARAMETER_TRANSACTIONNAME);
			transactionName = transactionName.trim();
			log.info("Received webapi request with the transactionName=\"" + transactionName + "\"");

			sourceMDN = subscriberService.normalizeMDN(sourceMDN);
			if (!isLoginStatusValid(transactionName, sourceMDN)) {
				webAPIUtilsService.sendSessionTimeoutError(writer, sourceMDN);
				return false;
			}
			return true;
		}
		else {
			webAPIUtilsService.sendError(NotificationCode_InvalidWebAPIRequest_ParameterMissing, writer, sourceMDN, ApiConstants.PARAMETER_SOURCE_MDN);
			return false;
		}
	}

	public boolean isWebapiSecurityTrue() {
		String envVar = System.getenv(ApiConstants.ENV_VARIABLE_ENABLE_WEBAPI_SECURITY);
		if (ApiConstants.CONSTANT_VALUE_FALSE.equals(envVar))
			return false;
		return true;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public IUserDataContainer getRequestData(HttpServletRequest request, ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException {
		log.info("getRequestData: Begin");
		IUserDataContainer udContainer = null;
		
		ReceivedUserDataContainer rudContainer = initUserDataContainer(request);
		
		if (bypassSessionChecks(rudContainer.getTransactionName(), rudContainer.getServiceName()) || !isWebapiSecurityTrue()) {
			return SecurityDisabledUserDataContainer.createSecurityDisabledUserDataContainer(rudContainer);
		}

//		SubscriberMDNDAO sdao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN smdn = subscriberMdnService.getByMDN(rudContainer.getSourceMdn());

		if (smdn == null) {
			throw new InvalidMDNException("MDN is not valid");
		}

//		ChannelSessionManagementDAO dao = DAOFactory.getInstance().getChannelSessionManagementDAO();
		ChannelSessionManagement csm = channelSessionManagementService.getChannelSessionManagemebtByMDNID(smdn.getID());
		if(isLoginEnabled)
		{
			if (csm == null || !isCSMStatusValid(csm)) {
				throw new InvalidWeabpiSessionException("Session is not valid");
			}
			Timestamp ts = new Timestamp();
			csm.setLastRequestTime(ts);
			csm.setRequestCountAfterLogin(csm.getRequestCountAfterLogin() + 1);
			csm.setLastUpdateTime(ts);
			channelSessionManagementService.saveCSM(csm);
		}		

		KeyParameter kp = new KeyParameter(CryptographyService.hexToBin(csm.getSessionKey().toCharArray()));
		udContainer = SecurityEnabledUserDataContainer.createSecurityEnabledUserDataContainer(rudContainer, kp);
		udContainer.setKeyParameter(kp);
		log.info("getRequestData: End");
		return udContainer;
	}

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public  IUserDataContainer getHttpsRequestData(HttpServletRequest request, ServletOutputStream writer, boolean isLoginEnabled) throws InvalidWeabpiSessionException, InvalidMDNException {
		log.info("getHttpsRequestData: Begin");
		IUserDataContainer udContainer = null;

		ReceivedUserDataContainer rudContainer = initUserDataContainer(request);
		
		if (bypassSessionChecks(rudContainer.getTransactionName(), rudContainer.getServiceName()) || !isWebapiSecurityTrue()) {
			return SecurityDisabledUserDataContainer.createSecurityDisabledUserDataContainer(rudContainer);
		}
		
//		SubscriberMDNDAO sdao = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMDN smdn = subscriberMdnService.getByMDN(rudContainer.getSourceMdn());

		if (smdn == null) {
			throw new InvalidMDNException("MDN is not valid");
		}
//		ChannelSessionManagementDAO dao = DAOFactory.getInstance().getChannelSessionManagementDAO();
		ChannelSessionManagement csm = channelSessionManagementService.getChannelSessionManagemebtByMDNID(smdn.getID());
		if(isLoginEnabled)
		{
			if (csm == null || !isCSMStatusValid(csm)) {
				throw new InvalidWeabpiSessionException("Session is not valid");
			}
			Timestamp ts = new Timestamp();
			csm.setLastRequestTime(ts);
			csm.setRequestCountAfterLogin(csm.getRequestCountAfterLogin() + 1);
			csm.setLastUpdateTime(ts);
			channelSessionManagementService.saveCSM(csm);
		}		
		
		udContainer = SecurityDisabledUserDataContainer.createSecurityDisabledUserDataContainer(rudContainer);
		log.info("getHttpsRequestData: End");
		return udContainer;
	}

	/**
	 * @param request
	 * @param log
	 * @return
	 * @throws InvalidMDNException
	 * @throws InvalidWeabpiSessionException
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	protected  ReceivedUserDataContainer initUserDataContainer(HttpServletRequest request) throws InvalidMDNException,
			InvalidWeabpiSessionException {
		log.info("initUserDataContainer: Begin");
		ReceivedUserDataContainer rudContainer = new ReceivedUserDataContainer();

		String sourceMDN = request.getParameter(ApiConstants.PARAMETER_SOURCE_MDN);
		String transactionName = request.getParameter(ApiConstants.PARAMETER_TRANSACTIONNAME);
		String sourcePIN = request.getParameter(ApiConstants.PARAMETER_SOURCE_PIN);
		String serviceName = request.getParameter(ApiConstants.PARAMETER_SERVICE_NAME);
		String channelCodeStr = request.getParameter(ApiConstants.PARAMETER_CHANNEL_ID);
		String otp = request.getParameter(ApiConstants.PARAMETER_OTP);
		String newPIN = request.getParameter(ApiConstants.PARAMETER_NEW_PIN);
		String confirmPIN = request.getParameter(ApiConstants.PARAMETER_CONFIRM_PIN);
		String transferIDStr = request.getParameter(ApiConstants.PARAMETER_TRANSFER_ID);
		String salt = request.getParameter(ApiConstants.PARAMETER_SALT);
		String authentication = request.getParameter(ApiConstants.PARAMETER_AUTHENTICATION_STRING);
		String srcMessage = request.getParameter(ApiConstants.PARAMETER_SRC_MESSAGE);
		String firstName = request.getParameter(ApiConstants.PARAMETER_SUB_FIRSTNAME);
		String lastName = request.getParameter(ApiConstants.PARAMETER_SUB_LASTNAME);
		String mothersMaidenName = request.getParameter(ApiConstants.PARAMETER_SUB_MothersMaidenName);
		String applicationID = request.getParameter(ApiConstants.PARAMETER_APPLICATION_ID);
		String amountStr = request.getParameter(ApiConstants.PARAMETER_AMOUNT);
		String submdn = request.getParameter(ApiConstants.PARAMETER_SUB_MDN);
		String dob = request.getParameter(ApiConstants.PARAMETER_DOB);
		String accontTypeStr = request.getParameter(ApiConstants.PARAMETER_ACCOUNT_TYPE);
		String destMDN = request.getParameter(ApiConstants.PARAMETER_DEST_MDN);
		String destPocketCode = request.getParameter(ApiConstants.PARAMETER_DEST_POCKET_CODE);
		String parentTrxnIdStr = request.getParameter(ApiConstants.PARAMETER_PARENTTXN_ID);
		String confirmedStr = request.getParameter(ApiConstants.PARAMETER_CONFIRMED);
		String srcPocketCode = request.getParameter(ApiConstants.PARAMETER_SRC_POCKET_CODE);
		String agentCode = request.getParameter(ApiConstants.PARAMETER_AGENT_CODE);
		String partnerCode = request.getParameter(ApiConstants.PARAMETER_PARTNER_CODE);
		String activationNewPIN = request.getParameter(ApiConstants.PARAMETER_ACTIVATION_NEWPIN);
		String activationConfirmPIN = request.getParameter(ApiConstants.PARAMETER_ACTIVATION_CONFIRMPIN);
		String billerCode = request.getParameter(ApiConstants.PARAMETER_BILLER_CODE);
		String billNo = request.getParameter(ApiConstants.PARAMETER_BILL_NO);
		String companyID = request.getParameter(ApiConstants.COMPANY_ID);
		String destAccountNo = request.getParameter(ApiConstants.PARAMETER_DEST_ACCOUNT_NO);
		String destBankCode = request.getParameter(ApiConstants.PARAMETER_DEST_BANK_CODE);
		String secreteCode = request.getParameter(ApiConstants.PARAMETER_SECRETE_CODE);
		String destinationBankAccountNo = request.getParameter(ApiConstants.PARAMETER_DESTINATION_BANK_ACCOUNT_NO);
		String onBehalfOfMDN = request.getParameter(ApiConstants.PARAMETER_ON_BEHALF_OF_MDN);
		String category  = request.getParameter(ApiConstants.PARAMETER_CATEGORY);
		String version   = request.getParameter(ApiConstants.PARAMETER_VERSION);
		String otherMDN = request.getParameter(ApiConstants.PARAMETER_OTHER_MDN);
		
		String kycType = request.getParameter(ApiConstants.PARAMETER_KYC_TYPE);
		String city = request.getParameter(ApiConstants.PARAMETER_CITY);
		String nextOfKin = request.getParameter(ApiConstants.PARAMETER_NEXT_OF_KIN);
		String nextOfKinNo = request.getParameter(ApiConstants.PARAMETER_NEXT_OF_KIN_NO);
		String email = request.getParameter(ApiConstants.PARAMETER_EMAIL);
		String plotNo = request.getParameter(ApiConstants.PARAMETER_PLOT_NO);
		String streetAddress = request.getParameter(ApiConstants.PARAMETER_STREET_ADDRESS);
		String regionName = request.getParameter(ApiConstants.PARAMETER_REGION_NAME);
		String country = request.getParameter(ApiConstants.PARAMETER_COUNTRY);
		String idType = request.getParameter(ApiConstants.PARAMETER_ID_TYPE);
		String idNumber = request.getParameter(ApiConstants.PARAMETER_ID_NUMBER);
		String dateOfExpiry = request.getParameter(ApiConstants.PARAMETER_DATE_OF_EXPIRY);
		String addressProof = request.getParameter(ApiConstants.PARAMETER_ADDRESS_PROOF);
		String birthPlace = request.getParameter(ApiConstants.PARAMETER_BIRTH_PLACE);
		String nationality = request.getParameter(ApiConstants.PARAMETER_NATIONALITY);
		String companyName = request.getParameter(ApiConstants.PARAMETER_COMPANY_NAME);
		String subscriberMobileCompany = request.getParameter(ApiConstants.PARAMETER_SUBSCRIBER_MOBILE_COMPANY);
		String certOfIncorp = request.getParameter(ApiConstants.PARAMETER_CERT_OF_INCORP);
		String language = request.getParameter(ApiConstants.PARAMETER_LANG);
		String notificationMethod = request.getParameter(ApiConstants.PARAMETER_NOTIFICATION_METHOD);
		String institutionID = request.getParameter(ApiConstants.PARAMETER_INSTITUTION_ID);
		//tarun
		boolean approvalRequired = true;
		//approvalRequired = (request.getParameter(ApiConstants.PARAMETER_APPROVAL_REQUIRED)).equalsIgnoreCase("true");
		if (("false").equalsIgnoreCase(request.getParameter(ApiConstants.PARAMETER_APPROVAL_REQUIRED))) {
			approvalRequired = false;
		}
		String bankAccountType = request.getParameter(ApiConstants.PARAMETER_BANK_ACCOUNT_TYPE);
		String cardPAN = request.getParameter(ApiConstants.PARAMETER_CARD_PAN);
		String cardAlias = request.getParameter(ApiConstants.PARAMETER_CARD_ALIAS);
		String authorizingFirstName = request.getParameter(ApiConstants.PARAMETER_AUTHORIZING_FIRSTNAME );
		String authorizingLastName = request.getParameter(ApiConstants.PARAMETER_AUTHORIZING_LASTNAME );
		String authorizingIdNumber = request.getParameter(ApiConstants.PARAMETER_AUTHORIZING_IDNUMBER);
		String approvalComments = request.getParameter(ApiConstants.PARAMETER_APPROVAL_COMMENTS);
		String narration = request.getParameter(ApiConstants.PARAMETER_NARRATION);
		String benOpCode = request.getParameter(ApiConstants.PARAMETER_BEN_OP_CODE); 
		String description = request.getParameter(ApiConstants.PARAMETER_DESCRIPTION);
		String mfaTransaction = request.getParameter(ApiConstants.PARAMETER_MFA_TRANSACTION);
		String mfaOtp = request.getParameter(ApiConstants.PARAMETER_MFA_OTP);
		String transID = request.getParameter(ApiConstants.PARAMETER_TRANSID);
		String pageNumber = request.getParameter(ApiConstants.PARAMETER_PAGE_NUMBER);
		String numRecords = request.getParameter(ApiConstants.PARAMETER_NUM_RECORDS);
		String newEmail = request.getParameter(ApiConstants.PARAMETER_NEW_EMAIL);
		String confirmEmail = request.getParameter(ApiConstants.PARAMETER_CONFIRM_EMAIL);
		String nickname = request.getParameter(ApiConstants.PARAMETER_SUB_NICKNAME);
		String parentTransID = request.getParameter(ApiConstants.PARAMETER_PARENT_TRANSID);
		String favoriteCategoryID = request.getParameter(ApiConstants.PARAMETER_FAVORITE_CATEGORY_ID);
		String favoriteLabel = request.getParameter(ApiConstants.PARAMETER_FAVORITE_LABEL);
		String favoriteValue = request.getParameter(ApiConstants.PARAMETER_FAVORITE_VALUE);
		String favoriteCode = request.getParameter(ApiConstants.PARAMETER_FAVORITE_CODE);		
		String	partnerType = request.getParameter(ApiConstants.PARAMETER_PARTNER_TYPE);
		String	tradeName = request.getParameter(ApiConstants.PARAMETER_TRADE_NAME);
		String	postalCode = request.getParameter(ApiConstants.PARAMETER_POSTAL_CODE);
		String	userName = request.getParameter(ApiConstants.PARAMETER_USER_NAME);
		String	outletClasification = request.getParameter(ApiConstants.PARAMETER_OUTLET_CLASIFICATION);
		String	franchisePhoneNumber = request.getParameter(ApiConstants.PARAMETER_FRANCHISE_NUMBER);
		String	faxNumber = request.getParameter(ApiConstants.PARAMETER_FAX_NUBER);
		String	typeOfOrganization = request.getParameter(ApiConstants.PARAMETER_TYPEOF_ORGANIZATION);
		String	webSite = request.getParameter(ApiConstants.PARAMETER_WEBSITE);
		String	industryClassification = request.getParameter(ApiConstants.PARAMETER_INDUSTRY_CLASIFICATION);
		String	numberOfOutlets = request.getParameter(ApiConstants.PARAMETER_NOOF_OUTLETS);
		String	yearEstablished = request.getParameter(ApiConstants.PARAMETER_YEAR_ESTABLISHED);
		String	outletAddressLine1 = request.getParameter(ApiConstants.PARAMETER_OUTLET_LINE1);
		String	outletAddressLine2 = request.getParameter(ApiConstants.PARAMETER_OUTLET_LINE2);
		String	outletAddressCity = request.getParameter(ApiConstants.PARAMETER_OUTLET_CITY);
		String	outletAddressState = request.getParameter(ApiConstants.PARAMETER_OUTLET_STATE);
		String	outletAddressZipcode = request.getParameter(ApiConstants.PARAMETER_OUTLET_ZIPCODE);
		String	outletAddressCountry = request.getParameter(ApiConstants.PARAMETER_OUTLET_COUNTRY);
		String	authorizedRepresentative = request.getParameter(ApiConstants.PARAMETER_AUTHORIZED_REPRESENTATIVE);
		String	representativeName = request.getParameter(ApiConstants.PARAMETER_REPRESENTATIVE_NAME);
		String	designation = request.getParameter(ApiConstants.PARAMETER_DESIGNATION);
		String	authorizedFaxNumber = request.getParameter(ApiConstants.PARAMETER_AUTHORIZED_FAX_NUMBER);
		String  paymentMode = request.getParameter(ApiConstants.PARAMETER_PAYMENT_MODE);
		String  fromDate = request.getParameter(ApiConstants.PARAMETER_FROM_DATE);
		String toDate = request.getParameter(ApiConstants.PARAMETER_TO_DATE);
		String addressLine1 = request.getParameter(ApiConstants.PARAMETER_ADDRESS_LINE_1);
		String zipCode = request.getParameter(ApiConstants.PARAMETER_ZIP_CODE);
		String state = request.getParameter(ApiConstants.PARAMETER_STATE);
		String merchantData = request.getParameter(ApiConstants.PARAMETER_MERCHANT_DATA);
		String denomCode = request.getParameter(ApiConstants.PARAMETER_DENOM_CODE);
		String nominalAmount = request.getParameter(ApiConstants.PARAMETER_NOMINAL_AMOUNT);
		String userAPIKey = request.getParameter(ApiConstants.PARAMETER_USER_API_KEY);
		String sctlId = request.getParameter(ApiConstants.PARAMETER_SCTL_ID);
		String discountAmount = request.getParameter(ApiConstants.PARAMETER_DISCOUNT_AMOUNT);
		String loyalityName = request.getParameter(ApiConstants.PARAMETER_LOYALITY_NAME);
		String discountType = request.getParameter(ApiConstants.PARAMETER_DISCOUNT_TYPE);
		String numberOfCoupons = request.getParameter(ApiConstants.PARAMETER_NOOF_COUPONS);
		String tippingAmount = request.getParameter(ApiConstants.PARAMETER_TIPPING_AMOUNT);
		String pointsRedeemed = request.getParameter(ApiConstants.PARAMETER_POINTS_REDEEMED);
		String amountRedeemed = request.getParameter(ApiConstants.PARAMETER_AMOUNT_REDEEMED);



		String ktpId = request.getParameter(ApiConstants.PARAMETER_KTPID);
		String ktpValidUntil = request.getParameter(ApiConstants.PARAMETER_KTP_VALID_UNTIL);
		String ktpLifetime = request.getParameter(ApiConstants.PARAMETER_KTP_LIFETIME);
		String ktpLine1 = request.getParameter(ApiConstants.PARAMETER_KTP_LINE1);
		String ktpLine2 = request.getParameter(ApiConstants.PARAMETER_KTP_LINE2);
		String ktpCity = request.getParameter(ApiConstants.PARAMETER_KTP_CITY);
		String ktpState = request.getParameter(ApiConstants.PARAMETER_KTP_STATE);
		String ktpSubState = request.getParameter(ApiConstants.PARAMETER_KTP_SUB_STATE);
		String ktpCountry = request.getParameter(ApiConstants.PARAMETER_KTP_COUNTRY);
		String ktpZipCode = request.getParameter(ApiConstants.PARAMETER_KTP_ZIP_CODE);
		String ktpRegionName = request.getParameter(ApiConstants.PARAMETER_KTP_REGION_NAME);
		String domesticIdentity = request.getParameter(ApiConstants.PARAMETER_DOMESTIC_IDENTITY);
		String work = request.getParameter(ApiConstants.PARAMETER_WORK);
		String income = request.getParameter(ApiConstants.PARAMETER_INCOME);
		String goalOfOpeningAccount = request.getParameter(ApiConstants.PARAMETER_GOAL_OF_OPENING_ACCOUNT);
		String sourceOfFunds = request.getParameter(ApiConstants.PARAMETER_SOURCE_OF_FUNDS);
		String transactionId = request.getParameter(ApiConstants.PARAMETER_TRANSACTION_ID);
		String ktpDocument = request.getParameter(ApiConstants.PARAMETER_KTP_DOCUMENT);
		String subscriberFormDocument = request.getParameter(ApiConstants.PARAMETER_SUBSCRIBER_FORM_DOCUMENT);
		String supportingDocument = request.getParameter(ApiConstants.PARAMETER_SUPPORTING_DOCUMENT);
		String rt = request.getParameter(ApiConstants.PARAMETER_RT);
		String rw = request.getParameter(ApiConstants.PARAMETER_RW);
		String ktpRt = request.getParameter(ApiConstants.PARAMETER_KTP_RT);
		String ktpRw = request.getParameter(ApiConstants.PARAMETER_KTP_RW);
		
		
		String fullName = request.getParameter(ApiConstants.PARAMETER_FULL_NAME);
		String productDesired = request.getParameter(ApiConstants.PARAMETER_PRODUCT_DESIRED);
		String others = request.getParameter(ApiConstants.PARAMETER_OTHERS);
		String otherWork=request.getParameter(ApiConstants.PARAMETER_OTHER_WORK);
		
		
		
		
		sourceMDN = subscriberService.normalizeMDN(sourceMDN);
		if (! (ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER_INQUIRY.equalsIgnoreCase(transactionName) || 
				ServiceAndTransactionConstants.TRANSACTION_INTER_EMONEY_TRANSFER.equalsIgnoreCase(transactionName)) ) {
			destMDN = subscriberService.normalizeMDN(destMDN);
		}
		rudContainer.setTransactionName(transactionName);
		try {
			rudContainer.setSourceMdn(sourceMDN);
		}
		catch (Exception ex) {
			log.error("Error occurred while setting data in user container: ", ex);
		}
		
		rudContainer.setSourcePin(sourcePIN);
		rudContainer.setServiceName(serviceName);
		rudContainer.setChannelId(channelCodeStr);
		rudContainer.setOtp(otp);
		rudContainer.setNewPin(newPIN);
		rudContainer.setConfirmPin(confirmPIN);
		rudContainer.setTransferId(transferIDStr);
		rudContainer.setSalt(salt);
		rudContainer.setAuthenticationString(authentication);
		rudContainer.setSourceMessage(srcMessage);
		rudContainer.setFirstName(firstName);
		rudContainer.setLastName(lastName);
		rudContainer.setMothersMaidenName(mothersMaidenName);
		rudContainer.setApplicationID(applicationID);
		rudContainer.setAmount(amountStr);
		rudContainer.setSubscriberMDN(submdn);
		rudContainer.setDateOfBirth(dob);
		rudContainer.setAccountType(accontTypeStr);
		rudContainer.setDestinationMdn(destMDN);
		rudContainer.setDestinationPocketCode(destPocketCode);
		rudContainer.setParentTxnId(parentTrxnIdStr);
		rudContainer.setConfirmed(confirmedStr);
		rudContainer.setSourcePocketCode(srcPocketCode);
		rudContainer.setAgentCode(agentCode);
		rudContainer.setActivationNewPin(activationNewPIN);
		rudContainer.setActivationConfirmPin(activationConfirmPIN);
		rudContainer.setPartnerCode(partnerCode);
		rudContainer.setBillerCode(billerCode);
		rudContainer.setBillNo(billNo);
		rudContainer.setCompanyID(companyID);
		rudContainer.setDestAccountNumber(destAccountNo);
		rudContainer.setDestBankCode(destBankCode);
		rudContainer.setSecreteCode(secreteCode);
		rudContainer.setAppOS(request.getParameter(ApiConstants.PARAMETER_APPOS));
		rudContainer.setAppVersion(request.getParameter(ApiConstants.PARAMETER_APPVERSION));
		rudContainer.setAppType(request.getParameter(ApiConstants.PARAMETER_APPTYPE));
		rudContainer.setDestinationBankAccountNo(destinationBankAccountNo);
		
		rudContainer.setKycType(kycType);
		rudContainer.setCity(city);
		rudContainer.setNextOfKin(nextOfKin);
		rudContainer.setNextOfKinNo(nextOfKinNo);
		rudContainer.setEmail(email);
		rudContainer.setPlotNo(plotNo);
		rudContainer.setStreetAddress(streetAddress);
		rudContainer.setRegionName(regionName);
		rudContainer.setCountry(country);
		rudContainer.setIdType(idType);
		rudContainer.setIdNumber(idNumber);
		rudContainer.setDateOfExpiry(dateOfExpiry);
		rudContainer.setAddressProof(addressProof);
		rudContainer.setBirthPlace(birthPlace);
		rudContainer.setNationality(nationality);
		rudContainer.setCompanyName(companyName);
		rudContainer.setSubscriberMobileCompany(subscriberMobileCompany);
		rudContainer.setCertOfIncorp(certOfIncorp);
		rudContainer.setLanguage(language);
		rudContainer.setNotificationMethod(notificationMethod);
		rudContainer.setInstitutionID(institutionID);
		rudContainer.setOnBehalfOfMDN(onBehalfOfMDN);
		rudContainer.setCategory(category);
		rudContainer.setVersion(version);
		//tarun
		rudContainer.setApprovalRequired(approvalRequired);
		rudContainer.setBankAccountType(bankAccountType);
		rudContainer.setCardPAN(cardPAN);
		rudContainer.setCardAlias(cardAlias);
		rudContainer.setAuthorizingFirstName(authorizingFirstName);
		rudContainer.setAuthorizingLastName(authorizingLastName);
		rudContainer.setAuthorizingIdNumber(authorizingIdNumber);
		rudContainer.setApprovalComments(approvalComments);
		rudContainer.setNarration(narration);
		rudContainer.setBenOpCode(benOpCode);
		rudContainer.setDescription(description);
		rudContainer.setMfaTransaction(mfaTransaction);	
		rudContainer.setMfaOtp(mfaOtp);
		rudContainer.setTransID(transID);
		rudContainer.setPageNumber(pageNumber);
		rudContainer.setNumRecords(numRecords);
		rudContainer.setNewEmail(newEmail);
		rudContainer.setConfirmEmail(confirmEmail);
		rudContainer.setNickname(nickname);		
		rudContainer.setOtherMdn(otherMDN);
		rudContainer.setParentTransID(parentTransID);
		rudContainer.setFavoriteCategoryID(favoriteCategoryID);
		rudContainer.setFavoriteLabel(favoriteLabel);
		rudContainer.setFavoriteValue(favoriteValue);
		rudContainer.setFavoriteCode(favoriteCode);
		rudContainer.setPartnerType(partnerType);
		rudContainer.setTradeName(tradeName);
		rudContainer.setPostalCode(postalCode);
		rudContainer.setUserName(userName);
		rudContainer.setOutletClasification(outletClasification);
		rudContainer.setFranchisePhoneNumber(franchisePhoneNumber);
		rudContainer.setFaxNumber(faxNumber);
		rudContainer.setTypeOfOrganization(typeOfOrganization);
		rudContainer.setWebSite(webSite);
		rudContainer.setIndustryClassification(industryClassification);
		rudContainer.setNumberOfOutlets(numberOfOutlets);
		rudContainer.setYearEstablished(yearEstablished);
		rudContainer.setOutletAddressLine1(outletAddressLine1);
		rudContainer.setOutletAddressLine2(outletAddressLine2);
		rudContainer.setOutletAddressCity(outletAddressCity);
		rudContainer.setOutletAddressState(outletAddressState);
		rudContainer.setOutletAddressZipcode(outletAddressZipcode);
		rudContainer.setOutletAddressCountry(outletAddressCountry);
		rudContainer.setAuthorizedRepresentative(authorizedRepresentative);
		rudContainer.setRepresentativeName(representativeName);
		rudContainer.setDesignation(designation);
		rudContainer.setAuthorizedFaxNumber(authorizedFaxNumber);
		rudContainer.setPaymentMode(paymentMode);
		rudContainer.setFromDate(fromDate);
		rudContainer.setToDate(toDate);
		rudContainer.setAddressLine1(addressLine1);
		rudContainer.setZipCode(zipCode);
		rudContainer.setState(state);
		rudContainer.setMerchantData(merchantData);
		rudContainer.setDenomCode(denomCode);
		rudContainer.setNominalAmount(nominalAmount);
		rudContainer.setUserAPIKey(userAPIKey);
		rudContainer.setSctlId(sctlId);
		rudContainer.setDiscountAmount(discountAmount);
		rudContainer.setLoyalityName(loyalityName);
		rudContainer.setDiscountType(discountType);
		rudContainer.setNumberOfCoupons(numberOfCoupons);
		rudContainer.setTippingAmount(tippingAmount);
		rudContainer.setPointsRedeemed(pointsRedeemed);
		rudContainer.setAmountRedeemed(amountRedeemed);

		rudContainer.setKtpId(ktpId);
		rudContainer.setKtpValidUntil(ktpValidUntil);
		rudContainer.setKtpLifetime(ktpLifetime);
		rudContainer.setKtpLine1(ktpLine1);
		rudContainer.setKtpLine2(ktpLine2);
		rudContainer.setKtpCity(ktpCity);
		rudContainer.setKtpState(ktpState);
		rudContainer.setKtpSubState(ktpSubState);
		rudContainer.setKtpCountry(ktpCountry);
		rudContainer.setKtpZipCode(ktpZipCode);
		rudContainer.setKtpRegionName(ktpRegionName);
		rudContainer.setDomesticIdentity(domesticIdentity);
		rudContainer.setWork(work);
		rudContainer.setIncome(income);
		rudContainer.setGoalOfOpeningAccount(goalOfOpeningAccount);
		rudContainer.setSourceOfFunds(sourceOfFunds);
		rudContainer.setTransactionId(transactionId);
		rudContainer.setKtpDocument(ktpDocument);
		rudContainer.setSubscriberFormDocument(subscriberFormDocument);
		rudContainer.setSupportingDocument(supportingDocument);
		rudContainer.setRT(rt);
		rudContainer.setRW(rw);
		rudContainer.setKtpRT(ktpRt);
		rudContainer.setKtpRW(ktpRw);
		
		rudContainer.setFullName(fullName);
		rudContainer.setProductDesired(productDesired);
		rudContainer.setOthers(others);
		
		rudContainer.setOtherWork(otherWork);
		
		log.info("initUserDataContainer: End");
		return rudContainer;
	}

	public boolean bypassSessionChecks(String transactionName, String serviceName) {
		boolean byPass = false;
		if (ApiConstants.TRANSACTION_LOGIN.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_LOGOUT.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_ACTIVATION.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_AGENTACTIVATION.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_SUBSCRIBER_REGISTRATION_THROUGH_WEB.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_SUBSCRIBERREGISTRATION.equals(transactionName) && ApiConstants.SERVICE_ACCOUNT.equals(serviceName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION.equals(transactionName))
			byPass = true;
		else if (ApiConstants.TRANSACTION_REACTIVATION.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_GET_REGISTRATION_MEDIUM.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_GET_THIRD_PARTY_DATA.equals(transactionName))
			byPass=true;
		else if(ApiConstants.TRANSACTION_GET_THIRD_PARTY_LOCATION.equals(transactionName))
			byPass=true;
		else if(ApiConstants.TRANSACTION_RESEND_OTP.equals(transactionName))
			byPass=true;
		else if (ApiConstants.TRANSACTION_SUBSCRIBER_DETAILS.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_SUBSCRIBER_STATUS.equals(transactionName))
			byPass = true;
		
		else if(ApiConstants.TRANSACTION_RESETPIN_BY_OTP.equals(transactionName))
			byPass=true;
		else if(ApiConstants.TRANSACTION_GENERATE_OTP.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_VALIDATE_OTP.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_REGISTRATION_WITH_ACTIVATION_HUB.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_PARTNER_REGISTRATION_THROUGH_API.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_GET_PUBLIC_KEY.equals(transactionName))
			byPass = true;
		else if(ApiConstants.TRANSACTION_FORGOTPIN_INQUIRY.equals(transactionName) || ApiConstants.TRANSACTION_FORGOTPIN.equals(transactionName))
			byPass = true;	
		else if(ApiConstants.TRANSACTION_GET_PROMO_IMAGE.equals(transactionName))
			byPass = true;	
		log.info("bypassSessionChecks for transaction: "+transactionName+" is: "+byPass);
		return byPass;
	}
}
