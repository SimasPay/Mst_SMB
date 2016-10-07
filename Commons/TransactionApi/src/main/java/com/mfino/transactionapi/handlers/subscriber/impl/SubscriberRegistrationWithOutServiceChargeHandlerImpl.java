package com.mfino.transactionapi.handlers.subscriber.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.KtpDetailsDAO;
import com.mfino.dao.query.KtpDetailsQuery;
import com.mfino.domain.Address;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.KtpDetails;
import com.mfino.domain.Notification;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberAddiInfo;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberRegistration;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.result.Result;
import com.mfino.result.XMLResult;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.NotificationService;
import com.mfino.service.PartnerService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.SystemParametersService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationWithOutServiceChargeHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.RegistrationXMLResult;
import com.mfino.transactionapi.service.TransactionApiValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.Base64;

/*
 *
 * @author Bala Sunku
 */
@Service("SubscriberRegistrationWithOutServiceChargeHandlerImpl")
public class SubscriberRegistrationWithOutServiceChargeHandlerImpl extends FIXMessageHandler implements SubscriberRegistrationWithOutServiceChargeHandler{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	private CMSubscriberRegistration subscriberRegistration;

	@Autowired
	@Qualifier("TransactionApiValidationServiceImpl")
	private TransactionApiValidationService transactionApiValidationService;
	
	@Autowired
	@Qualifier("NotificationServiceImpl")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceExtendedImpl")
	private SubscriberServiceExtended subscriberServiceExtended;
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;

	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService ;

	@Autowired
	@Qualifier("TransactionLogServiceImpl")
	private TransactionLogService transactionLogService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	public Result handle(TransactionDetails txnDetails) {
		
		ChannelCode cc = txnDetails.getCc();
		
		subscriberRegistration = new CMSubscriberRegistration();
		subscriberRegistration.setSourceMDN(txnDetails.getSourceMDN());
		subscriberRegistration.setMDN(subscriberService.normalizeMDN(txnDetails.getDestMDN()));
		subscriberRegistration.setFirstName(txnDetails.getFirstName());
		subscriberRegistration.setLastName(txnDetails.getLastName());
		subscriberRegistration.setMothersMaidenName(txnDetails.getMothersMaidenName());
		subscriberRegistration.setApplicationID(txnDetails.getApplicationId());
		subscriberRegistration.setDateOfBirth(new Timestamp(txnDetails.getDateOfBirth()));
		subscriberRegistration.setKYCLevel(Long.parseLong(String.valueOf(CmFinoFIX.SubscriberKYCLevel_UnBanked)));
		subscriberRegistration.setChannelCode(cc.getChannelcode());
		subscriberRegistration.setSourceApplication((int)cc.getChannelsourceapplication());
		subscriberRegistration.setPin(txnDetails.getSourcePIN());
		subscriberRegistration.setTransactionIdentifier(txnDetails.getTransactionIdentifier());
		
		TransactionLog transactionsLog = null;
		log.info("Handling subscriber services Registration webapi request");
		XMLResult result = new RegistrationXMLResult();

		transactionsLog = transactionLogService.saveTransactionsLog(CmFinoFIX.MessageType_SubscriberRegistration,subscriberRegistration.DumpFields());
		result.setSourceMessage(subscriberRegistration);
		result.setDestinationMDN(subscriberRegistration.getMDN());
		result.setTransactionTime(transactionsLog.getTransactiontime());
		result.setTransactionID(transactionsLog.getId().longValue());
		subscriberRegistration.setTransactionID(transactionsLog.getId().longValue());

		result.setActivityStatus(false);
		
		SubscriberMdn agentMDN = subscriberMdnService.getByMDN(subscriberRegistration.getSourceMDN());
		Integer validationResult = transactionApiValidationService.validateAgentMDN(agentMDN);
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			return result;
		}
		
		validationResult = transactionApiValidationService.validatePin(agentMDN, subscriberRegistration.getPin());		
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			
			validationResult = processValidationResultForAgent(validationResult); // Gets the corresponding Agent Notification message
			result.setNotificationCode(validationResult);
			result.setNumberOfTriesLeft((int)(systemParametersService.getInteger(SystemParameterKeys.MAX_WRONGPIN_COUNT)-agentMDN.getWrongpincount()));
			
			return result;
		}
		
		SubscriberMdn destMDN = subscriberMdnService.getByMDN(txnDetails.getDestMDN());
		
		if(destMDN != null) {
			
			result.setNotificationCode(CmFinoFIX.NotificationCode_MDNAlreadyRegistered_Destination);
			return result;
			
		}

		// Check whether the agent has the Service or not.

		Transaction transactionDetails = null;
		ServiceCharge sc = new ServiceCharge();
		sc.setSourceMDN(subscriberRegistration.getSourceMDN());
		sc.setDestMDN(subscriberRegistration.getMDN());
		sc.setChannelCodeId(cc.getId().longValue());
		sc.setServiceName(ServiceAndTransactionConstants.SERVICE_AGENT);
		sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION);
		sc.setTransactionAmount(BigDecimal.ZERO);
		sc.setTransactionLogId(transactionsLog.getId().longValue());
		sc.setTransactionIdentifier(subscriberRegistration.getTransactionIdentifier());

		try{
			
			transactionDetails =transactionChargingService.getCharge(sc);
			
		}catch (InvalidServiceException e) {
			
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			return result;
			
		} catch (InvalidChargeDefinitionException e) {
			
			log.error(e.getMessage());
			result.setNotificationCode(CmFinoFIX.NotificationCode_InvalidChargeDefinitionException);
			return result;
		}
		
		ServiceChargeTxnLog sctl = transactionDetails.getServiceChargeTransactionLog();
		subscriberRegistration.setServiceChargeTransactionLogID(sctl.getId().longValue());
		
		try{
			
			if (!transactionChargingService.checksPartnerService(sc)) {
				log.info("Service Not Registered for the Agent");
				result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNOTAvailableForAgent);
				transactionChargingService.failTheTransaction(sctl, MessageText._("Service Not Registered for the Agent"));
				return result;
			}
			
		}catch (InvalidServiceException e) {
			
			log.error("Exception occured in getting charges",e);
			result.setNotificationCode(CmFinoFIX.NotificationCode_ServiceNotAvailable);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Service Not Avialable"));
			return result;
		}
		
		boolean isDataValid = validateKtpDetails(txnDetails);
		
		if(!isDataValid) {
			
			log.debug("KTP Details are invalid");
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberKtpValidationFailed);
			
			return result;
			
		}
		
		if(StringUtils.isNotBlank(txnDetails.getDomesticIdentity())) {
			
			if((CmFinoFIX.DomAddrIdentity_Contrast_to_Identity == Integer.parseInt(txnDetails.getDomesticIdentity())) && !validateDomesticAddress(txnDetails)) {
				
				log.debug("Domestic Address is invalid");
				result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberRegistrationfailed);
				
				return result;
			}
		}
		
		Address ktpAddress = new Address();
		ktpAddress.setLine1(txnDetails.getKtpLine1());
		ktpAddress.setCity(txnDetails.getKtpCity());
		ktpAddress.setState(txnDetails.getKtpState());
		ktpAddress.setSubstate(txnDetails.getKtpSubState());
		ktpAddress.setRegionname(txnDetails.getKtpRegionName());
		ktpAddress.setZipcode(txnDetails.getKtpZipCode());
		ktpAddress.setRt(txnDetails.getKtpRT());
		ktpAddress.setRw(txnDetails.getKtpRW());
		
		
		Address domesticAddress = new Address();
		domesticAddress.setLine1(txnDetails.getAddressLine1());
		domesticAddress.setCity(txnDetails.getCity());
		domesticAddress.setState(txnDetails.getState());
		domesticAddress.setSubstate(txnDetails.getSubState());
		domesticAddress.setRegionname(txnDetails.getRegionName());
		domesticAddress.setZipcode(txnDetails.getZipCode());
		domesticAddress.setRt(txnDetails.getRT());
		domesticAddress.setRw(txnDetails.getRW());

		Subscriber subscriber = new Subscriber();
		SubscriberMdn subscriberMDN = new SubscriberMdn();
		SubscriberAddiInfo subscriberAddiFields = new SubscriberAddiInfo();
		Pocket epocket = new Pocket();
		Partner partner = partnerService.getPartner(agentMDN);
		subscriber.setRegisteringpartnerid(partner.getId());
		
		if(txnDetails.isKtpLifetime()) {
			
			subscriberMDN.setIsidlifetime(CmFinoFIX.ISIDLifetime_LifeTime_True.toString());
			
		} else {
			
			subscriberMDN.setIsidlifetime(CmFinoFIX.ISIDLifetime_LifeTime_False.toString());
			subscriber.setIdexiparetiontime(new Timestamp(txnDetails.getKtpValidUntil()));
		}
		
		if(StringUtils.isNotBlank(txnDetails.getDomesticIdentity())) {
		
			subscriberMDN.setDomaddridentity(txnDetails.getDomesticIdentity());
		}
		
		subscriberMDN.setKtpid(txnDetails.getKtpId());
		subscriberMDN.setSubscriber(subscriber);
		subscriberMDN.getSubscriber().setEmail(txnDetails.getEmail());
		subscriberMDN.getSubscriber().setMothersmaidenname(txnDetails.getMothersMaidenName());
		
		String ktpDocument = txnDetails.getKtpDocument();
		String subscriberFormDoc = txnDetails.getSubscriberFormDocument();
		String supportingDoc = txnDetails.getSupportingDocument();
		
		try {
			
			String documentPath = System.getProperty("catalina.home") + File.separator + "webapps";
			
			File docFile = new File(documentPath + File.separator + "Documents" + File.separator + txnDetails.getDestMDN());
			
			if(!docFile.exists()) {
				
				docFile.mkdir();
			}
			
			if(StringUtils.isNotBlank(ktpDocument)) {
				
				byte[] ktpDocImageByteArray = Base64.decode(ktpDocument);
				 
				FileOutputStream fileOuputStream = new FileOutputStream(docFile.getAbsoluteFile() + File.separator + "KTP_Document.jpg");
				fileOuputStream.write(ktpDocImageByteArray);
				fileOuputStream.close();
				  
				subscriberMDN.setKtpdocumentpath("Documents" + File.separator + txnDetails.getDestMDN() + File.separator + "KTP_Document.jpg");
			}
			
			if(StringUtils.isNotBlank(subscriberFormDoc)) {
				
				byte[] subFormDocImageByteArray = Base64.decode(subscriberFormDoc);
				 
				FileOutputStream fileOuputStream = new FileOutputStream(docFile.getAbsoluteFile() + File.separator + "Subscriber_Form_Document.jpg");
				fileOuputStream.write(subFormDocImageByteArray);
				fileOuputStream.close();
				  
				subscriberMDN.setSubscriberformpath("Documents" + File.separator + txnDetails.getDestMDN() + File.separator + "Subscriber_Form_Document.jpg");
			}
			
			if(StringUtils.isNotBlank(supportingDoc)) {
				
				byte[] supportingDocImageByteArray = Base64.decode(supportingDoc);
				 
				FileOutputStream fileOuputStream = new FileOutputStream(docFile.getAbsoluteFile() + File.separator + "Supporting_Document.jpg");
				fileOuputStream.write(supportingDocImageByteArray);
				fileOuputStream.close();
				  
				subscriberMDN.setSupportingdocumentpath("Documents" + File.separator + txnDetails.getDestMDN() + File.separator + "Supporting_Document.jpg");
			}
			
		} catch (Exception ex) {
			
			log.error("Error while uploading the images...." + ex);
		}
		
		subscriberAddiFields.setWork(txnDetails.getWork());
		if(txnDetails.getWork()!=null && txnDetails.getWork().equalsIgnoreCase("lainnya")){
			subscriberAddiFields.setOtherwork(txnDetails.getOtherWork());
		}
		subscriberAddiFields.setIncome(txnDetails.getIncome());
		subscriberAddiFields.setGoalofacctopening(txnDetails.getGoalOfOpeningAccount());
		subscriberAddiFields.setSourceoffund(txnDetails.getSourceOfFunds());
		
		Integer regResponse = subscriberServiceExtended.registerSubscriberByAgent(subscriber, subscriberMDN, subscriberRegistration,
				epocket,partner, ktpAddress, domesticAddress, subscriberAddiFields);
		
		if (!regResponse.equals(CmFinoFIX.ResponseCode_Success)) {
			Notification notification = notificationService.getByNoticationCode(regResponse);
			String notificationName = null;
			
			if(notification != null){
				notificationName = notification.getCodename();
			}else{
				log.error("Could not find the failure notification code: "+regResponse);
			}
			
			result.setActivityStatus(false);
			result.setNotificationCode(regResponse);
			transactionChargingService.failTheTransaction(sctl, MessageText._("Subscriber Registration failed. Notification Code: "+regResponse+" NotificationName: "+notificationName));
		
		}else{
			
			result.setActivityStatus(true);
			result.setNotificationCode(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToAgent);
			
			if (sctl != null) {
				// Calculate the Commission and generates the logs for the same
				sc.setSctlId(sctl.getId().longValue());
				try{
					transactionDetails =transactionChargingService.getCharge(sc);
				} catch (InvalidChargeDefinitionException e) {
					log.error(e.getMessage());
				} catch (Exception e) {
					log.error("Exception occured in getting charges for Registration",e);
				}
				transactionChargingService.confirmTheTransaction(sctl);
			}
		}

		result.setSctlID(sctl.getId().longValue());
		return result;

	}

	private boolean validateKtpDetails(TransactionDetails transactionDetails) {
		
		boolean isDataValid = false;
		
		KtpDetailsDAO ktpDetailsDAO = DAOFactory.getInstance().getKtpDetailsDAO();
		
		if(null != transactionDetails) {
			
			KtpDetailsQuery query = new KtpDetailsQuery();
			query.setId(transactionDetails.getTransactionId());
			
			List<KtpDetails> ktpDetails = ktpDetailsDAO.get(query);
			
			if(!ktpDetails.isEmpty() ) {
				
				KtpDetails ktpDetail = ktpDetails.get(0);
				
				if(ktpDetail.getKtpid().equals(transactionDetails.getKtpId())) {
					
					log.debug("KTP ID Matched.....");
					
					if(ktpDetail.getFullname().equals(transactionDetails.getFirstName())) {
						
						log.debug("Name Matched.....");
						
						if(getDateOfBirth(ktpDetail.getDateofbirth()).equals(getDateOfBirth(new java.sql.Timestamp(transactionDetails.getDateOfBirth().getTime())))) {
							
							log.debug("Date of Birth Matched....");
							isDataValid  = true;
							
						}
					}
				}
			}
		}
		
		return isDataValid;
	}
	
	private String getDateOfBirth(java.sql.Timestamp dobTime) {
		
		StringBuffer dobStrBuf = new StringBuffer();
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(dobTime.getTime());
		
		dobStrBuf.append(cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH));
		dobStrBuf.append(cal.get(Calendar.MONTH) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1));
		dobStrBuf.append(cal.get(Calendar.YEAR));
		
		return dobStrBuf.toString();
	}
	
	private boolean validateDomesticAddress(TransactionDetails transactionDetails) {
		
		boolean result = true;
		
		if(StringUtils.isBlank(transactionDetails.getAddressLine1()) || 
				StringUtils.isBlank(transactionDetails.getCity()) ||
				StringUtils.isBlank(transactionDetails.getState()) ||
				StringUtils.isBlank(transactionDetails.getRegionName()) ||
				StringUtils.isBlank(transactionDetails.getZipCode()) || 
				StringUtils.isBlank(transactionDetails.getRT()) ||
				StringUtils.isBlank(transactionDetails.getRW())) {
		
				result = false;
		}
		
		return result;
	}
}