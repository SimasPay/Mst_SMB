/**
 * 
 */
package com.mfino.transactionapi.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.IntegrationPartnerMapping;
import com.mfino.domain.MFSBiller;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.service.IntegrationPartnerMappingService;
import com.mfino.service.MFAService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.agent.ProductReferralHandler;
import com.mfino.transactionapi.handlers.money.MoneyTransferHandler;
import com.mfino.transactionapi.handlers.money.TransferInquiryHandler;
import com.mfino.transactionapi.handlers.payment.BillPayConfirmHandler;
import com.mfino.transactionapi.handlers.payment.BillPayInquiryHandler;
import com.mfino.transactionapi.handlers.payment.agent.AgentBillPayConfirmHandler;
import com.mfino.transactionapi.handlers.payment.agent.AgentBillPayInquiryHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberKtpValidationHandler;
import com.mfino.transactionapi.handlers.subscriber.SubscriberRegistrationWithOutServiceChargeHandler;
import com.mfino.transactionapi.handlers.wallet.AgentCashInConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.AgentCashInInquiryHandler;
import com.mfino.transactionapi.handlers.wallet.UnregisteredSubscriberCashOutConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.UnregisteredSubscriberCashOutInquiryHandler;
import com.mfino.transactionapi.service.AgentAPIServices;
import com.mfino.transactionapi.service.BaseAPIService;
import com.mfino.transactionapi.service.TransactionRequestValidationService;
import com.mfino.transactionapi.vo.TransactionDetails;

/**
 * Handles all Agent Service related transactions Subscriber Registration Cash
 * In
 * 
 * @author Bala Sunku
 * 
 */
@Service("AgentAPIServicesImpl")
public class AgentAPIServicesImpl extends BaseAPIService implements AgentAPIServices{

	public static final String	IN_CODE_VISAFONE	= "1";

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("AgentCashInInquiryHandlerImpl")
	private AgentCashInInquiryHandler agentCashInInquiryHandler;
	
	@Autowired
	@Qualifier("IntegrationPartnerMappingServiceImpl")
	private IntegrationPartnerMappingService integrationPartnerMappingService;
	
	@Autowired
	@Qualifier("BillPayInquiryHandlerImpl")
	private BillPayInquiryHandler billPayInquiryHandler;
	
	@Autowired
	@Qualifier("BillPayConfirmHandlerImpl")
	private BillPayConfirmHandler billPayConfirmHandler;

	@Autowired
	@Qualifier("AgentBillPayInquiryHandlerImpl")
	private AgentBillPayInquiryHandler agentBillPayInquiryHandler;
	
	@Autowired
	@Qualifier("AgentBillPayConfirmHandlerImpl")
	private AgentBillPayConfirmHandler agentBillPayConfirmHandler;
	
	@Autowired
	@Qualifier("AgentCashInConfirmHandlerImpl")
	private AgentCashInConfirmHandler agentCashInConfirmHandlerImpl;
	
	@Autowired
	@Qualifier("UnregisteredSubscriberCashOutConfirmHandlerImpl")
	private UnregisteredSubscriberCashOutConfirmHandler unregisteredSubscriberCashOutConfirmHandler;
	
	@Autowired
	@Qualifier("UnregisteredSubscriberCashOutInquiryHandlerImpl")
	private UnregisteredSubscriberCashOutInquiryHandler unregisteredSubscriberCashOutInquiryHandler;
	
	@Autowired
	@Qualifier("SubscriberRegistrationWithOutServiceChargeHandlerImpl")
	private SubscriberRegistrationWithOutServiceChargeHandler subRegWithOutServiceChargeHandler;
	
	@Autowired
	@Qualifier("TransactionRequestValidationServiceImpl")
	private TransactionRequestValidationService transactionRequestValidationService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("TransferInquiryHandlerImpl")
	private TransferInquiryHandler transferInquiryHandler;
	
	@Autowired
	@Qualifier("MoneyTransferHandlerImpl")
	private MoneyTransferHandler moneyTransferHandler;
	
	@Autowired
	@Qualifier("SubscriberKtpValidationHandlerImpl")
	private SubscriberKtpValidationHandler subscriberKtpValidationHandler;
	
	@Autowired
	@Qualifier("ProductReferralHandlerImpl")
	private ProductReferralHandler productReferralHandler;
	
	@Autowired
	@Qualifier("MFAServiceImpl")
	private MFAService mfaService;
	
 	public XMLResult handleRequest(TransactionDetails transactionDetails) throws InvalidDataException {
		XMLResult xmlResult = null;


		String transactionName = transactionDetails.getTransactionName();
		String sourceMessage = transactionDetails.getSourceMessage();
		ChannelCode channelCode = transactionDetails.getCc();

		if (ServiceAndTransactionConstants.TRANSACTION_SUBSCRIBERREGISTRATION.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_SUBSCRIBERREGISTRATION");
			
			transactionRequestValidationService.validateSubscriberRegistrationByAgentDetails(transactionDetails);
			xmlResult = (XMLResult) subRegWithOutServiceChargeHandler.handle(transactionDetails);

		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHIN_INQUIRY.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_CASHIN_INQUIRY ");
		
			transactionRequestValidationService.validateCashInInquiryDetails(transactionDetails);
			if (StringUtils.isBlank(sourceMessage)) {
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_IN);
			}
			//xmlResult = (XMLResult) agentCashInInquiryHandler.handle(transactionDetails);
			
			String mfaTransaction = transactionDetails.getMfaTransaction();
			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_AGENT, ServiceAndTransactionConstants.TRANSACTION_CASHIN, channelCode.getID()) == true) {
				if(mfaTransaction != null
						&& (mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY) 
									|| mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
					xmlResult = (XMLResult) agentCashInInquiryHandler.handle(transactionDetails);
				}
				else{
					log.info("mfaTransaction parameter is Invalid");
				}
			}else{
				xmlResult = new XMLResult();
				Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
				xmlResult.setLanguage(language);
				xmlResult.setTransactionTime(new Timestamp());
				xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHIN.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_CASHIN ") ;

			transactionRequestValidationService.validateCashInConfirmDetails(transactionDetails);
			//xmlResult = (XMLResult) agentCashInConfirmHandlerImpl.handle(transactionDetails);
			String mfaTransaction = transactionDetails.getMfaTransaction();
			if(mfaService.isMFATransaction(ServiceAndTransactionConstants.SERVICE_AGENT, transactionName, channelCode.getID()) == true) {
				if(mfaTransaction != null
						&& (mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_INQUIRY) 
									|| mfaTransaction.equals(ServiceAndTransactionConstants.MFA_TRANSACTION_CONFIRM))){
					xmlResult = (XMLResult) agentCashInConfirmHandlerImpl.handle(transactionDetails);
				}
				else{
					log.info("mfaTransaction parameter is Invalid");
				}
			}else{
				xmlResult = new XMLResult();
				Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
				xmlResult.setLanguage(language);
				xmlResult.setTransactionTime(new Timestamp());
				xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
			}
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED_INQUIRY.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_CASHOUT_UNREGISTERED_INQUIRY");

			transactionRequestValidationService.validateUnregisteredCashOutInquiryDetails(transactionDetails);
			if (StringUtils.isBlank(sourceMessage)) {
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_CASH_OUT_UNREGISTERED);
			}
			xmlResult = (XMLResult) unregisteredSubscriberCashOutInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_CASHOUT_UNREGISTERED");
	
			transactionRequestValidationService.validateUnregisteredCashOutConfirmDetails(transactionDetails);
			xmlResult = (XMLResult) unregisteredSubscriberCashOutConfirmHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY_INQUIRY.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_AGENT_BILL_PAY_INQUIRY");
			transactionRequestValidationService.validateAgentBillPayInquiryDetails(transactionDetails);
			String billerCode = systemParametersService.getString(SystemParameterKeys.STARTIMES_BILLER_CODE);
			if(billerCode!=null && billerCode.equals(transactionDetails.getBillerCode())){
				if (StringUtils.isBlank(sourceMessage)) {
					sourceMessage = ServiceAndTransactionConstants.MESSAGE_STARTIMES_PAYMENT;
				}
				transactionDetails.setSourceMessage(sourceMessage);
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_PAYMENT);
				transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_STARTIMES_PAYMENT);
			}else{
				if (StringUtils.isBlank(sourceMessage)) {
					transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AGENT_BILL_PAY);
				}
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
				transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_BILL_PAY);
			}
			xmlResult = (XMLResult) agentBillPayInquiryHandler.handle(transactionDetails);

		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AGENT_BILL_PAY.equalsIgnoreCase(transactionName)) {
			log.info("AgentAPIService :: handleRequest() TRANSACTION_AGENT_BILL_PAY");

			transactionRequestValidationService.validateBillPayConfirmDetails(transactionDetails);
			xmlResult = (XMLResult) agentBillPayConfirmHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName)) {

			transactionRequestValidationService.validateAirtimePurchaseInquiryDetails(transactionDetails);
			String IN_ID = SystemParameterKeys.IN_PARTNER_SUFFIX + transactionDetails.getCompanyID();
			
			IntegrationPartnerMapping partnerMap = integrationPartnerMappingService.getByInstitutionID(IN_ID);
			if(partnerMap==null){
				throw new InvalidDataException("Invalid Company ID", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
						ApiConstants.COMPANY_ID);	
			}
			MFSBiller biller = partnerMap.getMFSBiller();
			String billerCode = biller.getMFSBillerCode();
			transactionDetails.setBillerCode(billerCode);
			
			if (StringUtils.isBlank(sourceMessage)) {
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE);
			}
			log.info("AgentAPIService :: handleRequest() TRANSACTION_AIRTIME_PURCHASE_INQUIRY billerCode=" + billerCode);
			transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE);
			xmlResult = (XMLResult) agentBillPayInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PURCHASE.equalsIgnoreCase(transactionName)) {
		
			transactionRequestValidationService.validateAirtimePurchaseDetails(transactionDetails);
			String IN_ID = SystemParameterKeys.IN_PARTNER_SUFFIX + transactionDetails.getCompanyID();
			
			log.info("AgentAPIService handleRequest() TRANSACTION_AIRTIME_PURCHASE partnerCode="+IN_ID);
			
			IntegrationPartnerMapping partnerMap = integrationPartnerMappingService.getByInstitutionID(IN_ID);
			if(partnerMap==null){
				throw new InvalidDataException("Invalid Company ID", CmFinoFIX.NotificationCode_InvalidWebAPIRequest_ParameterMissing, 
						ApiConstants.COMPANY_ID);	
			}
			MFSBiller biller = partnerMap.getMFSBiller();
			String billerCode = biller.getMFSBillerCode();
			
			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_AIRTIME_PURCHASE;
			}

			log.info("AgentAPIService :: handleRequest() TRANSACTION_AIRTIME_PURCHASE IN_Code=" + IN_ID);

			transactionDetails.setBillerCode(billerCode);
			transactionDetails.setSourceMessage(sourceMessage);

			xmlResult = (XMLResult) agentBillPayConfirmHandler.handle(transactionDetails);
		}else if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PIN_PURCHASE_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateAirtimePinPurchaseInquiryDetails(transactionDetails);
			

			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_AIRTIME_PIN_PURCHASE;
			}
			
			String terminalID = systemParametersService.getString(SystemParameterKeys.AIRTIME_PIN_PURCHASE_TERMINAL_ID);
			String billerCode = systemParametersService.getString(SystemParameterKeys.AIRTIME_PIN_PURCHASE_BILLER_CODE);
			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PIN_PURCHASE);			
			transactionDetails.setBillerCode(billerCode); 
			transactionDetails.setNarration(terminalID);
			transactionDetails.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PIN_PURCHASE);

			xmlResult = (XMLResult) billPayInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_AIRTIME_PIN_PURCHASE.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateAirtimePinPurchaseConfirmDetails(transactionDetails);
			

			String billerCode = systemParametersService.getString(SystemParameterKeys.AIRTIME_PIN_PURCHASE_BILLER_CODE);
			transactionDetails.setBillerCode(billerCode);
			xmlResult = (XMLResult) billPayConfirmHandler.handle(transactionDetails);
		}
		
		else if (ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT_INQUIRY.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateFRSCPaymentInquiryDetails(transactionDetails);

			if (StringUtils.isBlank(sourceMessage)) {
				sourceMessage = ServiceAndTransactionConstants.MESSAGE_FRSC_PAYMENT;
			}

			String frscCode = systemParametersService.getString(SystemParameterKeys.FRSC_PAYMENT_CODE);

 			transactionDetails.setBillerCode(frscCode);
 			transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
 			transactionDetails.setServiceName(transactionDetails.getServiceName());
 			transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT);
//			transactionDetails.setMessageType(CmFinoFIX.MessageType_BillPayInquiry);

 			transactionDetails.setParentTxnId(0L);
			xmlResult = (XMLResult) agentBillPayInquiryHandler.handle(transactionDetails);

		}
		else if (ServiceAndTransactionConstants.TRANSACTION_FRSC_PAYMENT.equalsIgnoreCase(transactionName)) {
			transactionRequestValidationService.validateFRSCPaymentConfirmDetails(transactionDetails);
			
			String frscCode = systemParametersService.getString(SystemParameterKeys.FRSC_PAYMENT_CODE);
			transactionDetails.setBillerCode(frscCode);

			xmlResult = (XMLResult) agentBillPayConfirmHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASH_IN_TO_AGENT_INQUIRY.equalsIgnoreCase(transactionName)) {

			xmlResult = (XMLResult) transferInquiryHandler.handle(transactionDetails);
		}
		else if (ServiceAndTransactionConstants.TRANSACTION_CASH_IN_TO_AGENT.equalsIgnoreCase(transactionName)) {
			
			transactionDetails.setSystemIntiatedTransaction(true);
			xmlResult = (XMLResult) moneyTransferHandler.handle(transactionDetails);
			
		} else if (ServiceAndTransactionConstants.SUBSCRIBER_KTP_VALIDATION.equalsIgnoreCase(transactionName)) {
			
			transactionRequestValidationService.validateSubscriberKtpDetails(transactionDetails);
			xmlResult = (XMLResult) subscriberKtpValidationHandler.handle(transactionDetails);
		}
		
		else if (ServiceAndTransactionConstants.PRODUCT_REFERRAL.equalsIgnoreCase(transactionName)) {
						
			transactionRequestValidationService.validateProductReferralDetails(transactionDetails);
			xmlResult = (XMLResult) productReferralHandler.handle(transactionDetails);
		}
		

		else {
			xmlResult = new XMLResult();
			Integer language = systemParametersService.getInteger(SystemParameterKeys.DEFAULT_LANGUAGE_OF_SUBSCRIBER);
			xmlResult.setLanguage(language);
			xmlResult.setTransactionTime(new Timestamp());
			xmlResult.setNotificationCode(CmFinoFIX.NotificationCode_TransactionNotAvailable);
		}
		return xmlResult;
	}
}
