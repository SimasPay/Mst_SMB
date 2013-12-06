package com.mfino.fix2notification.processor;

import java.util.ArrayList;
import java.util.List;

import com.mfino.dao.ActivitiesLogDAO;
import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMGetTransactions;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.validators.DefaultBOBPocketValidator;
import com.mfino.validators.SubscriberValidator;
import com.mfino.validators.Validator;

public class GetShareLoadHistoryProcessor implements IFix2NotifListProcessor {

	@Override
	public List<NotificationWrapper> process(CFIXMsg msg) throws Exception {
		List<NotificationWrapper> notificationMsgList = new ArrayList<NotificationWrapper>();
    	NotificationWrapper notificationMsg = new NotificationWrapper();
    	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    	CMGetTransactions transactionsReq = (CMGetTransactions) msg;
    	if (transactionsReq.checkRequiredFields()) {
    		notificationMsg.setCode(CmFinoFIX.NotificationCode_RequiredParametersMissing);
    		notificationMsgList.add(notificationMsg);
    		return notificationMsgList;
    	}
    	// Do a special case for subscriber validator so that we avoid multiple fetch SubscriberMDN
    	SubscriberValidator subscriberValidator = new SubscriberValidator(transactionsReq.getSourceMDN());
    	Integer validationResult = subscriberValidator.validate();
    	SubscriberMDN subscriberMDN = subscriberValidator.getSubscriberMDN();
        Company subsCompany = null;
        Integer language = null;
    	if(null != subscriberMDN)
    	{
        	subsCompany = subscriberMDN.getSubscriber().getCompany();
            language = subscriberMDN.getSubscriber().getLanguage();
            notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
    		notificationMsg.setCompany(subsCompany);
        	notificationMsg.setLanguage(language);
        	notificationMsg.setFirstName(subscriberMDN.getSubscriber().getFirstName());
        	notificationMsg.setLastName(subscriberMDN.getSubscriber().getLastName());
    	}
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
    		notificationMsgList.add(notificationMsg);
    		return notificationMsgList;
		}
    	if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions()))) {
			notificationMsg.setCode(CmFinoFIX.NotificationCode_MDNIsRestricted);
            notificationMsgList.add(notificationMsg);
            return notificationMsgList;
		}
//    	SourceMDNValidator sourceMDNValidator = new SourceMDNValidator(subscriberMDN);
    	//PINValidator pinValidator = new PINValidator(subscriberMDN, transactionsReq.getPin());
    	DefaultBOBPocketValidator sourcePocketValidator = new DefaultBOBPocketValidator(subscriberMDN,false);
    	Validator validator = new Validator();
//    	validator.addValidator(sourceMDNValidator);
    	//validator.addValidator(pinValidator);
    	validator.addValidator(sourcePocketValidator);
    	validationResult = validator.validateAll();
	    if (!CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
            notificationMsg.setCode(validationResult);
            notificationMsgList.add(notificationMsg);
            return notificationMsgList;
        }
    	CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    	CommodityTransferQuery commodityTransferQuery = new CommodityTransferQuery();
    	List<Integer> messageTypes = new ArrayList<Integer>();
		messageTypes.add(CmFinoFIX.MsgType_ShareLoad);
		commodityTransferQuery.setMessageTypes(messageTypes);
		commodityTransferQuery.setSourceSubscMDN(subscriberMDN);
    	commodityTransferQuery.setStart(0);
    	commodityTransferQuery.setLimit(transactionsReq.getMaxCount());
        Pocket pocket = sourcePocketValidator.getDefaultBOBPocket();
    	List<CommodityTransfer> topupHistory = commodityTransferDAO.get(commodityTransferQuery);
    	if(topupHistory != null && topupHistory.size() > 0) {
	    	for (CommodityTransfer commodityTransfer : topupHistory) {
	    		notificationMsg = new NotificationWrapper();
	    		notificationMsg.setTransactionId(transactionsReq.getParentTransactionID());
	    		notificationMsg.setCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
	        	notificationMsg.setCommodityTransfer(commodityTransfer);
                        notificationMsg.setSourcePocket(pocket);
	        	notificationMsg.setCompany(subsCompany);
	        	notificationMsg.setLanguage(language);
	        	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
	        	notificationMsgList.add(notificationMsg);
			}
    	} else {
    		notificationMsg.setCode(CmFinoFIX.NotificationCode_NoCompletedTransactionsWereFound);
    		notificationMsgList.add(notificationMsg);
    	}    	
    	ActivitiesLogDAO activitiesLogDAO = DAOFactory.getInstance().getActivitiesLogDAO();
    	ActivitiesLog activitiesLog = new ActivitiesLog();
    	activitiesLog.setMsgType(CmFinoFIX.MessageType_GetTransactions);// _ checkBalance.m_pHeader.getMsgType());
    	activitiesLog.setSourceApplication(transactionsReq.getSourceApplication());
    	activitiesLog.setSourceMDNID(subscriberMDN.getID());
    	activitiesLog.setSourceMDN(subscriberMDN.getMDN());
    	activitiesLog.setParentTransactionID(transactionsReq.getTransactionID());
    	activitiesLog.setNotificationCode(notificationMsg.getCode());
    	activitiesLog.setActivityCategory(transactionsReq.getGetTransactionType());
    	if(pocket != null) {
    		activitiesLog.setSourcePocketID(pocket.getID());
    		activitiesLog.setSourcePocketType(pocket.getPocketTemplate().getType());
    	}
        activitiesLog.setCompany(subsCompany);
    	activitiesLogDAO.save(activitiesLog);
		return notificationMsgList;
	}

}
