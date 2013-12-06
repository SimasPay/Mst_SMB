package com.mfino.tools.adjustments;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.NotificationQuery;
import com.mfino.domain.Notification;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMSubscriberNotification;
import com.mfino.mce.core.MCEMessage;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.mce.notification.impl.NotificationServiceDefaultImpl;

public class AdjustmentMessageCreator implements Processor{
	
	private static Logger log = LoggerFactory.getLogger(AdjustmentMessageCreator.class);
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void process(Exchange exchange) throws Exception {
		log.info("Creating the adjustment related notification in class AdjustmentMessageCreator" );
		CMSubscriberNotification adjustmentMessage = new CMSubscriberNotification();
		MCEMessage mce = (MCEMessage) exchange.getIn().getBody();
		BackendResponse be = (BackendResponse) mce.getResponse();
		adjustmentMessage.setResult(be.getResult());
		Integer notificationCode = com.mfino.mce.core.util.NotificationCodes.getNotificationCodeFromInternalCode(be.getInternalErrorCode());
		String notificationText = getNotificationText(be,notificationCode);
		if(notificationText==null){
			if(mce.getRequest() !=null && mce.getRequest() instanceof BackendResponse){
				notificationText = ((BackendResponse)mce.getRequest()).getDescription();
			}
		}
		adjustmentMessage.setText(notificationText);
		adjustmentMessage.setCode(notificationCode);
		log.info("adjustmentMessage : "+adjustmentMessage.DumpFields());
		exchange.getIn().setBody(adjustmentMessage);
		//return ex;
		
	}
	
	public String getNotificationText(BackendResponse be,Integer notificationCode){
		Notification notification = null;
		String notificationText = null;
		NotificationQuery notificationQuery = new NotificationQuery();
		notificationQuery.setNotificationCode(notificationCode);
		notificationQuery.setNotificationMethod(CmFinoFIX.NotificationMethod_Web);
		List<Notification> notificationList = DAOFactory.getInstance().getNotificationDAO().get(notificationQuery);

		if(CollectionUtils.isNotEmpty(notificationList)){
			notification = notificationList.get(0);
			NotificationServiceDefaultImpl nsdi = new NotificationServiceDefaultImpl(); 
			log.info("Sending the data to NotificationServiceDefaultImpl for notification creation");
			notificationText = nsdi.getNotificationText(be, notification);
		}
		else{
			log.error("Notification with notification code : "+notificationCode+" not found");
		}
		
		return notificationText;

	}
}
