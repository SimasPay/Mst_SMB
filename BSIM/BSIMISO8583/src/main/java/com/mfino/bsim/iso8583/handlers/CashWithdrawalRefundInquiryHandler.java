package com.mfino.bsim.iso8583.handlers;

import java.math.BigDecimal;

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

import com.mfino.bsim.iso8583.GetConstantCodes;
import com.mfino.constants.ServiceAndTransactionConstants;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMCashWithdrawalRefundInquiry;
import com.mfino.handlers.FIXMessageHandler;
import com.mfino.service.SubscriberMdnService;
import com.mfino.service.SubscriberService;
import com.mfino.service.TransactionLogService;
import com.mfino.util.ConfigurationUtil;


public class CashWithdrawalRefundInquiryHandler extends FIXMessageHandler {

	private static Logger log = LoggerFactory.getLogger(CashWithdrawalRefundInquiryHandler.class);
	private SessionFactory sessionFactory;
	private HibernateTransactionManager htm;
	private static CashWithdrawalRefundInquiryHandler cashwithdrawalRefundInquiryHandler;
	private SubscriberMdnService subscriberMdnService; 
	private SubscriberService subscriberService;
	private TransactionLogService transactionLogService;

	public static CashWithdrawalRefundInquiryHandler createInstance(){
		if(cashwithdrawalRefundInquiryHandler==null){
			cashwithdrawalRefundInquiryHandler = new CashWithdrawalRefundInquiryHandler();
		}
		
		return cashwithdrawalRefundInquiryHandler;
	}
	
	public static CashWithdrawalRefundInquiryHandler getInstance(){
		if(cashwithdrawalRefundInquiryHandler==null){
			throw new RuntimeException("Instance is not already created");
		}
		return cashwithdrawalRefundInquiryHandler;
	}

	@Transactional(readOnly=false, propagation=Propagation.REQUIRED)
	public void handle(ISOMsg msg) throws Exception {
		
		try{
			
			log.info("CMCashWithdrawalRefundInquiryHandler :: handle()");
			Integer response=null;
			sessionFactory = htm.getSessionFactory();
			Session session = sessionFactory.openSession();
			
			if(!TransactionSynchronizationManager.hasResource(sessionFactory)){
				
				log.info("Opening and Binding Session for thread : "+ Thread.currentThread().getName());
				TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
			}
			
			String accnumber = msg.getString("103");
			if(StringUtils.isNotBlank(accnumber) && !accnumber.startsWith(ConfigurationUtil.getCodeForTransferUsingEMoney())) {
				
				msg.set(39,GetConstantCodes.FAILURE);
				return;
			}
			
			String sourceMDN = subscriberService.normalizeMDN(accnumber.substring(ConfigurationUtil.getCodeForTransferUsingEMoney().length()));
			SubscriberMdn subMDNByMDN = subscriberMdnService.getByMDN(sourceMDN);
			
			logTransactionRecord(msg, sourceMDN);
			
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
			
			msg.set(39,GetConstantCodes.SUCCESS);
			return;
			
		} catch(Exception e){
			
			log.error(e.getMessage());
			
		} finally{
			
			SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(sessionHolder.getSession());
		}
	}
	
	private void logTransactionRecord(ISOMsg msg, String accountNumber) {
		
		CMCashWithdrawalRefundInquiry cashWithdrawalRefundInquiry = new CMCashWithdrawalRefundInquiry();
		cashWithdrawalRefundInquiry.setAmount(new BigDecimal(msg.getString("4")));
		cashWithdrawalRefundInquiry.setDestMDN(accountNumber);
		cashWithdrawalRefundInquiry.setOriginalReferenceID(new Long(msg.getString("11")));
		cashWithdrawalRefundInquiry.setServiceName(ServiceAndTransactionConstants.TRANSACTION_CASHWITHDRAWAL_REFUND);
		
		getTransactionLogService().saveTransactionsLog(CmFinoFIX.MessageType_CashWithdrawalRefundInquiry,cashWithdrawalRefundInquiry.DumpFields());
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
}