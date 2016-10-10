package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberGroupDao;
import com.mfino.domain.SubscriberGroups;
import com.mfino.exceptions.MfinoRuntimeException;
import com.mfino.service.SubscriberGroupService;

@Service("SubscriberGroupServiceImpl")
public class SubscriberGroupServiceImpl implements SubscriberGroupService {
	public void save(SubscriberGroups subscriberGroup){
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		subscriberGroupDao.save(subscriberGroup);
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public SubscriberGroups getBySubscriberID(Long subscriberID) throws MfinoRuntimeException
	{	
		SubscriberGroupDao subscriberGroupDao = DAOFactory.getInstance().getSubscriberGroupDao();
		return subscriberGroupDao.getBySubscriberID(subscriberID);	
	}
}
