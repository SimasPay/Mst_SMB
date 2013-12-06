package com.mfino.dbcopytool.dbcopy;

import java.util.HashMap;
import java.util.Map;

public class QueryStringAndDomainConstants {

	public static final Map<String, String[]> domainQueryMap = new HashMap<String, String[]>(
			10);
	public static final String lastUpdateTime = "lastUpdateTime";
	public static final String currentUpdateTime = "currentUpdateTime";

	// subscriber
	public static final String subscriberQuery = "select subMdn.Subscriber.id, subMdn.Subscriber.FirstName,subMdn.Subscriber.LastName, "
			+ "subMdn.MDN, subMdn.Subscriber.KYCLevelByKYCLevel.KYCLevelName,subMdn.Subscriber.Type,  "
			+ "subMdn.IDType,subMdn.IDNumber, "
			+ "subMdn.Subscriber.AddressBySubscriberAddressID.City, subMdn.Subscriber.AddressBySubscriberAddressID.State, subMdn.Subscriber.AddressBySubscriberAddressID.Line1, subMdn.Subscriber.AddressBySubscriberAddressID.Line2, subMdn.Subscriber.AddressBySubscriberAddressID.ZipCode, "
			+ "subMdn.Subscriber.CreateTime, subMdn.LastUpdateTime,subMdn.CreatedBy,subMdn.UpdatedBy, "
			+ "et2.EnumValue, subMdn.Subscriber.Timezone, et1.DisplayText, subMdn.Subscriber.Restrictions,subMdn.Subscriber.DateOfBirth,subMdn.Subscriber.Email,subMdn.Subscriber.Currency "
			+ "from SubscriberMDN as subMdn, EnumText et1,EnumText et2 "
			+ "where subMdn.Subscriber.Type=0 " 
			+ "and (et1.TagID=5024 and et1.EnumCode=subMdn.Subscriber.Status) " 
			+ "and (et2.TagID=5134 and et2.EnumCode=subMdn.Subscriber.Language) " 
			+ "and subMdn.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;
	
	
	public static final String partnerQueryForSubscriber ="select subMdn.Subscriber.id, subMdn.Subscriber.FirstName,subMdn.Subscriber.LastName, "
			+ "subMdn.MDN, subMdn.Subscriber.KYCLevelByKYCLevel.KYCLevelName,subMdn.Subscriber.Type,  "
			+ "subMdn.IDType,subMdn.IDNumber, "
			+ "p.AddressByMerchantAddressID.State,p.AddressByMerchantAddressID.City,p.AddressByMerchantAddressID.Line1,p.AddressByMerchantAddressID.Line2,p.AddressByMerchantAddressID.ZipCode,  "
			+ "subMdn.Subscriber.CreateTime,subMdn.LastUpdateTime,subMdn.CreatedBy,subMdn.UpdatedBy, "
			+ "et3.EnumValue, subMdn.Subscriber.Timezone, et1.DisplayText, subMdn.Subscriber.Restrictions,subMdn.Subscriber.DateOfBirth,subMdn.Subscriber.Email,subMdn.Subscriber.Currency, "
			+ "p.PartnerCode, et2.EnumValue, p.User.Username, p.TradeName, p.IndustryClassification, "
			+ "p.RepresentativeName, p.Designation, p.FranchisePhoneNumber, p.AuthorizedFaxNumber, p.TypeOfOrganization  "
			+ "from Partner as p, SubscriberMDN as subMdn, EnumText et1, EnumText et2, EnumText et3 "
			+ "where (subMdn.Subscriber.Type=2) "
			+ "and (et1.TagID=5024 and et1.EnumCode=subMdn.Subscriber.Status) "
			+ "and (et2.TagID=6079 and p.BusinessPartnerType=et2.EnumCode) "
			+ "and (et3.TagID=5134 and et3.EnumCode=subMdn.Subscriber.Language) "
			+ "and subMdn.Subscriber.id=p.Subscriber.id "
			+ "and subMdn.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// partners
	public static final String partnerQuery = "select p.Subscriber.id, p.TradeName, p.id, "
			+ "p.PartnerCode, p.BusinessPartnerType, p.AddressByMerchantAddressID.City, "
			+ "p.AddressByMerchantAddressID.State, p.Subscriber.CreateTime,p.LastUpdateTime "
			+ "from Partner as p "
			+ "where p.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Pocket
	public static final String pocketQuery = "select pk.ID, pk.SubscriberMDNByMDNID.MDN,pk.SubscriberMDNByMDNID.Subscriber.ID, "
			+ "pk.CurrentBalance,pk.CardPAN,pk.CreateTime,pk.LastUpdateTime,pk.CurrentDailyExpenditure,pk.CurrentWeeklyExpenditure," 
			+ "pk.CurrentMonthlyExpenditure,pk.CurrentDailyTxnsCount,pk.CurrentWeeklyTxnsCount,pk.CurrentMonthlyTxnsCount," 
			+ "pk.PocketTemplate.Description,pk.PocketTemplate.MaxAmountPerTransaction,"
			+ "pk.PocketTemplate.MinAmountPerTransaction,pk.PocketTemplate.MaxAmountPerDay,pk.PocketTemplate.MaxAmountPerWeek,pk.PocketTemplate.MaxAmountPerMonth," 
			+ "pk.PocketTemplate.MaxTransactionsPerDay,pk.PocketTemplate.MaxTransactionsPerWeek,pk.PocketTemplate.MaxTransactionsPerMonth,et2.EnumValue,et1.DisplayText "
			+ "from Pocket as pk, EnumText et1, EnumText et2 " 
			+ "where (et1.TagID=5051 and et1.EnumCode=pk.Status) " 
			+ "and (et2.TagID=5059 and et2.EnumCode=pk.PocketTemplate.Commodity) "
			+ "and pk.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Kin Information Missing Account
	public static final String kinInfoMissingQuery = "select subMdn.Subscriber.id, subMdn.Subscriber.FirstName, "
			+ "subMdn.MDN, et.DisplayText, subMdn.Subscriber.KYCLevelByKYCLevel.KYCLevelName "
			+ "from SubscriberMDN as subMdn, EnumText et, SubscribersAdditionalFields  sa "
			+ "where subMdn.Subscriber.Type=0 "
			+ "and et.TagID=5024 and et.EnumCode=subMdn.Subscriber.Status "
			+ "and subMdn.Subscriber.id=sa.Subscriber.id and sa.KinMDN=''";
	
	

	// Commodity Transfer
	public static final String ctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
			+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
			+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
			+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
			+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
			+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN, "
			+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
			+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
			+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
			+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
			+ "from CommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
			+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
			+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
			+ "and ct.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Pending Commodity Transfer
//	public static final String pctQuery = "select pct.ID, pct.TransactionsLogByTransactionID.ID, pct.StartTime, pct.UICategory, "
//			+ "et1.DisplayText, pct.TransferStatus, "
//			+ "pct.PocketBySourcePocketID.ID, pct.DestPocketID, pct.Amount, pct.Charges,"
//			+ "pct.TaxAmount, pct.TransactionChargeID, pct.IsPartOfSharedUpChain, "
//			+ "pct.SubscriberBySourceSubscriberID.ID, pct.SourcePocketType, pct.SourceMessage, "
//			+ "pct.DestSubscriberID, pct.DestPocketType, pct.SourceMDN, "
//			+ "pct.DestMDN, pct.SourceApplication,ctsctl.SctlId,pct.LastUpdateTime  "
//			+ "from PendingCommodityTransfer as pct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1 "
//			+ "where et1.TagID=5636 and et1.EnumCode=pct.UICategory and pct.ID = ctsctl.CommodityTransferID and pct.LastUpdateTime between "
//			+ lastUpdateTime + " and " + currentUpdateTime;
	
	public static final String pctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
			+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
			+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
			+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
			+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
			+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN, "
			+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
			+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
			+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
			+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
			+ "from PendingCommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
			+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
			+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
			+ "and ct.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;
	
	// Subscriber Based Transaction when sourceMDN as TransactionMDn
		public static final String sbtctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
				+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
				+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
				+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
				+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
				+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN,ct.SourceMDN, "
				+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
				+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
				+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
				+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
				+ "from CommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
				+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
				+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
				+ "and ct.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime;
		
		public static final String sbtpctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
				+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
				+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
				+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
				+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
				+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN,ct.SourceMDN, "
				+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
				+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
				+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
				+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
				+ "from CommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
				+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
				+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
				+ "and ct.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime;
		
		// Subscriber Based Transaction when desMDN as TransactionMDn
				public static final String dbtctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
						+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
						+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
						+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
						+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
						+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN,ct.DestMDN, "
						+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
						+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
						+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
						+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
						+ "from CommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
						+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
						+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
						+ "and ct.LastUpdateTime between "
						+ lastUpdateTime + " and " + currentUpdateTime;
				
				public static final String dbtpctQuery = "select ct.ID, ct.TransactionsLogByTransactionID.ID, ct.CreateTime, ct.UICategory, "
						+ "et1.DisplayText, ct.TransferStatus, ct.TransferFailureReason, et2.DisplayText, "
						+ "ct.PocketBySourcePocketID.ID, ct.DestPocketID, ct.Amount, ct.Charges,"
						+ "ct.TaxAmount, ct.TransactionChargeID, ct.IsPartOfSharedUpChain, "
						+ "ct.SubscriberBySourceSubscriberID.ID, ct.SourcePocketType, ct.SourceMessage, "
						+ "ct.DestSubscriberID, ct.DestPocketType, ct.SourceMDN,ct.DestMDN,ct.DestMDN, "
						+ "ct.SourcePocketBalance, ct.DestPocketBalance, "
						+ "ct.CSRAction, ct.CSRActionTime, ct.CSRUserID, ct.CSRUserName, ct.NotificationCode,ctsctl.SctlId,ct.LastUpdateTime, "
						+ "ct.BankRetrievalReferenceNumber, ct.BankSystemTraceAuditNumber, "
						+ "ct.Currency,ct.OperatorRRN, ct.OperatorSTAN, ct.BankCode, ct.OperatorResponseCode "
						+ "from CommodityTransfer as ct,ChargeTxnCommodityTransferMap ctsctl, EnumText et1, EnumText et2 "
						+ "where et1.TagID=5636 and et1.EnumCode=ct.UICategory and ct.ID = ctsctl.CommodityTransferID "
						+ "and et2.TagID=5068 and et2.EnumCode=ct.TransferFailureReason "
						+ "and ct.LastUpdateTime between "
						+ lastUpdateTime + " and " + currentUpdateTime;
				
				
	// SCTL_Txn
	public static final String sctlTxnQuery1 = "select sctl.ID, sctl.TransactionID, "
			+ "sctl.SourceMDN, sctl.DestMDN, "
			+ "sctl.ServiceID,ss.ServiceName, "
			+ "sctl.TransactionTypeID, tt.TransactionName, "
			+ "sctl.ChannelCodeID,cc.ChannelName, "
			+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
			+ "sctl.CommodityTransferID,  "
			+ "sctl.Status,et.DisplayText, "
			+ "sctl.MFSBillerCode, "
			+ "sctl.Info1,sctl.InvoiceNo, "
			+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime, "
			+ "'', '' "
			+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et "
			+ "where sctl.TransactionTypeID = tt.ID "
			+ "and sctl.ChannelCodeID = cc.ID "
			+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
			+ "and sctl.CommodityTransferID is null "
			+ "and sctl.ServiceID = ss.ID and sctl.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime
			+ " Group By sctl.ID ";
	
	
	// SCTL_Txn
	public static final String sctlTxnQuery2 = "select sctl.ID, sctl.TransactionID, "
			+ "sctl.SourceMDN, sctl.DestMDN, "
			+ "sctl.ServiceID,ss.ServiceName, "
			+ "sctl.TransactionTypeID, tt.TransactionName, "
			+ "sctl.ChannelCodeID,cc.ChannelName, "
			+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
			+ "sctl.CommodityTransferID,  "
			+ "sctl.Status,et.DisplayText, "
			+ "sctl.MFSBillerCode, "
			+ "sctl.Info1,sctl.InvoiceNo, "
			+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime, "
			+ "min(srcCtctm.CommodityTransferID) as srcCommodityTransferID, max(srcCtctm.CommodityTransferID) as srcCommodityTransferID "
			+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et,ChargeTxnCommodityTransferMap srcCtctm,CommodityTransfer as destct "
			+ "where sctl.TransactionTypeID = tt.ID "
			+ "and sctl.ChannelCodeID = cc.ID "
			+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
			+ "and srcCtctm.SctlId = sctl.ID "
			+ "and sctl.Status != 5  "
			+ "and sctl.ServiceID = ss.ID " 
			+ "and (destct.ID = srcCtctm.CommodityTransferID and destct.UICategory != 31) " 
			+ "and sctl.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime
			+ " Group By sctl.ID ";
	
	// SCTL_Txn
		public static final String sctlTxnQuery4 = "select sctl.ID, sctl.TransactionID, "
				+ "sctl.SourceMDN, sctl.DestMDN, "
				+ "sctl.ServiceID,ss.ServiceName, "
				+ "sctl.TransactionTypeID, tt.TransactionName, "
				+ "sctl.ChannelCodeID,cc.ChannelName, "
				+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
				+ "sctl.CommodityTransferID,  "
				+ "sctl.Status,et.DisplayText, "
				+ "sctl.MFSBillerCode, "
				+ "sctl.Info1,sctl.InvoiceNo, "
				+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime, "
				+ "min(srcCtctm.CommodityTransferID) as srcCommodityTransferID, max(srcCtctm.CommodityTransferID) as srcCommodityTransferID "
				+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et,ChargeTxnCommodityTransferMap srcCtctm,PendingCommodityTransfer as destpct "
				+ "where sctl.TransactionTypeID = tt.ID "
				+ "and sctl.ChannelCodeID = cc.ID "
				+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
				+ "and srcCtctm.SctlId = sctl.ID "
				+ "and sctl.Status != 5  "
				+ "and sctl.ServiceID = ss.ID " 
				+ "and (destpct.ID = srcCtctm.CommodityTransferID and destpct.UICategory != 31) " 
				+ "and sctl.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime
				+ " Group By sctl.ID ";
	
	// SCTL_Txn
		public static final String sctlTxnQuery3 = "select sctl.ID, sctl.TransactionID, "
				+ "sctl.SourceMDN, sctl.DestMDN, "
				+ "sctl.ServiceID,ss.ServiceName, "
				+ "sctl.TransactionTypeID, tt.TransactionName, "
				+ "sctl.ChannelCodeID,cc.ChannelName, "
				+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
				+ "sctl.CommodityTransferID,  "
				+ "sctl.Status,et.DisplayText, "
				+ "sctl.MFSBillerCode, "
				+ "sctl.Info1,sctl.InvoiceNo, "
				+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime, "
				+ "min(srcCtctm.CommodityTransferID) as srcCommodityTransferID, min(srcCtctm.CommodityTransferID) as srcCommodityTransferID "
				+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et,ChargeTxnCommodityTransferMap srcCtctm "
				+ "where sctl.TransactionTypeID = tt.ID "
				+ "and sctl.ChannelCodeID = cc.ID "
				+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
				+ "and srcCtctm.SctlId = sctl.ID "
				+ "and sctl.Status = 5  "
				+ "and sctl.ServiceID = ss.ID and sctl.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime
				+ " Group By sctl.ID ";
	
	
	
	// Subscriber Based SctlTxn where sourceMdn as SubscriberMdn
		public static final String sourceSubsctlTxnQuery = "select sctl.ID, sctl.TransactionID, "
				+ "sctl.SourceMDN, sctl.DestMDN, sctl.SourceMDN, "
				+ "sctl.ServiceID,ss.ServiceName, "
				+ "sctl.TransactionTypeID, tt.TransactionName, "
				+ "sctl.ChannelCodeID,cc.ChannelName, "
				+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
				+ "sctl.CommodityTransferID,  "
				+ "sctl.Status,et.DisplayText, "
				+ "sctl.MFSBillerCode, "
				+ "sctl.Info1,sctl.InvoiceNo, "
				+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime "
				+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et "
				+ "where sctl.TransactionTypeID = tt.ID "
				+ "and sctl.ChannelCodeID = cc.ID "
				+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
				+ "and sctl.ServiceID = ss.ID and sctl.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime;
		
	// Subscriber Based SctlTxn where destMdn as SubscriberMdn
		public static final String destSubsctlTxnQuery = "select sctl.ID, sctl.TransactionID, "
				+ "sctl.SourceMDN, sctl.DestMDN, sctl.DestMDN, "
				+ "sctl.ServiceID,ss.ServiceName, "
				+ "sctl.TransactionTypeID, tt.TransactionName, "
				+ "sctl.ChannelCodeID,cc.ChannelName, "
				+ "sctl.TransactionAmount, sctl.CalculatedCharge, "
				+ "sctl.CommodityTransferID,  "
				+ "sctl.Status,et.DisplayText, "
				+ "sctl.MFSBillerCode, "
				+ "sctl.Info1,sctl.InvoiceNo, "
				+ "sctl.IsChargeDistributed, sctl.CreateTime, sctl.LastUpdateTime "
				+ "from ServiceChargeTransactionLog as sctl,TransactionType tt,Service ss,ChannelCode cc,EnumText et "
				+ "where sctl.TransactionTypeID = tt.ID "
				+ "and sctl.ChannelCodeID = cc.ID "
				+ "and et.TagID = 6089 and sctl.Status = et.EnumCode "
				+ "and sctl.ServiceID = ss.ID and sctl.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime;
	
	

	// UsersRoles
	public static final String userRolesQuery = "select mu.id, mu.Username, mu.Role, "
			+ "mu.Status, mu.LastUpdateTime, mu.UpdatedBy,mu.CreateTime  "
			+ "from User as mu "
			+ "where mu.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Users
	public static final String usersQuery = "select  mu.Username,mu.Role "
			+ "from User as mu " + "where mu.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Authorities
	public static final String authoritiesQuery = "select  mu.Username,mu.Role "
			+ "from User as mu "
			+ "where mu.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;

	// Ledger
	public static final String ledgerQuery = "select l.ID,l.CommodityTransferID,l.SourceMDN,l.SourcePocketID,"
			+ "l.SourcePocketBalance,l.DestMDN,l.DestPocketID,l.DestPocketBalance,l.Amount,l.CreateTime,l.LastUpdateTime from Ledger as l where l.LastUpdateTime between "
			+ lastUpdateTime + " and " + currentUpdateTime;
	
	// Integration Summary
	public static final String integreationSummaryQuery = "select itgsum.ID,itgsum.SctlId,itgsum.IntegrationType,itgsum.ReconcilationID1, "
				+ "itgsum.ReconcilationID2,itgsum.ReconcilationID3 " 
				+ "from IntegrationSummary as itgsum where itgsum.LastUpdateTime between "
				+ lastUpdateTime + " and " + currentUpdateTime;
	
	// Transaction Pending Summary
	public static final String txnPendingSummaryQuery = "select tps.ID,tps.SctlId,tps.CSRUserID,tps.CSRUserName,et.DisplayText,tps.CSRComment,tps.CSRActionTime "
			+ "from TransactionPendingSummary tps,EnumText et "
			+ "where et.TagID = 5327 and tps.CSRAction = et.EnumCode ";
			
	

	static {
		domainQueryMap.put("Subscriber", new String[] { subscriberQuery,partnerQueryForSubscriber });
		domainQueryMap.put("Partner", new String[] { partnerQuery });
		domainQueryMap.put("Pocket", new String[] { pocketQuery });
		domainQueryMap.put("UserRoles", new String[] { userRolesQuery });
		domainQueryMap.put("Users", new String[] { usersQuery });
		domainQueryMap.put("Authorities", new String[] { authoritiesQuery });
		domainQueryMap.put("KinInformationMissingAccount",new String[] { kinInfoMissingQuery });
		domainQueryMap.put("CommodityTransfer", new String[] { ctQuery,pctQuery });
		domainQueryMap.put("SctlTxn", new String[] { sctlTxnQuery1,sctlTxnQuery2,sctlTxnQuery4,sctlTxnQuery3 });
		domainQueryMap.put("Ledger", new String[] { ledgerQuery });
		domainQueryMap.put("SubscriberBasedCT", new String[] { sbtctQuery,sbtpctQuery,dbtctQuery,dbtpctQuery });
		domainQueryMap.put("SubscriberBasedSctlTxn", new String[] { sourceSubsctlTxnQuery,destSubsctlTxnQuery });
		domainQueryMap.put("IntegreationSummary", new String[] { integreationSummaryQuery });
		domainQueryMap.put("TransactionPendingSummary", new String[] { txnPendingSummaryQuery });

	}
	
	public static void main(String[] args){
		System.out.println(partnerQueryForSubscriber);
	}

}
