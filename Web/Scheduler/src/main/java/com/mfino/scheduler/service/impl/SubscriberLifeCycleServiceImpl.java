/**
 * 
 */
package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ExcludeSubscriberLifeCycle;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.SubscriberStatusEvent;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.result.XMLResult;
import com.mfino.scheduler.service.SubscriberLifeCycleService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.CommodityTransferService;
import com.mfino.service.ExcludeSubscriberLifeCycleService;
import com.mfino.service.MDNRetireService;
import com.mfino.service.MoneyClearanceGravedService;
import com.mfino.service.PartnerService;
import com.mfino.service.PartnerServicesService;
import com.mfino.service.PocketService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.SubscriberStatusEventService;
import com.mfino.service.SystemParametersService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceConfirmHandler;
import com.mfino.transactionapi.handlers.wallet.MoveBalanceInquiryHandler;
import com.mfino.transactionapi.vo.TransactionDetails;
import com.mfino.util.ConfigurationUtil;
import com.mfino.validators.PartnerValidator;
import com.mfino.validators.Validator;
/**
 * @author Bala Sunku
 *
 */
@Service("SubscriberLifeCycleServiceImpl")
public class SubscriberLifeCycleServiceImpl  implements SubscriberLifeCycleService {
	private static Logger log = LoggerFactory.getLogger(SubscriberLifeCycleServiceImpl.class);

	
	private static long MILLI_SECONDS_PER_DAY = 24*60*60*1000;
	private static long TIME_TO_SUSPEND_OF_NO_ACTIVATION = 2;
	private static long TIME_TO_SUSPEND_OF_INACTIVE = 270;
	private static long TIME_TO_RETIRE_OF_SUSPENDED = 180;
	private static long TIME_TO_GRAVE_OF_RETIRED = 365;
	private static long TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = 90;
	private static long DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = 15;
	private static long TIME_TO_MOVE_TO_NATIONALTREASURY = 180;
	private ChannelCode channelCode;
	private  boolean includeParnterInSLC;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Autowired
	@Qualifier("MoveBalanceInquiryHandlerImpl")
	private MoveBalanceInquiryHandler moveBalanceInquiryHandler;
	
	@Autowired
	@Qualifier("MoveBalanceConfirmHandlerImpl")
	private MoveBalanceConfirmHandler moveBalanceConfirmHandler;

	@Autowired
	@Qualifier("SubscriberServiceImpl")
	private SubscriberService subscriberService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("MDNRetireServiceImpl")
	private MDNRetireService mdnRetireService;
	
	@Autowired
	@Qualifier("SystemParametersServiceImpl")
	private SystemParametersService systemParametersService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("SubscriberMdnServiceImpl")
	private SubscriberMdnService subscriberMdnService;
	
	@Autowired
	@Qualifier("PartnerServicesServiceImpl")
	private PartnerServicesService partnerServicesService;
	
	@Autowired
	@Qualifier("CommodityTransferServiceImpl")
	private CommodityTransferService commodityTransferService;
	
	@Autowired
	@Qualifier("PocketServiceImpl")
	private PocketService pocketService;

	@Autowired
	@Qualifier("MoneyClearanceGravedServiceImpl")
	private MoneyClearanceGravedService moneyClearanceGravedService;
	
	@Autowired
	@Qualifier("ExcludeSubscriberLifeCycleServiceImpl")
	private ExcludeSubscriberLifeCycleService excludeSubscriberLifeCycleService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionsLogService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusEventService;
	
	@Autowired
	@Qualifier("SubscriberStatusEventServiceImpl")
	private SubscriberStatusEventService subscriberStatusNextEventService;
	
	
	@Override
	
	public void updateSubscriberStatus() {
		log.info("BEGIN updateSubscriberStatus");
			channelCode = channelCodeService.getChannelCodeByChannelCode(CmFinoFIX.SourceApplication_BackEnd
							.toString());
			SubscriberMdnQuery query = new SubscriberMdnQuery();
			long days = -1l;
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_NO_ACTIVATION);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_NO_ACTIVATION = days;
			}
			TIME_TO_SUSPEND_OF_NO_ACTIVATION = TIME_TO_SUSPEND_OF_NO_ACTIVATION * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_INACTIVE);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_INACTIVE = days;
			}
			TIME_TO_SUSPEND_OF_INACTIVE = TIME_TO_SUSPEND_OF_INACTIVE * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_RETIRE_OF_SUSPENDED);
			if (days != -1) {
				TIME_TO_RETIRE_OF_SUSPENDED = days;
			}
			TIME_TO_RETIRE_OF_SUSPENDED = TIME_TO_RETIRE_OF_SUSPENDED * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_GRAVE_OF_RETIRED);
			if (days != -1) {
				TIME_TO_GRAVE_OF_RETIRED = days;
			}
			TIME_TO_GRAVE_OF_RETIRED = TIME_TO_GRAVE_OF_RETIRED * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_NATIONAL_TREASURY_OF_GRAVED);
			if (days != -1) {
				TIME_TO_MOVE_TO_NATIONALTREASURY = days;
			}
			TIME_TO_MOVE_TO_NATIONALTREASURY = TIME_TO_MOVE_TO_NATIONALTREASURY * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY);
			if (days != -1) {
				TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = days;
			}
			TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT);
			if (days != -1) {
				DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = days;
			}
			DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT * MILLI_SECONDS_PER_DAY;

			Integer[] status = new Integer[5];
			status[0] = CmFinoFIX.SubscriberStatus_InActive;
			status[1] = CmFinoFIX.SubscriberStatus_Suspend;
			status[2] = CmFinoFIX.SubscriberStatus_PendingRetirement;
			status[3] = CmFinoFIX.SubscriberStatus_Active;
			status[4] = CmFinoFIX.SubscriberStatus_Retired;
			
			query.setStatusIn(status);
			
			includeParnterInSLC = ConfigurationUtil.getIncludePartnerInSLC();
			if(includeParnterInSLC == false){
				query.setOnlySubscribers(true);
			}
			List<SubscriberStatusEvent> statusEventList=subscriberStatusEventService.getSubscriberStatusEvent(includeParnterInSLC, status);
			log.info("No of SubscriberStatusEvent entries for updateSubscriberStatus: " + statusEventList!=null?statusEventList.size()+"":null);
//			List<SubscriberMDN> lst = subscriberMdnService.getByQuery(query);
			if (CollectionUtils.isNotEmpty(statusEventList)) {
				for (SubscriberStatusEvent subscriberStatusEvent : statusEventList) {
					checkSubscriber(subscriberStatusEvent);
				} 
			}
		log.info("END updateSubscriberStatus");
	}

	@Override
	
	public void forceGrave() {
		log.info("BEGIN forceGraveSubscribers");
			channelCode = channelCodeService
					.getChannelCodeByChannelCode(CmFinoFIX.SourceApplication_BackEnd
							.toString());
			SubscriberMdnQuery query = new SubscriberMdnQuery();
			long days = -1l;
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_NO_ACTIVATION);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_NO_ACTIVATION = days;
			}
			TIME_TO_SUSPEND_OF_NO_ACTIVATION = TIME_TO_SUSPEND_OF_NO_ACTIVATION * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_SUSPEND_OF_INACTIVE);
			if (days != -1) {
				TIME_TO_SUSPEND_OF_INACTIVE = days;
			}
			TIME_TO_SUSPEND_OF_INACTIVE = TIME_TO_SUSPEND_OF_INACTIVE * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_RETIRE_OF_SUSPENDED);
			if (days != -1) {
				TIME_TO_RETIRE_OF_SUSPENDED = days;
			}
			TIME_TO_RETIRE_OF_SUSPENDED = TIME_TO_RETIRE_OF_SUSPENDED * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_GRAVE_OF_RETIRED);
			if (days != -1) {
				TIME_TO_GRAVE_OF_RETIRED = days;
			}
			TIME_TO_GRAVE_OF_RETIRED = TIME_TO_GRAVE_OF_RETIRED * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_NATIONAL_TREASURY_OF_GRAVED);
			if (days != -1) {
				TIME_TO_MOVE_TO_NATIONALTREASURY = days;
			}
			TIME_TO_MOVE_TO_NATIONALTREASURY = TIME_TO_MOVE_TO_NATIONALTREASURY * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY);
			if (days != -1) {
				TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = days;
			}
			TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY = TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY * MILLI_SECONDS_PER_DAY;
			
			days = systemParametersService.getLong(SystemParameterKeys.DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT);
			if (days != -1) {
				DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = days;
			}
			DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT = DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT * MILLI_SECONDS_PER_DAY;

			Integer[] status = new Integer[5];
			//status[0] = CmFinoFIX.SubscriberStatus_InActive;
			//status[1] = CmFinoFIX.SubscriberStatus_Suspend;
			status[1] = CmFinoFIX.SubscriberStatus_PendingRetirement;
			//status[3] = CmFinoFIX.SubscriberStatus_Active;
			//status[4] = CmFinoFIX.SubscriberStatus_Retired;
			
			query.setStatusIn(status);
			//query.setOnlySubscribers(true);
			query.setIsForceCloseRequested(Boolean.TRUE);
			List<SubscriberStatusEvent> statusEventList=subscriberStatusEventService.getSubscriberStatusEvent(true, status);
			log.info("No of SubscriberStatusEvent entries for forceGraveSubscribers: " + statusEventList!=null?statusEventList.size()+"":null);

//			List<SubscriberMDN> lst = subscriberMdnService.getByQuery(query);
			if (CollectionUtils.isNotEmpty(statusEventList)) {
				for (SubscriberStatusEvent subscriberStatusEvent : statusEventList) {
					checkSubscriber(subscriberStatusEvent);
				} 
			}
		
		log.info("END forceGraveSubscribers");
	}
	
	
	private void checkSubscriber(SubscriberStatusEvent subscriberStatusEvent){
		Subscriber subscriber = null;
		Timestamp now = new Timestamp();
		if(subscriberStatusEvent!=null){
			SubscriberMDN subscriberMDN=getSubscriberMDNForSubscriber(subscriberStatusEvent.getSubscriber());
		if (subscriberMDN != null) {
			subscriber = subscriberMDN.getSubscriber();
			log.info("Checking the Subscriber MDN with id --> " + subscriberMDN.getID() + " And status is --> " + subscriber.getStatus());
			log.info("Checking the Subscriber with id --> " + subscriber.getID() + " And status is --> " + subscriber.getStatus());
			if (subscriber != null && subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType()) 
						&& subscriber.getActivationTime() == null && CmFinoFIX.UpgradeState_Approved.intValue() == subscriber.getUpgradeState()) {
					if ( ((now.getTime() - subscriber.getApproveOrRejectTime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) && 
							((now.getTime() - subscriber.getStatusTime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) ) {
						subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
						subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
						subscriberMDN.setStatusTime(now);
						subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
						subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
						subscriber.setStatusTime(now);
						subscriberMdnService.saveSubscriberMDN(subscriberMDN);
						subscriberService.saveSubscriber(subscriber);
						
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Suspend);
							partnerService.savePartner(partner);
						}
						log.info("Suspended the Partner with subscriber id --> " + subscriber.getID());
						subscriberStatusEvent.setProcessingStatus(true);
						subscriberStatusEventService.save(subscriberStatusEvent);
						subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					}
			}else if (CmFinoFIX.SubscriberStatus_InActive.intValue() == subscriberMDN.getStatus()) {
				if ((now.getTime() - subscriber.getStatusTime().getTime()) > TIME_TO_SUSPEND_OF_INACTIVE) {
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					subscriberMDN.setStatusTime(now);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					subscriber.setStatusTime(now);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					
					if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Suspend);
							partnerService.savePartner(partner);
						}
					}
					subscriberStatusEvent.setProcessingStatus(true);
					subscriberStatusEventService.save(subscriberStatusEvent);
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					log.info("Suspended the Subscriber with id --> " + subscriber.getID());
				}
			}
			else if (CmFinoFIX.SubscriberStatus_Suspend.intValue() == subscriberMDN.getStatus()) {
				//Retire the subscriber if he remains suspended for a period of TIME_TO_RETIRE_OF_SUSPENDED
				//if (isSuspendedSubscriberEligibleTobeRetired(now,subscriber,subscriberMDN)) {
				if ((now.getTime() - subscriber.getStatusTime().getTime()) > TIME_TO_RETIRE_OF_SUSPENDED){
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subscriberMDN.setStatusTime(now);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subscriber.setStatusTime(now);
					// change the pocket status to pending retirement
					subscriberService.retireSubscriber(subscriberMDN);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
							partnerService.retireServices(partner);
							partnerService.savePartner(partner);
						}
					}
					log.info("Moved to Pending retired the subscriber with id -->" + subscriber.getID());
					subscriberStatusEvent.setProcessingStatus(true);
					subscriberStatusEventService.save(subscriberStatusEvent);
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);

				}
				//}
			}
			else if (CmFinoFIX.SubscriberStatus_PendingRetirement.intValue() == subscriberMDN.getStatus()) {
				//Grave the subscriber if he is in retired state for a period of TIME_TO_GRAVE_OF_RETIRED
				if (((now.getTime() - subscriberMDN.getStatusTime().getTime()) > TIME_TO_GRAVE_OF_RETIRED) || 
						(subscriberMDN.getIsForceCloseRequested() != null && subscriberMDN.getIsForceCloseRequested().booleanValue())) {
					
					List<Pocket> srcPocketList = getSubscriberPocketsListWithBalance(subscriberMDN);
					if(srcPocketList != null && srcPocketList.size() > 0){
						boolean moneyMovedSuccessfully = false;
						Pocket destSystemProviderPocket = getDestPocket(systemParametersService.getLong(SystemParameterKeys.RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET));
						SubscriberMDN destSystemProviderMDN=null;
						if(destSystemProviderPocket != null){
							destSystemProviderMDN = destSystemProviderPocket.getSubscriberMDNByMDNID();
						}
						else{
							log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
									+ subscriber.getID() + " due to non availability of system provider pocket and hence the subscriber will not be graved");
							return;
						}
						//The system collector pocket need to be suspense pocket
						if(destSystemProviderPocket.getPocketTemplate().getIsSuspencePocket() != true){
							log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
									+ subscriber.getID() + " as the system provider pocket is not of suspense type and hence the subscriber will not be graved");
							return;
						}
						
						
						TransactionDetails txnDetails= new TransactionDetails();
						txnDetails.setSourceMDN(subscriberMDN.getMDN());
						txnDetails.setDestMDN(destSystemProviderMDN.getMDN());
						//txnDetails.setDestPocketCode("1");
						//txnDetails.setSourcePocketCode("1");
						txnDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
						txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM_INQUIRY);
						txnDetails.setDestinationPocketId(destSystemProviderPocket.getID());
						for(Pocket sourcePocket:srcPocketList){
							moneyMovedSuccessfully = false;
							txnDetails.setSourcePocketCode(String.valueOf(sourcePocket.getPocketTemplate().getType()));
							txnDetails.setDestPocketCode(String.valueOf(destSystemProviderPocket.getPocketTemplate().getType()));
							txnDetails.setSrcPocketId(sourcePocket.getID());
							txnDetails.setSourcePIN("1503");
							txnDetails.setCc(channelCode);
							moneyMovedSuccessfully = moveRetiredSubscriberBalanceMoney(subscriberMDN,sourcePocket,destSystemProviderMDN,destSystemProviderPocket,txnDetails);
							if(moneyMovedSuccessfully){
								log.info("Successfully Moved Balance in EMoney Pockets of Retired Subscriber with subscriber ID --> " 
										+ subscriber.getID() + " to the configured pocket with pocket Id -->" + destSystemProviderPocket.getID());							
							}
							else{
								log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
										+ subscriber.getID() + " and hence the subscriber will not be graved");
								return;
							}
						}
					}
					
					Integer status = mdnRetireService.retireMDN(subscriberMDN.getID());
					
					if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						if (CmFinoFIX.ResolveAs_success.equals(status)) {
							log.info("Moved to Retired state the Subscriber with id -->" + subscriber.getID());
							Partner partner = getPartnerForSubscriber(subscriber);
							if (partner != null) {
								partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_Retired);
								partnerService.savePartner(partner);
								retireServices(partner);
							}
						}
					}
				}
			}
			else if(CmFinoFIX.SubscriberStatus_Retired.intValue() == subscriberMDN.getStatus()){
				if ((now.getTime() - subscriberMDN.getStatusTime().getTime()) > TIME_TO_MOVE_TO_NATIONALTREASURY){
					log.info("Trying to move money from system collector pocket to National Treasury for subscriber ID -->" 
							+ subscriber.getID()) ;
					Pocket srcSystemProviderPocket = getSrcSystemProvidePocket(systemParametersService.getLong(SystemParameterKeys.RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET));
					SubscriberMDN srcSystemProviderMDN=null;
					if(srcSystemProviderPocket != null){
						srcSystemProviderMDN = srcSystemProviderPocket.getSubscriberMDNByMDNID();
					}
					else{
						log.info("Unable to get System Provider MDN, Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
								+ subscriber.getID()) ;
						return;
					}
					if(srcSystemProviderPocket.getPocketTemplate().getIsSuspencePocket() != true){
						log.info("Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
								+ subscriber.getID() + " as the system provider pocket is not of suspense type");
						return;
					}
				
					//String partnerCode = systemParametersService.getString(SystemParameterKeys.NATIONAL_TREASURY_PARTNER_CODE);
					//SubscriberMDN destMDN = getDestSubscriberMDN(partnerCode);
					Pocket destNationalTreasuryPocket = getDestPocket(systemParametersService.getLong(SystemParameterKeys.NATIONAL_TREASURY_POCKET));
					SubscriberMDN destMDN = null;
					if(destNationalTreasuryPocket != null){
						destMDN = destNationalTreasuryPocket.getSubscriberMDNByMDNID();
					}
					else{
						log.info("Failed to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" 
								+ subscriber.getID()) ;
						return;
					}
					
					if(destMDN == null){
						log.info("Failed to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" 
								+ subscriber.getID()) ;
						return;
					}
					
					MoneyClearanceGravedQuery mcgQuery = new MoneyClearanceGravedQuery();
					mcgQuery.setMdnId(subscriberMDN.getID());
					List<MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(mcgQuery);
					log.info("Getting Money Clearance information to move to National Treasury for subscriber ID -->" 
							+ subscriber.getID()) ;
					for(MoneyClearanceGraved mcg:lst){
						if(mcg.getMCStatus().intValue() ==  CmFinoFIX.MCStatus_INITIALIZED.intValue()){
							TransactionDetails txnDetails = new TransactionDetails();
							txnDetails.setSourceMDN(srcSystemProviderMDN.getMDN());
							txnDetails.setSrcPocketId(srcSystemProviderPocket.getID());
							txnDetails.setDestMDN(subscriberService.normalizeMDN(destMDN.getMDN()));
							txnDetails.setSourcePIN("1234");
							txnDetails.setAmount(mcg.getAmount());
							txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
							txnDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
							txnDetails.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
							txnDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
							txnDetails.setDestinationPocketId(destNationalTreasuryPocket.getID());
							txnDetails.setCc(channelCode);
							log.info("Intializing money moment from system collector pocket to National Treasury for subscriber ID -->" 
									+ subscriber.getID()) ;
							log.info("Got subscriberMDN from money clearance graved  -->" 
									+ mcg.getSubscriberMDNByMDNID()) ;
							boolean moneyMovedSuccessfully = moveMoneyToNationalTreasury( txnDetails, mcg.getSubscriberMDNByMDNID());

							if(moneyMovedSuccessfully){
								log.info("Successfully moved money from system collector pocket to National Treasury for subscriber ID -->" 
										+ subscriber.getID()) ;						
							}
							else{
								log.info("Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
										+ subscriber.getID()) ;
								return;
							}				
						}
					}
				}
			}
			else if (CmFinoFIX.SubscriberStatus_Active.intValue() == subscriberMDN.getStatus()) {
				inActivateActiveSubscriber(subscriber,subscriberMDN,subscriberStatusEvent);
			}
		}
	}	
	}
	
	/**
	 * Returns the Partner for the given Subscriber
	 * 
	 * @param subscriber
	 * @return
	 */
	
	private Partner getPartnerForSubscriber(Subscriber subscriber) {
		Partner partner = null;
		if (subscriber != null) {
			Set<Partner> partners = subscriber.getPartnerFromSubscriberID();
			if ((partners != null) && (partners.size()!=0)) { 
				partner = partners.iterator().next();
			}
		}
		return partner;
	}
	
	/**
	 * Change the Status of the Partner Services to Retired.
	 * 
	 * @param objPartner
	 */
	
	private void retireServices(Partner objPartner) {
		Set<PartnerServices> partnerServices = objPartner.getPartnerServicesFromPartnerID();
		if (CollectionUtils.isNotEmpty(partnerServices)) {
			for(PartnerServices ps:partnerServices){
				if (CmFinoFIX.PartnerServiceStatus_PendingRetirement.intValue() == ps.getStatus()) {
					ps.setStatus(CmFinoFIX.PartnerServiceStatus_Retired);
					partnerServicesService.save(ps);			
				}
			}
		}
	}
	
	/**
	 * Change the Status of the Active Subscriber to InActive .
	 * @param subscriber, subscriberMDN
	 */
	private void inActivateActiveSubscriber(Subscriber subscriber,SubscriberMDN subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		ExcludeSubscriberLifeCycle eslcSub = excludeSubscriberLifeCycleService.getBySubscriberMDN(subscriberMDN);
		if(eslcSub != null){
			log.info("We can't inactivate this subscriber because he is a part of excludeSubscriberLifeCycle and the subscriber id is -->" + subscriber.getID());
			return ;
		}
		if(CmFinoFIX.SubscriberType_Subscriber.equals(subscriber.getType())){
			inActivateActiveSubscriberOfNoFundMovement(subscriber,subscriberMDN,subscriberStatusEvent);
		}
		else{
			log.info("Partner is not inactivated due to no Fund movement for Subscriber ID --> " + subscriber.getID());
		}
		if(CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus())){
			inActivateActiveSubscriberOfNoActivity(subscriber,subscriberMDN,subscriberStatusEvent);
		}
	}
	
	private void inActivateActiveSubscriberOfNoActivity(Subscriber subscriber,SubscriberMDN subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		Timestamp now = new Timestamp();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		
		sctlQuery.setSourceMdn(subscriberMDN.getMDN());
		List<ServiceChargeTransactionLog> srcLst = serviceChargeTransactionsLogService.get(sctlQuery);
		
		sctlQuery.setSourceMdn(null);
		sctlQuery.setDestMdn(subscriberMDN.getMDN());
		List<ServiceChargeTransactionLog> dstLst = serviceChargeTransactionsLogService.get(sctlQuery);
		
		ServiceChargeTransactionLog srcLastTransaction = null;
		if(srcLst.size() != 0 ){
			srcLastTransaction = srcLst.get(srcLst.size()-1);
		}
		ServiceChargeTransactionLog dstLastTransaction = null;
		if(dstLst.size() != 0 ){
			dstLastTransaction = dstLst.get(dstLst.size()-1);
		}
		
		if((srcLst.size() == 0) && (dstLst.size() == 0)){
			if((now.getTime()-subscriber.getCreateTime().getTime()) > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY){
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.setStatusTime(now);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatusTime(now);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				subscriberService.saveSubscriber(subscriber);
				if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
					Partner partner = getPartnerForSubscriber(subscriber);
					if (partner != null) {
						partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
						partnerService.savePartner(partner);
					}
				}
				log.info("Subscriber Status is changed to Inactive because of no activity with id --> " + subscriber.getID());
				subscriberStatusEvent.setProcessingStatus(true);
				subscriberStatusEventService.save(subscriberStatusEvent);
				subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
			}else if(subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType()))){
				subscriberStatusEvent.setPickUpDateTime(new Timestamp(subscriber.getCreateTime().getTime() + TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		}else{
			Long lastTransactionTime = now.getTime();
			if(srcLastTransaction != null && dstLastTransaction != null){
				lastTransactionTime = (srcLastTransaction.getCreateTime()
						.getTime() > dstLastTransaction.getCreateTime()
						.getTime()) ? srcLastTransaction.getCreateTime()
						.getTime() : dstLastTransaction.getCreateTime()
						.getTime();
			}else if(srcLastTransaction != null){
				lastTransactionTime = srcLastTransaction.getCreateTime().getTime();
			}else if(dstLastTransaction != null){
				lastTransactionTime = dstLastTransaction.getCreateTime().getTime();
			}
			
			if((now.getTime()-lastTransactionTime) > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY){
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.setStatusTime(now);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatusTime(now);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				subscriberService.saveSubscriber(subscriber);
				if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
					Partner partner = getPartnerForSubscriber(subscriber);
					if (partner != null) {
						partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
						partnerService.savePartner(partner);
					}
				}
				subscriberStatusEvent.setProcessingStatus(true);
				subscriberStatusEventService.save(subscriberStatusEvent);
				subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
				log.info("Subscriber Status is changed to Inactive  because of no activity with id --> " + subscriber.getID());
			}else if(subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType()))){
				subscriberStatusEvent.setPickUpDateTime(new Timestamp(lastTransactionTime + TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		}
	}
	
	private void inActivateActiveSubscriberOfNoFundMovement(Subscriber subscriber,SubscriberMDN subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		Timestamp now = new Timestamp();
		CommodityTransferQuery ctQuery = new CommodityTransferQuery();
		
		try{
			ctQuery.setSourceDestnMDN(subscriberMDN.getMDN());
			ctQuery.setTransferStatus(CmFinoFIX.TransactionsTransferStatus_Completed.intValue());
			ctQuery.setLastUpdateTimeGE(new Date(now.getTime() - DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
			List<CommodityTransfer> ctLst = commodityTransferService.get(ctQuery);
			
			if((ctLst.size() == 0)){
				if((now.getTime()-subscriber.getLastUpdateTime().getTime()) > DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT){
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_NoFundMovement);
					subscriberMDN.setStatusTime(now);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_NoFundMovement);
					subscriber.setStatusTime(now);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					subscriberStatusEvent.setProcessingStatus(true);
					subscriberStatusEventService.save(subscriberStatusEvent);
					if (subscriber.getType() != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerStatus(CmFinoFIX.SubscriberStatus_InActive);
							partnerService.savePartner(partner);
						}
					}
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					log.info("Subscriber Status is changed to Inactive because of no Fund Movement with id --> " + subscriber.getID());
				}else{
					subscriberStatusEvent.setPickUpDateTime(new Timestamp(subscriber.getLastUpdateTime().getTime() + DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
					subscriberStatusEventService.save(subscriberStatusEvent);
				}
			}else{
				subscriberStatusEvent.setPickUpDateTime(new Timestamp(now.getTime() + DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		
		}catch(Exception e){
			log.error("Error while InActivating ActiveSubscriber",e);
		}		
	}
		
	private List<Pocket> getSubscriberPocketsListWithBalance(
			SubscriberMDN subscriberMDN) {
		List<Pocket> pkList = null;
		List<Pocket> pkListWithBalance = new ArrayList<Pocket>();
		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setMdnIDSearch(subscriberMDN.getID());
		//pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
		//pocketQuery.setPocketType(CmFinoFIX.PocketType_LakuPandai);
		pkList = pocketService.get(pocketQuery);
		if (pkList != null) {
			for (Pocket pk : pkList) {
				if (pk.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0) {
					pkListWithBalance.add(pk);
				}
			}
		}
		return pkListWithBalance;
	}

	private boolean moveRetiredSubscriberBalanceMoney(SubscriberMDN sourceMDN,
			Pocket sourcePocket, SubscriberMDN destMDN, Pocket destPocket,
			TransactionDetails txnDetails) {
		XMLResult inquiryResult;
		XMLResult confirmResult;
		inquiryResult = sendMoneyTransferInquiry(sourceMDN, sourcePocket,
				destMDN, destPocket, txnDetails);

		if (!isMoneyTransferInquirySuccessfull(inquiryResult)) {
			log.info("Enquiry for money transfer failed with notification code :"
					+ (inquiryResult.getCode()!=null?inquiryResult.getCode():inquiryResult.getNotificationCode())
					+ " for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getID()
					+ " and Pocket ID -->"
					+ sourcePocket.getID());
			return false;
		}

		MoneyClearanceGraved mcg = createAndSaveMoneyClearanceObject(sourceMDN,
				sourcePocket, destMDN, destPocket, inquiryResult);
		if (mcg == null) {
			log.info("Error in creating Money Clearance Object after enquiry for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getID()
					+ " and Pocket ID -->"
					+ sourcePocket.getID());
			return false;
		}
		
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM);
		confirmResult = sendMoneyTransferConfirm(sourceMDN, sourcePocket,
				destMDN, destPocket, inquiryResult, txnDetails);

		if (!isMoneyTransferConfirmSuccessfull(confirmResult)) {
			log.info("Confirm for money transfer failed with notification code :"
					+ confirmResult.getCode()
					+ " for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getID());
			return false;
		}

		updateMoneyClearanceObjectStatus(mcg);
		
		log.info("Confirm is successfull for money transfer of Retired subscriber with ID -->"
				+ sourceMDN.getSubscriber().getID()
				+ " and Pocket ID -->"
				+ sourcePocket.getID());

		log.info("Retired Subscriber with ID -->"
				+ sourceMDN.getSubscriber().getID()
				+ " and balance in Pocket ID --> "
				+ sourcePocket.getID()
				+ " is successfully transferd to system configured Pocket ID --> "
				+ destPocket.getID() + " and the money Clearance ID is -->"
				+ mcg.getID());
		return true;

	}

	private XMLResult sendMoneyTransferInquiry(SubscriberMDN srcMDN,
			Pocket sourcePocket, SubscriberMDN destMDN, Pocket destPocket,
			TransactionDetails txnDetails) {

		BigDecimal sourceMDNBalanceAmount = sourcePocket.getCurrentBalance();
		String sourceMessage = ServiceAndTransactionConstants.MESSAGE_MOVE_RETIRED_SUBSCRIBER_BALANCE_MONEY;
		XMLResult xmlResult = null;

		if (StringUtils.isBlank(sourceMessage)) {
			sourceMessage = ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER;
		}

		txnDetails.setDestMDN(subscriberService.normalizeMDN(destMDN.getMDN()));
		txnDetails.setAmount(sourceMDNBalanceAmount);

		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);

		log.info("Inquiry Response for Retired Subscriber with ID -->:"
				+ srcMDN.getSubscriber().getID() + "is: " + xmlResult);
		return xmlResult;
	}

	private XMLResult sendMoneyTransferInquiry(TransactionDetails txnDetails) {

		log.info("SubscriberLifeCycleServiceImpl::sendMoneyTransferInquiry :Begin");
		XMLResult xmlResult = null;
		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);

		log.info("Inquiry Response for Retired Subscriber " + " got result: "
				+ xmlResult);
		log.info("SubscriberLifeCycleServiceImpl::sendMoneyTransferInquiry :End");
		return xmlResult;
	}

	private XMLResult sendMoneyTransferConfirm(SubscriberMDN srcMDN,
			Pocket sourcePocket, SubscriberMDN destMDN, Pocket destPocket,
			XMLResult inquiryResult, TransactionDetails txnDetails) {
		log.info("Sending Confirm for money transfer of Retired subscriber with ID -->"
				+ srcMDN.getSubscriber().getID());
		XMLResult xmlResult = null;
		txnDetails.setTransferId(inquiryResult.getTransferID());
		txnDetails.setParentTxnId(inquiryResult.getParentTransactionID());

		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);

		log.info("Confirm Response for Retired Subscriber with ID -->:"
				+ srcMDN.getSubscriber().getID() + "is: " + xmlResult);
		return xmlResult;
	}

	private Pocket getDestPocket(Long pocketID) {
		Pocket destPocket = pocketService.getById(pocketID);
		return destPocket;
	}

	private SubscriberMDN getDestSubscriberMDN(String partnerCode) {
		PartnerQuery query = new PartnerQuery();
		query.setPartnerCode(partnerCode);
		List<Partner> lst = partnerService.get(query);
		Partner destPartner = lst.get(0);
		
		if(destPartner == null){
			log.info("Destination Partner not found for the given partner code --> " + partnerCode);
			return null;
		}
		Set<SubscriberMDN> set = destPartner.getSubscriber()
				.getSubscriberMDNFromSubscriberID();
		PartnerValidator pValidator = new PartnerValidator(set.iterator()
				.next());

		log.info("validating the partner");
		Validator validator = new Validator();
		validator.addValidator(pValidator);
		Integer validationResult = validator.validateAll();
		if (!validationResult.equals(CmFinoFIX.ResponseCode_Success)) {
			log.info("partner validation failed.result=" + validationResult);
			return null;
		}
		SubscriberMDN destMDN = pValidator.getSubscriberMDN();
		return destMDN;
	}

	private boolean isMoneyTransferInquirySuccessfull(XMLResult inquiryResult) {
		if (inquiryResult != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt
					.toString().equals(inquiryResult.getCode())) {
				return true;
			}
		}
		return false;
	}

	private void updateMoneyClearanceObjectStatus(MoneyClearanceGraved mcg){
		mcg.setMCStatus(CmFinoFIX.MCStatus_INITIALIZED);
		moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
		log.info("Successfully updated money clerance object status to initialized for Retired Subscriber with Money Clearance ID -->"
				+ mcg.getID());
	}
	
	
	
	
	private MoneyClearanceGraved createAndSaveMoneyClearanceObject(SubscriberMDN sourceMDN,
			Pocket sourcePocket, SubscriberMDN destMDN, Pocket destPocket,
			XMLResult confirmResult) {
		log.info("SubscriberLifeCycleServiceImpl::createAndSaveMoneyClearanceObject :Begin");
		log.info("Trying to save money clerance object for Retired Subscriber with ID -->"
				+ sourceMDN.getSubscriber().getID());
		
		MoneyClearanceGravedQuery query = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		query.setMdnId(sourceMDN.getID());
		query.setPocketId(sourcePocket.getID());
		List <MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(query);
		if(lst.size() > 0){
			mcg = lst.get(0);
		}else{
			mcg = new MoneyClearanceGraved();
		}
		
		ServiceChargeTransactionLog sctl = new ServiceChargeTransactionLog();
		if (confirmResult != null) {
			log.info("sctl ID from Confirm --> " + confirmResult.getSctlID()
					+ " for Retired Subscriber with ID --> "
					+ sourceMDN.getSubscriber().getID()
					+ " and Pocket ID is -->" + sourcePocket.getID());
			sctl = serviceChargeTransactionsLogService.getById(confirmResult.getSctlID());
			mcg.setSubscriberMDNByMDNID(sourceMDN);
			mcg.setPocket(sourcePocket);
			mcg.setServiceChargeTransactionLogBySctlId(sctl);
			mcg.setAmount(confirmResult.getCreditAmount());
			mcg.setMCStatus(-1);

			moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
			log.info("Successfully saved save money clerance object for Retired Subscriber with ID -->"
					+ sourceMDN.getSubscriber().getID()
					+ " and Pocket ID is -->" + sourcePocket.getID());
			log.info("SubscriberLifeCycleServiceImpl::createAndSaveMoneyClearanceObject :End");
			return mcg;			
		}
		return null;
	}

	private boolean moveMoneyToNationalTreasury(TransactionDetails txnDetails,
			SubscriberMDN mdn) {
		XMLResult inquiryResult;
		XMLResult confirmResult;
		log.info("SubscriberLifeCycleServiceImpl::moveMoneyToNationalTreasury :Begin");
		log.info("Sending Money transfer Inquiry for subscriber MDN ID -->" + mdn.getID());
		inquiryResult = sendMoneyTransferInquiry(txnDetails);
		if (!isMoneyTransferInquirySuccessfull(inquiryResult)) {
			log.info("Inquiry for money transfer failed with notification code :"
					+ inquiryResult.getNotificationCode());
			return false;
		}

		txnDetails.setConfirmString("true");
		txnDetails.setTransferId(inquiryResult.getTransferID());
		txnDetails.setParentTxnId(inquiryResult.getParentTransactionID());
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY);
		log.info("Sending Money transfer Confirm for subscriber MDN ID -->" + mdn.getID());
		confirmResult = sendMoneyTransferConfirm(txnDetails);

		if (!isMoneyTransferConfirmSuccessfull(confirmResult)) {
			log.info("Confirm for money transfer failed with notification code :"
					+ confirmResult.getCode() + " while moving to National Treasury for subscriber--> " + mdn.getID());
			return false;
		}

		log.info("Money transfer successful with notification code :"
				+ confirmResult.getCode() + " for retired subscriber ");

		Long moneyClearanceID = updateMCGRecord(confirmResult, mdn);

		if (moneyClearanceID.equals(new Long(-1L))) {
			log.info("Error in updating Money Clearance Object after transferring the amount to National Treasury of retired subscriber --> " + mdn.getID());
			return false;
		}

		log.info("SubscriberLifeCycleServiceImpl::moveMoneyToNationalTreasury :End");
		return true;

	}

	private Pocket getSrcSystemProvidePocket(long pocketID) {
		Pocket srcPocket = pocketService.getById(pocketID);
		return srcPocket;
	}

	private XMLResult sendMoneyTransferConfirm(TransactionDetails txnDetails) {
		log.info("SubscriberLifeCycleServiceImpl::sendMoneyTransferConfirm :Begin");
		log.info("Sending TransferConfirm for Settlement of Retired subscriber ");
		XMLResult xmlResult = null;
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER);
		txnDetails.setCc(channelCode);

		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);
		log.info("TransferConfirm return code for Settlement of Retired subscriber "
				+ " and the result is: " + xmlResult);
		log.info("SubscriberLifeCycleServiceImpl::sendMoneyTransferConfirm :End");
		return xmlResult;
	}

	private boolean isMoneyTransferConfirmSuccessfull(XMLResult confirmResult) {
		log.info("SubscriberLifeCycleServiceImpl::isMoneyTransferConfirmSuccessfull :Begin");
		if (confirmResult != null) {
			if (CmFinoFIX.NotificationCode_BankAccountToBankAccountCompletedToSenderMDN
					.toString().equals(confirmResult.getCode())
					|| CmFinoFIX.NotificationCode_EMoneytoEMoneyCompleteToSender
							.toString().equals(confirmResult.getCode())) {
				log.info("SubscriberLifeCycleServiceImpl::isMoneyTransferConfirmSuccessfull :End");
				return true;
			}
		}
		return false;
	}

	private Long updateMCGRecord(XMLResult confirmResult, SubscriberMDN mdn) {
		log.info("SubscriberLifeCycleServiceImpl::updateMCGRecord :Begin");
		log.info("Trying to save money clerance object for Retired Subscriber with ID -->"
				+ mdn.getID());
		MoneyClearanceGravedQuery query = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		query.setMdnId(mdn.getID());
		List<MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(query);
		if (lst.size() > 0) {
			mcg = lst.get(0);
		} else {
			mcg = new MoneyClearanceGraved();
		}

		ServiceChargeTransactionLog sctl = new ServiceChargeTransactionLog();
		if (confirmResult != null) {
			log.info("sctl ID from Confirm --> " + confirmResult.getSctlID()
					+ " for Retired Subscriber with ID --> " + mdn.getID());
			sctl = serviceChargeTransactionsLogService.getById(confirmResult.getSctlID());
			SubscriberMDN subscriberMDN = subscriberMdnService.getById(mdn.getID());
			SubscriberMDN destMDN = subscriberMdnService.getByMDN(confirmResult
					.getDestinationMDN());
			mcg.setSubscriberMDNByMDNID(subscriberMDN);
			mcg.setServiceChargeTransactionLogByRefundSctlID(sctl);
			mcg.setMCStatus(CmFinoFIX.MCStatus_MOVED_TO_NATIONAL_TREASURY);
			mcg.setAmount(confirmResult.getCreditAmount());
			mcg.setSubscriberMDNByRefundMDNID(destMDN);
			mcg.setPocketByRefundPocketID(confirmResult.getSourcePocket());
			moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
			log.info("Successfully saved save money clerance object for Retired Subscriber with ID -->"
					+ mdn.getID());
			log.info("SubscriberLifeCycleServiceImpl::updateMCGRecord :End");
			return mcg.getID();
		}		
		return new Long(-1L);
	}
	/**
	 * Returns the SubscriberMDN for the given Subscriber
	 * 
	 * @param subscriber
	 * @return
	 */
	
	private SubscriberMDN getSubscriberMDNForSubscriber(Subscriber subscriber) {
		SubscriberMDN subscriberMDN = null;
		if (subscriber != null) {
			Set<SubscriberMDN> subscriberMDNs = subscriber.getSubscriberMDNFromSubscriberID();
			if ((subscriberMDNs != null) && (subscriberMDNs.size()!=0)) { 
				subscriberMDN = subscriberMDNs.iterator().next();
			}
		}
		return subscriberMDN;
	}
	
	
}
