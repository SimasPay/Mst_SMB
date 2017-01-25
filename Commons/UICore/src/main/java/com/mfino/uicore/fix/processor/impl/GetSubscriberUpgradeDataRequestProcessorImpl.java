package com.mfino.uicore.fix.processor.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CFIXMsg;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.GetSubscriberUpgradeDataRequestProcessor;
import com.mfino.fix.CmFinoFIX.CMGetSubscriberUpgradeDataRequest;

@Service("GetSubscriberUpgradeDataRequestProcessorImpl")
public class GetSubscriberUpgradeDataRequestProcessorImpl extends BaseFixProcessor implements GetSubscriberUpgradeDataRequestProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		
		CMGetSubscriberUpgradeDataRequest realMsg = (CMGetSubscriberUpgradeDataRequest) msg;
		SubscriberUpgradeData sud = subscriberUpgradeDataDAO.getUpgradeDataByMdnId(realMsg.getMDNID());
		realMsg.setsuccess(Boolean.TRUE);
		if (sud != null) {
			realMsg.setSubscriberActivity(sud.getSubActivity());
			realMsg.settotal(1);
		} else {
			realMsg.settotal(0);
		}
		return realMsg;
	}
}
