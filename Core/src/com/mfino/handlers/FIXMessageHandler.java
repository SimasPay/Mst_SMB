/**
 * 
 */
package com.mfino.handlers;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.domain.Company;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.mFinoServiceProvider;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.fix.processor.MultixCommunicationHandler;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.result.Result;
import com.mfino.result.Result.ResultType;
import com.mfino.util.ConfigurationUtil;

/**
 * @author Deva
 * 
 */
public abstract class FIXMessageHandler extends MultixCommunicationHandler {

	private static Logger log = LoggerFactory.getLogger(FIXMessageHandler.class);
	public static final String ANY_PARTNER = "any";

	
	public static mFinoServiceProvider	msp;

	private ResultType	               resultType;
 	public static final String	       DEFAULT_URL	= ConfigurationUtil.getBackendURL();
	protected Boolean BOOL_TRUE = Boolean.valueOf(true);
	protected Boolean BOOL_FALSE = Boolean.valueOf(false);
	
	static {
		// this is required before start decoding fix messages
		CFIXMsg.SetFIXMsgCreator(new CmFinoFIX.CMessageCreator());
	}
	
	private SessionFactory sessionFactory = null;
	
	private HibernateSessionHolder hibernateSessionHolder = null;
	
	public CFIXMsg process(CFIXMsg msg) {
		log.info("Request to Core Engine " + msg.DumpFields());
		CFIXMsg errorMsg = null;
		try {
			errorMsg = handleRequestResponse((CMBase) msg, DEFAULT_URL, ((CMBase) msg).getSourceApplication());
			if (errorMsg != null)
				log.info("Response from Core Engine " + errorMsg.DumpFields());
			return errorMsg;
		}
		catch (Exception error) {
			// Send SMS from here
			log.error("Unexpected Exception " + error.getMessage(), error);
			return null;
		}
	}

	public CFIXMsg handleResponse(CFIXMsg pMsg) {
		if (pMsg instanceof CmFinoFIX.CMGetLastTransactionsFromBank || pMsg instanceof CmFinoFIX.CMBankResponse) {
			return pMsg;
		}
		else if (pMsg instanceof CmFinoFIX.CMSubscriberNotification) {
			CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
			CMSubscriberNotification errMsg = (CMSubscriberNotification) pMsg;
			if (CmFinoFIX.ResponseCode_Success.equals(errMsg.getResult())) {
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			}
			else {
				errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			}
			errorMsg.setDestinationUserName(((CmFinoFIX.CMSubscriberNotification) pMsg).getDestinationUserName());
			errorMsg.setDestinationType(((CmFinoFIX.CMSubscriberNotification) pMsg).getDestinationType());
			errorMsg.setAdditionalInfo(((CmFinoFIX.CMSubscriberNotification) pMsg).getAdditionalInfo());
			errorMsg.setBankName(((CmFinoFIX.CMSubscriberNotification) pMsg).getBankName());
			errorMsg.setParentTransactionID(((CmFinoFIX.CMSubscriberNotification) pMsg).getParentTransactionID());
			errorMsg.setErrorDescription(errMsg.getText());
			errorMsg.setPaymentInquiryDetails(((CmFinoFIX.CMSubscriberNotification) pMsg).getPaymentInquiryDetails());
			errorMsg.setBillPaymentReferenceID(((CmFinoFIX.CMSubscriberNotification) pMsg).getBillPaymentReferenceID());
			errorMsg.setTransferID(((CmFinoFIX.CMSubscriberNotification) pMsg).getTransactionID());
			errorMsg.setCode(((CmFinoFIX.CMSubscriberNotification) pMsg).getCode());
			errorMsg.setSourceCardPAN(((CmFinoFIX.CMSubscriberNotification) pMsg).getSourceCardPAN());
			errorMsg.setAmount(errMsg.getAmount());
			return errorMsg;

		}
		return super.handleResponse(pMsg);
	}
	

	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}



	/**
	 * Returns the Corresponding Agent Notification Message for the given message 
	 * @param validationResult
	 * @return
	 */
	public Integer processValidationResultForAgent(Integer validationResult) {
		if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_AgentAccountInactive;
		} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_AgentNotRegistered;
		} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_AgentAccountIsRestricted;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestricted;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActive;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_AgentAccountIsRestricted;
		} 
		return validationResult;
	}

	/**
	 * Returns the Corresponding Agent Notification Message for the given message 
	 * @param validationResult
	 * @return
	 */
	public Integer processValidationResultForPartner(Integer validationResult) {
		if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerRestriction;
		} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerNotFound;
		} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerRestriction;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerRestriction;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerRestriction;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_PartnerNotFound;
		} 
		return validationResult;
	}

	
	/**
	 * Returns the Corresponding Destination Agent Notification Message for the given message 
	 * @param validationResult
	 * @return
	 */
	public Integer processValidationResultForDestinationAgent(Integer validationResult) {
		if (CmFinoFIX.NotificationCode_MDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActive;
		} else if (CmFinoFIX.NotificationCode_MDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		} else if (CmFinoFIX.NotificationCode_MDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestricted;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsRestricted.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsRestricted;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNIsNotActive.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentIsNotActive;
		} else if (CmFinoFIX.NotificationCode_DestinationMDNNotFound.equals(validationResult)) {
			validationResult = CmFinoFIX.NotificationCode_DestinationAgentNotFound;
		} 
		return validationResult;
	}

	// /**


	/**
	 * Adds Company and Language to the result
	 * 
	 * @param SubscriberMDN
	 *            the MDN of the subscriber whose language and company are set
	 *            to result.Use this method if you already have a non null
	 *            subscriberMDN
	 * 
	 * @param Result
	 *            result returned by createResult();
	 * 
	 * @author Gurram Karthik
	 */

	public void addCompanyANDLanguageToResult(SubscriberMDN subscriberMDN, Result result) {
		if (subscriberMDN != null) {
			Company company = subscriberMDN.getSubscriber().getCompany();
			Integer language = subscriberMDN.getSubscriber().getLanguage();
			result.setCompany(company);
			result.setLanguage(language);
		}
	}


	protected String msgToData(CMBase base) {
		CMultiXBuffer buffer = new CMultiXBuffer();
		try{
			base.toFIX(buffer);
			String data =new String(buffer.DataPtr());
			return data;
		}catch (Exception error) {
			log.error("error in converting msg to data",error);
			return base.DumpFields();
		}
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return the hibernateSessionHolder
	 */
	public HibernateSessionHolder getHibernateSessionHolder() {
		return hibernateSessionHolder;
	}

	/**
	 * @param hibernateSessionHolder the hibernateSessionHolder to set
	 */
	public void setHibernateSessionHolder(
			HibernateSessionHolder hibernateSessionHolder) {
		this.hibernateSessionHolder = hibernateSessionHolder;
	}
	
	public Result handle(){
		return null;
	}

}