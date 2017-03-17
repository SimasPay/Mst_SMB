package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.jpos.iso.ISOMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.amazonaws.partitions.model.Service;
import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.dao.query.ServiceChargeTransactionsLogQuery;
import com.mfino.domain.Pocket;
import com.mfino.domain.ServiceCharge;
import com.mfino.domain.ServiceChargeTxnLog;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.Transaction;
import com.mfino.domain.TransactionLog;
import com.mfino.exceptions.InvalidChargeDefinitionException;
import com.mfino.exceptions.InvalidServiceException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashIn;
import com.mfino.fix.CmFinoFIX.CMCashInFromATM;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.mce.backend.BankService;
import com.mfino.mce.core.util.BackendResponse;
import com.mfino.service.NotificationService;
import com.mfino.service.PocketService;
import com.mfino.service.SCTLService;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionChargingService;
import com.mfino.service.TransactionLogService;
import com.mfino.util.ConfigurationUtil;


public class CashinHandler extends FIXMessageHandler {

	private static Logger log = LoggerFactory.getLogger(CashinHandler.class);
	private SessionFactory sessionFactory;
	private HibernateTransactionManager htm;
	private static CashinHandler cashinHandler;
	private SubscriberMdnService subscriberMdnService; 
	private SubscriberService subscriberService;
	private TransactionLogService transactionLogService;
	private TransactionChargingService transactionChargingService;
	private PocketService pocketService;
	private NotificationService notificationService;
	private BankService bankService;
	private SCTLService sctlService;

	public static CashinHandler createInstance(){
		if(cashinHandler==null){
			cashinHandler = new CashinHandler();
		}
		
		return cashinHandler;
	}
	
	public static CashinHandler getInstance(){
		if(cashinHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return cashinHandler;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void handle(ISOMsg msg) throws Exception {
		
		try{
			
			log.info("CashinHandler :: handle()");
			Integer response=null;
			sessionFactory = htm.getSessionFactory();
			Session session = sessionFactory.openSession();
			
			if(!TransactionSynchronizationManager.hasResource(sessionFactory)){
				
				log.info("Opening and Binding Session for thread : "+ Thread.currentThread().getName());
				TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			}
			
			String accnumber = msg.getString("103");
			long intTxnId = new Long(msg.getString("11"));
			String dateTime = msg.getString("7");
			String de100 = msg.getString("100");
			String de102 = msg.getString("102");
			String amount = msg.getString("4");
			BigDecimal actualAmount = new BigDecimal(amount).divide(new BigDecimal("100"));
			boolean isSelfBank = (msg.getString("3").startsWith("49") ? true : false);
			
			if(StringUtils.isNotBlank(accnumber) && !accnumber.startsWith(ConfigurationUtil.getCodeForTransferUsingEMoney())) {
				
				msg.set(39,GetConstantCodes.FAILURE);
				return;
			}
			
			String sourceMDN = subscriberService.normalizeMDN(accnumber.substring(ConfigurationUtil.getCodeForTransferUsingEMoney().length()));
			SubscriberMdn subMDNByMDN = subscriberMdnService.getByMDN(sourceMDN);
			
			CMCashIn tcashin = new CMCashIn();
			tcashin.setDestMDN(sourceMDN);
			tcashin.setOriginalReferenceID(intTxnId);
			tcashin.setServiceName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
			
			TransactionLog transactionLog = getTransactionLogService().saveTransactionsLog(CmFinoFIX.MessageType_CashIn,tcashin.DumpFields());
			
			response = validateDestinationMdn(subMDNByMDN);
			
			if (!response.equals(CmFinoFIX.ResponseCode_Success)) {
				
				log.error("Destination MDN has failed validations");
				
				if(response.equals(CmFinoFIX.NotificationCode_MDNIsRestricted)) {
					
					msg.set(39,GetConstantCodes.CUSTOMER_ACCOUNT_BLOCKED);
					
				} else {
				
					msg.set(39,GetConstantCodes.FAILURE);
				}
				
				return;
			}
			
			response = validateBankReferenceNumber(intTxnId, dateTime);
			
			if (!response.equals(CmFinoFIX.ResponseCode_Success)) {
				
				log.error("Duplicate Bank Reference Number so rejecting the transaction");
				
				msg.set(39,GetConstantCodes.DUPLICATE_TRANSMISSION);
				return;
			}
			
			Transaction transactionDetails = null;
			ServiceCharge sc = new ServiceCharge();
			sc.setSourceMDN(sourceMDN);
			sc.setDestMDN(sourceMDN);
			sc.setChannelCodeId(CmFinoFIX.SourceApplication_ATM);
			sc.setServiceName(ServiceAndTransactionConstants.SERVICE_WALLET);
			sc.setTransactionTypeName(ServiceAndTransactionConstants.TRANSACTION_CASHIN);
			sc.setTransactionAmount(actualAmount);
			sc.setTransactionLogId(transactionLog.getId());
			sc.setIntegrationTxnID(intTxnId);
			sc.setInfo1(dateTime);
			sc.setInfo2(de100);
			sc.setInfo3(de102);
			sc.setInfo4(accnumber);

			try{
				
				transactionDetails = transactionChargingService.getCharge(sc);
				
			}catch (InvalidServiceException e) {
				
				log.error("Exception occured in getting charges",e);
				msg.set(39,GetConstantCodes.FAILURE);
				return;
				
			} catch (InvalidChargeDefinitionException e) {
				
				log.error(e.getMessage());
				msg.set(39,GetConstantCodes.FAILURE);
				return;
			}
			
			ServiceChargeTxnLog sctl = transactionDetails.getServiceChargeTransactionLog();
			
			Pocket destPocket = pocketService.getDefaultPocket(subMDNByMDN, String.valueOf(CmFinoFIX.PocketType_SVA));
			
			if(destPocket == null) {
				
				log.info("E Money Pocket not found....");
				msg.set(39,GetConstantCodes.FAILURE);
				return;
			}
			
			CMCashInFromATM cashin = new CMCashInFromATM();
			cashin.setSourceMDN(sourceMDN);
			cashin.setDestMDN(sourceMDN);
			cashin.setAmount(actualAmount);
			cashin.setDestPocketID(destPocket.getId());
			cashin.setSourceApplication(CmFinoFIX.SourceApplication_ATM);
			cashin.setServiceChargeTransactionLogID(sctl.getId());
			cashin.setChannelCode(String.valueOf(CmFinoFIX.SourceApplication_ATM));
			cashin.setTransactionID(transactionLog.getId());
			cashin.setMessageType(CmFinoFIX.MessageType_CashIn);
			cashin.setOriginalReferenceID(intTxnId);
			cashin.setUICategory(CmFinoFIX.TransactionUICategory_Cashin_From_ATM);
			
			if(isSelfBank)
				cashin.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_SELF_BANK_CASHIN);
			else
				cashin.setSourceMessage(ServiceAndTransactionConstants.MESSAGE_OTHER_BANK_CASHIN);
			
			try {
	            
					InetAddress ownIP = InetAddress.getLocalHost();
		            cashin.setSourceIP(ownIP.toString());
	            
	        	} catch (UnknownHostException ex) {
	            
	        	log.error(ex.getMessage(), ex);
	        }
			
			CFIXMsg fixResponse = bankService.onCashInFromATM(cashin);
			
			if (fixResponse != null) {
				
				BackendResponse fixError = (BackendResponse) fixResponse;
				log.info(fixError.getResult() + "");
				
				if (fixError.getResult() == 0) {
					
					msg.set(39,GetConstantCodes.SUCCESS);
					transactionChargingService.confirmTheTransaction(sctl, fixError.getTransferID());
					
				} else {
					
					msg.set(39,GetConstantCodes.FAILURE);
					transactionChargingService.failTheTransaction(sctl, "Tansaction Failed");
				}
			} else {
				
				msg.set(39,GetConstantCodes.FAILURE);
				transactionChargingService.failTheTransaction(sctl, "Tansaction Failed");
			}
			
			return;
			
		} catch(Exception e){
		
			msg.set(39,GetConstantCodes.FAILURE);
			log.error(e.getMessage());
			
		} finally{
			
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
	
	private Integer validateBankReferenceNumber(long rrn, String dateTime) {
		
		ServiceChargeTransactionsLogQuery sctlQuery = new ServiceChargeTransactionsLogQuery();
		sctlQuery.setIntegrationTxnID(rrn);
		sctlQuery.setInfo1(dateTime);
		
		List<ServiceChargeTxnLog> oldsctlList = sctlService.getByQuery(sctlQuery);
		
		if(!oldsctlList.isEmpty()){
			
			return CmFinoFIX.ResponseCode_Failure; // Only one match would be there as we do not allow duplicate entry
		}
		
		return CmFinoFIX.ResponseCode_Success;
	}
	
	private Integer validateDestinationMdn(SubscriberMdn subscriberMDN) {
		
		if(subscriberMDN == null) {
		
			log.error("SourceMDN is null");
			return CmFinoFIX.NotificationCode_MDNNotFound;
		}
		
		Subscriber subscriber = null;
		
		if(subscriberMDN != null) {
			
			subscriber = subscriberMDN.getSubscriber();
		}
		
		// First Restrictions should be checked as we are allowing InActive Subscribers(of no activity) to login
		if (!(CmFinoFIX.SubscriberRestrictions_None.equals(subscriberMDN.getRestrictions())) &&
				!(CmFinoFIX.SubscriberRestrictions_NoFundMovement.equals(subscriberMDN.getRestrictions()))) {
			
			log.error("Source Subscriber with mdn: "+subscriberMDN.getMdn()+"is restricted");
			return CmFinoFIX.NotificationCode_MDNIsRestricted;
		}
		
		if(subscriber != null) {
			
			if( !(CmFinoFIX.MDNStatus_Active.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_Active.equals(subscriber.getStatus())) &&
					!(CmFinoFIX.MDNStatus_InActive.equals(subscriberMDN.getStatus())&& CmFinoFIX.SubscriberStatus_InActive.equals(subscriber.getStatus())) ){
				
				log.error("Source Subscriber with mdn: "+subscriberMDN.getMdn()+"is not active");
				return CmFinoFIX.NotificationCode_MDNIsNotActive;
			}
		}
		
		return CmFinoFIX.ResponseCode_Success;
	}
	
	public SubscriberMdnService getSubscriberMdnService() {
		return subscriberMdnService;
	}

	public void setSubscriberMdnService(SubscriberMdnService subscriberMdnService) {
		this.subscriberMdnService = subscriberMdnService;
	}

	public HibernateTransactionManager getHtm() {
		return htm;
	}

	public void setHtm(HibernateTransactionManager htm) {
		this.htm = htm;
	}
	
	public SubscriberService getSubscriberService() {
		return subscriberService;
	}

	public void setSubscriberService(SubscriberService subscriberService) {
		this.subscriberService = subscriberService;
	}

	public TransactionLogService getTransactionLogService() {
		return transactionLogService;
	}

	public void setTransactionLogService(TransactionLogService transactionLogService) {
		this.transactionLogService = transactionLogService;
	}
	
	public TransactionChargingService getTransactionChargingService() {
		return transactionChargingService;
	}

	public void setTransactionChargingService(TransactionChargingService transactionChargingService) {
		this.transactionChargingService = transactionChargingService;
	}

	public PocketService getPocketService() {
		return pocketService;
	}

	public void setPocketService(PocketService pocketService) {
		this.pocketService = pocketService;
	}

	/**
	 * @return the notificationService
	 */
	public NotificationService getNotificationService() {
		return notificationService;
	}

	/**
	 * @param notificationService the notificationService to set
	 */
	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	/**
	 * @return the bankService
	 */
	public BankService getBankService() {
		return bankService;
	}

	/**
	 * @param bankService the bankService to set
	 */
	public void setBankService(BankService bankService) {
		this.bankService = bankService;
	}

	/**
	 * @return the sctlService
	 */
	public SCTLService getSctlService() {
		return sctlService;
	}

	/**
	 * @param sctlService the sctlService to set
	 */
	public void setSctlService(SCTLService sctlService) {
		this.sctlService = sctlService;
	}
}