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
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.stereotype.Service;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.constants.SystemParameterKeys;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.dao.query.MoneyClearanceGravedQuery;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.ExcludeSubscriberLc;
import com.mfino.domain.MoneyClearanceGraved;
import com.mfino.domain.Partner;
import com.mfino.domain.PartnerServices;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
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
//			List<SubscriberMdn> lst = subscriberMdnService.getByQuery(query);
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

//			List<SubscriberMdn> lst = subscriberMdnService.getByQuery(query);
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
			SubscriberDAO subscriberDAO = DAOFactory.getInstance().getSubscriberDAO();
			Subscriber subscriberById = subscriberDAO.getById(subscriberStatusEvent.getSubscriberid().longValue());
			SubscriberMdn subscriberMDN = getSubscriberMDNForSubscriber(subscriberById);
		if (subscriberMDN != null) {
			subscriber = subscriberMDN.getSubscriber();
			log.info("Checking the Subscriber MDN with id --> " + subscriberMDN.getId() + " And status is --> " + subscriber.getStatus());
			log.info("Checking the Subscriber with id --> " + subscriber.getId() + " And status is --> " + subscriber.getStatus());
			if (subscriber != null && Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType()) 
						&& subscriber.getActivationtime() == null && CmFinoFIX.UpgradeState_Approved.intValue() == subscriber.getUpgradestate()) {
					if ( ((now.getTime() - subscriber.getApproveorrejecttime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) && 
							((now.getTime() - subscriber.getStatustime().getTime()) > TIME_TO_SUSPEND_OF_NO_ACTIVATION) ) {
						subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
						subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
						subscriberMDN.setStatustime(now);
						subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
						subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
						subscriber.setStatustime(now);
						subscriberMdnService.saveSubscriberMDN(subscriberMDN);
						subscriberService.saveSubscriber(subscriber);
						
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Suspend);
							partnerService.savePartner(partner);
						}
						log.info("Suspended the Partner with subscriber id --> " + subscriber.getId());
						subscriberStatusEvent.setProcessingstatus((short) 1);
						subscriberStatusEventService.save(subscriberStatusEvent);
						subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					}
			}else if (CmFinoFIX.SubscriberStatus_InActive.intValue() == subscriberMDN.getStatus()) {
				if ((now.getTime() - subscriber.getStatustime().getTime()) > TIME_TO_SUSPEND_OF_INACTIVE) {
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					subscriberMDN.setStatustime(now);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					subscriber.setStatustime(now);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					
					if (Long.valueOf(subscriber.getType() )!= null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Suspend);
							partnerService.savePartner(partner);
						}
					}
					subscriberStatusEvent.setProcessingstatus((short)1);
					subscriberStatusEventService.save(subscriberStatusEvent);
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					log.info("Suspended the Subscriber with id --> " + subscriber.getId());
				}
			}
			else if (CmFinoFIX.SubscriberStatus_Suspend.intValue() == subscriberMDN.getStatus()) {
				//Retire the subscriber if he remains suspended for a period of TIME_TO_RETIRE_OF_SUSPENDED
				//if (isSuspendedSubscriberEligibleTobeRetired(now,subscriber,subscriberMDN)) {
				if ((now.getTime() - subscriber.getStatustime().getTime()) > TIME_TO_RETIRE_OF_SUSPENDED){
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subscriberMDN.setStatustime(now);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_None);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					subscriber.setStatustime(now);
					// change the pocket status to pending retirement
					subscriberService.retireSubscriber(subscriberMDN);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					if (Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
							partnerService.retireServices(partner);
							partnerService.savePartner(partner);
						}
					}
					log.info("Moved to Pending retired the subscriber with id -->" + subscriber.getId());
					subscriberStatusEvent.setProcessingstatus((short)1);
					subscriberStatusEventService.save(subscriberStatusEvent);
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);

				}
				//}
			}
			else if (CmFinoFIX.SubscriberStatus_PendingRetirement.intValue() == subscriberMDN.getStatus()) {
				//Grave the subscriber if he is in retired state for a period of TIME_TO_GRAVE_OF_RETIRED
				if (((now.getTime() - subscriberMDN.getStatustime().getTime()) > TIME_TO_GRAVE_OF_RETIRED) || 
						(subscriberMDN.getIsforcecloserequested() != null && subscriberMDN.getIsforcecloserequested() != 0)) {
					
					List<Pocket> srcPocketList = getSubscriberPocketsListWithBalance(subscriberMDN);
					if(srcPocketList != null && srcPocketList.size() > 0){
						boolean moneyMovedSuccessfully = false;
						Pocket destSystemProviderPocket = getDestPocket(systemParametersService.getLong(SystemParameterKeys.RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET));
						SubscriberMdn destSystemProviderMDN=null;
						if(destSystemProviderPocket != null){
							destSystemProviderMDN = destSystemProviderPocket.getSubscriberMdn();
						}
						else{
							log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
									+ subscriber.getId() + " due to non availability of system provider pocket and hence the subscriber will not be graved");
							return;
						}
						//The system collector pocket need to be suspense pocket
						if(destSystemProviderPocket.getPocketTemplate().getIssuspencepocket() != (short) 1){
							log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
									+ subscriber.getId() + " as the system provider pocket is not of suspense type and hence the subscriber will not be graved");
							return;
						}
						
						
						TransactionDetails txnDetails= new TransactionDetails();
						txnDetails.setSourceMDN(subscriberMDN.getMdn());
						txnDetails.setDestMDN(destSystemProviderMDN.getMdn());
						//txnDetails.setDestPocketCode("1");
						//txnDetails.setSourcePocketCode("1");
						txnDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
						txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM_INQUIRY);
						txnDetails.setDestPocketId(destSystemProviderPocket.getId().toPlainString());
						for(Pocket sourcePocket:srcPocketList){
							moneyMovedSuccessfully = false;
							txnDetails.setSourcePocketCode(String.valueOf(sourcePocket.getPocketTemplate().getType()));
							txnDetails.setDestPocketCode(String.valueOf(destSystemProviderPocket.getPocketTemplate().getType()));
							txnDetails.setSrcPocketId(sourcePocket.getId().longValue());
							txnDetails.setSourcePIN("1503");
							txnDetails.setCc(channelCode);
							moneyMovedSuccessfully = moveRetiredSubscriberBalanceMoney(subscriberMDN,sourcePocket,destSystemProviderMDN,destSystemProviderPocket,txnDetails);
							if(moneyMovedSuccessfully){
								log.info("Successfully Moved Balance in EMoney Pockets of Retired Subscriber with subscriber ID --> " 
										+ subscriber.getId() + " to the configured pocket with pocket Id -->" + destSystemProviderPocket.getId());							
							}
							else{
								log.info("Failed to move balance in all/some of the EMoney Pockets of Retired Subscriber with subscriber ID --> " 
										+ subscriber.getId() + " and hence the subscriber will not be graved");
								return;
							}
						}
					}
					
					Integer status = mdnRetireService.retireMDN(subscriberMDN.getId().longValue());
					
					if (Long.valueOf(subscriber.getType() )!= null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						if (CmFinoFIX.ResolveAs_success.equals(status)) {
							log.info("Moved to Retired state the Subscriber with id -->" + subscriber.getId());
							Partner partner = getPartnerForSubscriber(subscriber);
							if (partner != null) {
								partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_Retired);
								partnerService.savePartner(partner);
								retireServices(partner);
							}
						}
					}
				}
			}
			else if(CmFinoFIX.SubscriberStatus_Retired.intValue() == subscriberMDN.getStatus()){
				if ((now.getTime() - subscriberMDN.getStatustime().getTime()) > TIME_TO_MOVE_TO_NATIONALTREASURY){
					log.info("Trying to move money from system collector pocket to National Treasury for subscriber ID -->" 
							+ subscriber.getId()) ;
					Pocket srcSystemProviderPocket = getSrcSystemProvidePocket(systemParametersService.getLong(SystemParameterKeys.RETIRED_SUBSCRIBER_SYSTEM_COLLECTOR_POCKET));
					SubscriberMdn srcSystemProviderMDN=null;
					if(srcSystemProviderPocket != null){
						srcSystemProviderMDN = srcSystemProviderPocket.getSubscriberMdn();
					}
					else{
						log.info("Unable to get System Provider MDN, Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
								+ subscriber.getId()) ;
						return;
					}
					if(srcSystemProviderPocket.getPocketTemplate().getIssuspencepocket() != 1){
						log.info("Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
								+ subscriber.getId() + " as the system provider pocket is not of suspense type");
						return;
					}
				
					//String partnerCode = systemParametersService.getString(SystemParameterKeys.NATIONAL_TREASURY_PARTNER_CODE);
					//SubscriberMdn destMDN = getDestSubscriberMDN(partnerCode);
					Pocket destNationalTreasuryPocket = getDestPocket(systemParametersService.getLong(SystemParameterKeys.NATIONAL_TREASURY_POCKET));
					SubscriberMdn destMDN = null;
					if(destNationalTreasuryPocket != null){
						destMDN = destNationalTreasuryPocket.getSubscriberMdn();
					}
					else{
						log.info("Failed to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" 
								+ subscriber.getId()) ;
						return;
					}
					
					if(destMDN == null){
						log.info("Failed to move money from system collector pocket to National Treasury as pocket code is not set for National Treasury in System Parameters for subscriber ID -->" 
								+ subscriber.getId()) ;
						return;
					}
					
					MoneyClearanceGravedQuery mcgQuery = new MoneyClearanceGravedQuery();
					mcgQuery.setMdnId(subscriberMDN.getId().longValue());
					List<MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(mcgQuery);
					log.info("Getting Money Clearance information to move to National Treasury for subscriber ID -->" 
							+ subscriber.getId()) ;
					for(MoneyClearanceGraved mcg:lst){
						if(mcg.getMcstatus() ==  CmFinoFIX.MCStatus_INITIALIZED.intValue()){
							TransactionDetails txnDetails = new TransactionDetails();
							txnDetails.setSourceMDN(srcSystemProviderMDN.getMdn());
							txnDetails.setSrcPocketId(srcSystemProviderPocket.getId().longValue());
							txnDetails.setDestMDN(subscriberService.normalizeMDN(destMDN.getMdn()));
							txnDetails.setSourcePIN("1234");
							txnDetails.setAmount(mcg.getAmount());
							txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
							txnDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
							txnDetails.setSourceMessage(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_TREASURY_INQUIRY);
							txnDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
							txnDetails.setDestinationPocketId(destNationalTreasuryPocket.getId().longValue());
							txnDetails.setCc(channelCode);
							log.info("Intializing money moment from system collector pocket to National Treasury for subscriber ID -->" 
									+ subscriber.getId()) ;
							log.info("Got subscriberMDN from money clearance graved  -->" 
									+ mcg.getSubscriberMdnByMdnid()) ;
							boolean moneyMovedSuccessfully = moveMoneyToNationalTreasury( txnDetails, mcg.getSubscriberMdnByMdnid());

							if(moneyMovedSuccessfully){
								log.info("Successfully moved money from system collector pocket to National Treasury for subscriber ID -->" 
										+ subscriber.getId()) ;						
							}
							else{
								log.info("Failed to move money from system collector pocket to National Treasury for subscriber ID -->" 
										+ subscriber.getId()) ;
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
			Set<Partner> partners = subscriber.getPartners();
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
		Set<PartnerServices> partnerServices = objPartner.getPartnerServicesesForPartnerid();
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
	private void inActivateActiveSubscriber(Subscriber subscriber,SubscriberMdn subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		ExcludeSubscriberLc eslcSub = excludeSubscriberLifeCycleService.getBySubscriberMDN(subscriberMDN);
		if(eslcSub != null){
			log.info("We can't inactivate this subscriber because he is a part of excludeSubscriberLifeCycle and the subscriber id is -->" + subscriber.getId());
			return ;
		}
		if(CmFinoFIX.SubscriberType_Subscriber.equals(subscriber.getType())){
			inActivateActiveSubscriberOfNoFundMovement(subscriber,subscriberMDN,subscriberStatusEvent);
		}
		else{
			log.info("Partner is not inactivated due to no Fund movement for Subscriber ID --> " + subscriber.getId());
		}
		if(CmFinoFIX.SubscriberStatus_Active.equals(subscriberMDN.getStatus())){
			inActivateActiveSubscriberOfNoActivity(subscriber,subscriberMDN,subscriberStatusEvent);
		}
	}
	
	private void inActivateActiveSubscriberOfNoActivity(Subscriber subscriber,SubscriberMdn subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		Timestamp now = new Timestamp();
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		
		sctlQuery.setSourceMdn(subscriberMDN.getMdn());
		List<ServiceChargeTxnLog> srcLst = serviceChargeTransactionsLogService.get(sctlQuery);
		
		sctlQuery.setSourceMdn(null);
		sctlQuery.setDestMdn(subscriberMDN.getMdn());
		List<ServiceChargeTxnLog> dstLst = serviceChargeTransactionsLogService.get(sctlQuery);
		
		ServiceChargeTxnLog srcLastTransaction = null;
		if(srcLst.size() != 0 ){
			srcLastTransaction = srcLst.get(srcLst.size()-1);
		}
		ServiceChargeTxnLog dstLastTransaction = null;
		if(dstLst.size() != 0 ){
			dstLastTransaction = dstLst.get(dstLst.size()-1);
		}
		
		if((srcLst.size() == 0) && (dstLst.size() == 0)){
			if((now.getTime()-subscriber.getCreatetime().getTime()) > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY){
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.setStatustime(now);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatustime(now);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				subscriberService.saveSubscriber(subscriber);
				if (Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
					Partner partner = getPartnerForSubscriber(subscriber);
					if (partner != null) {
						partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
						partnerService.savePartner(partner);
					}
				}
				log.info("Subscriber Status is changed to Inactive because of no activity with id --> " + subscriber.getId());
				subscriberStatusEvent.setProcessingstatus((short)1);
				subscriberStatusEventService.save(subscriberStatusEvent);
				subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
			}else if(Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType()))){
				subscriberStatusEvent.setPickupdatetime(new Timestamp(subscriber.getCreatetime().getTime() + TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		}else{
			Long lastTransactionTime = now.getTime();
			if(srcLastTransaction != null && dstLastTransaction != null){
				lastTransactionTime = (srcLastTransaction.getCreatetime()
						.getTime() > dstLastTransaction.getCreatetime()
						.getTime()) ? srcLastTransaction.getCreatetime()
						.getTime() : dstLastTransaction.getCreatetime()
						.getTime();
			}else if(srcLastTransaction != null){
				lastTransactionTime = srcLastTransaction.getCreatetime().getTime();
			}else if(dstLastTransaction != null){
				lastTransactionTime = dstLastTransaction.getCreatetime().getTime();
			}
			
			if((now.getTime()-lastTransactionTime) > TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY){
				subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriberMDN.setStatustime(now);
				subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
				subscriber.setStatustime(now);
				subscriberMdnService.saveSubscriberMDN(subscriberMDN);
				subscriberService.saveSubscriber(subscriber);
				if (Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
					Partner partner = getPartnerForSubscriber(subscriber);
					if (partner != null) {
						partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
						partnerService.savePartner(partner);
					}
				}
				subscriberStatusEvent.setProcessingstatus((short)1);
				subscriberStatusEventService.save(subscriberStatusEvent);
				subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
				log.info("Subscriber Status is changed to Inactive  because of no activity with id --> " + subscriber.getId());
			}else if(Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.equals(subscriber.getType()))){
				subscriberStatusEvent.setPickupdatetime(new Timestamp(lastTransactionTime + TIME_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_ACTIVITY));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		}
	}
	
	private void inActivateActiveSubscriberOfNoFundMovement(Subscriber subscriber,SubscriberMdn subscriberMDN,SubscriberStatusEvent subscriberStatusEvent){
		Timestamp now = new Timestamp();
		CommodityTransferQuery ctQuery = new CommodityTransferQuery();
		
		try{
			ctQuery.setSourceDestnMDN(subscriberMDN.getMdn());
			ctQuery.setTransferStatus(CmFinoFIX.TransactionsTransferStatus_Completed.intValue());
			ctQuery.setLastUpdateTimeGE(new Date(now.getTime() - DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
			List<CommodityTransfer> ctLst = commodityTransferService.get(ctQuery);
			
			if((ctLst.size() == 0)){
				if((now.getTime()-subscriber.getLastupdatetime().getTime()) > DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT){
					subscriberMDN.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriberMDN.setRestrictions(CmFinoFIX.SubscriberRestrictions_NoFundMovement);
					subscriberMDN.setStatustime(now);
					subscriber.setStatus(CmFinoFIX.SubscriberStatus_InActive);
					subscriber.setRestrictions(CmFinoFIX.SubscriberRestrictions_NoFundMovement);
					subscriber.setStatustime(now);
					subscriberMdnService.saveSubscriberMDN(subscriberMDN);
					subscriberService.saveSubscriber(subscriber);
					subscriberStatusEvent.setProcessingstatus((short)1);
					subscriberStatusEventService.save(subscriberStatusEvent);
					if (Long.valueOf(subscriber.getType()) != null && (CmFinoFIX.SubscriberType_Partner.intValue() == subscriber.getType())) {
						Partner partner = getPartnerForSubscriber(subscriber);
						if (partner != null) {
							partner.setPartnerstatus(CmFinoFIX.SubscriberStatus_InActive);
							partnerService.savePartner(partner);
						}
					}
					subscriberStatusNextEventService.upsertNextPickupDateForStatusChange(subscriber,false);
					log.info("Subscriber Status is changed to Inactive because of no Fund Movement with id --> " + subscriber.getId());
				}else{
					subscriberStatusEvent.setPickupdatetime(new Timestamp(subscriber.getLastupdatetime().getTime() + DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
					subscriberStatusEventService.save(subscriberStatusEvent);
				}
			}else{
				subscriberStatusEvent.setPickupdatetime(new Timestamp(now.getTime() + DAYS_TO_INACTIVATE_OF_ACTIVE_SUBSCRIBER_WHEN_NO_FUNDMOVEMENT));
				subscriberStatusEventService.save(subscriberStatusEvent);
			}
		
		}catch(Exception e){
			log.error("Error while InActivating ActiveSubscriber",e);
		}		
	}
		
	private List<Pocket> getSubscriberPocketsListWithBalance(
			SubscriberMdn subscriberMDN) {
		List<Pocket> pkList = null;
		List<Pocket> pkListWithBalance = new ArrayList<Pocket>();
		PocketQuery pocketQuery = new PocketQuery();
		pocketQuery.setMdnIDSearch(subscriberMDN.getId().longValue());
		//pocketQuery.setPocketType(CmFinoFIX.PocketType_SVA);
		//pocketQuery.setPocketType(CmFinoFIX.PocketType_LakuPandai);
		pkList = pocketService.get(pocketQuery);
		if (pkList != null) {
			for (Pocket pk : pkList) {
				if (new BigDecimal(pk.getCurrentbalance()).compareTo(BigDecimal.ZERO) > 0) {
					pkListWithBalance.add(pk);
				}
			}
		}
		return pkListWithBalance;
	}

	private boolean moveRetiredSubscriberBalanceMoney(SubscriberMdn sourceMDN,
			Pocket sourcePocket, SubscriberMdn destMDN, Pocket destPocket,
			TransactionDetails txnDetails) {
		XMLResult inquiryResult;
		XMLResult confirmResult;
		inquiryResult = sendMoneyTransferInquiry(sourceMDN, sourcePocket,
				destMDN, destPocket, txnDetails);

		if (!isMoneyTransferInquirySuccessfull(inquiryResult)) {
			log.info("Enquiry for money transfer failed with notification code :"
					+ (inquiryResult.getCode()!=null?inquiryResult.getCode():inquiryResult.getNotificationCode())
					+ " for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getId()
					+ " and Pocket ID -->"
					+ sourcePocket.getId());
			return false;
		}

		MoneyClearanceGraved mcg = createAndSaveMoneyClearanceObject(sourceMDN,
				sourcePocket, destMDN, destPocket, inquiryResult);
		if (mcg == null) {
			log.info("Error in creating Money Clearance Object after enquiry for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getId()
					+ " and Pocket ID -->"
					+ sourcePocket.getId());
			return false;
		}
		
		txnDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TRANSFER_TO_SYSTEM);
		confirmResult = sendMoneyTransferConfirm(sourceMDN, sourcePocket,
				destMDN, destPocket, inquiryResult, txnDetails);

		if (!isMoneyTransferConfirmSuccessfull(confirmResult)) {
			log.info("Confirm for money transfer failed with notification code :"
					+ confirmResult.getCode()
					+ " for retired subscriber with ID -->"
					+ sourceMDN.getSubscriber().getId());
			return false;
		}

		updateMoneyClearanceObjectStatus(mcg);
		
		log.info("Confirm is successfull for money transfer of Retired subscriber with ID -->"
				+ sourceMDN.getSubscriber().getId()
				+ " and Pocket ID -->"
				+ sourcePocket.getId());

		log.info("Retired Subscriber with ID -->"
				+ sourceMDN.getSubscriber().getId()
				+ " and balance in Pocket ID --> "
				+ sourcePocket.getId()
				+ " is successfully transferd to system configured Pocket ID --> "
				+ destPocket.getId() + " and the money Clearance ID is -->"
				+ mcg.getId());
		return true;

	}

	private XMLResult sendMoneyTransferInquiry(SubscriberMdn srcMDN,
			Pocket sourcePocket, SubscriberMdn destMDN, Pocket destPocket,
			TransactionDetails txnDetails) {

		BigDecimal sourceMDNBalanceAmount = new BigDecimal(sourcePocket.getCurrentbalance());
		String sourceMessage = ServiceAndTransactionConstants.MESSAGE_MOVE_RETIRED_SUBSCRIBER_BALANCE_MONEY;
		XMLResult xmlResult = null;

		if (StringUtils.isBlank(sourceMessage)) {
			sourceMessage = ServiceAndTransactionConstants.MESSAGE_MOBILE_TRANSFER;
		}

		txnDetails.setDestMDN(subscriberService.normalizeMDN(destMDN.getMdn()));
		txnDetails.setAmount(sourceMDNBalanceAmount);

		xmlResult = (XMLResult) moveBalanceInquiryHandler.handle(txnDetails);

		log.info("Inquiry Response for Retired Subscriber with ID -->:"
				+ srcMDN.getSubscriber().getId() + "is: " + xmlResult);
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

	private XMLResult sendMoneyTransferConfirm(SubscriberMdn srcMDN,
			Pocket sourcePocket, SubscriberMdn destMDN, Pocket destPocket,
			XMLResult inquiryResult, TransactionDetails txnDetails) {
		log.info("Sending Confirm for money transfer of Retired subscriber with ID -->"
				+ srcMDN.getSubscriber().getId());
		XMLResult xmlResult = null;
		txnDetails.setTransferId(inquiryResult.getTransferID());
		txnDetails.setParentTxnId(inquiryResult.getParentTransactionID());

		xmlResult = (XMLResult) moveBalanceConfirmHandler.handle(txnDetails);

		log.info("Confirm Response for Retired Subscriber with ID -->:"
				+ srcMDN.getSubscriber().getId() + "is: " + xmlResult);
		return xmlResult;
	}

	private Pocket getDestPocket(Long pocketID) {
		Pocket destPocket = pocketService.getById(pocketID);
		return destPocket;
	}

	private SubscriberMdn getDestSubscriberMDN(String partnerCode) {
		PartnerQuery query = new PartnerQuery();
		query.setPartnerCode(partnerCode);
		List<Partner> lst = partnerService.get(query);
		Partner destPartner = lst.get(0);
		
		if(destPartner == null){
			log.info("Destination Partner not found for the given partner code --> " + partnerCode);
			return null;
		}
		Set<SubscriberMdn> set = destPartner.getSubscriber()
				.getSubscriberMdns();
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
		SubscriberMdn destMDN = pValidator.getSubscriberMDN();
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
		mcg.setMcstatus(CmFinoFIX.MCStatus_INITIALIZED);
		moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
		log.info("Successfully updated money clerance object status to initialized for Retired Subscriber with Money Clearance ID -->"
				+ mcg.getId());
	}
	
	
	
	
	private MoneyClearanceGraved createAndSaveMoneyClearanceObject(SubscriberMdn sourceMDN,
			Pocket sourcePocket, SubscriberMdn destMDN, Pocket destPocket,
			XMLResult confirmResult) {
		log.info("SubscriberLifeCycleServiceImpl::createAndSaveMoneyClearanceObject :Begin");
		log.info("Trying to save money clerance object for Retired Subscriber with ID -->"
				+ sourceMDN.getSubscriber().getId());
		
		MoneyClearanceGravedQuery query = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		query.setMdnId(sourceMDN.getId().longValue());
		query.setPocketId(sourcePocket.getId().longValue());
		List <MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(query);
		if(lst.size() > 0){
			mcg = lst.get(0);
		}else{
			mcg = new MoneyClearanceGraved();
		}
		
		ServiceChargeTxnLog sctl = new ServiceChargeTxnLog();
		if (confirmResult != null) {
			log.info("sctl ID from Confirm --> " + confirmResult.getSctlID()
					+ " for Retired Subscriber with ID --> "
					+ sourceMDN.getSubscriber().getId()
					+ " and Pocket ID is -->" + sourcePocket.getId());
			sctl = serviceChargeTransactionsLogService.getById(confirmResult.getSctlID());
			mcg.setSubscriberMdnByMdnid(sourceMDN);
			mcg.setPocketByPocketid(sourcePocket);
			mcg.setServiceChargeTxnLogBySctlid(sctl);
			mcg.setAmount(confirmResult.getCreditAmount());
			mcg.setMcstatus(-1);

			moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
			log.info("Successfully saved save money clerance object for Retired Subscriber with ID -->"
					+ sourceMDN.getSubscriber().getId()
					+ " and Pocket ID is -->" + sourcePocket.getId());
			log.info("SubscriberLifeCycleServiceImpl::createAndSaveMoneyClearanceObject :End");
			return mcg;			
		}
		return null;
	}

	private boolean moveMoneyToNationalTreasury(TransactionDetails txnDetails,
			SubscriberMdn mdn) {
		XMLResult inquiryResult;
		XMLResult confirmResult;
		log.info("SubscriberLifeCycleServiceImpl::moveMoneyToNationalTreasury :Begin");
		log.info("Sending Money transfer Inquiry for subscriber MDN ID -->" + mdn.getId());
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
		log.info("Sending Money transfer Confirm for subscriber MDN ID -->" + mdn.getId());
		confirmResult = sendMoneyTransferConfirm(txnDetails);

		if (!isMoneyTransferConfirmSuccessfull(confirmResult)) {
			log.info("Confirm for money transfer failed with notification code :"
					+ confirmResult.getCode() + " while moving to National Treasury for subscriber--> " + mdn.getId());
			return false;
		}

		log.info("Money transfer successful with notification code :"
				+ confirmResult.getCode() + " for retired subscriber ");

		Long moneyClearanceID = updateMCGRecord(confirmResult, mdn);

		if (moneyClearanceID.equals(new Long(-1L))) {
			log.info("Error in updating Money Clearance Object after transferring the amount to National Treasury of retired subscriber --> " + mdn.getId());
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

	private Long updateMCGRecord(XMLResult confirmResult, SubscriberMdn mdn) {
		log.info("SubscriberLifeCycleServiceImpl::updateMCGRecord :Begin");
		log.info("Trying to save money clerance object for Retired Subscriber with ID -->"
				+ mdn.getId());
		MoneyClearanceGravedQuery query = new MoneyClearanceGravedQuery();
		MoneyClearanceGraved mcg;
		query.setMdnId(mdn.getId().longValue());
		List<MoneyClearanceGraved> lst = moneyClearanceGravedService.getMoneyClearanceGravedByQuery(query);
		if (lst.size() > 0) {
			mcg = lst.get(0);
		} else {
			mcg = new MoneyClearanceGraved();
		}

		ServiceChargeTxnLog sctl = new ServiceChargeTxnLog();
		if (confirmResult != null) {
			log.info("sctl ID from Confirm --> " + confirmResult.getSctlID()
					+ " for Retired Subscriber with ID --> " + mdn.getId());
			sctl = serviceChargeTransactionsLogService.getById(confirmResult.getSctlID());
			SubscriberMdn subscriberMDN = subscriberMdnService.getById(mdn.getId().longValue());
			SubscriberMdn destMDN = subscriberMdnService.getByMDN(confirmResult
					.getDestinationMDN());
			mcg.setSubscriberMdnByMdnid(subscriberMDN);
			mcg.setServiceChargeTxnLogByRefundsctlid(sctl);
			mcg.setMcstatus(CmFinoFIX.MCStatus_MOVED_TO_NATIONAL_TREASURY);
			mcg.setAmount(confirmResult.getCreditAmount());
			mcg.setSubscriberMdnByRefundmdnid(destMDN);
			mcg.setPocketByRefundpocketid(confirmResult.getSourcePocket());
			moneyClearanceGravedService.saveMoneyClearanceGraved(mcg);
			log.info("Successfully saved save money clerance object for Retired Subscriber with ID -->"
					+ mdn.getId());
			log.info("SubscriberLifeCycleServiceImpl::updateMCGRecord :End");
			return mcg.getId().longValue();
		}		
		return new Long(-1L);
	}
	/**
	 * Returns the SubscriberMdn for the given Subscriber
	 * 
	 * @param subscriber
	 * @return
	 */
	
	private SubscriberMdn getSubscriberMDNForSubscriber(Subscriber subscriber) {
		SubscriberMdn subscriberMDN = null;
		if (subscriber != null) {
			Set<SubscriberMdn> subscriberMDNs = subscriber.getSubscriberMdns();
			if ((subscriberMDNs != null) && (subscriberMDNs.size()!=0)) { 
				subscriberMDN = subscriberMDNs.iterator().next();
			}
		}
		return subscriberMDN;
	}
	
	
}
