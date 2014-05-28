package com.mfino.mce.notification.impl;

import static com.mfino.mce.core.util.MCEUtil.isNullOrEmpty;
import static com.mfino.mce.core.util.MCEUtil.safeString;
import static com.mfino.mce.core.util.MCEUtil.getCurrentDateTime;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.SimpleEmail;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromBank;
import com.mfino.fix.CmFinoFIX.CMBalanceInquiryFromNFC;
import com.mfino.fix.CmFinoFIX.CMBankAccountBalanceInquiry;
import com.mfino.fix.CmFinoFIX.CMBase;
import com.mfino.fix.CmFinoFIX.CMGetLastTransactionsFromBank;
import com.mfino.fix.CmFinoFIX.CMGetUserAPIKeyFromBank;
import com.mfino.fix.CmFinoFIX.CMSMSNotification;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.hibernate.Timestamp;
import com.mfino.mce.core.CoreDataWrapper;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.core.util.NotificationCodes;
import com.mfino.mce.core.util.ResponseCodes;
import com.mfino.mce.notification.EmailNotification;
import com.mfino.mce.notification.Notification;
import com.mfino.mce.notification.NotificationPersistenceService;
import com.mfino.mce.notification.NotificationService;
import com.mfino.mce.notification.NotificationWrapper;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberServiceExtended;
import com.mfino.service.impl.SystemParametersServiceImpl;
import com.mfino.util.MfinoUtil;


/**
 * @author sasidhar
 *
 */
public class NotificationServiceDefaultImpl implements NotificationService {

	private SessionFactory sessionFactory;
	private CoreDataWrapper coreDataWrapper;
	private NotificationPersistenceService notificationPersistenceService;
	private SubscriberServiceExtended subscriberServiceExtended;
	private PocketService pocketService;

	private SimpleEmail email = new SimpleEmail() ;

	private Log log = LogFactory.getLog(NotificationServiceDefaultImpl.class);

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public NotificationWrapper processMessage(MCEMessage mesg) 
	{
		log.info("NotificationServiceDefaultImpl :: processMessage");

		NotificationWrapper notificationWrapper = new NotificationWrapper();

		try
		{
			List<Notification> notifications = new ArrayList<Notification>();

			if(mesg.getResponse() instanceof BackendResponse){

				BackendResponse backendResponse = (BackendResponse)mesg.getResponse();
				if(!isNullOrEmpty(backendResponse.getSourceMDN())){
					SubscriberMDN subMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN());
					if ((subMDN != null) && (subMDN.getSubscriber() != null)) {
						Subscriber sub = subMDN.getSubscriber();
						backendResponse.setSenderFirstName(sub.getFirstName());
						backendResponse.setSenderLastName(sub.getLastName());
						if(CmFinoFIX.SubscriberType_Partner.equals(sub.getType())) {
							Partner part=DAOFactory.getInstance().getPartnerDAO().getPartnerBySubscriber(sub);
							backendResponse.setSenderTradeName(part.getTradeName());
						}
					}
				}
				else if(!isNullOrEmpty(backendResponse.getSenderMDN())) {
					SubscriberMDN subMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSenderMDN());
					if ((subMDN != null) && (subMDN.getSubscriber() != null)) {
						Subscriber sub = subMDN.getSubscriber();
						backendResponse.setSenderFirstName(sub.getFirstName());
						backendResponse.setSenderLastName(sub.getLastName());
						if(CmFinoFIX.SubscriberType_Partner.equals(sub.getType())) {
							Partner part=DAOFactory.getInstance().getPartnerDAO().getPartnerBySubscriber(sub);
							backendResponse.setSenderTradeName(part.getTradeName());
						}						
					}
				}
					
				if(!isNullOrEmpty(backendResponse.getReceiverMDN())){
					SubscriberMDN subMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getReceiverMDN());
					if ((subMDN != null) && (subMDN.getSubscriber() != null)) {
						Subscriber sub = subMDN.getSubscriber();
						backendResponse.setReceiverFirstName(sub.getFirstName());
						backendResponse.setReceiverLastName(sub.getLastName());
						if(CmFinoFIX.SubscriberType_Partner.equals(sub.getType())) {
							Partner part=DAOFactory.getInstance().getPartnerDAO().getPartnerBySubscriber(sub);
							backendResponse.setReceiverTradeName(part.getTradeName());
						}
					}
				}

				log.info("fixResponse.getInternalErrorCode()="+backendResponse.getInternalErrorCode());

				log.info("fixResponse dump fields "+backendResponse.DumpFields());

				Integer internalErrorCode = 0;

				if(!isNullOrEmpty(backendResponse.getExternalResponseCode())){
					ResponseCodes responseCode = ResponseCodes.getResponseCodes(1, backendResponse.getExternalResponseCode());
					if(responseCode != null){
						internalErrorCode = responseCode.getInternalErrorCode();
					}
					else{
						log.error("Could not find a response code for external error code "+backendResponse.getExternalResponseCode());
					}
				}
				else{
					internalErrorCode = backendResponse.getInternalErrorCode();
				}

				log.info("NotificationService : internalErrorCode "+internalErrorCode);

				NotificationCodes notificationCode = NotificationCodes.getNotificationCode(internalErrorCode);
				NotificationCodes receiverNotificationCode = NotificationCodes.getReceiverNotificationCode(internalErrorCode);
				NotificationCodes onBehalfOfNotificationCode = NotificationCodes.getOnBehalfOfNotificationCode(internalErrorCode);

				if(backendResponse.getLanguage() == null)
				{
					Integer language = CmFinoFIX.Language_English;
					SubscriberMDN smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN());
					if(smdn == null)
					{
						smdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSenderMDN());
					}
					if(smdn != null)
					{
						language = smdn.getSubscriber().getLanguage();
					}
					backendResponse.setLanguage(language);
				}
// Bala: Commented this as it not required for Hub.
//				if(backendResponse.getCharges() != null && backendResponse.getServiceChargeTransactionLogID()!=null)
//				{
//					ServiceChargeTransactionLog sctl = DAOFactory.getInstance().getServiceChargeTransactionLogDAO().getById(backendResponse.getServiceChargeTransactionLogID().longValue());
//					if (sctl.getTransactionRuleID() != null) {
//						TransactionRule transactionRule = DAOFactory.getInstance().getTransactionRuleDAO().getById(sctl.getTransactionRuleID());
//						if (CmFinoFIX.ServiceChargeType_Inclusive.equals(transactionRule.getChargeMode())) {
//							BigDecimal Amount = backendResponse.getAmount().add(backendResponse.getCharges());
//							backendResponse.setCharges(null);
//							backendResponse.setAmount(Amount);
//						}
//					}
//			     }

				log.info("NotificationServiceDefaultImpl :: notificationCode="+notificationCode);
				log.info("NotificationServiceDefaultImpl :: fixResponse.getLanguage()="+backendResponse.getLanguage());

				if(notificationCode != null) {
					log.info("code="+notificationCode.getNotificationCode() + ", language="+backendResponse.getLanguage() + ", notificationMethod="+CmFinoFIX.NotificationMethod_SMS);
					com.mfino.domain.Notification objSmsNotification = coreDataWrapper.getNotification(notificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_SMS);
					log.info("SMS Notification from DB = "+objSmsNotification);

					log.info("code="+notificationCode.getNotificationCode() + ", language="+backendResponse.getLanguage() + ", notificationMethod="+CmFinoFIX.NotificationMethod_Email);
					com.mfino.domain.Notification objEmailNotification = coreDataWrapper.getNotification(notificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_Email);
					log.info("Email Notification from DB = "+objEmailNotification);

					log.info("code="+notificationCode.getNotificationCode() + ", language="+backendResponse.getLanguage() + ", notificationMethod="+CmFinoFIX.NotificationMethod_Web);
					com.mfino.domain.Notification objWebNotification = coreDataWrapper.getNotification(notificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_Web);
					log.info("Web Notification from DB = "+objWebNotification);

					if((objWebNotification != null) && (objWebNotification.getIsActive()))
					{
						notificationWrapper.setWebResponse(getWebResponse(mesg, objWebNotification));
					}					

					if((objSmsNotification != null) && (objSmsNotification.getIsActive()))
					{
						//Skip SMS to Sender in case of STK Request (Channel code is 6)
						if (! (CmFinoFIX.SourceApplication_STK.equals(((CMBase)mesg.getRequest()).getSourceApplication())) ||
								(CmFinoFIX.SourceApplication_ATM.equals(((CMBase)mesg.getRequest()).getSourceApplication())) ){ 
							if(!isNullOrEmpty(backendResponse.getSourceMDN())){
								if((objSmsNotification != null) && (notificationCode.getIsNotificationRequired())){
									log.info("constructing notification for mdn="+backendResponse.getSourceMDN());
									Subscriber subscriber = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN()).getSubscriber();
									if((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_SMS) > 0)
									{
										backendResponse.setFirstName(subscriber.getFirstName());
										backendResponse.setLastName(subscriber.getLastName());

										SMSNotification notification  = new SMSNotification();
										notification.setMdn(backendResponse.getSourceMDN());
										notification.setContent(getNotificationText(backendResponse, objSmsNotification));
										notification.setNotificationCode(notificationCode.getNotificationCode());
										log.info("NotificationService source mdn="+notification.getMdn() + ", notificationCode="+notification.getNotificationCode());
										notifications.add(notification);
										notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), notification, CmFinoFIX.NotificationMethod_SMS, CmFinoFIX.NotificationReceiverType_Source);
									}
								}
							}
						}
					}

					if((objEmailNotification != null) && (objEmailNotification.getIsActive()))
					{
						if(!isNullOrEmpty(backendResponse.getSourceMDN())){
							if((objEmailNotification != null) && (notificationCode.getIsNotificationRequired())){
								log.info("constructing notification for mdn="+backendResponse.getSourceMDN());
								Subscriber subscriber = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN()).getSubscriber();

								if((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber))
								{
									backendResponse.setFirstName(subscriber.getFirstName());
									backendResponse.setLastName(subscriber.getLastName());

									EmailNotification emailNotification = new EmailNotification();
									String[] recipients = {subscriber.getEmail()};
									emailNotification.setToRecipents(recipients);
									emailNotification.setNotificationCode(notificationCode.getNotificationCode());
									emailNotification.setContent(getNotificationText(backendResponse, objEmailNotification));
									emailNotification.setSubject(notificationCode.name());
									notifications.add(emailNotification);
									notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), emailNotification, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Source);

								}								
							}
						}
					}
				}
				else{
					log.error("Could not find notification code for internal error code "+internalErrorCode);
				}

				if((receiverNotificationCode != null) && (receiverNotificationCode.getIsNotificationRequired()) && (!isNullOrEmpty(backendResponse.getReceiverMDN())))
				{
					if(!(backendResponse.getReceiverMDN().equals(backendResponse.getSourceMDN()))){
					log.info("receiverNotificationCode="+notificationCode.getNotificationCode() + ", language="+backendResponse.getLanguage() + ", notificationMethod="+CmFinoFIX.NotificationMethod_SMS);
					com.mfino.domain.Notification smsReceiverNotification = coreDataWrapper.getNotification(receiverNotificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_SMS);
					com.mfino.domain.Notification emailReceiverNotification = coreDataWrapper.getNotification(receiverNotificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_Email);

					SubscriberMDN subscriberMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getReceiverMDN());
					Subscriber subscriber = null;
					if (subscriberMDN != null) {
						subscriber = subscriberMDN.getSubscriber();
						backendResponse.setFirstName(subscriber.getFirstName());
						backendResponse.setLastName(subscriber.getLastName());
					}

					if (! (CmFinoFIX.SourceApplication_ATM.equals(((CMBase)mesg.getRequest()).getSourceApplication()))) {
						if((smsReceiverNotification != null) && ( smsReceiverNotification.getIsActive())){
							log.info("constructing notification for mdn="+backendResponse.getReceiverMDN());
							if((subscriber != null) && (subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_SMS) > 0)
							{
								SMSNotification notification  = new SMSNotification();
								notification.setMdn(backendResponse.getReceiverMDN());
								notification.setContent(getNotificationText(backendResponse, smsReceiverNotification));
								notification.setNotificationCode(receiverNotificationCode.getNotificationCode());
								log.info("NotificationService receiverMdn="+notification.getMdn() + ", notificationCode="+notification.getNotificationCode());
								notifications.add(notification);
								notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), notification, CmFinoFIX.NotificationMethod_SMS, CmFinoFIX.NotificationReceiverType_Destination);
							}
						}

						

						if((emailReceiverNotification != null) && (emailReceiverNotification.getIsActive()))
						{
							if((subscriber != null) && (subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0 && subscriberServiceExtended.isSubscriberEmailVerified(subscriber))
							{
								EmailNotification emailNotification = new EmailNotification();
								String[] recipients = {subscriber.getEmail()};
								emailNotification.setToRecipents(recipients);
								emailNotification.setNotificationCode(notificationCode.getNotificationCode());
								emailNotification.setContent(getNotificationText(backendResponse, emailReceiverNotification));
								emailNotification.setSubject(receiverNotificationCode.name());
								notifications.add(emailNotification);
								notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), emailNotification, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_Destination);

							}
						}
					}
				}
				}


				if(onBehalfOfNotificationCode !=null &&onBehalfOfNotificationCode.getIsNotificationRequired() && (!isNullOrEmpty(backendResponse.getOnBehalfOfMDN()))){
					if(!( (backendResponse.getOnBehalfOfMDN().equals(backendResponse.getSourceMDN())  || 
							backendResponse.getOnBehalfOfMDN().equals(backendResponse.getReceiverMDN())) &&
							( (notificationCode!=null && notificationCode.getNotificationCode().equals(onBehalfOfNotificationCode.getNotificationCode())) || 
							   (receiverNotificationCode!=null && receiverNotificationCode.getNotificationCode().equals(onBehalfOfNotificationCode.getNotificationCode())) ) )){
					log.info("onBehalfOfNotificationCode="+notificationCode.getNotificationCode() + ", language="+backendResponse.getLanguage() + ", notificationMethod="+CmFinoFIX.NotificationMethod_SMS);
					
					com.mfino.domain.Notification onBehalfOfSmsNotification = coreDataWrapper.getNotification(onBehalfOfNotificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_SMS);
					com.mfino.domain.Notification onBehalfOfEmailNotification = coreDataWrapper.getNotification(onBehalfOfNotificationCode.getNotificationCode(), backendResponse.getLanguage(), CmFinoFIX.NotificationMethod_Email);
					SubscriberMDN subscriberMDN = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getOnBehalfOfMDN());
					Subscriber subscriber = null;
					if(subscriberMDN != null ){
						subscriber = subscriberMDN.getSubscriber();
						backendResponse.setFirstName(subscriber.getFirstName());
						backendResponse.setLastName(subscriber.getLastName());
					}

					if((onBehalfOfSmsNotification != null) && (onBehalfOfSmsNotification.getIsActive())){
						log.info("constructing notification for mdn="+backendResponse.getOnBehalfOfMDN());
						if(subscriber==null || ((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_SMS) > 0))
						{
							SMSNotification notification  = new SMSNotification();
							notification.setMdn(backendResponse.getOnBehalfOfMDN());
							notification.setContent(getNotificationText(backendResponse, onBehalfOfSmsNotification));
							notification.setNotificationCode(onBehalfOfNotificationCode.getNotificationCode());
							log.info("NotificationService onBehalfOfMdn="+notification.getMdn() + ", notificationCode="+notification.getNotificationCode());
							notifications.add(notification);
							notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), notification, CmFinoFIX.NotificationMethod_SMS, CmFinoFIX.NotificationReceiverType_OnBehalfOfSubscriber);
						}
						
					}

					if((onBehalfOfEmailNotification != null) && (onBehalfOfEmailNotification.getIsActive()))
					{
						if(subscriber!=null && ((subscriber.getNotificationMethod() & CmFinoFIX.NotificationMethod_Email) > 0) && subscriberServiceExtended.isSubscriberEmailVerified(subscriber))
						{
							EmailNotification emailNotification = new EmailNotification();
							String[] recipients = {subscriber.getEmail()};
							emailNotification.setToRecipents(recipients);
							emailNotification.setNotificationCode(notificationCode.getNotificationCode());
							emailNotification.setContent(getNotificationText(backendResponse, onBehalfOfEmailNotification));
							emailNotification.setSubject(onBehalfOfNotificationCode.name());
							notifications.add(emailNotification);
							notificationPersistenceService.persistNotification(backendResponse.getServiceChargeTransactionLogID(), emailNotification, CmFinoFIX.NotificationMethod_Email, CmFinoFIX.NotificationReceiverType_OnBehalfOfSubscriber);

						}
					}
				}
				}

				notificationWrapper.setNotifications(notifications);
			}
			/*
			 * SMS request from web
			 */
			else if(mesg.getRequest() instanceof CmFinoFIX.CMSMSNotification)
			{
				CMSMSNotification request = (CMSMSNotification)mesg.getRequest();
				SMSNotification sms = new SMSNotification();
				sms.setMdn(request.getTo());
				sms.setContent(request.getText());
				sms.setNotificationLogDetailsID(request.getNotificationLogDetailsID());
				sms.setNotificationCode(request.getCode());
				notifications.add(sms);
				if(!request.getIsDuplicateSMS())
				{
					notificationPersistenceService.persistNotification(request.getServiceChargeTransactionLogID(), sms, CmFinoFIX.NotificationMethod_SMS, CmFinoFIX.NotificationReceiverType_Source);
				}
				notificationWrapper.setNotifications(notifications);
				/**
				 * TODO: populate the response properly
				 * All we need here to send to web is if a request to SMS was logged or not.
				 */
				CMSubscriberNotification response = new CMSubscriberNotification();
				response.setText("sms message queued for sending");
				notificationWrapper.setWebResponse(response);
			}
			else{
				log.error("Bug in routing logic, Notification Service cannot handle any thing other than CMFixResponse");
			}

//			session.getTransaction().commit();
		}
		catch(Exception error){
			log.error("Error processing message ", error);
//			session.getTransaction().rollback();			
		}
		
		//clean up code

		return notificationWrapper;
	}


	private CFIXMsg getWebResponse(MCEMessage mceMessage, com.mfino.domain.Notification notification){

		log.info("NotificationServiceDefaultImpl :: getWebResponse() BEGIN");
		CFIXMsg webResponse = null;
		BackendResponse backendResponse = (BackendResponse)mceMessage.getResponse();
		log.info("NotificationServiceDefaultImpl :: getWebResponse() SourceMDN=" + backendResponse.getSourceMDN());
		log.info("NotificationServiceDefaultImpl :: getWebResponse() SenderMDN=" + backendResponse.getSenderMDN());
		SubscriberMDN subscriberMdn = null;
		if(backendResponse.getSourceMDN() != null)
		{
			subscriberMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN());
		}
		else if(backendResponse.getSenderMDN() != null)
		{
			subscriberMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSenderMDN());
		}
		if(subscriberMdn != null)
		{
			backendResponse.setFirstName(subscriberMdn.getSubscriber().getFirstName());
			backendResponse.setLastName(subscriberMdn.getSubscriber().getLastName());
		}

		if(mceMessage.getRequest() instanceof CMBankAccountBalanceInquiry){
			CMSubscriberNotification response = new CMSubscriberNotification();

			log.info("NotificationServiceDefaultImpl :: getWebResponse for request=CMBankAccountBalanceInquiry backendResponse.Dump "+backendResponse.DumpFields());

			response.setReceiveTime(backendResponse.getReceiveTime());
			response.setCode(notification.getCode());
			response.setLanguage(backendResponse.getLanguage());
			response.setMSPID(backendResponse.getMSPID());
			response.setParentTransactionID(backendResponse.getParentTransactionID());
			response.setResult(backendResponse.getResult());
			response.setAmount(backendResponse.getSourceMDNBalance());
			backendResponse.setSourceCardPAN(((CMBankAccountBalanceInquiry)mceMessage.getRequest()).getCardPAN());
			response.setText(getNotificationText(backendResponse, notification));

			log.info("NotificationServiceDefaultImpl: getWebResponse()==> "+response.DumpFields());
			webResponse = response;
		} else if (mceMessage.getRequest() instanceof CMGetLastTransactionsFromBank) {
			webResponse = mceMessage.getRequest();
			log.info("Got the Response for Transaction History from Bank --> " + webResponse.DumpFields());
		}
		else if (mceMessage.getRequest() instanceof CMBalanceInquiryFromNFC) {
			webResponse = mceMessage.getRequest();
			log.info("Got the Response for Balance Inquiry from Bank --> " + webResponse.DumpFields());
		} else if(mceMessage.getRequest() instanceof CMGetUserAPIKeyFromBank){
			webResponse = mceMessage.getRequest();
			log.info("Got the UserAPIKey from Flashiz --> " + webResponse.DumpFields());
		}
		else 
		{
			CMSubscriberNotification response = new CMSubscriberNotification();

			log.info("NotificationServiceDefaultImpl# :: getWebResponse for request=CMBankAccountToBankAccount backendResponse.Dump "+backendResponse.DumpFields());

			response.setReceiveTime(backendResponse.getReceiveTime());
			response.setCode(notification.getCode());
			response.setLanguage(backendResponse.getLanguage());
			response.setMSPID(backendResponse.getMSPID());
			response.setParentTransactionID(backendResponse.getParentTransactionID());
			response.setResult(backendResponse.getResult());
			response.setText(getNotificationText(backendResponse, notification));
			response.setSourceMDN(backendResponse.getSourceMDN());
			response.setSourceCardPAN(backendResponse.getSourceCardPAN());
			response.setReceiverMDN(backendResponse.getReceiverMDN());
			response.setLanguage(backendResponse.getLanguage());
			response.setMessageType(backendResponse.getMessageType());
			response.setTransactionID(backendResponse.getTransferID());
			response.setDestinationType(backendResponse.getDestinationType());
			response.setBankName(backendResponse.getBankName());
			response.setDestinationUserName(backendResponse.getDestinationUserName());
			response.setAdditionalInfo(backendResponse.getAdditionalInfo());
			response.setNominalAmount(backendResponse.getNominalAmount());
			response.setPaymentInquiryDetails(backendResponse.getPaymentInquiryDetails());
			if(backendResponse.getAmount() != null)
				response.setAmount(backendResponse.getAmount());
			response.setBillPaymentReferenceID(backendResponse.getBillPaymentReferenceID());
			response.setOperatorCharges(backendResponse.getCharges());
			response.setOperatorMessage(backendResponse.getOperatorMessage());
			log.info("NotificationServiceDefaultImpl: getWebResponse()==> "+response.DumpFields());
			webResponse = response;
		}

		return webResponse;
	}

	public CoreDataWrapper getCoreDataWrapper() {
		return coreDataWrapper;
	}

	public void setCoreDataWrapper(CoreDataWrapper coreDataWrapper) {
		this.coreDataWrapper = coreDataWrapper;
	}

	public SimpleEmail getEmail() {
		return email;
	}

	public void setEmail(SimpleEmail email) {
		this.email = email;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * This method can be overrriden to provide a different pattern for customer specific 
	 * implementation 
	 * @return
	 */
	protected String getNumberFormatPattern()
	{
		return "###,###,###,###.00";
	}

	public String getNotificationText(BackendResponse backendResponse, com.mfino.domain.Notification notification){

		if(notification == null) return "";

		String rawNotificationText = notification.getText();
		NumberFormat numberFormat = MfinoUtil.getNumberFormat();
		SystemParametersServiceImpl systemParametersServiceImpl = new SystemParametersServiceImpl();
		SubscriberMDN senderMdn = null;
        if(rawNotificationText.contains("$(SenderFirstName)") || rawNotificationText.contains("$(SenderLastName)"))
        {
        	senderMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSourceMDN());
        	if(senderMdn == null)
        		senderMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getSenderMDN());			
        }
        SubscriberMDN receiverMdn = null;
        if(rawNotificationText.contains("$(ReceiverFirstName)") || rawNotificationText.contains("$(ReceiverLastName)"))
        {
        	receiverMdn = DAOFactory.getInstance().getSubscriberMdnDAO().getByMDN(backendResponse.getReceiverMDN());		
        }
        
        Pocket pocket = null;
        Pocket destPocket = null;
        if(rawNotificationText.contains("$(CardPan)") || rawNotificationText.contains("$(CardPAN)") || rawNotificationText.contains("$(CardAlias)"))
        {
        	if(backendResponse.getSourceCardPAN() != null)
        	{
        		pocket = pocketService.getByCardPan(backendResponse.getSourceCardPAN());
        	}
        	else
        	{
        		pocket = pocketService.getById(backendResponse.getSourcePocketId());
        	}
        }
        if(rawNotificationText.contains("$(DestinationCardPan)") || rawNotificationText.contains("$(DestinationCardPAN)") || rawNotificationText.contains("$(DestinationCardAlias)"))
        {
			destPocket = pocketService.getById(backendResponse.getDestPocketId());
        }
        
		try
		{
			if(rawNotificationText.contains("$(CurrentDateTime)")){
				rawNotificationText = rawNotificationText.replace("$(CurrentDateTime)", getCurrentDateTime());
			}

			if(rawNotificationText.contains("$(CommodityBalanceValue)")){
				BigDecimal sourceMdnBalance = backendResponse.getSourceMDNBalance();
				String strSourceMdnBalance = ((sourceMdnBalance != null) && (!(BigDecimal.valueOf(-1).equals(sourceMdnBalance)))) ? numberFormat.format(sourceMdnBalance) : "Not Available";
				rawNotificationText = rawNotificationText.replace("$(CommodityBalanceValue)", strSourceMdnBalance);			
			}

			if(rawNotificationText.contains("$(Currency)")){
				log.info("backendResponse.getCurrency()="+backendResponse.getCurrency());
				rawNotificationText = rawNotificationText.replace("$(Currency)", backendResponse.getCurrency() != null ? backendResponse.getCurrency() : systemParametersServiceImpl.getString(SystemParameterKeys.DEFAULT_CURRENCY_CODE));
			}

			if(rawNotificationText.contains("$(Amount)")){
				rawNotificationText = rawNotificationText.replace("$(Amount)", (backendResponse.getAmount() != null ? numberFormat.format(backendResponse.getAmount()) : ""));
			}
			
			//For Bill Payment to separate out actual bill amount and the third party fee, if any
			if(rawNotificationText.contains("$(NominalAmount)")){
				rawNotificationText = rawNotificationText.replace("$(NominalAmount)", (backendResponse.getNominalAmount() != null ? numberFormat.format(backendResponse.getNominalAmount()) : ""));
			}

			if(rawNotificationText.contains("$(ReceiverMDN)")){
				rawNotificationText = rawNotificationText.replace("$(ReceiverMDN)", safeString(backendResponse.getReceiverMDN()));
			}

			if(rawNotificationText.contains("$(SenderMDN)")){
				rawNotificationText = rawNotificationText.replace("$(SenderMDN)", safeString(backendResponse.getSourceMDN()));
			}

			if(rawNotificationText.contains("$(ReceiverAccountName)")){
				rawNotificationText = rawNotificationText.replace("$(ReceiverAccountName)", safeString(backendResponse.getReceiverName()));
			}

			if(rawNotificationText.contains("$(TransferID)")){
				if(backendResponse.getServiceChargeTransactionLogID()!=null){
					rawNotificationText = rawNotificationText.replace("$(TransferID)", ""+backendResponse.getServiceChargeTransactionLogID());
				}else if(backendResponse.getTransferID()!=null){
					rawNotificationText = rawNotificationText.replace("$(TransferID)", ""+backendResponse.getTransferID());
				}else{
					rawNotificationText = rawNotificationText.replace("$(TransferID)", "");
				}
			}

			if(rawNotificationText.contains("$(ParentTransactionID)")){
				rawNotificationText = rawNotificationText.replace("$(ParentTransactionID)", ""+backendResponse.getParentTransactionID());
			}

			if(rawNotificationText.contains("$(TransactionDateTime)")){
				rawNotificationText = rawNotificationText.replace("$(TransactionDateTime)", getCurrentDateTime());
			}

			if(rawNotificationText.contains("$(TransactionID)")){
				if(backendResponse.getServiceChargeTransactionLogID()!=null){
					rawNotificationText = rawNotificationText.replace("$(TransactionID)", ""+backendResponse.getServiceChargeTransactionLogID());
				}else{
					rawNotificationText = rawNotificationText.replace("$(TransactionID)", ""+backendResponse.getTransactionID());
				}
			}

			if(rawNotificationText.contains("$(CustomerServiceShortCode)")){
				rawNotificationText = rawNotificationText.replace("$(CustomerServiceShortCode)", ""+backendResponse.getCustomerServiceShortCode());
			}

			if(rawNotificationText.contains("$(ContactCenterNo)")){
				rawNotificationText = rawNotificationText.replace("$(ContactCenterNo)", safeString(backendResponse.getCustomerServiceShortCode()));
			}

			if(rawNotificationText.contains("$(BankName)")){
				rawNotificationText = rawNotificationText.replace("$(BankName)", safeString(backendResponse.getBankName()));
			}
			if(rawNotificationText.contains("$(serviceCharge)"))
			{
				rawNotificationText = rawNotificationText.replace("$(serviceCharge)", backendResponse.getCharges() != null? numberFormat.format(backendResponse.getCharges()):"0");
			}
			if(rawNotificationText.contains("$(NumberOfTriesLeft)"))
			{
				rawNotificationText = rawNotificationText.replace("$(NumberOfTriesLeft)", backendResponse.getNumberOfTrailsLeft()+"");
			} 

			if (rawNotificationText.contains("$(DestinationMDNBalance)")) {
				BigDecimal destinationMdnBalance = backendResponse.getDestinationMDNBalance();
				String strDestinationMdnBalance = ((destinationMdnBalance != null) && (!(BigDecimal.valueOf(-1).equals(destinationMdnBalance)))) ? numberFormat.format(destinationMdnBalance): "Not Available";
				rawNotificationText = rawNotificationText.replace("$(DestinationMDNBalance)", strDestinationMdnBalance);
			}

			if(rawNotificationText.contains("$(BankAccountCurrency"))
			{
				rawNotificationText = rawNotificationText.replace("$(BankAccountCurrency)", (backendResponse.getCurrency()!=null?backendResponse.getCurrency(): ""));

			}

			if(rawNotificationText.contains("$(BankAccountBalanceValue)"))
			{
				rawNotificationText = rawNotificationText.replace("$(BankAccountBalanceValue)", (backendResponse.getAmount()!=null?numberFormat.format(backendResponse.getAmount()): ""));
			}
			if (rawNotificationText.contains("$(PartnerCode)")) {
				rawNotificationText = rawNotificationText.replace("$(PartnerCode)", safeString(backendResponse.getPartnerCode()));
			}

			if (rawNotificationText.contains("$(BillerCode)")) {
				rawNotificationText = rawNotificationText.replace("$(BillerCode)", safeString(backendResponse.getBillerCode()));
			}
			if (rawNotificationText.contains("$(BillerName)")) {
				MFSBillerDAO mfsBillerDao = DAOFactory.getInstance().getMFSBillerDAO();
				MFSBiller mfsBiller = mfsBillerDao.getByBillerCode(backendResponse.getBillerCode());
				if(mfsBiller != null)
				{
					rawNotificationText = rawNotificationText.replace("$(BillerName)", safeString(mfsBiller.getMFSBillerName()));
				}
				else
				{
					rawNotificationText = rawNotificationText.replace("$(BillerName)", safeString(""));
				}
			}

			if (rawNotificationText.contains("$(InvoiceNumber)")) {
				rawNotificationText = rawNotificationText.replace("$(InvoiceNumber)", safeString(backendResponse.getInvoiceNumber()));
			}
			if (rawNotificationText.contains("$(OriginalTransferID)")) {
				rawNotificationText = rawNotificationText.replace("$(OriginalTransferID)", (backendResponse.getOriginalReferenceID()!=null?backendResponse.getOriginalReferenceID()+"":""));
			}
			if (rawNotificationText.contains("$(OneTimePin)")) {
				rawNotificationText = rawNotificationText.replace("$(OneTimePin)", safeString(backendResponse.getOneTimePin()));
			}
			if (rawNotificationText.contains("$(BankAccountNumber)")) {
				rawNotificationText = rawNotificationText.replace("$(BankAccountNumber)", safeString(backendResponse.getDestBankAccountNumber()));
			}
			if (rawNotificationText.contains("$(SourceBankAccountNumber)")) {
				rawNotificationText = rawNotificationText.replace("$(SourceBankAccountNumber)", safeString(backendResponse.getSourceBankAccountNumber()));
			}
			if (rawNotificationText.contains("$(DestinationBankAccountNumber)")) {
				rawNotificationText = rawNotificationText.replace("$(DestinationBankAccountNumber)", safeString(backendResponse.getDestBankAccountNumber()));
			}
			if (rawNotificationText.contains("$(DestinationType)")) {
				rawNotificationText = rawNotificationText.replace("$(DestinationType)", safeString(backendResponse.getDestinationType()));
			}
			if (rawNotificationText.contains("$(CustomerName)")) {
				rawNotificationText = rawNotificationText.replace("$(CustomerName)", safeString(backendResponse.getFirstName() + " " + backendResponse.getLastName()));
			}
			if (rawNotificationText.contains("$(FirstName)")) {
				rawNotificationText = rawNotificationText.replace("$(FirstName)", safeString(backendResponse.getFirstName()));
			}
			if (rawNotificationText.contains("$(SenderFirstName)")) {
				rawNotificationText = rawNotificationText.replace("$(SenderFirstName)", (senderMdn != null)?safeString(senderMdn.getSubscriber().getFirstName()):"");
			}
			if (rawNotificationText.contains("$(SenderLastName)")) {
				rawNotificationText = rawNotificationText.replace("$(SenderLastName)", (senderMdn != null)?safeString(senderMdn.getSubscriber().getLastName()):"");
			}
			if (rawNotificationText.contains("$(ReceiverFirstName)")) {
				rawNotificationText = rawNotificationText.replace("$(ReceiverFirstName)", (receiverMdn != null)?safeString(receiverMdn.getSubscriber().getFirstName()):"");
			}
			if (rawNotificationText.contains("$(ReceiverLastName)")) {
				rawNotificationText = rawNotificationText.replace("$(ReceiverLastName)", (receiverMdn != null)?safeString(receiverMdn.getSubscriber().getLastName()):"");
			}
			if (rawNotificationText.contains("$(SenderTradeName)")) {
				rawNotificationText = rawNotificationText.replace("$(SenderTradeName)", safeString(backendResponse.getSenderTradeName()));
			}
			if (rawNotificationText.contains("$(ReceiverTradeName)")) {
				rawNotificationText = rawNotificationText.replace("$(ReceiverTradeName)", safeString(backendResponse.getReceiverTradeName()));
			}
			if (rawNotificationText.contains("$(LastName)")) {
				rawNotificationText = rawNotificationText.replace("$(LastName)", safeString(backendResponse.getLastName()));
			}
			if (rawNotificationText.contains("$(MAXTxnLimit)")) {
				rawNotificationText = rawNotificationText.replace("$(MAXTxnLimit)", (backendResponse.getMaxTransactionLimit()!=null?backendResponse.getMaxTransactionLimit()+"":""));
			}
			if (rawNotificationText.contains("$(MINTxnLimit)")) {
				rawNotificationText = rawNotificationText.replace("$(MINTxnLimit)", (backendResponse.getMinTransactionLimit()!=null?backendResponse.getMinTransactionLimit()+"":""));
			}
			if (rawNotificationText.contains("$(ReceiverBankAccount)")) {
				rawNotificationText = rawNotificationText.replace("$(ReceiverBankAccount)", safeString(backendResponse.getReceiverAccountNo()));
			}
			if (rawNotificationText.contains("$(AgentName)")) {
				rawNotificationText = rawNotificationText.replace("$(AgentName)", safeString(backendResponse.getFirstName()));
			}
			if (rawNotificationText.contains("$(Reason)")) {
				rawNotificationText = rawNotificationText.replace("$(Reason)", safeString(backendResponse.getDescription()));
			}
			if (rawNotificationText.contains("$(OnBehalfOfMDN)")) {
				rawNotificationText = rawNotificationText.replace("$(OnBehalfOfMDN)", safeString(backendResponse.getOnBehalfOfMDN()));
			}
			if (rawNotificationText.contains("$(BenificiaryName)")) {
				rawNotificationText = rawNotificationText.replace("$(BenificiaryName)", safeString(backendResponse.getBeneficiaryName()));
			}
			if (rawNotificationText.contains("$(CardPAN)")) {
				rawNotificationText = rawNotificationText.replace("$(CardPAN)", safeString(pocket != null ?pocket.getCardPAN() : ""));
			}
			if (rawNotificationText.contains("$(DestinationCardPAN)")) {
				rawNotificationText = rawNotificationText.replace("$(DestinationCardPAN)", safeString(destPocket != null ?destPocket.getCardPAN() : ""));
			}
			if (rawNotificationText.contains("$(CardPan)")) {
				rawNotificationText = rawNotificationText.replace("$(CardPan)", safeString(pocket != null ?pocket.getCardPAN() : ""));
			}
			if (rawNotificationText.contains("$(DestinationCardPan)")) {
				rawNotificationText = rawNotificationText.replace("$(DestinationCardPan)", safeString(destPocket != null ?destPocket.getCardPAN() : ""));
			}
			if (rawNotificationText.contains("$(CardAlias)")) {
				rawNotificationText = rawNotificationText.replace("$(CardAlias)", safeString(pocket != null ?pocket.getCardAlias() : ""));
			}
			if (rawNotificationText.contains("$(DestinationCardAlias)")) {
				rawNotificationText = rawNotificationText.replace("$(DestinationCardAlias)", safeString(destPocket != null ?destPocket.getCardAlias() : ""));
			}
			//RechargePin variable is being used to populate VoucherToken which we get as part of PLN transaction for bill payments
			if (rawNotificationText.contains("$(VoucherToken)") && StringUtils.isNotBlank(backendResponse.getRechargePin())) {
				rawNotificationText = rawNotificationText.replace("$(VoucherToken)", " Voucher Token - " + safeString(backendResponse.getRechargePin()));
			} else {
				rawNotificationText = rawNotificationText.replace("$(VoucherToken)", "");
			}
			if (rawNotificationText.contains("$(OperatorMessage)")) {
				rawNotificationText = rawNotificationText.replace("$(OperatorMessage)", safeString(backendResponse.getOperatorMessage()));
			}
			if (rawNotificationText.contains("$(NotificationCode)")) {
				StringBuilder NotificationCode = new StringBuilder();
				NotificationCode.append("(");
				NotificationCode.append(notification.getCode());
				NotificationCode.append(")");
				rawNotificationText = rawNotificationText.replace("$(NotificationCode)", safeString(NotificationCode.toString()));
			}
		}
		catch(Exception e){
			log.error("NotificationServiceDefaultImpl :: getNotificationText() : there was an error ", e);
		}

		return rawNotificationText;
	}
	
	
	public NotificationPersistenceService getNotificationPersistenceSerive() {
		return notificationPersistenceService;
	}


	public void setNotificationPersistenceService(NotificationPersistenceService notificationPersistenceService) {
		this.notificationPersistenceService = notificationPersistenceService;
	}


	public SubscriberServiceExtended getSubscriberServiceExtended() {
		return subscriberServiceExtended;
	}


	public void setSubscriberServiceExtended(
			SubscriberServiceExtended subscriberServiceExtended) {
		this.subscriberServiceExtended = subscriberServiceExtended;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}
	

}