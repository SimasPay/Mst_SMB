package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMReleaseSuspendSubscriberEmoneyPocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ReleaseSuspendSubscriberEmoneyPocketProcessor;
import com.mfino.util.ConfigurationUtil;

@Service("ReleaseSuspendSubscriberEmoneyPocketProcessorImpl")
public class ReleaseSuspendSubscriberEmoneyPocketProcessorImpl extends BaseFixProcessor implements ReleaseSuspendSubscriberEmoneyPocketProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In ReleaseSuspendSubscriberEmoneyPocketProcessorImpl Process method");
		
		CMReleaseSuspendSubscriberEmoneyPocket realMsg = (CMReleaseSuspendSubscriberEmoneyPocket) msg;
		CmFinoFIX.CMJSError result = new CmFinoFIX.CMJSError();
		result.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
	        int count=  subscriberUpgradeDataDAO.getCountByMdnId(realMsg.getMDNID());
			if(count == 0) {
				SubscriberUpgradeData subscriberUpgradeData=new SubscriberUpgradeData();
				subscriberUpgradeData.setMdnId(realMsg.getMDNID());
				subscriberUpgradeData.setCreatedby(getLoggedUserName());
				subscriberUpgradeData.setCreatetime(new Timestamp());
				subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Release_Suspend_For_Emoney_Pocket);
				subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
				subscriberUpgradeData.setComments(realMsg.getComments());
				subscriberUpgradeDataDAO.save(subscriberUpgradeData);
				result.setErrorDescription("Request to release subscriber's e-Money pocket suspension has been submitted successfully. " +
						"Suspension will be released once approved.");
				result.setsuccess(Boolean.TRUE);
			} else{
	    		result.setErrorDescription(ConfigurationUtil.getSubscriberActivityActiveMessage());
	    		result.setsuccess(Boolean.FALSE);
	    	}
			return result;
		}
		else if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			SubscriberUpgradeData sud = subscriberUpgradeDataDAO.getUpgradeDataByMdnId(realMsg.getMDNID());
			sud.setSubsActivityApprovedBY(getLoggedUserName());
			sud.setSubsActivityAprvTime(new Timestamp());
			sud.setSubsActivityComments(realMsg.getComments());
			
			if (CmFinoFIX.AdminAction_Approve.intValue() == realMsg.getAdminAction()) {
				sud.setAdminAction(CmFinoFIX.AdminAction_Approve);
				Pocket pocket = pocketService.getDefaultPocket(subscriberMdnService.getById(realMsg.getMDNID()), CmFinoFIX.PocketType_SVA.toString());
				pocket.setStatus(CmFinoFIX.PocketStatus_Active);
				pocketService.save(pocket);
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				result.setErrorDescription("Subscriber's e-money pocket is Active.");
				result.setsuccess(Boolean.TRUE);
			}
			else if (CmFinoFIX.AdminAction_Reject.intValue() == realMsg.getAdminAction()) {
				sud.setAdminAction(CmFinoFIX.AdminAction_Reject);
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				result.setErrorDescription("Rejected the release subscriber's e-money pocket suspension request.");
				result.setsuccess(Boolean.TRUE);
			}
			subscriberUpgradeDataDAO.save(sud);
        	return result;
		}
		return realMsg;
	}
}
