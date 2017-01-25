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
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.UserService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.SuspendSubscriberEmoneyPocketProcessor;
import com.mfino.util.ConfigurationUtil;
import com.mfino.fix.CmFinoFIX.CMSuspendSubscriberEmoneyPocket;

@Service("SuspendSubscriberEmoneyPocketProcessorImpl")
public class SuspendSubscriberEmoneyPocketProcessorImpl extends BaseFixProcessor implements SuspendSubscriberEmoneyPocketProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
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
		CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
		error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
	        int count=  subscriberUpgradeDataDAO.getCountByMdnId(realMsg.getMDNID());
			if(count == 0) {
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				if (CollectionUtils.isNotEmpty(lst)) {
		    		error.setErrorDescription(MessageText._("Can't suspend the E-money pocket as there are pending transactions need to be resolved."));
		    		error.setsuccess(Boolean.FALSE);
		        	return error;
				}
				SubscriberUpgradeData subscriberUpgradeData=new SubscriberUpgradeData();
				subscriberUpgradeData.setMdnId(realMsg.getMDNID());
				subscriberUpgradeData.setCreatedby(userService.getCurrentUser().getUsername());
				subscriberUpgradeData.setCreatetime(new Timestamp());
				subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Suspend_Emoney_Pocket);
				subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
				subscriberUpgradeData.setComments(realMsg.getComments());
				subscriberUpgradeDataDAO.save(subscriberUpgradeData);
				log.info("SuspendSubscriberEmoneyPocketProcessorImpl Process method Completed");
				realMsg.setsuccess(Boolean.TRUE);
	        	return realMsg;
			} else{
	    		error.setErrorDescription(MessageText._(ConfigurationUtil.getSubscriberActivityActiveMessage()));
	    		error.setsuccess(Boolean.FALSE);
	        	return error;
	    	}
		}
		else if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			SubscriberUpgradeData sud = subscriberUpgradeDataDAO.getUpgradeDataByMdnId(realMsg.getMDNID());
			
			if (CmFinoFIX.AdminAction_Approve.intValue() == realMsg.getAdminAction()) {
				sud.setAdminAction(CmFinoFIX.AdminAction_Approve);
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				if (CollectionUtils.isNotEmpty(lst)) {
		    		error.setErrorDescription(MessageText._("Can't suspend the E-money pocket as there are pending transactions need to be resolved."));
		    		error.setsuccess(Boolean.FALSE);
		    		sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Failed);
		        	return error;
				}
				//TODO
			}
			else if (CmFinoFIX.AdminAction_Reject.intValue() == realMsg.getAdminAction()) {
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				sud.setAdminAction(CmFinoFIX.AdminAction_Reject);
				
			}
			subscriberUpgradeDataDAO.save(sud);
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
