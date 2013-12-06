/**
 * 
 */
package com.mfino.sms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.CompanyDAO;
import com.mfino.dao.NotificationDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Notification;
import com.mfino.domain.SMSCode;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBankAccountActivation;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMChangePin;
import com.mfino.fix.CmFinoFIX.CMCheckBalance;
import com.mfino.fix.CmFinoFIX.CMCheckBalanceDownline;
import com.mfino.fix.CmFinoFIX.CMGetTransactionDetails;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.fix.CmFinoFIX.CMMobileAgentRecharge;
import com.mfino.fix.CmFinoFIX.CMResetDescendentPin;
import com.mfino.fix.CmFinoFIX.CMResume;
import com.mfino.fix.CmFinoFIX.CMSelfResume;
import com.mfino.fix.CmFinoFIX.CMSelfSuspend;
import com.mfino.fix.CmFinoFIX.CMSubscriberActivation;
import com.mfino.fix.CmFinoFIX.CMSuspend;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.mailer.NotificationMessageParser;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.SMSService;
import com.mfino.sms.handlers.CheckBalanceDownlineHandler;
import com.mfino.sms.handlers.CheckBalanceHandler;
import com.mfino.sms.handlers.TransactionDetailsHandler;
import com.mfino.sms.handlers.TransactionsHistoryHandler;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.PasswordGenUtil;

/**
 * @author Deva
 *
 */
public class SMSCodeHandler extends MultixCommunicationHandler{
	
	private String sourceMDN = null;
	
	private String destnMDN = null;
	
	private String message = null;
	
	private String smsc = null;
	
	private final int MAX_TRANSACTION_COUNT = 4;
	
	private static final String SMS_COMMAND_SEPARATOR = ".";

	private static final Integer SHARE_LOAD_HISTORY_COUNT = 3;
	
	private static final Integer SHARE_LOAD_ESCAPEPIN = 1;
	 
	private static final String PIN_SOURCE = "0123456789";
	private static final String dummyPIN = "123456";
	
	private static final String DEFAULT_URL = ConfigurationUtil.getBackendURL();
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	static {
	    // this is required before start decoding fix messages
	    CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
	}
	
	public SMSCodeHandler(String sourceMDN, String destnMDN, String message) {
		this.sourceMDN = sourceMDN;
		this.destnMDN = destnMDN;
		this.message = message;
	}
	
	/**
	 * @param sender
	 * @param destination
	 * @param trim
	 * @param smsc2
	 */
	public SMSCodeHandler(String sourceMDN, String destnMDN, String message,
			String smsc2) {
		this.sourceMDN = sourceMDN;
		this.destnMDN = destnMDN;
		this.message = message;
		this.smsc = smsc2;
	}

	public void handleSMS () {
		StringTokenizer st = new StringTokenizer( message, SMS_COMMAND_SEPARATOR);
		List<String> tokenList = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			tokenList.add(st.nextToken());
		}
		if (tokenList.size() == 0 ) {
			sendErrorMessage(sourceMDN);
			return;
		}
		String smsCode = tokenList.get(0).toUpperCase();
//		String serviceName = SMSCodesCache.getServiceName(smsCode);
//		SMSCodeDAO smsCodeDAO = new SMSCodeDAO();
//		SMSCode smsCodeObj = smsCodeDAO.getByCode(smsCode);
		SMSCode smsCodeObj = SMSCodesCache.getCodesMap().get(smsCode);
		if (smsCodeObj == null) {
			sendErrorMessage(sourceMDN, CmFinoFIX.NotificationCode_InvalidSMSCommand);
			return;
		}
		if (smsCodeObj.getAllowedShortCodes() != null && !smsCodeObj.getAllowedShortCodes().contains(destnMDN)) {
			sendErrorMessage(sourceMDN, CmFinoFIX.NotificationCode_InvalidSMSCommandToCode);
			return;
		}
		String serviceName = smsCodeObj.getServiceName();
		log.info("Short Code =" + smsCode + " Service Name = " + serviceName);
		CMBase baseMsg = null;
		if (CmFinoFIX.ServiceName_SHARE_LOAD_WITHOUT_PIN.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			BigDecimal amount = null;
			try {
				amount = new BigDecimal(tokenList.get(2));
			} catch(Exception e) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMShareLoad();
			CmFinoFIX.CMShareLoad shareLoadRequest = (CmFinoFIX.CMShareLoad) baseMsg;
			shareLoadRequest.setSourceMDN(sourceMDN);
			shareLoadRequest.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			shareLoadRequest.setDestMDN(tokenList.get(1));
			shareLoadRequest.setAmount(amount);
			shareLoadRequest.setPin(dummyPIN);
			shareLoadRequest.setEscapePINCheck(SHARE_LOAD_ESCAPEPIN);
		} else if (CmFinoFIX.ServiceName_SHARE_LOAD_HISTORY.equals(serviceName)) {
			if (tokenList.size() != 1) {
				sendErrorMessage(sourceMDN);
				return;
			}			
			baseMsg = new CmFinoFIX.CMGetTransactions();
			CMGetTransactions shareLoadRequest = (CMGetTransactions) baseMsg;
			shareLoadRequest.setSourceMDN(sourceMDN);
			shareLoadRequest.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			shareLoadRequest.setGetTransactionType(CmFinoFIX.GetTransactionType_ShareLoad);
			shareLoadRequest.setMaxCount(SHARE_LOAD_HISTORY_COUNT);
			// dummy PIN however, we do not require PIN to check shareload
			// as of now PIN is mandatory for Get Transactions.
			shareLoadRequest.setPin(dummyPIN);

		} else if (CmFinoFIX.ServiceName_CHECK_BALANCE.equals(serviceName)) {
			// For check balance the first Word in the message is the command 
			// Second word is the pin
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMCheckBalance(); 
			CMCheckBalance checkBalanceRequest = (CMCheckBalance) baseMsg;
			checkBalanceRequest.setServiceName(serviceName);
			checkBalanceRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			checkBalanceRequest.setSourceMDN(sourceMDN);
			checkBalanceRequest.setPin(tokenList.get(1));
		} else if (CmFinoFIX.ServiceName_TRANSFER_HISTORY.equals(serviceName)) {
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMGetTransactions();
			CMGetTransactions transactionHistory = (CMGetTransactions) baseMsg;
			transactionHistory.setServletPath(CmFinoFIX.ServletPath_Merchants);
			transactionHistory.setSourceMDN(sourceMDN);
			transactionHistory.setPin(tokenList.get(1));
			transactionHistory.setGetTransactionType(CmFinoFIX.GetTransactionType_Transfer);
			transactionHistory.setMaxCount(MAX_TRANSACTION_COUNT);
		} else if (CmFinoFIX.ServiceName_TOPUP_HISTORY.equals(serviceName)) {
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMGetTransactions();
			CMGetTransactions transactionHistory = (CMGetTransactions) baseMsg;
			transactionHistory.setServletPath(CmFinoFIX.ServletPath_Merchants);
			transactionHistory.setSourceMDN(sourceMDN);
			transactionHistory.setPin(tokenList.get(1));
			transactionHistory.setGetTransactionType(CmFinoFIX.GetTransactionType_TopUp);
			transactionHistory.setMaxCount(MAX_TRANSACTION_COUNT);
		} else if (CmFinoFIX.ServiceName_CHANGE_PIN.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			String newPin = tokenList.get(2);
			if (newPin == null || newPin.length() > ConfigurationUtil.getMaxPINLength() || newPin.length() < ConfigurationUtil.getMinPINLength() ) {
				sendErrorMessage(sourceMDN, CmFinoFIX.NotificationCode_ChangeEPINFailedInvalidConfirmPIN);
				return;
			}
			baseMsg = new CMChangePin();
			CMChangePin changePinRequest = (CMChangePin) baseMsg;
			changePinRequest.setOldPin(tokenList.get(1));
			changePinRequest.setNewPin(tokenList.get(2));
			changePinRequest.setSourceMDN(sourceMDN);
			changePinRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			
		}  else if (CmFinoFIX.ServiceName_GET_TRANSACTIONS.equals(serviceName)) {
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMGetTransactions();
			CMGetTransactions transactionsRequest = (CMGetTransactions) baseMsg;
			transactionsRequest.setMaxCount(MAX_TRANSACTION_COUNT);
			transactionsRequest.setPin(tokenList.get(1));
			transactionsRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			transactionsRequest.setSourceMDN(sourceMDN);
			
		} else if (CmFinoFIX.ServiceName_MOBILE_AGENT_DISTRIBUTE.equals(serviceName)) {
			if (tokenList.size() != 4) {
				sendErrorMessage(sourceMDN);
				return;
			}
			BigDecimal amount = null;
			try {
				amount = new BigDecimal(tokenList.get(2));
			} catch(Exception e) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMMobileAgentDistribute();
			CmFinoFIX.CMMobileAgentDistribute distributeRequest = (CmFinoFIX.CMMobileAgentDistribute) baseMsg;
			distributeRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			distributeRequest.setSourceMDN(sourceMDN);
			distributeRequest.setDestMDN(tokenList.get(1));
			distributeRequest.setAmount(amount);
			distributeRequest.setPin(tokenList.get(3));
		} else if (CmFinoFIX.ServiceName_MOBILE_AGENT_RECHARGE.equals(serviceName)) {
			if (tokenList.size() != 4) {
				sendErrorMessage(sourceMDN);
				return;
			}
			BigDecimal amount = null;
			try {
				amount = new BigDecimal(tokenList.get(2));
			} catch(Exception e) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMMobileAgentRecharge();
			CMMobileAgentRecharge mobileAgentRechargeRequest = (CMMobileAgentRecharge)baseMsg;
			mobileAgentRechargeRequest.setDestMDN(tokenList.get(1));
			mobileAgentRechargeRequest.setAmount(amount);
			mobileAgentRechargeRequest.setPin(tokenList.get(3));
			mobileAgentRechargeRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			mobileAgentRechargeRequest.setSourceMDN(sourceMDN);
			mobileAgentRechargeRequest.setBucketType(CmFinoFIX.BucketType_Recharge_Call_And_SMS);
		}  else if (CmFinoFIX.ServiceName_SELF_SUSPEND.equals(serviceName)) {
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMSelfSuspend();
			CMSelfSuspend selfSuspendRequest = (CMSelfSuspend) baseMsg;
			selfSuspendRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			selfSuspendRequest.setPin(tokenList.get(1));
			selfSuspendRequest.setSourceMDN(sourceMDN);
		} else if (CmFinoFIX.ServiceName_SELF_RESUME.equals(serviceName)) {
			if (tokenList.size() != 2) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMSelfResume();
			CMSelfResume selfResumeRequest = (CMSelfResume) baseMsg;
			selfResumeRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			selfResumeRequest.setPin(tokenList.get(1));
			selfResumeRequest.setSourceMDN(sourceMDN);
		} else if (CmFinoFIX.ServiceName_SUSPEND.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMSuspend();
			CMSuspend suspendRequest = (CMSuspend) baseMsg;
			suspendRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			suspendRequest.setDestMDN(tokenList.get(1));
			suspendRequest.setPin(tokenList.get(2));
			suspendRequest.setSourceMDN(sourceMDN);
		} else if (CmFinoFIX.ServiceName_RESUME.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMResume();
			CMResume resumeRequest = (CMResume) baseMsg;
			resumeRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			resumeRequest.setDestMDN(tokenList.get(1));
			resumeRequest.setPin(tokenList.get(2));
			resumeRequest.setSourceMDN(sourceMDN);
		} else if (CmFinoFIX.ServiceName_RESET_DESCENDENT_PIN.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMResetDescendentPin();
			CMResetDescendentPin resetDescendentPinRequest = (CMResetDescendentPin) baseMsg;
			resetDescendentPinRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			resetDescendentPinRequest.setSourceMDN(sourceMDN);
			resetDescendentPinRequest.setDestMDN(tokenList.get(1));
			resetDescendentPinRequest.setPin(tokenList.get(2));
		    int pinLength = 0;

		    String pinStr = ConfigurationUtil.getPINLength();
		    if (pinStr != null) {
		      pinLength = Integer.parseInt(pinStr);
		    }
		    String genPin = PasswordGenUtil.generate(PIN_SOURCE, pinLength);
			resetDescendentPinRequest.setNewPin(genPin);
		} else if (CmFinoFIX.ServiceName_SUBSCRIBER_ACTIVATION.equals(serviceName)) {
			if (tokenList.size() != 4) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMSubscriberActivation();
			CMSubscriberActivation subscActivationRequest = (CMSubscriberActivation) baseMsg;
			subscActivationRequest.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			subscActivationRequest.setSourceMDN(sourceMDN);
			subscActivationRequest.setPin(tokenList.get(1));
			subscActivationRequest.setAuthenticationPhrase(tokenList.get(2));
			subscActivationRequest.setContactNumber(tokenList.get(3));
		} else if (CmFinoFIX.ServiceName_BANK_ACCOUNT_ACTIVATION.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMBankAccountActivation();
			CMBankAccountActivation bankAccountActivationRequest =(CMBankAccountActivation) baseMsg;
			bankAccountActivationRequest.setServletPath(CmFinoFIX.ServletPath_Subscribers);
			bankAccountActivationRequest.setSourceMDN(sourceMDN);
			bankAccountActivationRequest.setPin(tokenList.get(1));
			bankAccountActivationRequest.setCardPANSuffix(tokenList.get(2));
		} else if (CmFinoFIX.ServiceName_TRANSACTION_DETAIL.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CmFinoFIX.CMGetTransactionDetails();
			CMGetTransactionDetails transactionDetailsRequest =(CMGetTransactionDetails) baseMsg;
			transactionDetailsRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			transactionDetailsRequest.setSourceMDN(sourceMDN);
			try {
				transactionDetailsRequest.setTransID(Long.parseLong(tokenList.get(1)));
			} catch(Exception e) {
				sendErrorMessage(sourceMDN);
				return;
			}
			transactionDetailsRequest.setPin(tokenList.get(2));
		} else if (CmFinoFIX.ServiceName_CHECK_BALANCE_DOWNLINE.equals(serviceName)) {
			if (tokenList.size() != 3) {
				sendErrorMessage(sourceMDN);
				return;
			}
			baseMsg = new CMCheckBalanceDownline();
			CMCheckBalanceDownline cbdRequest = (CMCheckBalanceDownline) baseMsg;
			cbdRequest.setServletPath(CmFinoFIX.ServletPath_Merchants);
			cbdRequest.setSourceMDN(sourceMDN);
			cbdRequest.setDownlineMDN(tokenList.get(1));
			cbdRequest.setPin(tokenList.get(2));
		}
		else
		{
			sendErrorMessage(sourceMDN, CmFinoFIX.NotificationCode_InvalidSMSCommand);
			return;
		}
		if (baseMsg instanceof CMCheckBalance) {
			CheckBalanceHandler checkBalanceHandler = new CheckBalanceHandler( (CMCheckBalance)baseMsg, message);
			checkBalanceHandler.handle();
		} else if (baseMsg instanceof CMGetTransactions) {
			TransactionsHistoryHandler transactionsHandler = new TransactionsHistoryHandler((CMGetTransactions) baseMsg, message);
			transactionsHandler.handle();
		} else if (baseMsg instanceof CMGetTransactionDetails) {
			TransactionDetailsHandler trxnDetailsHandler = new TransactionDetailsHandler((CMGetTransactionDetails) baseMsg, message);
			trxnDetailsHandler.handle();
		}else if (baseMsg instanceof CMCheckBalanceDownline) {
			CheckBalanceDownlineHandler cbdHandler = new CheckBalanceDownlineHandler((CMCheckBalanceDownline) baseMsg, message);
			cbdHandler.handle();
		} else {
			log.info("Request to Core Engine " + baseMsg.DumpFields());
			CFIXMsg msg = this.process(baseMsg);
			if(msg != null)
				log.info("Response from Core Engine " + msg.DumpFields());
		}
	}
	
	private void sendErrorMessage(String destinationMDN, Integer notificationCode) {
		SubscriberMDNDAO subscriberMDNDAO = new SubscriberMDNDAO();
		SubscriberMDN subscriberMDN = subscriberMDNDAO.getByMDN(destinationMDN);
		Integer language = null;
		Company company = null;
		if (subscriberMDN != null) {
			language = subscriberMDN.getSubscriber().getLanguage();
			company = subscriberMDN.getSubscriber().getCompany();
		} else {
			language = CmFinoFIX.Language_English;
			CompanyDAO companyDAO = new CompanyDAO();
			company = companyDAO.getById(1L);
		}
		NotificationDAO notificationDAO = new NotificationDAO();
        NotificationQuery query = new NotificationQuery();
        query.setNotificationCode(notificationCode);
        query.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
        query.setLanguage(language);
        query.setCompany(company);
        List list = notificationDAO.get(query);
        if (list.size() == 0) {
            System.out.println("No notification method found so returning null");
            return;
        }
        Notification notification = (Notification) list.get(0);
        NotificationWrapper notificationWrapper = new NotificationWrapper(notification);
		NotificationMessageParser nmp = new NotificationMessageParser(notificationWrapper);
		String notificationText = nmp.buildMessage();
		SMSService service = new SMSService();
		service.setDestinationMDN(destinationMDN);
		service.setSourceMDN(notification.getSMSNotificationCode());
		service.setAccessCode(notification.getAccessCode());
		service.setMessage(notificationText);
		service.setSmsc(smsc);
		System.out.println(notificationText);
		try {
			service.send();
		} catch (Exception err) {
			log.error("Exception while sending sms: ", err);
		}
	}
	
	private void sendErrorMessage(String destinationMDN) {
		sendErrorMessage(destinationMDN, CmFinoFIX.NotificationCode_RequiredSMSParametersMissing);
	}
	/*
	 * This method creates the fix message and sends it to the multix.
	 * The message type will be obtained based on the service name.
	 * Will check for the required number of parameters for each service and returns error message from here
	 * 
	 */
	
	@Override
	public CFIXMsg process(CFIXMsg msg) {
		CFIXMsg errorMsg = null;
		try {
			 errorMsg = handleRequestResponse((CMBase)msg, DEFAULT_URL, CmFinoFIX.SourceApplication_SMS);
			 return errorMsg;
		} catch (Exception e) {
			// Send SMS from here
			System.out.println("Unexpected Exception " + e.getMessage());
			sendErrorMessage(sourceMDN, CmFinoFIX.NotificationCode_Failure);
			return null;
		}
		
	}
	
	/**
	 * @return the sourceMDN
	 */
	public String getSourceMDN() {
		return sourceMDN;
	}

	/**
	 * @param sourceMDN the sourceMDN to set
	 */
	public void setSourceMDN(String sourceMDN) {
		this.sourceMDN = sourceMDN;
	}

	/**
	 * @return the destination
	 */
	public String getDestination() {
		return destnMDN;
	}

	/**
	 * @param destination the destination to set
	 */
	public void setDestination(String destination) {
		this.destnMDN = destination;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	public static void main(String[] args) {
		HibernateUtil.getCurrentTransaction().begin();
		SMSCodeHandler handler = new SMSCodeHandler("6288112345", "6288112345", "6288112345");
		handler.sendErrorMessage("6288112345");
		HibernateUtil.getCurrentTransaction().commit();
	}

	/**
	 * @return the smsc
	 */
	public String getSmsc() {
		return smsc;
	}

	/**
	 * @param smsc the smsc to set
	 */
	public void setSmsc(String smsc) {
		this.smsc = smsc;
	}
}
