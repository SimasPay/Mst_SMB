package com.mfino.fix2notification.processor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.mfino.validators.DefaultSVAPocketValidator;
import com.mfino.validators.PINValidator;
import com.mfino.validators.SourceMDNValidator;
import com.mfino.validators.SourceMerchantValidator;
import com.mfino.validators.SubscriberValidator;
import com.mfino.validators.Validator;

public class GetTransactionHistoryProcessor implements IFix2NotifListProcessor {

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
        String firstName = null;
        String lastName = null;
    	if(null != subscriberMDN)
    	{
                subsCompany = subscriberMDN.getSubscriber().getCompany();
		language = subscriberMDN.getSubscriber().getLanguage();
        	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
        	firstName = subscriberMDN.getSubscriber().getFirstName();
        	lastName = subscriberMDN.getSubscriber().getLastName();
    		notificationMsg.setCompany(subsCompany);
        	notificationMsg.setLanguage(language);
        	notificationMsg.setFirstName(firstName);
        	notificationMsg.setLastName(lastName);
    	}
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
    		notificationMsgList.add(notificationMsg);
    		return notificationMsgList;
		}
    	SourceMDNValidator sourceMDNValidator = new SourceMDNValidator(subscriberMDN);
    	SourceMerchantValidator srcMerchantValidator = new SourceMerchantValidator(subscriberMDN);
    	PINValidator pinValidator = new PINValidator(subscriberMDN, transactionsReq.getPin());
    	DefaultSVAPocketValidator sourcePocketValidator = new DefaultSVAPocketValidator(subscriberMDN);
    	Validator validator = new Validator();
//    	validator.addValidator(subscriberValidator);
    	validator.addValidator(sourceMDNValidator);
    	validator.addValidator(srcMerchantValidator);
    	validator.addValidator(pinValidator);
    	validator.addValidator(sourcePocketValidator);
    	validationResult = validator.validateAll();
    	if ( ! CmFinoFIX.ResponseCode_Success.equals(validationResult)) {
    		notificationMsg.setCode(validationResult);
        	notificationMsg.setNotificationMethod(CmFinoFIX.NotificationMethod_SMS);
                notificationMsgList.add(notificationMsg);
    		return notificationMsgList;
		}
    	Pocket pocket = sourcePocketValidator.getDefaultSVAPocket();
    	CommodityTransferDAO commodityTransferDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    	CommodityTransferQuery commodityTransferQuery = new CommodityTransferQuery();
    	
    	List<Integer> messageTypes = new ArrayList<Integer>();
    	List<CommodityTransfer> transactionsHistory = null; 
    	if(CmFinoFIX.GetTransactionType_TopUp.equals(transactionsReq.getGetTransactionType())) {
    		messageTypes.add(CmFinoFIX.MsgType_H2HTopup);
    		messageTypes.add(CmFinoFIX.MsgType_MobileAgentRecharge);
    		commodityTransferQuery.setMessageTypes(messageTypes);
    		commodityTransferQuery.setSourceSubscMDN(subscriberMDN);
    		commodityTransferQuery.setStart(0);
        	commodityTransferQuery.setLimit(transactionsReq.getMaxCount());
        	transactionsHistory = commodityTransferDAO.get(commodityTransferQuery);
    	} else if (CmFinoFIX.GetTransactionType_Transfer.equals(transactionsReq.getGetTransactionType())) {
    		List<CommodityTransfer> transfersList = null;
    		messageTypes.add(CmFinoFIX.MsgType_H2HDistribute);
    		messageTypes.add(CmFinoFIX.MsgType_MobileAgentDistribute);
    		commodityTransferQuery.setMessageTypes(messageTypes);
    		commodityTransferQuery.setStart(0);
        	commodityTransferQuery.setLimit(transactionsReq.getMaxCount());
    		commodityTransferQuery.setSourceSubscMDN(subscriberMDN);
    		transfersList =  commodityTransferDAO.get(commodityTransferQuery);
    		commodityTransferQuery.setSourceSubscMDN(null);
    		commodityTransferQuery.setDestinationMDN(transactionsReq.getSourceMDN());
    		transfersList.addAll(commodityTransferDAO.get(commodityTransferQuery));
                Collections.sort(transfersList, new Comparator<CommodityTransfer>() {
				@Override
				public int compare(CommodityTransfer ct1, CommodityTransfer ct2) {
					return ((int)(ct2.getID() - ct1.getID()));
				}
			});
                if(transfersList != null && transfersList.size() >= transactionsReq.getMaxCount()){
                    transactionsHistory = transfersList.subList(0, transactionsReq.getMaxCount());
                }else {
                    transactionsHistory = transfersList;
                }
    	}
    	if(transactionsHistory != null && transactionsHistory.size() > 0) {
	    	for (CommodityTransfer commodityTransfer : transactionsHistory) {
	    		notificationMsg = new NotificationWrapper();
	    		notificationMsg.setTransactionId(transactionsReq.getParentTransactionID());
	    		notificationMsg.setCode(CmFinoFIX.NotificationCode_CommodityTransaferDetails);
	        	notificationMsg.setSourcePocket(pocket);
	        	notificationMsg.setCommodityTransfer(commodityTransfer);
	        	notificationMsg.setCompany(subsCompany);
	        	notificationMsg.setLanguage(language);
	        	notificationMsg.setFirstName(firstName);
	        	notificationMsg.setLastName(lastName);
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
    	activitiesLog.setActivityCategory(transactionsReq.getGetTransactionType());
    	if( pocket != null)
    	{
    		activitiesLog.setSourcePocketID(pocket.getID());
    		activitiesLog.setSourcePocketType(pocket.getPocketTemplate().getType());
    	}
    	activitiesLog.setNotificationCode(notificationMsg.getCode());
    	activitiesLog.setCompany(subsCompany);
    	activitiesLogDAO.save(activitiesLog);
		return notificationMsgList;
	}

}
