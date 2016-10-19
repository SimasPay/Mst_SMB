package com.mfino.scheduler.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.KycLevel;
import com.mfino.domain.Pocket;
import com.mfino.domain.PocketTemplate;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberSyncRecord;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.mailer.NotificationWrapper;
import com.mfino.scheduler.upload.service.impl.SubscriberBulkUploadServiceImpl;
import com.mfino.service.impl.MailServiceImpl;
import com.mfino.service.impl.NotificationMessageParserServiceImpl;
import com.mfino.service.impl.SMSServiceImpl;
import com.mfino.util.SubscriberSyncErrors;

public class BulkUploadUtil {
	
	private static Logger log = LoggerFactory.getLogger(SubscriberBulkUploadServiceImpl.class);
	private static ExecutorService threadPool = Executors.newCachedThreadPool();
	
	public static boolean hasNextToken(String[] array, int index) {
		return index < array.length;
	}

	public static CMJSError checkCardPan(String cardPan) {
		CMJSError error = new CMJSError();
		error.setErrorCode(SubscriberSyncErrors.Success);
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		PocketQuery query = new PocketQuery();
		query.setCardPan(cardPan);
		List<Pocket> pocket = pocketDao.get(query);
		if (!pocket.isEmpty()) {
			error.setErrorCode(SubscriberSyncErrors.CardPan_Already_Exist);
			error.setErrorDescription(SubscriberSyncErrors.errorCodesMap.get(SubscriberSyncErrors.CardPan_Already_Exist));
		}

		return error;
	}
	public static CMJSError checkMDN(SubscriberSyncRecord syncRecord) {
		CMJSError error = new CMJSError();
		error.setErrorCode(SubscriberSyncErrors.Success);
		SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
		SubscriberMdn subscriberMDN = subscriberMDNDAO.getByMDN(syncRecord.getMdn());
		if (subscriberMDN!=null) {
			syncRecord.setId(subscriberMDN.getId().longValue());
			if(( subscriberMDN.getStatus()).equals(CmFinoFIX.MDNStatus_NotRegistered)){
			error.setErrorCode(SubscriberSyncErrors.Notregistered_MDN);
			error.setErrorDescription(SubscriberSyncErrors.errorCodesMap.get(SubscriberSyncErrors.Notregistered_MDN));
			}else{
			error.setErrorCode(SubscriberSyncErrors.MDN_Already_Exist);
			error.setErrorDescription(SubscriberSyncErrors.errorCodesMap.get(SubscriberSyncErrors.MDN_Already_Exist));
			}

		}

		return error;
	}
	public static PocketTemplate getPocketTemplateForEmoney(Long kyclevel) {
		KYCLevelDAO kycLevelDAO = DAOFactory.getInstance().getKycLevelDAO();
		KycLevel KycLevel = kycLevelDAO.getByKycLevel(kyclevel);
		return KycLevel.getPocketTemplate();
	}

	public static void sendSMS(String destination,NotificationWrapper wrapper){ 	
				try{
					NotificationMessageParserServiceImpl parser = new NotificationMessageParserServiceImpl(wrapper);
					String message = parser.buildMessage();					
					SMSServiceImpl service = new SMSServiceImpl();
					service.setDestinationMDN(destination);
					service.setMessage(message);
					service.setNotificationCode(wrapper.getCode());
					service.asyncSendSMS();
				}catch (Exception e) {
					log.error("failed to send message to "+destination,e);
				}		
			} 	

	public static void sendMail(String email, String to,String subject,NotificationWrapper wrapper){ 	
				try{
					NotificationMessageParserServiceImpl parser = new NotificationMessageParserServiceImpl(wrapper);
					String message = parser.buildMessage();
					MailServiceImpl mailServiceImpl = new MailServiceImpl();
					mailServiceImpl.asyncSendEmail(email, to, subject, message);
				}catch (Exception e) {
					log.error("failed to send Mail to "+to,e);
				}		  	
	}


}
