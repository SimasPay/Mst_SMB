
package com.mfino.uicore.fix.processor.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.NotificationLogDetailsDAO;
import com.mfino.domain.NotificationLog;
import com.mfino.domain.NlogDetails;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSResendNotification;
import com.mfino.i18n.MessageText;
import com.mfino.service.MailService;
import com.mfino.service.SMSService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ResendNotificationProcessor;
import com.mfino.util.EncryptionUtil;

/**
 * @author Amar
 */
@Service("ResendNotificationProcessorImpl")
public class ResendNotificationProcessorImpl extends BaseFixProcessor implements ResendNotificationProcessor{

	@Autowired
	@Qualifier("SMSServiceImpl")
	private SMSService smsService;
	
	@Autowired
	@Qualifier("MailServiceImpl")
	private MailService mailService;
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {

		CMJSResendNotification realMsg = (CMJSResendNotification) msg;
		CMJSError error = new CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);

		if(StringUtils.isNotBlank(realMsg.getListOfNotificationLogIDs()))
		{
			String[] notificationLogIDs = realMsg.getListOfNotificationLogIDs().split(",");

			for(String notificationLogID : notificationLogIDs)
			{

				NotificationLog notificationLog = DAOFactory.getInstance().getNotificationLogDao().getById(Long.parseLong(notificationLogID));
				NlogDetails notificationLogDetails = new NlogDetails();
				NotificationLogDetailsDAO notificationLogDetailsDAO = DAOFactory.getInstance().getNotificationLogDetailsDao();	
				notificationLogDetails.setNotificationLog(notificationLog);
				notificationLogDetails.setStatus(CmFinoFIX.SendNotificationStatus_Sending);
				notificationLogDetailsDAO.save(notificationLogDetails);
				if(notificationLog != null)
				{
					String text = EncryptionUtil.getDecryptedString(notificationLog.getText());
					
					Long tempNotLogCodeL = notificationLog.getCode();
					Integer tempNotLogCodeLI = tempNotLogCodeL.intValue();
					
					if(CmFinoFIX.NotificationMethod_SMS.equals(notificationLog.getNotificationmethod())) 
					{
						smsService.setDestinationMDN(notificationLog.getSourceaddress());
						smsService.setMessage(text);
						smsService.setNotificationCode(tempNotLogCodeLI);
						smsService.setSctlId(notificationLog.getSctlid().longValue());
						smsService.setDuplicateSMS(true);
						smsService.setNotificationLogDetailsID(notificationLogDetails.getId().longValue());
						smsService.asyncSendSMS();
					}
					else if(CmFinoFIX.NotificationMethod_Email.equals(notificationLog.getNotificationmethod()))
					{
						mailService.asyncSendEmail(notificationLog.getSourceaddress(), "", notificationLog.getEmailsubject(), text, notificationLogDetails.getId().longValue());
					}
					else
					{
						error.setErrorDescription(MessageText._("Notification Method not mentioned for this Notification Log message"));
						return error;
					}
				}			

			}
			error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			error.setErrorDescription(MessageText._("Messages sent successfully"));
		}

		return error;

	}

}
