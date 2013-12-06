package com.mfino.replicatool.replica;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.replicatool.domain.decrypted.CopyScheduleInfo;
import com.mfino.replicatool.persistence.CopyScheduleInfoDAO;
import com.mfino.replicatool.persistence.MfinoDbHibernateUtil;
import com.mfino.replicatool.persistence.RepilcaDbHibernateUtil;

public class Replicator {
	
	private static Logger log = LoggerFactory.getLogger(Replicator.class);
	private int batchSize = 1000;
	private int fetchSize = 1000;
	private Properties prop = new Properties();
	
	public void loadProperties(){
		try {
			InputStream ins = this.getClass().getResourceAsStream("/replica.properties");
			prop.load(ins);
			ins.close();
			batchSize = Integer.parseInt(prop.getProperty("batchSize"));
    		fetchSize = Integer.parseInt(prop.getProperty("fetchSize"));
    		log.info("BatchSize: "+batchSize);
    		log.info("FetchSize: "+fetchSize);	
		} catch (Exception e) {
			log.error("File not found Exception or Could not load replica.properties",e);
		}
	}
	
	
	public static void main(String[] args){
		Long totalTime = System.currentTimeMillis();
		log.info("Replicator Tool Started Copying");
		Replicator rep = new Replicator();
		rep.loadProperties();
		rep.copyDomainObjects();
		log.info("Replicator Tool Completed Copying");
		totalTime = System.currentTimeMillis() - totalTime;
		double totalTimeInSec = totalTime/1000.0;
		log.info("Total Time Taken to Complete "+totalTimeInSec+ "Seconds");
	}
	
	public void copyDomainObjects(){
		for(int i=0; i< classNames.length;i++){
			try{
				copyObject(classNames[i]);
			}catch(Exception e){
				log.error("copyDomainObjects(): Error Occured while copying the domain Object"+classNames[i],e);
			}
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
		
		StatelessSession ms = MfinoDbHibernateUtil.getSessionFactory().openStatelessSession();
		Query countQuery = ms.createQuery("select count(*) from "+className+" where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		long totalRows = 0;
		if(countQuery.list().size() > 0){
			totalRows =  (Long) countQuery.list().get(0);
		}
		
		log.info("Total Number of "+className+" Domain Objects Present in mfino DB: "+totalRows);
		ms.close();
			
		//i = i+fetchSize
		long currentRowCount = 0;
		for(long i=0,j=fetchSize; currentRowCount < totalRows; i = j+1,j = j+fetchSize){
			StatelessSession mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openStatelessSession();
			Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
			Query domainObjectQuery = mfinoSession.createQuery("from "+className+" where (LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime+") and (ID between "+i+" and "+j+")");
			ScrollableResults sr = domainObjectQuery.scroll();
			int readCount = 0;
			while(sr.next()) { 
				CmFinoFIX.CRBase obj = (CmFinoFIX.CRBase) sr.get(0);
			//	mfinoSession.evict(obj);
			
				if(readCount == 0){
					replicaSession.beginTransaction();
				}
				
				try{
					replicaSession.saveOrUpdate(obj);
				}catch(Exception e){
					log.error("Error Inserting "+className+" Domain Object with ID: "+obj.getID(),e);
				}
				
				currentRowCount++;
				readCount++;
				if (readCount % batchSize == 0) {
					 readCount = 0;
					 replicaSession.getTransaction().commit();
//					 mfinoSession.flush();
//					 mfinoSession.clear();
					 replicaSession.flush();
					 replicaSession.clear();
				}
			}
			if (readCount % batchSize != 0) {
				 readCount = 0;
				 replicaSession.getTransaction().commit();
//				 mfinoSession.flush();
//				 mfinoSession.clear();
				 replicaSession.flush();
				 replicaSession.clear();
			}
			replicaSession.close();
			mfinoSession.close();
		}
		
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
			"com.mfino.domain.AgentCashInTransactions",
			"com.mfino.domain.AirtimePurchase",
			"com.mfino.domain.mFinoServiceProvider",
			"com.mfino.domain.AuthorizingPerson",
			"com.mfino.domain.AutoReversals",
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
			"com.mfino.domain.ClosedAccountSettlementMDN",
			"com.mfino.domain.TransactionsLog",
			"com.mfino.domain.LOP",
			"com.mfino.domain.CreditCardTransaction",
			"com.mfino.domain.CommodityTransfer",
			"com.mfino.domain.CommodityTransferNextID",
			"com.mfino.domain.CreditCardDestinations",
			"com.mfino.domain.CreditCardProduct",
			"com.mfino.domain.DbParam",
			"com.mfino.domain.DCTRestrictions",
			"com.mfino.domain.Denomination",
			"com.mfino.domain.EnumText",
			"com.mfino.domain.ExcludeSubscriberLifeCycle",
			"com.mfino.domain.ExpirationType",
			"com.mfino.domain.Purpose",
			"com.mfino.domain.FundEvents",
			"com.mfino.domain.FundDefinition",
			"com.mfino.domain.UnRegisteredTxnInfo",
			"com.mfino.domain.FundDistributionInfo",
			"com.mfino.domain.Group",
			"com.mfino.domain.MFSBiller",
			"com.mfino.domain.IntegrationPartnerMapping",
			"com.mfino.domain.IntegrationSummary",
			"com.mfino.domain.InterBankCode",
			"com.mfino.domain.InterbankTransfer",
			"com.mfino.domain.IPMapping",
			"com.mfino.domain.KYCFields",
			"com.mfino.domain.Ledger",
			"com.mfino.domain.LOPHistory",
			"com.mfino.domain.MDNRange",
			"com.mfino.domain.MerchantCode",
			"com.mfino.domain.MerchantPrefixCode",
			"com.mfino.domain.MFSBillerPartner",
			"com.mfino.domain.MFSDenominations",
			"com.mfino.domain.MobileNetworkOperator",
			"com.mfino.domain.ServiceChargeTransactionLog",
			"com.mfino.domain.MoneyClearanceGraved",
			"com.mfino.domain.Notification",
			"com.mfino.domain.NotificationLog",
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
			"com.mfino.domain.PTC_Group_Map",
			"com.mfino.domain.ReportParameters",
			"com.mfino.domain.Role",
			"com.mfino.domain.RolePermission",
			"com.mfino.domain.SAPGroupID",
			"com.mfino.domain.ServiceAudit",
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
			"com.mfino.domain.TransactionPendingSummary",
			"com.mfino.domain.VisafoneTxnGenerator", 
		};


}