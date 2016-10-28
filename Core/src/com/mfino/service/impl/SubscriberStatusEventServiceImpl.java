package com.mfino.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberStatusEventDAO;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.SubscriberStatusEvent;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SubscriberStatusTimeService;

@Service("SubscriberStatusEventServiceImpl")
public class SubscriberStatusEventServiceImpl implements
		SubscriberStatusEventService {

	@Autowired
	@Qualifier("SubscriberStatusTimeServiceImpl")
	private SubscriberStatusTimeService subscriberStatusTimeService;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public List<SubscriberStatusEvent> getSubscriberStatusEvent(
			boolean includeParnterInSLC, Integer[] statuses) {
		SubscriberStatusEventDAO statusEventDAO = DAOFactory.getInstance()
				.getSubscriberStatusEventDAO();
		return statusEventDAO.getSubscriberStatusEvent(includeParnterInSLC, statuses);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void save(SubscriberStatusEvent subscriberStatusEvent) {

		SubscriberStatusEventDAO statusEventDAO = DAOFactory.getInstance()
				.getSubscriberStatusEventDAO();
		statusEventDAO.save(subscriberStatusEvent);

	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void upsertNextPickupDateForStatusChange(Subscriber subscriber,boolean isOnline) {
		SubscriberStatusEvent subscriberExistingEvent = getSubscriberStatusEventForSubscriber(subscriber,isOnline);
		SubscriberStatusEventDAO statusEventDAO = DAOFactory.getInstance()
				.getSubscriberStatusEventDAO();
		
		SubscriberMdn subMDN = null;
		
		if(subscriber.getSubscriberMdns() != null && !(subscriber.getSubscriberMdns().size() == 0)){
			
			subMDN	= subscriber.getSubscriberMdns().iterator().next();
		}
		 
		if (subscriberExistingEvent != null) {
			if (Boolean.valueOf(subscriberExistingEvent.getProcessingstatus().toString())) {
				SubscriberStatusEvent statusNextEvent = new SubscriberStatusEvent();
				statusNextEvent.setSubscriberid(BigDecimal.valueOf(subscriber.getId()));
				Long temp = subscriber.getStatus().longValue();
				Integer tempI = temp.intValue();
				Timestamp nextTimeStamp = new Timestamp(
						subscriber.getStatustime().getTime()
								+ subscriberStatusTimeService
										.getTimeToNextStatus(tempI));
				if (subMDN!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString())!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString()) &&
						subscriber.getStatus() == CmFinoFIX.SubscriberStatus_PendingRetirement.intValue())
					nextTimeStamp = new Timestamp();
				statusNextEvent.setPickupdatetime(nextTimeStamp);
				statusNextEvent.setStatusonpickup(subscriber.getStatus());
				statusNextEvent.setSubscribertype(subscriber.getType());
				statusNextEvent.setProcessingstatus(Boolean.FALSE);
				statusEventDAO.save(statusNextEvent);
			} else {
				Long temp = subscriber.getStatus().longValue();
				Integer tempI = temp.intValue();
				Timestamp nextTimeStamp = new Timestamp(
						subscriber.getStatustime().getTime()
								+ subscriberStatusTimeService
										.getTimeToNextStatus(tempI));
				if (subMDN!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString())!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString()) &&
						subscriber.getStatus() == CmFinoFIX.SubscriberStatus_PendingRetirement.intValue())
					nextTimeStamp = new Timestamp();
				subscriberExistingEvent.setPickupdatetime(nextTimeStamp);
				subscriberExistingEvent.setStatusonpickup(subscriber
						.getStatus());
				statusEventDAO.save(subscriberExistingEvent);
			}
		}else if(!CmFinoFIX.SubscriberStatus_Initialized.equals(subscriber.getStatus())){
			SubscriberStatusEvent statusNextEvent = new SubscriberStatusEvent();
			statusNextEvent.setSubscriberid(BigDecimal.valueOf(subscriber.getId()));
			Long temp = subscriber.getStatus().longValue();
			Integer tempI = temp.intValue();
			Timestamp nextTimeStamp = new Timestamp(
					subscriber.getStatustime().getTime()
							+ subscriberStatusTimeService
									.getTimeToNextStatus(tempI));
			if (subMDN!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString())!=null && Boolean.valueOf(subMDN.getIsforcecloserequested().toString()) &&
					subscriber.getStatus() == CmFinoFIX.SubscriberStatus_PendingRetirement.intValue())
				nextTimeStamp = new Timestamp();
			statusNextEvent.setPickupdatetime(nextTimeStamp);
			statusNextEvent.setStatusonpickup(subscriber.getStatus());
			statusNextEvent.setSubscribertype(subscriber.getType());
			statusNextEvent.setProcessingstatus(Boolean.FALSE);
			statusEventDAO.save(statusNextEvent);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private SubscriberStatusEvent getSubscriberStatusEventForSubscriber(
			Subscriber subscriber,boolean  isOnline) {
		SubscriberStatusEvent subscriberStatusEvent = null;
		if (subscriber != null) {
			SubscriberStatusEventDAO subscriberStatusEventDAO = DAOFactory.getInstance().getSubscriberStatusEventDAO();
			List<SubscriberStatusEvent> subscriberStatusEvents = subscriberStatusEventDAO.getAllBySubscriberId(BigDecimal.valueOf(subscriber.getId()));
			
			if ((subscriberStatusEvents != null)
					&& (subscriberStatusEvents.size() != 0)) {
				for (SubscriberStatusEvent statusEvent : subscriberStatusEvents) {
					if (isOnline&&!Boolean.valueOf(statusEvent.getProcessingstatus().toString()) ) {
						subscriberStatusEvent = statusEvent;
						return subscriberStatusEvent;
					}else if(!isOnline&&Boolean.valueOf(statusEvent.getProcessingstatus().toString())){
						subscriberStatusEvent = statusEvent;
						return subscriberStatusEvent;
					}
				}
			}
		}
		return subscriberStatusEvent;
	}

}
