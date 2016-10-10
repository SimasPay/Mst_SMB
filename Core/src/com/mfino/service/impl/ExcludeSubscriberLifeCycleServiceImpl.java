package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ExcludeSubscriberLifeCycleDAO;
import com.mfino.domain.ExcludeSubscriberLc;
import com.mfino.domain.SubscriberMdn;
import com.mfino.service.ExcludeSubscriberLifeCycleService;

@Service("ExcludeSubscriberLifeCycleServiceImpl")
public class ExcludeSubscriberLifeCycleServiceImpl implements
		ExcludeSubscriberLifeCycleService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ExcludeSubscriberLc getBySubscriberMDN(SubscriberMdn subscriberMDN){
		ExcludeSubscriberLifeCycleDAO eslcDAO = DAOFactory.getInstance().getExcludeSubscriberLifeCycleDao();
		return eslcDAO.getBySubscriberMDN(subscriberMDN);
	}
}
