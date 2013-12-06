package com.mfino.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberStatusEventDAO;
import com.mfino.domain.Subscriber;
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
			boolean includeParnterInSLC) {
		SubscriberStatusEventDAO statusEventDAO = DAOFactory.getInstance()
				.getSubscriberStatusEventDAO();
		return statusEventDAO.getSubscriberStatusEvent(includeParnterInSLC);
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
		if (subscriberExistingEvent != null) {
			if (subscriberExistingEvent.getProcessingStatus()) {
				SubscriberStatusEvent statusNextEvent = new SubscriberStatusEvent();
				statusNextEvent.setSubscriber(subscriber);
				Timestamp nextTimeStamp = new Timestamp(
						subscriber.getStatusTime().getTime()
								+ subscriberStatusTimeService
										.getTimeToNextStatus(subscriber
												.getStatus()));
				statusNextEvent.setPickUpDateTime(nextTimeStamp);
				statusNextEvent.setStatusOnPickup(subscriber.getStatus());
				statusNextEvent.setSubscriberType(subscriber.getType());
				statusNextEvent.setProcessingStatus(false);
				statusEventDAO.save(statusNextEvent);
			} else {
				Timestamp nextTimeStamp = new Timestamp(
						subscriber.getStatusTime().getTime()
								+ subscriberStatusTimeService
										.getTimeToNextStatus(subscriber
												.getStatus()));
				subscriberExistingEvent.setPickUpDateTime(nextTimeStamp);
				subscriberExistingEvent.setStatusOnPickup(subscriber
						.getStatus());
				statusEventDAO.save(subscriberExistingEvent);
			}
		}else if(!CmFinoFIX.SubscriberStatus_Initialized.equals(subscriber.getStatus())){
			SubscriberStatusEvent statusNextEvent = new SubscriberStatusEvent();
			statusNextEvent.setSubscriber(subscriber);
			Timestamp nextTimeStamp = new Timestamp(
					subscriber.getStatusTime().getTime()
							+ subscriberStatusTimeService
									.getTimeToNextStatus(subscriber
											.getStatus()));
			statusNextEvent.setPickUpDateTime(nextTimeStamp);
			statusNextEvent.setStatusOnPickup(subscriber.getStatus());
			statusNextEvent.setSubscriberType(subscriber.getType());
			statusNextEvent.setProcessingStatus(false);
			statusEventDAO.save(statusNextEvent);
		}
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private SubscriberStatusEvent getSubscriberStatusEventForSubscriber(
			Subscriber subscriber,boolean  isOnline) {
		SubscriberStatusEvent subscriberStatusEvent = null;
		if (subscriber != null) {
			Set<SubscriberStatusEvent> subscriberStatusEvents = subscriber
					.getSubscriberStatusEventFromSubscriberID();
			if ((subscriberStatusEvents != null)
					&& (subscriberStatusEvents.size() != 0)) {
				for (SubscriberStatusEvent statusEvent : subscriberStatusEvents) {
					if (isOnline&&!statusEvent.getProcessingStatus() ) {
						subscriberStatusEvent = statusEvent;
						return subscriberStatusEvent;
					}else if(!isOnline&&statusEvent.getProcessingStatus()){
						subscriberStatusEvent = statusEvent;
						return subscriberStatusEvent;
					}
				}
			}
		}
		return subscriberStatusEvent;
	}

}
