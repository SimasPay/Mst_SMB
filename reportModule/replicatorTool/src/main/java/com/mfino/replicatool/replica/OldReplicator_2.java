package com.mfino.replicatool.replica;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.replicatool.domain.decrypted.CopyScheduleInfo;
import com.mfino.replicatool.persistence.CopyScheduleInfoDAO;
import com.mfino.replicatool.persistence.MfinoDbHibernateUtil;
import com.mfino.replicatool.persistence.RepilcaDbHibernateUtil;

public class OldReplicator_2 {
	
	private static Logger log = LoggerFactory.getLogger(OldReplicator_2.class);
	
	public static void main(String[] args){
		Long totalTime = System.currentTimeMillis();
		log.info("Replicator Tool Started Copying");
		OldReplicator_2 rep = new OldReplicator_2();
		rep.copyDomainObjects();
		log.info("Replicator Tool Completed Copying");
		totalTime = System.currentTimeMillis() - totalTime;
		double totalTimeInSec = totalTime/1000.0;
		log.info("Total Time Take to Complete "+totalTimeInSec+ "Seconds");
	}
	
	public void copyDomainObjects(){
		for(int i=0; i< classNames.length;i++){
			copyObject(classNames[i]);
		}
	}
	
	public void copyObject(String className){
		log.info("Started Copying "+className+" Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo(className);
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);
		
		Long duration = System.currentTimeMillis();
		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from "+className+" where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		int readCount = 0;
		int batchSize = 100;
		while(sr.next()) { 
			CmFinoFIX.CRBase obj = (CmFinoFIX.CRBase) sr.get(0);
			mfinoSession.evict(obj);
		
			if(readCount == 0){
				replicaSession.beginTransaction();
			}
			
			try{
				replicaSession.saveOrUpdate(obj);
			}catch(Exception e){
				log.error("Error Inserting "+className+" Domain Object with ID: "+obj.getID(),e);
			}
			
			readCount++;
			if (readCount % batchSize == 0) {
				 readCount = 0;
				 replicaSession.getTransaction().commit();
				 mfinoSession.flush();
				 mfinoSession.clear();
				 replicaSession.flush();
				 replicaSession.clear();
			}
		}
		if (readCount % batchSize != 0) {
			 readCount = 0;
			 replicaSession.getTransaction().commit();
			 mfinoSession.flush();
			 mfinoSession.clear();
			 replicaSession.flush();
			 replicaSession.clear();
		}
		replicaSession.close();
		mfinoSession.close();
		duration = System.currentTimeMillis() - duration;
		double durationInSeconds = duration/1000.0d;
		log.info("Completed Copying "+className+" Domain Object in "+durationInSeconds+" Seconds");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
		}

	public static String getDateString(Date d){
		return "'"+d.toString()+"'";
	}
	
	private String[] classNames ={
			"com.mfino.domain.Company", 
			"com.mfino.domain.ActivitiesLog", 
			"com.mfino.domain.Address", 
			"com.mfino.domain.AirtimePurchase", 
			"com.mfino.domain.mFinoServiceProvider", 
			"com.mfino.domain.AuthorizingPerson", 
			"com.mfino.domain.Bank", 
			"com.mfino.domain.User", 
			"com.mfino.domain.BankAdmin", 
			"com.mfino.domain.Biller", 
			"com.mfino.domain.BillPayments", 
			"com.mfino.domain.PocketTemplate", 
			"com.mfino.domain.KYCLevel", 
			"com.mfino.domain.Subscriber", 
			"com.mfino.domain.BillPaymentTransaction", 
			"com.mfino.domain.Brand", 
			"com.mfino.domain.BulkBankAccount", 
			"com.mfino.domain.Region", 
			"com.mfino.domain.Merchant", 
			"com.mfino.domain.SubscriberMDN", 
			"com.mfino.domain.Service", 
			"com.mfino.domain.DistributionChainTemplate", 
			"com.mfino.domain.TransactionType", 
			"com.mfino.domain.DistributionChainLevel", 
			"com.mfino.domain.BulkLOP", 
			"com.mfino.domain.Pocket", 
			"com.mfino.domain.BulkUpload", 
			"com.mfino.domain.BulkUploadEntry", 
			"com.mfino.domain.BulkUploadFile", 
			"com.mfino.domain.CardInfo", 
			"com.mfino.domain.ChannelCode", 
			"com.mfino.domain.ChannelSessionManagement", 
			"com.mfino.domain.ChargeType", 
			"com.mfino.domain.Partner", 
			"com.mfino.domain.ChargeDefinition", 
			"com.mfino.domain.ChargePricing", 
			"com.mfino.domain.ChargeTxnCommodityTransferMap", 
			"com.mfino.domain.TransactionsLog", 
			"com.mfino.domain.LOP", 
			"com.mfino.domain.CreditCardTransaction", 
			"com.mfino.domain.CommodityTransfer", 
			"com.mfino.domain.CreditCardDestinations", 
			"com.mfino.domain.DbParam", 
			"com.mfino.domain.DCTRestrictions", 
			"com.mfino.domain.Denomination", 
			"com.mfino.domain.EnumText", 
			"com.mfino.domain.Group", 
			"com.mfino.domain.MFSBiller", 
			"com.mfino.domain.IntegrationPartnerMapping", 
			"com.mfino.domain.InterBankCode", 
			"com.mfino.domain.InterbankTransfer", 
			"com.mfino.domain.KYCFields", 
			"com.mfino.domain.Ledger", 
			"com.mfino.domain.LOPHistory", 
			"com.mfino.domain.MDNRange", 
			"com.mfino.domain.MerchantCode", 
			"com.mfino.domain.MerchantPrefixCode", 
			"com.mfino.domain.MFSBillerPartner", 
			"com.mfino.domain.MFSDenominations", 
			"com.mfino.domain.MobileNetworkOperator", 
			"com.mfino.domain.Notification", 
			"com.mfino.domain.OfflineReport", 
			"com.mfino.domain.OfflineReportForCompany", 
			"com.mfino.domain.OfflineReportReceiver", 
			"com.mfino.domain.PartnerRestrictions", 
			"com.mfino.domain.PartnerServices", 
			"com.mfino.domain.PendingCommodityTransfer", 
			"com.mfino.domain.PendingTransactionsEntry", 
			"com.mfino.domain.PendingTransactionsFile", 
			"com.mfino.domain.PermissionItems", 
			"com.mfino.domain.Person2Person", 
			"com.mfino.domain.PocketTemplateConfig", 
			"com.mfino.domain.ProductIndicator", 
			"com.mfino.domain.ReportParameters", 
			"com.mfino.domain.RolePermission", 
			"com.mfino.domain.SAPGroupID", 
			"com.mfino.domain.ServiceAudit", 
			"com.mfino.domain.ServiceChargeTransactionLog", 
			"com.mfino.domain.SettlementTemplate", 
			"com.mfino.domain.ServiceSettlementConfig", 
			"com.mfino.domain.ServiceTransaction", 
			"com.mfino.domain.SettlementSchedulerLogs", 
			"com.mfino.domain.SettlementTransactionLogs", 
			"com.mfino.domain.TransactionRule", 
			"com.mfino.domain.TransactionCharge", 
			"com.mfino.domain.SharePartner", 
			"com.mfino.domain.SMSPartner", 
			"com.mfino.domain.SMSC", 
			"com.mfino.domain.SMSCode", 
			"com.mfino.domain.SMSTransactionsLog", 
			"com.mfino.domain.SubscriberGroup", 
			"com.mfino.domain.SubscribersAdditionalFields", 
			"com.mfino.domain.SystemParameters", 
			"com.mfino.domain.TransactionAmountDistributionLog", 
			"com.mfino.domain.TransactionChargeLog", 
			"com.mfino.domain.UnRegisteredTxnInfo", 
			"com.mfino.domain.VisafoneTxnGenerator", 
		};


	

	
	

	
	
	
	
	
	



}