package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberUpgradeDataDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberUpgradeData;
import com.mfino.exceptions.InvalidDataException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMRetireSubscriberEmoneyPocket;
import com.mfino.hibernate.Timestamp;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.PendingCommodityTransferService;
import com.mfino.service.PocketService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.subscriber.SubscriberEMoneyClosingHandler;
import com.mfino.transactionapi.result.xmlresulttypes.subscriber.SubscriberAccountClosingXMLResult;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.RetireEmoneyPocketSubscriberProcessor;
import com.mfino.util.ConfigurationUtil;

@Service("RetireEmoneyPocketSubscriberProcessorImpl")
public class RetireEmoneyPocketSubscriberProcessorImpl extends BaseFixProcessor implements RetireEmoneyPocketSubscriberProcessor {
	
	private SubscriberUpgradeDataDAO subscriberUpgradeDataDAO = DAOFactory.getInstance().getSubscriberUpgradeDataDAO();
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("PendingCommodityTransferServiceImpl")
	private PendingCommodityTransferService pendingCommodityTransferService;
	
	@Autowired
	@Qualifier("SubscriberEMoneyClosingHandlerImpl")
	private SubscriberEMoneyClosingHandler subscriberEMoneyClosingHandler;

	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService ;
	
	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		log.info("In RetireEmoneyPocketSubscriberProcessorImpl Process method");
		
		CMRetireSubscriberEmoneyPocket realMsg = (CMRetireSubscriberEmoneyPocket) msg;
		CmFinoFIX.CMJSError result = new CmFinoFIX.CMJSError();
		result.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		
		if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
	        int count=  subscriberUpgradeDataDAO.getCountByMdnId(realMsg.getMDNID());
			if(count == 0) {
				
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				if (CollectionUtils.isNotEmpty(lst)) {
		    		
					result.setErrorDescription("Can't retire the e-money pocket as there are pending transactions need to be resolved.");
		    		result.setsuccess(Boolean.FALSE);
		    		
				} else {
					
					Pocket svaPocket = subscriberService.getDefaultPocket(realMsg.getMDNID(), CmFinoFIX.PocketType_SVA, CmFinoFIX.Commodity_Money);
					BigDecimal currentbalance = svaPocket.getCurrentbalance();
					BigDecimal maxClosingAmount = systemParametersService.getBigDecimal(SystemParameterKeys.MAXIMUM_SUBSCRIBER_CLOSING_AMOUNT);
					if(currentbalance != null && currentbalance.compareTo(maxClosingAmount) > 0){
						result.setErrorDescription("Can't retire the e-money pocket because the pocket balance more than "+maxClosingAmount);
			    		result.setsuccess(Boolean.FALSE);
			    		return result;
					}
					
					SubscriberUpgradeData subscriberUpgradeData=new SubscriberUpgradeData();
					subscriberUpgradeData.setMdnId(realMsg.getMDNID());
					subscriberUpgradeData.setCreatedby(getLoggedUserName());
					subscriberUpgradeData.setCreatetime(new Timestamp());
					subscriberUpgradeData.setSubActivity(CmFinoFIX.SubscriberActivity_Retire_Subscriber_Emoney_Pocket);
					subscriberUpgradeData.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Initialized);
					subscriberUpgradeData.setComments(realMsg.getComments());
					subscriberUpgradeDataDAO.save(subscriberUpgradeData);
					result.setErrorDescription("Request to Retire Subscriber's E-Money Pocket has been Submitted Successfully. " +
							"Subscriber's E-Money Pocket will be Retired once Approved.");
					result.setsuccess(Boolean.TRUE);
				}
			} else{
	    		
				result.setErrorDescription(ConfigurationUtil.getSubscriberActivityActiveMessage());
	    		result.setsuccess(Boolean.FALSE);
	    	}
			
			return result;
			
		} else if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
			
			SubscriberUpgradeData sud = subscriberUpgradeDataDAO.getUpgradeDataByMdnId(realMsg.getMDNID());
			sud.setSubsActivityApprovedBY(getLoggedUserName());
			sud.setSubsActivityAprvTime(new Timestamp());
			sud.setSubsActivityComments(realMsg.getComments());
			
			if (CmFinoFIX.AdminAction_Approve.intValue() == realMsg.getAdminAction()) {
				
				sud.setAdminAction(CmFinoFIX.AdminAction_Approve);
				
				List<PendingCommodityTransfer> lst = getPendingTransactions(realMsg);
				
				if (CollectionUtils.isNotEmpty(lst)) {
		    		
					result.setErrorDescription("Can't retire the e-money pocket as there are pending transactions need to be resolved.");
		    		result.setsuccess(Boolean.FALSE);
		    		sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Failed);
		        	return result;
				}
				
				TransactionDetails td =new TransactionDetails();
				ChannelCode channelCode = getChannelCode(String.valueOf(CmFinoFIX.SourceApplication_WebAPI));
				
				td.setCc(channelCode);
				td.setDestMDN(subscriberMdnService.getById(realMsg.getMDNID()).getMdn());
				td.setSystemIntiatedTransaction(true);
				td.setHttps(true);
				
				SubscriberAccountClosingXMLResult subscriberClosingResult = (SubscriberAccountClosingXMLResult)subscriberEMoneyClosingHandler.handle(td);
				
				if(null != subscriberClosingResult) {
					
					if(String.valueOf(CmFinoFIX.NotificationCode_SubscriberClosingSuccess).equals(subscriberClosingResult.getCode())) {
					
						result.setErrorCode(CmFinoFIX.ErrorCode_NoError);
						result.setErrorDescription("Subscriber Account Closed Successfully");
						
					} else {
						
						result.setErrorCode(CmFinoFIX.ErrorCode_NoError);
						result.setErrorDescription("Transaction Failed. Please retry once again");
					}
				}
				
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				result.setsuccess(Boolean.TRUE);
				
			} else if (CmFinoFIX.AdminAction_Reject.intValue() == realMsg.getAdminAction()) {
				
				sud.setSubsActivityStatus(CmFinoFIX.SubscriberActivityStatus_Completed);
				sud.setAdminAction(CmFinoFIX.AdminAction_Reject);
				result.setErrorDescription("Rejected the Subscriber's E-Money Pocket Retired Request.");
				result.setsuccess(Boolean.TRUE);
			}
			
			subscriberUpgradeDataDAO.save(sud);
        	return result;
		}
		
		return realMsg;
	}

	private List<PendingCommodityTransfer> getPendingTransactions(CMRetireSubscriberEmoneyPocket realMsg) throws Exception {
		
		Pocket pocket = pocketService.getDefaultPocket(subscriberMdnService.getById(realMsg.getMDNID()), CmFinoFIX.PocketType_SVA.toString());
		CommodityTransferQuery query = new CommodityTransferQuery();
		query.setSourceDestnPocket(pocket);
		List<PendingCommodityTransfer> lst = pendingCommodityTransferService.getByQuery(query);
		return lst;
	}
	
	private ChannelCode getChannelCode(String channelCode) throws InvalidDataException{
		
		ChannelCode cc = channelCodeService.getChannelCodeByChannelCode(channelCode);
		
		if(cc==null){
			throw new InvalidDataException("Invalid ChannelID", CmFinoFIX.NotificationCode_InvalidData, ApiConstants.PARAMETER_CHANNEL_ID);
		}
		
		return cc;
	}
}
