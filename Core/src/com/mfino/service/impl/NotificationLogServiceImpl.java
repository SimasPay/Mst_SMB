/**
 * 
 */
package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDAO;
import com.mfino.domain.NotificationLog;
import com.mfino.service.NotificationLogService;
import com.mfino.util.MfinoUtil;

/**
 * Service class for NotificationLog
 * @author Sreenath
 *
 */
@Service("NotificationLogServiceImpl")
public class NotificationLogServiceImpl implements NotificationLogService {

	private Logger log = LoggerFactory.getLogger(MfinoUtil.class);

	/**
	 * Saves the NotificationLog record to the database
	 * @param notificationLog
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveNotificationLog(NotificationLog notificationLog) {
		log.info("Saving the NotificationLog record: "+notificationLog);
		NotificationLogDAO notificationLogDAO = DAOFactory.getInstance().getNotificationLogDao();
		notificationLogDAO.save(notificationLog);
	}

}
