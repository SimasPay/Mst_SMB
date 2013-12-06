package com.mfino.scheduler.service.impl;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.hibernate3.HibernateTransactionManager;

import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Service;
import com.mfino.domain.ServiceChargeTransactionLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CmFinoFIX;
import com.mfino.result.XMLResult;
import com.mfino.scheduler.service.TellerEMoneyClearanceService;
import com.mfino.service.ChannelCodeService;
import com.mfino.service.MfinoService;
import com.mfino.service.PartnerService;
import com.mfino.service.ServiceChargeTransactionLogService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionTypeService;
import com.mfino.transactionapi.constants.ApiConstants;
import com.mfino.transactionapi.handlers.money.BankTransferInquiryHandler;
import com.mfino.transactionapi.handlers.money.MoneyTransferHandler;
import com.mfino.transactionapi.vo.TransactionDetails;
/**
 * Runs periodically, handles failed transactions of Teller.
 * @author Sasi
 */

@org.springframework.stereotype.Service("TellerEMoneyClearanceServiceImpl")
public class TellerEMoneyClearanceServiceImpl implements TellerEMoneyClearanceService{
	
	private static Logger log = LoggerFactory.getLogger(TellerEMoneyClearanceServiceImpl.class);	
	private static final String NOBACKEND_RESPONSE = "Your request is queued. Please check after sometime.";
	
	
	@Autowired
	@Qualifier("BankTransferInquiryHandlerImpl")
	private BankTransferInquiryHandler bankTransferInquiryHandler;
	
	@Autowired
	@Qualifier("MoneyTransferHandlerImpl")
	private MoneyTransferHandler moneyTransferHandler;
	
	@Autowired
	@Qualifier("TransactionChargingServiceImpl")
	private TransactionChargingService transactionChargingService;

	@Autowired
	@Qualifier("TransactionTypeServiceImpl")
	private TransactionTypeService transactionTypeService;
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("MfinoServiceImpl")
	private MfinoService mfinoService;
	
	@Autowired
	@Qualifier("PartnerServiceImpl")
	private PartnerService partnerService;
	
	@Autowired
	@Qualifier("ServiceChargeTransactionLogServiceImpl")
	private ServiceChargeTransactionLogService serviceChargeTransactionLogService;
	
	private HibernateTransactionManager txManager;
	
	public HibernateTransactionManager getTxManager() {
		return txManager;
	}

	public void setTxManager(HibernateTransactionManager txManager) {
		this.txManager = txManager;
	}

	@Override
	
	public void clearTellerEMoney() {
		log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() BEGIN");
		
		TransactionType cashInTxnType = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
		TransactionType cashOutTxnType = transactionTypeService.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT);
//		TransactionType cashOutUnRegTxnType = transactionTypeDao.getTransactionTypeByName(ServiceAndTransactionConstants.TRANSACTION_CASHOUT_UNREGISTERED);
				
		ChannelCode channelCode = channelCodeService.getChannelCodebySourceApplication(CmFinoFIX.SourceApplication_Web);
		
		Service tellerService =mfinoService.getServiceByName(ServiceAndTransactionConstants.SERVICE_TELLER);
		
		Integer[] status = new Integer[]{CmFinoFIX.SCTLStatus_Pending_Resolved};
		
		List<ServiceChargeTransactionLog> sctlList = serviceChargeTransactionLogService.getByStatus(status);
		
		log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() sctlList="+sctlList);
		
		Map<Long, Set<ServiceChargeTransactionLog>> partnerPendingResolveSctlMap = new HashMap<Long, Set<ServiceChargeTransactionLog>>();
		
		for(ServiceChargeTransactionLog sctl : sctlList){
			if(tellerService.getID().equals(sctl.getServiceID())){
				transactionChargingService.changeStatusToPendingResolvedProcessing(sctl);	
				
				Long partnerId = sctl.getSourcePartnerID() != null ? sctl.getSourcePartnerID() : sctl.getDestPartnerID();
				if(partnerId != null){
					if(partnerPendingResolveSctlMap.containsKey(partnerId)){
						partnerPendingResolveSctlMap.get(partnerId).add(sctl);
						
					}else{
						Set<ServiceChargeTransactionLog> partnerSctls = new HashSet<ServiceChargeTransactionLog>();
						partnerSctls.add(sctl);
						partnerPendingResolveSctlMap.put(partnerId, partnerSctls);
					}
				}
			}
		}
		
		log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() partnerPendingResolveSctlMap="+partnerPendingResolveSctlMap);
		
		for(Long partnerId : partnerPendingResolveSctlMap.keySet()){
			BigDecimal amount = new BigDecimal(0);
			for(ServiceChargeTransactionLog sctl : partnerPendingResolveSctlMap.get(partnerId)){
				if (cashInTxnType.getID().equals(sctl.getTransactionTypeID())) {
					amount = amount.add(sctl.getTransactionAmount());
				} 
				else if (cashOutTxnType.getID().equals(sctl.getTransactionTypeID())) {
					amount = amount.add(sctl.getTransactionAmount().subtract(sctl.getCalculatedCharge()));
				}
			}
			
			log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() partnerId="+partnerId+", amount="+amount);
			
			Partner partner = partnerService.getPartnerById(partnerId);
			Subscriber subscriber = partner.getSubscriber();
			SubscriberMDN subscriberMdn = subscriber.getSubscriberMDNFromSubscriberID().iterator().next();
			
			Set<Pocket> pockets = subscriberMdn.getPocketFromMDNID();
			
			Pocket sourcePocket = null;
			Pocket destPocket = null;
			
			for(Pocket pocket: pockets){
				if((pocket.getPocketTemplate().getType().intValue() == CmFinoFIX.PocketType_SVA.intValue()) && (!(pocket.getPocketTemplate().getIsCollectorPocket()))){
					sourcePocket = pocket;
				}
				else if((pocket.getPocketTemplate().getType().intValue() == CmFinoFIX.PocketType_BankAccount.intValue())){
					destPocket = pocket;
				}
			}
			
			log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() partnerId="+partnerId+", amount="+amount+", sourcePocket="+sourcePocket+", destPocket="+destPocket);
			
			if((sourcePocket != null) && (destPocket != null)){

				TransactionDetails transactionDetails = new TransactionDetails();

				transactionDetails.setSourcePocketId(sourcePocket.getID().toString());
				transactionDetails.setTransactionName(ServiceAndTransactionConstants.TRANSACTION_TELLER_EMONEY_CLEARANCE_INQUIRY);
				transactionDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
				transactionDetails.setDestPocketCode(ApiConstants.POCKET_CODE_BANK);
				transactionDetails.setSrcPocketId(sourcePocket.getID());
				transactionDetails.setDestinationPocketId(destPocket.getID());
				transactionDetails.setSourceMDN(subscriberMdn.getMDN());
				transactionDetails.setDestMDN(subscriberMdn.getMDN());
				transactionDetails.setSystemIntiatedTransaction(true);
				transactionDetails.setAmount(amount);
				transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
				transactionDetails.setServletPath(CmFinoFIX.ServletPath_Subscribers);
				transactionDetails.setChannelCode(channelCode.getChannelCode());
				transactionDetails.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_TELLER_CLEARANCE);
				transactionDetails.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
				transactionDetails.setSourcePIN("1234");

				XMLResult result = (XMLResult)bankTransferInquiryHandler.handle(transactionDetails);
				
				Set<ServiceChargeTransactionLog> tellerSctls = partnerPendingResolveSctlMap.get(partnerId);
				
				if (result != null) {
					log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() INQUIRY result.getCode()="+result.getCode());
					if(CmFinoFIX.NotificationCode_BankAccountToBankAccountConfirmationPrompt.toString().equals(result.getCode())){
						ServiceChargeTransactionLog tellerClearanceSctl = serviceChargeTransactionLogService.getById(result.getSctlID());
						
						
						for(ServiceChargeTransactionLog sctl: tellerSctls){
							sctl.setParentSCTLID(tellerClearanceSctl.getID());
						}
						transactionDetails = new TransactionDetails();
						transactionDetails.setCc(channelCode);
						transactionDetails.setSourceMDN(subscriberMdn.getMDN());
						transactionDetails.setDestMDN(subscriberMdn.getMDN());
						transactionDetails.setTransferId(result.getTransferID());
						transactionDetails.setConfirmString("true");
						transactionDetails.setParentTxnId(result.getParentTransactionID());
						transactionDetails.setSourcePocketCode(ApiConstants.POCKET_CODE_SVA);
						transactionDetails.setDestPocketCode(ApiConstants.POCKET_CODE_BANK);
						transactionDetails.setSourcePocketId(""+sourcePocket.getID());
						
						result = (XMLResult)moneyTransferHandler.handle(transactionDetails);
						
						if (result != null && result.getDetailsOfPresentTransaction()!= null) {
							log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() CONFIRMATION result.getCode()="+result.getCode());
							CommodityTransfer ct = result.getDetailsOfPresentTransaction();
							if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferStatus())) {
								log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() transfer successful");
								
								//confirm this transaction
								transactionChargingService.confirmTheTransaction(tellerClearanceSctl);
								
								//confirm all the sctls that were in pending_resolved state for this partner.
								for(ServiceChargeTransactionLog sctl: tellerSctls){
									/*
									 * If transaction type is cash out, or unregistered cash out confirm, 
									 * other wise cash in fail.
									 */
									if(cashInTxnType.getID().equals(sctl.getTransactionTypeID())){
										transactionChargingService.failTheTransaction(sctl, "TellerEMoneyClearance-FAIL");
									}
									else{
										transactionChargingService.confirmTheTransaction(sctl);
									}
								}
							}
							else{
								 //Before Correcting errors reported by Findbugs:
									//log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() CONFIRMATION FAILED code="+(((result != null)) ? result.getCode() : "result=null"));
							
								//After Correcting the errors reported by Findbugs:null check of result was not necessary since the code is inside a 
									//if loop which allows only non-null values of result
								log.info("TellerEMoneyClearanceServiceImpl :: clearTellerEMoney() CONFIRMATION FAILED code="+ result.getCode());							
								handleTellerEMoneyTransactionFailure(tellerClearanceSctl, tellerSctls);
							}
						}
						else if(result != null && NOBACKEND_RESPONSE.equals(result.getMessage())){
							/*Pending case do nothing, Need to be resolved manually*/
							log.debug("TellerEMoneyClearanceServiceImpl :: NOBACKEND_RESPONSE for E-Money Clearance request");
						}
						else{
							handleTellerEMoneyTransactionFailure(null, tellerSctls);
						}
					}
					else{
						handleTellerEMoneyTransactionFailure(null, tellerSctls);
					}
				}
				else{
					log.debug("TellerEMoneyClearanceServiceImpl :: Inquiry Failed");
					handleTellerEMoneyTransactionFailure(null, tellerSctls);
				}
			}
		}
	}
	
	private void handleTellerEMoneyTransactionFailure(ServiceChargeTransactionLog tellerClearanceSctl, Collection<ServiceChargeTransactionLog> partnerSctlCol)
	{
		for(ServiceChargeTransactionLog sctl: partnerSctlCol){
			transactionChargingService.changeStatusToPendingResolved(sctl);
		}
		
		if(tellerClearanceSctl != null){
			transactionChargingService.failTheTransaction(tellerClearanceSctl, "TellerEMoneyClearance-FAIL");
		}
	}
	
}
