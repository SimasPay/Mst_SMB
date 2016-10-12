package com.mfino.bsim.iso8583.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberDetailsFromBank;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;


public class ATMRequestHandler extends FIXMessageHandler {

	private static Logger log = LoggerFactory.getLogger(ATMRequestHandler.class);
	private SessionFactory sessionFactory;
	private HibernateTransactionManager htm;
	private static ATMRequestHandler atmRequestHandler;
	private SubscriberMdnService subscriberMdnService; 
	private PocketService pocketService;
	private SubscriberService subscriberService;
	private SMSService smsService;
	private static Properties property;

	private NotificationMessageParserService notificationMessageParserService;
	
	public static ATMRequestHandler createInstance(){
		if(atmRequestHandler==null){
			atmRequestHandler = new ATMRequestHandler();
			property = new Properties();
		}
		
		return atmRequestHandler;
	}
	
	public static ATMRequestHandler getInstance(){
		if(atmRequestHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return atmRequestHandler;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void handle(ISOMsg msg) throws Exception {
		String notificationLanguage=msg.getString(121); 
		if(StringUtils.isNotBlank(notificationLanguage)){ 
			notificationLanguage=notificationLanguage.toUpperCase(); 
		} 
		try{
		log.info("ATMRequestHandler :: handle()");
		FIXMessageHandler handler = null;
		Integer response=null;
		sessionFactory = htm.getSessionFactory();
		Session session = SessionFactoryUtils.getSession(sessionFactory, true);
		TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		try {
			property.load(new FileInputStream("mfino_conf"+File.separator+"ATMCodes.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			log.error("Cannot load ATMCodes properties file " +e.getMessage());
			msg.set(39,GetConstantCodes.FAILURE);
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("Cannot load ATMCodes properties file " + e.getMessage());
			msg.set(39,GetConstantCodes.FAILURE);
			return;
		}
		String sourceMDN=subscriberService.normalizeMDN(msg.getString("61"));
		String accountNumber=msg.getString("102");
		SubscriberMdn subMDNByMDN = subscriberMdnService.getByMDN(sourceMDN);
		SubscriberMdn subMDNByAccountNumber = null;
		PocketQuery query = new PocketQuery();
		query.setCardPan(accountNumber);
		List<Pocket> pocketList = pocketService.get(query);
		if (!pocketList.isEmpty()) {
			subMDNByAccountNumber = pocketList.get(0).getSubscriberMdn();
		}
		if(null==subMDNByMDN && null==subMDNByAccountNumber){
			//new mdn and new account number
			handler= ATMRegistrationHandler.getInstance();
			response = ((ATMRegistrationHandler) handler).handle(msg,session);
			if(response.equals(CmFinoFIX.NotificationCode_SubscriberRegistrationSuccessfulToAgent)){
				msg.set(62, property.getProperty(GetConstantCodes.ATMCode_NewRegistrationSuccess+"_"+notificationLanguage));
			}else if(response.equals(CmFinoFIX.NotificationCode_InvalidMDNLength)){ 
			    msg.set(62, property.getProperty(GetConstantCodes.ATMCode_RejectRequest+"_"+notificationLanguage));
			}else{
				msg.set(62,property.getProperty(GetConstantCodes.ATMCode_InternalFailure+"_"+notificationLanguage));
			}
		}else if(null!=subMDNByMDN && null!=subMDNByAccountNumber && subMDNByAccountNumber.equals(subMDNByMDN)){
			//old mdn and old accountnumber . Change pin request
			handler= ATMChangePinHandler.getInstance();
			response = ((ATMChangePinHandler) handler).handle(msg,session);
			if(response.equals(CmFinoFIX.NotificationCode_ChangePINCompleted)){
				msg.set(62,property.getProperty(GetConstantCodes.ATMCode_ChangePinSuccess+"_"+notificationLanguage));
			}else{
				msg.set(62,property.getProperty(GetConstantCodes.ATMCode_InternalFailure+"_"+notificationLanguage));
			}
		}else if(null!=subMDNByMDN && null!=subMDNByAccountNumber && !(subMDNByAccountNumber.equals(subMDNByMDN))){
			//existing mdn and accountnumber mapped to different mdn. Reject Request
			log.info("ATMRequestHandler :: handle() Received request for existing mdn and accountnumber mapped to different mdn");
			msg.set(39,GetConstantCodes.REJECT);
			msg.set(62,property.getProperty(GetConstantCodes.ATMCode_RejectRequest+"_"+notificationLanguage));
		}else if(null==subMDNByMDN && null!=subMDNByAccountNumber){
			//new mdn and existing account . Change MDN not handling as of now
			log.info("ATMRequestHandler :: handle() Received request for change mdn. Reject the request");
			msg.set(39,GetConstantCodes.REJECT);
			msg.set(62,property.getProperty(GetConstantCodes.ATMCode_ChangeMDNFailure+"_"+notificationLanguage));
		}else if(null!=subMDNByMDN && null==subMDNByAccountNumber){
			//existing mdn and new account number . Reject the request
			log.info("ATMRequestHandler :: handle() Received request for existing mdn and new account number. Reject the request");
			msg.set(39,GetConstantCodes.REJECT);
			msg.set(62,property.getProperty(GetConstantCodes.ATMCode_RejectRequest+"_"+notificationLanguage));
		}

		Integer notificationCode = null;
		if(null==response){
			log.info("ATMRequestHandler :: handle Got Response Null");
			return;
		}else{
			notificationCode=response;
		}
		try{
			NotificationWrapper notificationWrapper=new NotificationWrapper();
			notificationWrapper.setCode(notificationCode);
			notificationWrapper.setDestMDN(sourceMDN);
			smsService.setDestinationMDN(sourceMDN);
			notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			smsService.setMessage(notificationMessageParserService.buildMessage(notificationWrapper,true));
			//smsService.setSctlId(sctl.getID());
			smsService.send();
		}catch(Exception e){
			log.error(e.getMessage());
			msg.set(62,property.getProperty(GetConstantCodes.ATMCode_InternalFailure+"_"+notificationLanguage));
		}
		}finally{
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void updateDetails(ISOMsg msg){
		try{
			sessionFactory = htm.getSessionFactory();
			Session session = SessionFactoryUtils.getSession(sessionFactory, true);
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			CmFinoFIX.CMGetSubscriberDetailsToBank toBank = new CmFinoFIX.CMGetSubscriberDetailsToBank();
			String sourceMDN=subscriberService.normalizeMDN(msg.getString("61"));
			String transactionID = msg.getString("11");
			log.info("TransactionHandler :: updateDetails transactionID ="+transactionID);
			Long transID = Long.parseLong(transactionID);
			log.info("TransactionHandler :: updateDetails transID ="+transID);
			toBank.setSourceMDN(sourceMDN);
			toBank.setTransactionID(transID);
			toBank.setSourceCardPAN(msg.getString("2"));
			toBank.setApplicationID(msg.getString("48").substring(3,23));
			toBank.setSourceApplication(CmFinoFIX.SourceApplication_BackEnd);
			SubscriberMdn subMDNByMDN = subscriberMdnService.getByMDN(sourceMDN);
			Subscriber subscriber = subMDNByMDN.getSubscriber();
			if("Simobicustomer".equalsIgnoreCase(subscriber.getFirstname()))
			{
				CFIXMsg responseMessage = super.process(toBank);
				if(null!=responseMessage && responseMessage instanceof CMGetSubscriberDetailsFromBank){
					CMGetSubscriberDetailsFromBank fromBank = (CMGetSubscriberDetailsFromBank) responseMessage;
					log.info("ATMRequestHandler :: Response Success "+ fromBank.getResponseCode());
					if(fromBank.getResponseCode().equals(CmFinoFIX.ISO8583_ResponseCode_Success)) {
						subscriber.setFirstname(fromBank.getFirstName());
						subscriber.setLastname(fromBank.getLastName());
						subscriber.setEmail(fromBank.getEmail());	
						subscriberService.saveSubscriber(subscriber);
					}
					log.info("TransactionHandler :: finally block sourcemdn "+ fromBank.getSourceMDN());
				}
			}
		}finally{
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			sessionHolder.getSession().flush();
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
	
	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public SMSService getSmsService() {
		return smsService;
	}

	public void setSmsService(SMSService smsService) {
		this.smsService = smsService;
	}

	public NotificationMessageParserService getNotificationMessageParserService() {
		return notificationMessageParserService;
	}

	public void setNotificationMessageParserService(
			NotificationMessageParserService notificationMessageParserService) {
		this.notificationMessageParserService = notificationMessageParserService;
	}
}