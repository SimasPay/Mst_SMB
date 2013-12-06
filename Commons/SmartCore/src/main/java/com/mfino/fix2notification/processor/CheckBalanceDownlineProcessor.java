/**
 * 
 */
package com.mfino.fix2notification.processor;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCheckBalanceDownline;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.util.MfinoUtil;
import com.mfino.validators.DefaultSVAPocketValidator;
import com.mfino.validators.DestMDNValidator;
import com.mfino.validators.DestMerchantValidator;
import com.mfino.validators.DownlineSVAPocketValidator;
import com.mfino.validators.MerchantHierarchyValidator;
import com.mfino.validators.PINValidator;
import com.mfino.validators.SourceMDNValidator;
import com.mfino.validators.SourceMerchantValidator;
import com.mfino.validators.SubscriberValidator;
import com.mfino.validators.Validator;

/**
 * @author Deva
 *
 */
public class CheckBalanceDownlineProcessor implements IFix2NotifProcessor{
	
	@Override
	public NotificationWrapper process(CFIXMsg msg) throws Exception {
		NotificationWrapper notificationMsg = new NotificationWrapper();
    	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    	CMCheckBalanceDownline checkBalance = (CMCheckBalanceDownline) msg;
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
    	validator.addValidator(sourceMDNValidator);
    	validator.addValidator(srcMerchantValidator);
    	validator.addValidator(pinValidator);
    	validator.addValidator(sourcePocketValidator);
    	validationResult = validator.validateAll();
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
    	
    	// Downline related validations
    	SubscriberValidator dlsubscriberValidator = new SubscriberValidator(MfinoUtil.normalizeMDN(checkBalance.getDownlineMDN()));
    	
    	validationResult = dlsubscriberValidator.validate();
    	
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
    	SubscriberMDN dlMDN = dlsubscriberValidator.getSubscriberMDN();
    	DestMDNValidator dlMDNValidator =new DestMDNValidator(dlMDN);
    	DownlineSVAPocketValidator downlinePocketValidator = new DownlineSVAPocketValidator(dlMDN);
    	DestMerchantValidator destMerchantValidator = new DestMerchantValidator(dlMDN);
    	MerchantHierarchyValidator hierarchyValidator = new MerchantHierarchyValidator(dlMDN, subscriberMDN);
    	Validator downlineValidator = new Validator();
    	downlineValidator.addValidator(dlMDNValidator);
    	downlineValidator.addValidator(destMerchantValidator);
        downlineValidator.addValidator(downlinePocketValidator);
    	downlineValidator.addValidator(hierarchyValidator);
    	validationResult = downlineValidator.validateAll();
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
    	notificationMsg.setCode(CmFinoFIX.NotificationCode_DownlineSVABalanceDetails);
    	Pocket pocket = downlinePocketValidator.getDefaultSVAPocket();
    	notificationMsg.setSourcePocket(pocket);
    	// Insert Record into activities log
    	ActivitiesLogDAO activitiesLogDAO = DAOFactory.getInstance().getActivitiesLogDAO();
    	ActivitiesLog activitiesLog = new ActivitiesLog();
    	activitiesLog.setMsgType(CmFinoFIX.MessageType_CheckBalanceDownline);// _ checkBalance.m_pHeader.getMsgType());
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
