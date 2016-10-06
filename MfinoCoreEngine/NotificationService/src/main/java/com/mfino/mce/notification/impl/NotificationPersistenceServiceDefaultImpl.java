package com.mfino.mce.notification.impl;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDAO;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLog;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CmFinoFIX;
import com.mfino.mce.notification.EmailNotification;
import com.mfino.mce.notification.Notification;
import com.mfino.mce.notification.NotificationPersistenceService;
import com.mfino.mce.notification.SMSNotification;
import com.mfino.util.EncryptionUtil;


/**
 * @author Amar
 *
 */
public class NotificationPersistenceServiceDefaultImpl  implements NotificationPersistenceService{

	private Set<Integer> sensitiveNotificationCodes = new HashSet<Integer>();
		
	public void setSensitiveNotificationCodes(String notificationCodes)
	{
		String[] ncs = notificationCodes.split(",");
		for(String nc : ncs)
		{
			sensitiveNotificationCodes.add(Integer.parseInt(nc.trim()));
		}
	}
	
	
	/**
	 * 
	 * @param ServiceChargeTransactionLogID
	 * @param notification
	 * @param notificationMethod
	 * @param notificationReceiverType
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void persistNotification(Long ServiceChargeTransactionLogID, Notification notification, Integer notificationMethod, Integer notificationReceiverType) 
	{
		NotificationLog notificationLog = new NotificationLog();
		NotificationLogDAO notificationLogDAO = DAOFactory.getInstance().getNotificationLogDao();
				
		if(ServiceChargeTransactionLogID != null)
		{
			notificationLog.setSctlid(BigDecimal.valueOf(ServiceChargeTransactionLogID));
			notificationLog.setNotificationmethod(notificationMethod);
			notificationLog.setNotificationreceivertype(notificationReceiverType);
			notificationLog.setCode(notification.getNotificationCode());
			if(sensitiveNotificationCodes.contains(notification.getNotificationCode()))
			{
				notificationLog.setIssensitivedata((short)1);
			}
			else
			{
				notificationLog.setIssensitivedata((short)0);
			}
			if(notification instanceof SMSNotification)
			{
				SMSNotification smsNotifcation =  (SMSNotification)notification;
				notificationLog.setText(EncryptionUtil.getEncryptedString(smsNotifcation.getContent()));
				notificationLog.setSourceaddress(smsNotifcation.getMdn());
			}
			else if(notification instanceof EmailNotification)
			{
				EmailNotification emailNotifcation =  (EmailNotification)notification;
				notificationLog.setText(EncryptionUtil.getEncryptedString(emailNotifcation.getContent()));
				notificationLog.setSourceaddress(emailNotifcation.getToRecipents()[0]);
				notificationLog.setEmailsubject(emailNotifcation.getSubject());
			}
			notificationLogDAO.save(notificationLog);
			
			NotificationLogDetails notificationLogDetails = new NotificationLogDetails();
			NotificationLogDetailsDAO notificationLogDetailsDAO = DAOFactory.getInstance().getNotificationLogDetailsDao();	
			notificationLogDetails.setNotificationLog(notificationLog);
			notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Sending);
			notificationLogDetailsDAO.save(notificationLogDetails);
			notification.setNotificationLogDetailsID(notificationLogDetails.getId().longValue());			
			
		}
	}
	

}