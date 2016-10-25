/**
 * 
 */
package com.mfino.service.impl;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLog;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.NotificationLogDetailsService;
import com.mfino.service.NotificationLogService;
import com.mfino.util.EncryptionUtil;

/**
 * Service class for NotificationLogDetails related database access
 * @author Sreenath
 *
 */
@Service("NotificationLogDetailsServiceImpl")
public class NotificationLogDetailsServiceImpl implements NotificationLogDetailsService{

	private static Logger log = LoggerFactory.getLogger(NotificationLogDetailsServiceImpl.class);
	
	@Autowired
	@Qualifier("NotificationLogServiceImpl")
	private NotificationLogService notificationLogService;
	
	/**
	 * Gets the NotificationLogDetails record by the NotificationLogDetails id 
	 * @param notificationLogDetailsID
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public NlogDetails getNotificationLogDetailsById(Long notificationLogDetailsID) {
		NlogDetails notificationLogDetails = null;
		if(notificationLogDetailsID!=null){
			log.info("Getting the NotificationLogDetails record for id: "+notificationLogDetailsID);
			NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
			notificationLogDetails = notificationLogDetailsDao.getById(notificationLogDetailsID);
		}
		return notificationLogDetails;
	}
	
	/**
	 * Saves the NotificationLogDetails record to database
	 * @param notificationLogDetails
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveNotificationLogDetails(NlogDetails notificationLogDetails) {
		log.info("Saving the NotificationLogDetails record: "+notificationLogDetails);
		NotificationLogDetailsDAO notificationLogDetailsDao = DAOFactory.getInstance().getNotificationLogDetailsDao();
		notificationLogDetailsDao.save(notificationLogDetails);		
	}
	
	/**
	 * 
	 * @param toAddress
	 * @param emailSubject
	 * @param text
	 * @param ServiceChargeTransactionLogID
	 * @param notificationCode
	 * @param notificationMethod
	 * @param notificationReceiverType
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Long persistNotification(String toAddress, String emailSubject, String text, Long ServiceChargeTransactionLogID, Integer notificationCode, Integer notificationMethod, Integer notificationReceiverType) 
	{
		NotificationLog notificationLog = new NotificationLog();
		
		if(ServiceChargeTransactionLogID != null)
		{
			notificationLog.setId(ServiceChargeTransactionLogID);
			notificationLog.setNotificationmethod(notificationMethod);
			notificationLog.setNotificationreceivertype(notificationReceiverType);
			notificationLog.setCode(notificationCode);
			notificationLog.setText(EncryptionUtil.getEncryptedString(text));
			notificationLog.setSourceaddress(toAddress);
			notificationLog.setEmailsubject(emailSubject);
			notificationLog.setIssensitivedata((short) 0);
			notificationLogService.saveNotificationLog(notificationLog);
			
			NlogDetails notificationLogDetails = new NlogDetails();
			notificationLogDetails.setNotificationLog(notificationLog);
			notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Sending);
			saveNotificationLogDetails(notificationLogDetails);
			
			return notificationLogDetails.getId().longValue();
		}
		return null;
	}

}
