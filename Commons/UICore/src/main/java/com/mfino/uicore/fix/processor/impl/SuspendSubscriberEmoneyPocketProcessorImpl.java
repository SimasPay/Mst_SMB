package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
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
import com.mfino.fix.CmFinoFIX.CMSuspendSubscriberEmoneyPocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SuspendSubscriberEmoneyPocketProcessor;
import com.mfino.util.ConfigurationUtil;

@Service("SuspendSubscriberEmoneyPocketProcessorImpl")
public class SuspendSubscriberEmoneyPocketProcessorImpl extends BaseFixProcessor implements SuspendSubscriberEmoneyPocketProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In SuspendSubscriberEmoneyPocketProcessorImpl Process method");
		
		CMSuspendSubscriberEmoneyPocket realMsg = (CMSuspendSubscriberEmoneyPocket) msg;
		CmFinoFIX.CMJSError result = new CmFinoFIX.CMJSError();
		result.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
	        int count=  subscriberUpgradeDataDAO.getCountByMdnId(realMsg.getMDNID());
			if(count == 0) {
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				if (CollectionUtils.isNotEmpty(lst)) {
		    		result.setErrorDescription("Can't suspend the e-money pocket as there are pending transactions need to be resolved.");
		    		result.setsuccess(Boolean.FALSE);
				} else {
					SubscriberUpgradeData subscriberUpgradeData=new SubscriberUpgradeData();
					subscriberUpgradeData.setMdnId(realMsg.getMDNID());
					subscriberUpgradeData.setCreatedby(getLoggedUserName());
					subscriberUpgradeData.setCreatetime(new Timestamp());
					subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Suspend_Emoney_Pocket);
					subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
					subscriberUpgradeData.setComments(realMsg.getComments());
					subscriberUpgradeDataDAO.save(subscriberUpgradeData);
					result.setErrorDescription("Request to suspend subscriber's e-Money pocket has been submitted successfully. " +
							"Subscriber's e-money pocket will be suspended once approved.");
					result.setsuccess(Boolean.TRUE);
				}
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
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				if (CollectionUtils.isNotEmpty(lst)) {
		    		result.setErrorDescription("Can't suspend the e-money pocket as there are pending transactions need to be resolved.");
		    		result.setsuccess(Boolean.FALSE);
		    		sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Failed);
		        	return result;
				}
				Pocket pocket = pocketService.getDefaultPocket(subscriberMdnService.getById(realMsg.getMDNID()), CmFinoFIX.PocketType_SVA.toString());
				pocket.setStatus(CmFinoFIX.PocketStatus_Suspend);
				pocketService.save(pocket);
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				result.setErrorDescription("Subscriber's e-money pocket is suspended.");
				result.setsuccess(Boolean.TRUE);
			}
			else if (CmFinoFIX.AdminAction_Reject.intValue() == realMsg.getAdminAction()) {
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				sud.setAdminAction(CmFinoFIX.AdminAction_Reject);
				result.setErrorDescription("Rejected the subscriber's e-money pocket suspend request.");
				result.setsuccess(Boolean.TRUE);
			}
			subscriberUpgradeDataDAO.save(sud);
        	return result;
		}
		return realMsg;
	}

	private List<PendingCommodityTransfer> getPendingTransactions(CMSuspendSubscriberEmoneyPocket realMsg) throws Exception {
		Pocket pocket = pocketService.getDefaultPocket(subscriberMdnService.getById(realMsg.getMDNID()), CmFinoFIX.PocketType_SVA.toString());
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setSourceDestnPocket(pocket);
		List<PendingCommodityTransfer> lst = pendingCommodityTransferService.getByQuery(query);
		return lst;
	}
}
