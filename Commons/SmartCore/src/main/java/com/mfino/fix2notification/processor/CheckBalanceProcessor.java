package com.mfino.fix2notification.processor;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCheckBalance;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.validators.DefaultSVAPocketValidator;
import com.mfino.validators.PINValidator;
import com.mfino.validators.SourceMDNValidator;
import com.mfino.validators.SourceMerchantValidator;
import com.mfino.validators.SubscriberValidator;
import com.mfino.validators.Validator;

public class CheckBalanceProcessor implements IFix2NotifProcessor {

	@Override
	public NotificationWrapper process(CFIXMsg msg) throws Exception {
		NotificationWrapper notificationMsg = new NotificationWrapper();
    	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    	CMCheckBalance checkBalance = (CMCheckBalance) msg;
    	if (checkBalance.checkRequiredFields()) {
    		notificationMsg.setCode(CmFinoFIX.NotificationCode_RequiredParametersMissing);
    		return notificationMsg;
    	}
    	// Do a special case for subscriber validator so that we avoid multiple fetch SubscriberMDN
    	SubscriberValidator subscriberValidator = new SubscriberValidator(checkBalance.getSourceMDN());
    	Integer validationResult = subscriberValidator.validate();
    	SubscriberMDN subscriberMDN = subscriberValidator.getSubscriberMDN();
    	if(null != subscriberMDN)
    	{
        	Company subsCompany = subscriberMDN.getSubscriber().getCompany();
    		Integer language = subscriberMDN.getSubscriber().getLanguage();
        	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    		notificationMsg.setCompany(subsCompany);
        	notificationMsg.setLanguage(language);
        	notificationMsg.setFirstName(subscriberMDN.getSubscriber().getFirstName());
        	notificationMsg.setLastName(subscriberMDN.getSubscriber().getLastName());
    	}
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
    	SourceMDNValidator sourceMDNValidator = new SourceMDNValidator(subscriberMDN);
    	PINValidator pinValidator = new PINValidator(subscriberMDN, checkBalance.getPin());
    	DefaultSVAPocketValidator sourcePocketValidator = new DefaultSVAPocketValidator(subscriberMDN);
    	SourceMerchantValidator srcMerchantValidator = new SourceMerchantValidator(subscriberMDN);
    	Validator validator = new Validator();
//    	validator.addValidator(subscriberValidator);
    	validator.addValidator(sourceMDNValidator);
    	validator.addValidator(srcMerchantValidator);
    	validator.addValidator(pinValidator);
    	validator.addValidator(sourcePocketValidator);
    	validationResult = validator.validateAll();
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
    	Pocket pocket = sourcePocketValidator.getDefaultSVAPocket();
    	notificationMsg.setCode(CmFinoFIX.NotificationCode_SVABalanceDetails);
    	notificationMsg.setSourcePocket(pocket);
    	// Insert Record into activities log
    	ActivitiesLogDAO activitiesLogDAO = DAOFactory.getInstance().getActivitiesLogDAO();
    	ActivitiesLog activitiesLog = new ActivitiesLog();
    	activitiesLog.setMsgType(CmFinoFIX.MessageType_CheckBalance);// _ checkBalance.m_pHeader.getMsgType());
    	activitiesLog.setSourceApplication(checkBalance.getSourceApplication());
    	activitiesLog.setSourceMDNID(subscriberMDN.getID());
    	activitiesLog.setSourceMDN(subscriberMDN.getMDN());
    	activitiesLog.setParentTransactionID(checkBalance.getTransactionID());
    	if( pocket != null)
    	{
    		activitiesLog.setSourcePocketID(pocket.getID());
    		activitiesLog.setSourcePocketType(pocket.getPocketTemplate().getType());
    	}
    	activitiesLog.setNotificationCode(notificationMsg.getCode());
    	activitiesLog.setCompany(notificationMsg.getCompany());
    	activitiesLogDAO.save(activitiesLog);
    	return notificationMsg;
	}
}
