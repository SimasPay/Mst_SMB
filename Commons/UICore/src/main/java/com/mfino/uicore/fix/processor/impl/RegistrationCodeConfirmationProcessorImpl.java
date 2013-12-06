/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.fix.processor.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CreditCardDestinationDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.UserDAO;
import com.mfino.domain.CardInfo;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSConfirmationCode;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.service.NotificationMessageParserService;
import com.mfino.service.PocketService;
import com.mfino.service.SMSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RegistrationCodeConfirmationProcessor;
import com.mfino.util.ConfigurationUtil;

/**
 * 
 * @author admin
 */
@Service("RegistrationCodeConfirmationProcessorImpl")
public class RegistrationCodeConfirmationProcessorImpl extends BaseFixProcessor implements RegistrationCodeConfirmationProcessor{
	private UserDAO userDao = DAOFactory.getInstance().getUserDAO();
	private CardInfoDAO cardDao = DAOFactory.getInstance().getCardInfoDAO();
	private CreditCardDestinationDAO creditCardDestinationDAO = DAOFactory.getInstance().getCreditCardDestinationDAO();

	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("NotificationMessageParserServiceImpl")
	private NotificationMessageParserService notificationMessageParserService;
	
	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSConfirmationCode realMsg = (CMJSConfirmationCode) msg;
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		User user = userDao.getByUserName(realMsg.getUsername());
		Integer userAccountStatus=0;
		if (user == null) {
			errorMsg.setErrorCode(1);
			errorMsg.setErrorDescription(MessageText._("Invalid user or confirmation code."));
		} else if (CmFinoFIX.UserStatus_Expired.equals(user.getStatus())) {
			log.info("user already expired");
			errorMsg.setErrorCode(2);
			errorMsg.setErrorDescription(MessageText._("Your registration request has been expired"));
		} else if (CmFinoFIX.UserStatus_Confirmed.equals(user.getStatus())) {
			log.info("user already confirmed");
			errorMsg.setErrorCode(3);
			errorMsg.setErrorDescription(MessageText._("Your registration request status was confirmed, please wait to get it activated."));
		} else if (CmFinoFIX.UserStatus_Registered.equals(user.getStatus())) {
			Set<Subscriber> subs = user.getSubscriberFromSubscriberUserID();
			Subscriber sub =  subs.iterator().next();
			SubscriberMDN subscriberMDN = (SubscriberMDN) sub.getSubscriberMDNFromSubscriberID().toArray()[0];
			Set<CardInfo> cards = sub.getCardInfoFromSubscriberID();
			List<CreditCardDestinations> ccDestinations = creditCardDestinationDAO.getAllDestinations(sub);
			// check for expiry
			if ((user.getCreateTime() != null)&& (System.currentTimeMillis()- user.getCreateTime().getTime() 
					< ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs() * 60 * 60 * 1000)) {

				log.info("Activating user");
				user.setStatus(CmFinoFIX.UserStatus_Active);
				user.setStatusTime(new Timestamp());
				user.setConfirmationTime(user.getStatusTime());
				user.setUserActivationTime(new Timestamp());
				userDao.save(user);

				log.info("Activating cards");
				for (Iterator<CardInfo> cardIterator = cards.iterator(); cardIterator.hasNext();) {
					CardInfo card = cardIterator.next();
					if (card.getCardStatus().equals(CmFinoFIX.UserStatus_Registered)) {
						PocketTemplateDAO poctetTemplateDao = DAOFactory.getInstance().getPocketTemplateDao();
						PocketTemplate ccTemplate = poctetTemplateDao.getById(ConfigurationUtil.getDefaultPocketTemplateCreditCard());
						Pocket creditCardPocket = pocketService.createActivePocket(ccTemplate, subscriberMDN, false);
						if(creditCardPocket!=null){
						card.setPocket(creditCardPocket);
						}else{
							//not a possible case
							log.info("CreditCardPocket creation failed for user"+user.getUsername());
							throw new Exception();
						}
						card.setCardStatus(CmFinoFIX.UserStatus_Active);
						card.setisConformationRequired(false);
					}
				}
				cardDao.save(cards);

				log.info("Activating Destinations");
				for (Iterator<CreditCardDestinations> creIterator = ccDestinations.iterator(); creIterator.hasNext();) {
					CreditCardDestinations ccdeDestination = creIterator.next();
					if (ccdeDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Registered)) {
						ccdeDestination.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Active);
					}
				}
				creditCardDestinationDAO.save(ccDestinations);

				errorMsg.setErrorCode(0);
				errorMsg.setErrorDescription(MessageText._("Your registration has been Activated"));
				userAccountStatus = CmFinoFIX.AdminAction_Approve;
				log.info(user.getUsername() + " Account Activated");
			} else {
				expireUser(user);
				errorMsg.setErrorCode(2);
				errorMsg.setErrorDescription(MessageText._("Your registration request status has been expired"));
				userAccountStatus = CmFinoFIX.AdminAction_Reject;
				log.info(user.getUsername() + " Account has expired");

			}

			log.info("Sending SMS to the user:" + user.getUsername());
			String smsMsg = null;
			try {
				NotificationWrapper notificationWrapper = new NotificationWrapper();
				notificationWrapper.setLanguage(user.getLanguage());
				notificationWrapper.setFirstName(user.getFirstName());
				notificationWrapper.setLastName(user.getLastName());
				notificationWrapper.setCompany(user.getCompany());
				notificationWrapper.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
				if (CmFinoFIX.AdminAction_Approve.equals(userAccountStatus)) {
					notificationWrapper.setCode(CmFinoFIX.NotificationCode_CCActivated);
				} else if (CmFinoFIX.AdminAction_Reject.equals(userAccountStatus)) {
					notificationWrapper.setCode(CmFinoFIX.NotificationCode_CCRejected);
				}
				smsMsg = notificationMessageParserService.buildMessage(notificationWrapper,true);
				log.info("SMS is:" + smsMsg);
				smsService.setDestinationMDN(user.getUsername());
				smsService.setSourceMDN(ConfigurationUtil.getCCCodeNotificationSource());
				smsService.setMessage(smsMsg);
				smsService.setSmsc(ConfigurationUtil.getCCCodeNotificationSMSC());
				smsService.send();
			} catch (Exception ee) {
				log.error("Failed to send SMS", ee);
			}
		} else if (CmFinoFIX.UserStatus_Active.equals(user.getStatus())) {
			log.info("code confirmation for user"+user.getUsername());
			errorMsg.setErrorCode(5);
			errorMsg.setErrorDescription(MessageText._("Changes already confirmed or expired"));
			Set<Subscriber> subs = user.getSubscriberFromSubscriberUserID();
			Subscriber sub = subs.iterator().next();
			Set<CardInfo> cards = sub.getCardInfoFromSubscriberID();
			List<CreditCardDestinations> ccDestinations = creditCardDestinationDAO.getAllDestinations(sub);
			int updatedcards = 0;
			for (Iterator<CardInfo> cardIterator = cards.iterator(); cardIterator.hasNext();) {
				CardInfo card = cardIterator.next();
				if (card.getCardStatus().equals(CmFinoFIX.UserStatus_Active)&& card.getisConformationRequired()) {
					if ((System.currentTimeMillis()- card.getLastUpdateTime().getTime() 
							< ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs() * 60 * 60 * 1000)) {
						card.setisConformationRequired(false);
						errorMsg.setErrorCode(4);
						errorMsg.setErrorDescription(MessageText._("Your profile changes has been confirmed"));
						updatedcards++;
						cardDao.save(card);
						log.info(user.getUsername()+" profile updates confirmed");
					}else if ((System.currentTimeMillis()- card.getLastUpdateTime().getTime() 
							> ConfigurationUtil.getCreditcardRegistrationExpirationTimeInHrs() * 60 * 60 * 1000)){
							errorMsg.setErrorCode(5);
    						errorMsg.setErrorDescription(MessageText._("Your profile changes has been Expired"));
    						log.info(user.getUsername()+"profile updates expired");
                						
					}
				}
			}
			
			if (updatedcards > 0) {
				for (Iterator<CreditCardDestinations> creIterator = ccDestinations.iterator(); creIterator.hasNext();) {
					CreditCardDestinations ccdeDestination = creIterator.next();
					if (ccdeDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_New)
							|| ccdeDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Updated)) {
						ccdeDestination.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Active);
						creditCardDestinationDAO.save(ccdeDestination);
					}
				}
			}
		} else {
			errorMsg.setErrorCode(1);
			errorMsg.setErrorDescription(MessageText._("Invalid user or confirmation code."));
		}

		return errorMsg;
	}

	public void expireUser(User user) {
		Set<Subscriber> subs = user.getSubscriberFromSubscriberUserID();
		Subscriber sub = subs.iterator().next();
		//SubscriberMDN subscriberMDN = (SubscriberMDN) sub.getSubscriberMDNFromSubscriberID().toArray()[0];
		Set<CardInfo> cards = sub.getCardInfoFromSubscriberID();
		List<CreditCardDestinations> ccDestinations = creditCardDestinationDAO.getAllDestinations(sub);
		
		log.info("Expiring user "+user.getUsername());
		user.setStatus(CmFinoFIX.UserStatus_Expired);
		user.setStatusTime(new Timestamp());
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// setting user name as user name, expired tag and time stamp
		user.setUsername(user.getUsername() + UserDAO.EXPIRY_TAG+ df.format(user.getStatusTime()));
		user.setExpirationTime(user.getStatusTime());
		userDao.save(user);

		log.info("Expiring cards for user"+user.getUsername());
		for (Iterator<CardInfo> cardIterator = cards.iterator(); cardIterator.hasNext();) {
			CardInfo card = cardIterator.next();
			if (card.getCardStatus().equals(CmFinoFIX.UserStatus_Registered)) {
				card.setCardStatus(CmFinoFIX.UserStatus_Expired);
			}
		}
		cardDao.save(cards);
		
		log.info("Expiring Destinations for user"+user.getUsername());
		for (Iterator<CreditCardDestinations> creIterator = ccDestinations.iterator(); creIterator.hasNext();) {
			CreditCardDestinations ccdeDestination = creIterator.next();
			if (ccdeDestination.getCCMDNStatus().equals(CmFinoFIX.CCMDNStatus_Registered)) {
				ccdeDestination.setCCMDNStatus(CmFinoFIX.CCMDNStatus_Expired);
			}
		}
		creditCardDestinationDAO.save(ccDestinations);
		
	}
}