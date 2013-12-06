package com.mfino.fix2notification.processor;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactionDetails;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.validators.DefaultSVAPocketValidator;
import com.mfino.validators.PINValidator;
import com.mfino.validators.SourceMDNValidator;
import com.mfino.validators.SourceMerchantValidator;
import com.mfino.validators.SubscriberValidator;
import com.mfino.validators.Validator;

public class GetTransactionDetailsProcessor implements IFix2NotifProcessor {

	@Override
	public NotificationWrapper process(CFIXMsg msg) throws Exception {
		NotificationWrapper notificationMsg = new NotificationWrapper();
		notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		CMGetTransactionDetails transactionsReq = (CMGetTransactionDetails) msg;
		if (transactionsReq.checkRequiredFields()) {
			notificationMsg
					.setCode(CmFinoFIX.NotificationCode_RequiredParametersMissing);
			return notificationMsg;
		}
		SubscriberValidator subscriberValidator = new SubscriberValidator(
				transactionsReq.getSourceMDN());
		Integer validationResult = subscriberValidator.validate();
		SubscriberMDN subscriberMDN = subscriberValidator.getSubscriberMDN();
		if (null != subscriberMDN) {
			Company subsCompany = subscriberMDN.getSubscriber().getCompany();
			Integer language = subscriberMDN.getSubscriber().getLanguage();
			notificationMsg
					.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
			notificationMsg.setCompany(subsCompany);
			notificationMsg.setLanguage(language);
			notificationMsg.setFirstName(subscriberMDN.getSubscriber().getFirstName());
        	notificationMsg.setLastName(subscriberMDN.getSubscriber().getLastName());
		}
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
		SourceMDNValidator sourceMDNValidator = new SourceMDNValidator(
				subscriberMDN);
		SourceMerchantValidator srcMerchantValidator = new SourceMerchantValidator(
				subscriberMDN);
		PINValidator pinValidator = new PINValidator(subscriberMDN,
				transactionsReq.getPin());
		DefaultSVAPocketValidator sourcePocketValidator = new DefaultSVAPocketValidator(
				subscriberMDN);
		Validator validator = new Validator();
		// validator.addValidator(subscriberValidator);
		validator.addValidator(sourceMDNValidator);
		validator.addValidator(srcMerchantValidator);
		validator.addValidator(pinValidator);
		validator.addValidator(sourcePocketValidator);
		validationResult = validator.validateAll();
		if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
			notificationMsg.setCode(validationResult);
			return notificationMsg;
		}
		Pocket pocket = sourcePocketValidator.getDefaultSVAPocket();
		CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
		CommodityTransfer commodityTransfer = commodityTransferDAO
				.getById(transactionsReq.getTransID());
		if (null != commodityTransfer
				&& (transactionsReq.getSourceMDN().equals(commodityTransfer.getSourceMDN()) 
						|| transactionsReq.getSourceMDN().equals(commodityTransfer.getDestMDN()))) {
			notificationMsg.setTransactionId(transactionsReq
					.getParentTransactionID());
			notificationMsg
					.setCode(CmFinoFIX.NotificationCode_CheckDetailTransaction);
			notificationMsg.setSourcePocket(pocket);
			notificationMsg.setCommodityTransfer(commodityTransfer);
			return notificationMsg;
		} else {
			notificationMsg
					.setCode(CmFinoFIX.NotificationCode_CheckDetailTransactionNotFound);
			notificationMsg
					.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
		}
		ActivitiesLogDAO activitiesLogDAO = DAOFactory.getInstance().getActivitiesLogDAO();
		ActivitiesLog activitiesLog = new ActivitiesLog();
		activitiesLog.setMsgType(CmFinoFIX.MessageType_GetTransactionDetails);
		activitiesLog.setSourceApplication(transactionsReq
				.getSourceApplication());
		activitiesLog.setSourceMDNID(subscriberMDN.getID());
		activitiesLog.setSourceMDN(subscriberMDN.getMDN());
		activitiesLog
				.setParentTransactionID(transactionsReq.getTransactionID());
		if (pocket != null) {
			activitiesLog.setSourcePocketID(pocket.getID());
			activitiesLog.setSourcePocketType(pocket.getPocketTemplate()
					.getType());
		}
		activitiesLog.setNotificationCode(notificationMsg.getCode());
		activitiesLog.setCompany(subscriberMDN.getSubscriber().getCompany());
		activitiesLogDAO.save(activitiesLog);
		return notificationMsg;
	}

}
