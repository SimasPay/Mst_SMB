package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.service.SubscriberUpgradeDataService;

@Service("SubscriberUpgradeDataServiceImpl")
public class SubscriberUpgradeDataServiceImpl implements SubscriberUpgradeDataService {
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(SubscriberUpgradeData subscriberUpgradeData){
		subscriberUpgradeDataDAO.save(subscriberUpgradeData);
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public SubscriberUpgradeData getByMdnId(Long mdnId) {
		return subscriberUpgradeDataDAO.getByMdnId(mdnId);
	}
}
