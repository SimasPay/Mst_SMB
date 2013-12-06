package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDAO;
import com.mfino.dao.query.NotificationLogDetailsQuery;
import com.mfino.dao.query.NotificationLogQuery;
import com.mfino.domain.Notification;
import com.mfino.domain.NotificationLog;
import com.mfino.domain.NotificationLogDetails;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSNotificationLog;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.NotificationLogProcessor;
import com.mfino.uicore.web.WebContextError;
import com.mfino.util.EncryptionUtil;

/**
 * @author Amar
 *
 */
@Service("NotificationLogProcessorImpl")
public class NotificationLogProcessorImpl extends BaseFixProcessor implements NotificationLogProcessor{

	//@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {

		CMJSNotificationLog realMsg = (CMJSNotificationLog) msg;
		NotificationLogDAO notificationLogDAO = DAOFactory.getInstance().getNotificationLogDao();
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			CMJSNotificationLog.CGEntries[] entries = realMsg.getEntries();

			for (CMJSNotificationLog.CGEntries e : entries) {
				if(e != null)
				{
					NotificationLog notificationLog = notificationLogDAO.getById(e.getNotificationLogID());

					// Check for Stale Data
					if (!e.getRecordVersion().equals(notificationLog.getVersion())) {
						handleStaleDataException();
					}
					updateEntity(notificationLog, e);
					try {
						validate(notificationLog);
						notificationLogDAO.save(notificationLog);
					} catch (Exception ex) {
						handleException(ex);
					}
					updateMessage(notificationLog, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			NotificationLogQuery query = new NotificationLogQuery();

			if (realMsg.getSctlId() != null) {
				query.setSctlID(realMsg.getSctlId());
			}
			if (realMsg.getNotificationCode() != null) {
				query.setCode(realMsg.getNotificationCode());
			}
			if (realMsg.getNotificationMethod() != null) {
				query.setNotificationMethod(realMsg.getNotificationMethod());
			}
			if (realMsg.getNotificationReceiverType() != null) {
				query.setNotificationReceiverType(realMsg.getNotificationReceiverType());
			}
			if (StringUtils.isNotBlank(realMsg.getSourceAddress())) {
				query.setSourceAddress(realMsg.getSourceAddress());
			}
			if(realMsg.getstart() != null){
				query.setStart(realMsg.getstart());
			}
			if(realMsg.getlimit() != null)
			{
				query.setLimit(realMsg.getlimit());
			}
			/*
			 * fetch only those notification messages that are not sensitive i.e. which doesn't contain sensitive information like passwords, PINs FACs etc
			 */
			//query.setSensitiveData(false);
			List<NotificationLog> results = notificationLogDAO.get(query);
			realMsg.allocateEntries(results.size());
			for (int i = 0; i < results.size(); i++) {
				NotificationLog notificationLog = results.get(i);
				CMJSNotificationLog.CGEntries entry = new CMJSNotificationLog.CGEntries();
				updateMessage(notificationLog, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			return realMsg;
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
			CMJSNotificationLog.CGEntries[] entries = realMsg.getEntries();

			for (CMJSNotificationLog.CGEntries e : entries) {
				if(e != null)
				{
					NotificationLog notificationLog = new NotificationLog();
					updateEntity(notificationLog, e);
					try {
						validate(notificationLog);
						notificationLogDAO.save(notificationLog);
					} catch (Exception ex) {
						handleException(ex);
					}
					updateMessage(notificationLog, e);
				}
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
			CMJSNotificationLog.CGEntries[] entries = realMsg.getEntries();
			for (CMJSNotificationLog.CGEntries e : entries) {
				notificationLogDAO.deleteById(e.getNotificationLogID());
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(entries.length);
		}
		return realMsg;

	}


	private void validate(NotificationLog notificationLog) throws Exception 
	{
		if(notificationLog.getSctlId() == null)
		{
			throw new Exception("Sctl ID can't be null.");
		}
		if(notificationLog.getNotificationMethod() == null)
		{
			throw new Exception("NotificationMethod can't be null. It should be either SMS or Email");
		}
		if(notificationLog.getCode() == null)
		{
			throw new Exception("NotificationCode can't be null.");
		}
		if(notificationLog.getNotificationReceiverType() == null)
		{
			throw new Exception("Notification Receiver Type can't be null.");
		}
		if(notificationLog.getSourceAddress() == null)
		{
			throw new Exception("Source Address can't be null.");
		}
	}

	private void updateEntity(NotificationLog notificationLog, CMJSNotificationLog.CGEntries e) {
		if (e.getSctlId() != null) {
			notificationLog.setSctlId(e.getSctlId());
		}
		if (StringUtils.isNotBlank(e.getText())) {
			notificationLog.setText(EncryptionUtil.getEncryptedString(e.getText()));
		}
		if (e.getNotificationCode() != null) {
			notificationLog.setCode(e.getNotificationCode());
		}
		if (e.getNotificationMethod() != null) {
			notificationLog.setNotificationMethod(e.getNotificationMethod());
		}
		if (e.getNotificationReceiverType() != null) {
			notificationLog.setNotificationReceiverType(e.getNotificationReceiverType());
		}
		if (StringUtils.isNotBlank(e.getSourceAddress())) {
			notificationLog.setSourceAddress(e.getSourceAddress());
		}
		if (StringUtils.isNotBlank(e.getEmailSubject())) {
			notificationLog.setEmailSubject(e.getEmailSubject());
		}
	}

	private void updateMessage(NotificationLog notificationLog, CMJSNotificationLog.CGEntries e) {
		e.setNotificationLogID(notificationLog.getID());
		e.setSctlId(notificationLog.getSctlId());
		if(notificationLog.getIsSensitiveData())
		{
			e.setText(" ***** This message contains sensitive information and hence its not displayed here ***** ");
		}
		else
		{
			e.setText(EncryptionUtil.getDecryptedString(notificationLog.getText()));
		}
		e.setNotificationCode(notificationLog.getCode());
		Notification notification = DAOFactory.getInstance().getNotificationDAO().getByNotificationCode(notificationLog.getCode());
		if(notification != null)
		{
			e.setNotificationCodeName(notification.getCodeName());
		}
		e.setNotificationMethod(notificationLog.getNotificationMethod());
		if(CmFinoFIX.NotificationMethod_SMS.equals(notificationLog.getNotificationMethod()))
		{
			e.setNotificationMethodText("SMS");
		}
		else if(CmFinoFIX.NotificationMethod_Email.equals(notificationLog.getNotificationMethod()))
		{
			e.setNotificationMethodText("Email");
		}
		e.setNotificationReceiverType(notificationLog.getNotificationReceiverType());
		if(CmFinoFIX.NotificationReceiverType_Source.equals(notificationLog.getNotificationReceiverType()))
		{
			e.setNotificationReceiverTypeText("Sender");
		}
		else if(CmFinoFIX.NotificationReceiverType_Destination.equals(notificationLog.getNotificationReceiverType()))
		{
			e.setNotificationReceiverTypeText("Receiver");
		}
		else if(CmFinoFIX.NotificationReceiverType_OnBehalfOfSubscriber.equals(notificationLog.getNotificationReceiverType()))
		{
			e.setNotificationReceiverTypeText("OnBehalfOfSubscriber");
		}
		e.setSourceAddress(notificationLog.getSourceAddress());
		e.setEmailSubject(notificationLog.getEmailSubject());		
		e.setRecordVersion(notificationLog.getVersion());
		e.setCreatedBy(notificationLog.getCreatedBy());
		e.setCreateTime(notificationLog.getCreateTime());
		e.setUpdatedBy(notificationLog.getUpdatedBy());
		e.setLastUpdateTime(notificationLog.getLastUpdateTime());
		
		NotificationLogDetailsQuery query = new NotificationLogDetailsQuery(); 
		query.setNotificationLog(notificationLog);
		List<NotificationLogDetails> notificationLogDetails = DAOFactory.getInstance().getNotificationLogDetailsDao().get(query);
		e.setCount(notificationLogDetails.size());
		query.setSendNotificationtatus(CmFinoFIX.SendNotificationStatus_Success);
		notificationLogDetails = DAOFactory.getInstance().getNotificationLogDetailsDao().get(query);
		e.setSuccessfulNotificationsCount(notificationLogDetails.size());
		
		
	}
	
	private CFIXMsg handleException(Exception e) throws Exception {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		CmFinoFIX.CMJSError.CGEntries[] newEntries = errorMsg.allocateEntries(1);
		newEntries[0] = new CmFinoFIX.CMJSError.CGEntries();
		errorMsg.setErrorDescription(e.getMessage());
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		newEntries[0].setErrorDescription(e.getMessage());
		log.warn(e.getMessage(), e);
		WebContextError.addError(errorMsg);
		throw e;
	}
}
