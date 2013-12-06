/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


DROP TABLE IF EXISTS `mfino`.`service_provider`;

DROP TABLE IF EXISTS `mfino`.`service`;
CREATE TABLE  `mfino`.`service` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Service_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_Service_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`transaction_type`;
CREATE TABLE  `mfino`.`transaction_type` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `TransactionName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionType_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionType_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`partner`;
CREATE TABLE  `mfino`.`partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `PartnerCode` int(11) NOT NULL,
  `PartnerStatus` int(11) NOT NULL,
  `TradeName` varchar(255) DEFAULT NULL,
  `TypeOfOrganization` varchar(255) DEFAULT NULL,
  `FaxNumber` varchar(255) DEFAULT NULL,
  `WebSite` varchar(255) DEFAULT NULL,
  `AuthorizedRepresentative` varchar(255) DEFAULT NULL,
  `RepresentativeName` varchar(255) DEFAULT NULL,
  `Designation` varchar(255) DEFAULT NULL,
  `FranchisePhoneNumber` varchar(255) DEFAULT NULL,
  `FranchiseOutletAddressID` bigint(20) DEFAULT NULL,
  `MerchantAddressID` bigint(20) DEFAULT NULL,
  `Classification` varchar(255) DEFAULT NULL,
  `NumberOfOutlets` int(11) DEFAULT NULL,
  `IndustryClassification` varchar(255) DEFAULT NULL,
  `YearEstablished` int(11) DEFAULT NULL,
  `AuthorizedFaxNumber` varchar(255) DEFAULT NULL,
  `AuthorizedEmail` varchar(255) DEFAULT NULL,
  `BusinessPartnerType` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Index_TradeName` (`TradeName`),
  KEY `FK_Partner_AddressByMerchantAddressID` (`MerchantAddressID`),
  KEY `FK_Partner_Subscriber` (`SubscriberID`),
  KEY `FK_Partner_AddressByFranchiseOutletAddressID` (`FranchiseOutletAddressID`),
  KEY `FK_Partner_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_Partner_User` (`UserID`),
  CONSTRAINT `FK_Partner_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FK_Partner_AddressByFranchiseOutletAddressID` FOREIGN KEY (`FranchiseOutletAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_Partner_AddressByMerchantAddressID` FOREIGN KEY (`MerchantAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_Partner_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_Partner_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;   

DROP TABLE IF EXISTS `mfino`.`service_provider_services`;
CREATE TABLE  `mfino`.`service_provider_services` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `ServiceProviderID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceProviderServices_ServiceProvider` (`ServiceProviderID`),
  KEY `FK_ServiceProviderServices_Service` (`ServiceID`),
  KEY `FK_ServiceProviderServices_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceProviderServices_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceProviderServices_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_ServiceProviderServices_ServiceProvider` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_transaction`;
CREATE TABLE  `mfino`.`service_transaction` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `TransactionTypeID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceTransaction_TransactionType` (`TransactionTypeID`),
  KEY `FK_ServiceTransaction_Service` (`ServiceID`),
  KEY `FK_ServiceTransaction_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceTransaction_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceTransaction_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_ServiceTransaction_TransactionType` FOREIGN KEY (`TransactionTypeID`) REFERENCES `transaction_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `mfino`.`partner_services`;
CREATE TABLE  `mfino`.`partner_services` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceProviderServicesID` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `DistributionChainTemplateID` bigint(20) DEFAULT NULL,
  `ParentID` bigint(20) DEFAULT NULL,
  `Level` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `CollectorPocket` bigint(20) NOT NULL,
  `SourcePocket` bigint(20) DEFAULT NULL,
  `IsServiceChargeShare` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ServiceProviderServicesID` (`ServiceProviderServicesID`,`PartnerID`),
  KEY `FK_PartnerServices_Partner` (`PartnerID`),
  KEY `FK_PartnerServices_ServiceProviderServices` (`ServiceProviderServicesID`),
  KEY `FK_PartnerServices_PocketByCollectorPocket` (`CollectorPocket`),
  KEY `FK_PartnerServices_DistributionChainTemplate` (`DistributionChainTemplateID`),
  KEY `FK_PartnerServices_PartnerByParentID` (`ParentID`),
  KEY `FK_PartnerServices_PocketBySourcePocket` (`SourcePocket`),
  KEY `FK_PartnerServices_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_PartnerServices_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_PartnerServices_DistributionChainTemplate` FOREIGN KEY (`DistributionChainTemplateID`) REFERENCES `distribution_chain_template` (`ID`),
  CONSTRAINT `FK_PartnerServices_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_PartnerServices_PartnerByParentID` FOREIGN KEY (`ParentID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_PartnerServices_PocketByCollectorPocket` FOREIGN KEY (`CollectorPocket`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_PartnerServices_PocketBySourcePocket` FOREIGN KEY (`SourcePocket`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_PartnerServices_ServiceProviderServices` FOREIGN KEY (`ServiceProviderServicesID`) REFERENCES `service_provider_services` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_charge_template`;
CREATE TABLE  `mfino`.`service_charge_template` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `ServiceTransactionID` bigint(20) NOT NULL,
  `ChannelCodeID` bigint(20) NOT NULL,
  `ServiceProviderID` bigint(20) NOT NULL,
  `ChargeType` int(11) DEFAULT NULL,
  `ServiceChargeType` int(11) DEFAULT NULL,
  `TaxesType` int(11) DEFAULT NULL,
  `MaxAmountPerTransaction` bigint(20) NOT NULL DEFAULT '0',
  `MinAmountPerTransaction` bigint(20) NOT NULL DEFAULT '0',
  `MaxAmountPerDay` bigint(20) NOT NULL DEFAULT '0',
  `MaxAmountPerWeek` bigint(20) NOT NULL DEFAULT '0',
  `MaxAmountPerMonth` bigint(20) NOT NULL DEFAULT '0',
  `MaxTransactionsPerDay` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerWeek` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerMonth` int(11) NOT NULL DEFAULT '0',
  `MinTimeBetweenTransactions` int(11) NOT NULL DEFAULT '0',
  `BusinessPartnerSharePercentage` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargeTemplate_ServiceProvider` (`ServiceProviderID`),
  KEY `FK_ServiceChargeTemplate_ChannelCode` (`ChannelCodeID`),
  KEY `FK_ServiceChargeTemplate_ServiceTransaction` (`ServiceTransactionID`),
  KEY `FK_ServiceChargeTemplate_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargeTemplate_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_ServiceProvider` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_ServiceTransaction` FOREIGN KEY (`ServiceTransactionID`) REFERENCES `service_transaction` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_charge_pricing`;
CREATE TABLE  `mfino`.`service_charge_pricing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `MinAmount` bigint(20) DEFAULT NULL,
  `MaxAmount` bigint(20) DEFAULT NULL,
  `ChargeInFixed` double DEFAULT NULL,
  `ChargeInPercentage` double DEFAULT NULL,
  `IsDefault` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargePricing_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_ServiceChargePricing_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargePricing_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceChargePricing_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`share_partners`;
CREATE TABLE  `mfino`.`share_partners` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `SharePercentage` double DEFAULT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SharePartners_Partner` (`PartnerID`),
  KEY `FK_SharePartners_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_SharePartners_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_SharePartners_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_SharePartners_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_SharePartners_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`partner_transactions`;
CREATE TABLE  `mfino`.`partner_transactions` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `ServiceTransactionID` bigint(20) NOT NULL,
  `ChannelCodeID` bigint(20) NOT NULL,
  `PartnerServicesID` bigint(20) NOT NULL,
  `CurrentBalance` bigint(20) DEFAULT NULL,
  `CurrentDailyExpenditure` bigint(20) NOT NULL DEFAULT '0',
  `CurrentWeeklyExpenditure` bigint(20) NOT NULL DEFAULT '0',
  `CurrentMonthlyExpenditure` bigint(20) NOT NULL DEFAULT '0',
  `CurrentDailyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentWeeklyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentMonthlyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `LastTransactionTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PartnerTransactions_ChannelCode` (`ChannelCodeID`),
  KEY `FK_PartnerTransactions_PartnerServices` (`PartnerServicesID`),
  KEY `FK_PartnerTransactions_ServiceTransaction` (`ServiceTransactionID`),
  KEY `FK_PartnerTransactions_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_PartnerTransactions_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_PartnerTransactions_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_PartnerTransactions_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`),
  CONSTRAINT `FK_PartnerTransactions_PartnerServices` FOREIGN KEY (`PartnerServicesID`) REFERENCES `partner_services` (`ID`),
  CONSTRAINT `FK_PartnerTransactions_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`),
  CONSTRAINT `FK_PartnerTransactions_ServiceTransaction` FOREIGN KEY (`ServiceTransactionID`) REFERENCES `service_transaction` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_partner`;
CREATE TABLE  `mfino`.`service_partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServicePartner_Partner` (`PartnerID`),
  CONSTRAINT `FK_ServicePartner_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`integration_partner`;
CREATE TABLE  `mfino`.`integration_partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_IntegrationPartner_Partner` (`PartnerID`),
  CONSTRAINT `FK_IntegrationPartner_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
                                       
DROP TABLE IF EXISTS `mfino`.`business_partner`;
CREATE TABLE  `mfino`.`business_partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BusinessPartner_Partner` (`PartnerID`),
  CONSTRAINT `FK_BusinessPartner_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`mfs_biller`;
CREATE TABLE  `mfino`.`mfs_biller` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `MFSBillerName` varchar(255) NOT NULL,
  `MFSBillerCode` varchar(255) NOT NULL,
  `MFSBillerType` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MFSBiller_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_MFSBiller_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`mfsbiller_partner_mapping`;
CREATE TABLE  `mfino`.`mfsbiller_partner_mapping` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `MFSBillerId` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `PartnerBillerCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MFSBillerPartner_Partner` (`PartnerID`),
  KEY `FK_MFSBillerPartner_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_MFSBillerPartner_MFSBiller` (`MFSBillerId`),
  CONSTRAINT `FK_MFSBillerPartner_MFSBiller` FOREIGN KEY (`MFSBillerId`) REFERENCES `mfs_biller` (`ID`),
  CONSTRAINT `FK_MFSBillerPartner_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_MFSBillerPartner_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_charge_transaction_log`;
CREATE TABLE  `mfino`.`service_charge_transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `TransactionID` bigint(20) DEFAULT NULL,
  `MFSBillerCode` varchar(255) NOT NULL,
  `PartnerID` bigint(20) DEFAULT NULL,
  `SourceMDN` varchar(255) NOT NULL,
  `SourcePocketID` bigint(20) DEFAULT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `ServiceName` varchar(255) NOT NULL,
  `TransactionTypeID` bigint(20) NOT NULL,
  `TransactionName` varchar(255) NOT NULL,
  `ServiceProviderID` bigint(20) DEFAULT NULL,
  `ChannelCodeID` bigint(20) NOT NULL,
  `TransactionAmount` double NOT NULL,
  `Mode` varchar(255) DEFAULT NULL,
  `InvoiceNo` varchar(255) DEFAULT NULL,
  `ServiceChargeTemplateID` bigint(20) DEFAULT NULL,
  `ServiceCharge` double DEFAULT NULL,
  `CommodityTransferID` bigint(20) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `FailureReason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargeTransactionLog_PocketBySourcePocketID` (`SourcePocketID`),
  KEY `FK_ServiceChargeTransactionLog_Partner` (`PartnerID`),
  KEY `FK_ServiceChargeTransactionLog_TransactionType` (`TransactionTypeID`),
  KEY `FK_ServiceChargeTransactionLog_ChannelCode` (`ChannelCodeID`),
  KEY `FK_ServiceChargeTransactionLog_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_ServiceChargeTransactionLog_Service` (`ServiceID`),
  KEY `FK_ServiceChargeTransactionLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_PocketBySourcePocketID` FOREIGN KEY (`SourcePocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_TransactionType` FOREIGN KEY (`TransactionTypeID`) REFERENCES `transaction_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`transaction_amount_distribution_log`;
CREATE TABLE  `mfino`.`transaction_amount_distribution_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTransactionLogID` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `PocketID` bigint(20) NOT NULL,
  `ShareAmount` double NOT NULL,
  `IsActualAmount` tinyint(4) DEFAULT NULL,
  `IsPartOfSharedUpChain` tinyint(4) DEFAULT NULL,
  `SettlementType` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionAmountDistributionLog_Pocket` (`PocketID`),
  KEY `FK_TransactionAmountDistributionLog_Partner` (`PartnerID`),
  KEY `FK_TransactionAmountDistributionLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`settlement_template`;
CREATE TABLE  `mfino`.`settlement_template` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `SettlementName` varchar(255) NOT NULL,
  `SettlementPocket` bigint(20) NOT NULL,
  `SettlementType` int(11) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SettlementTemplate_Partner` (`PartnerID`),
  KEY `FK_SettlementTemplate_PocketBySettlementPocket` (`SettlementPocket`),
  KEY `FK_SettlementTemplate_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_SettlementTemplate_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_SettlementTemplate_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_SettlementTemplate_PocketBySettlementPocket` FOREIGN KEY (`SettlementPocket`) REFERENCES `pocket` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`service_settlement_config`;
CREATE TABLE  `mfino`.`service_settlement_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `SettlementTemplateID` bigint(20) NOT NULL,
  `PartnerServiceID` bigint(20) NOT NULL,
  `StartDate` datetime DEFAULT NULL,
  `EndDate` datetime DEFAULT NULL,
  `IsDefault` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceSettlementConfig_SettlementTemplate` (`SettlementTemplateID`),
  KEY `FK_ServiceSettlementConfig_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_ServiceSettlementConfig_PartnerServicesByPartnerServiceID` (`PartnerServiceID`),
  CONSTRAINT `FK_ServiceSettlementConfig_PartnerServicesByPartnerServiceID` FOREIGN KEY (`PartnerServiceID`) REFERENCES `partner_services` (`ID`),
  CONSTRAINT `FK_ServiceSettlementConfig_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceSettlementConfig_SettlementTemplate` FOREIGN KEY (`SettlementTemplateID`) REFERENCES `settlement_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
          
DROP TABLE IF EXISTS `mfino`.`tax_pricing`;
CREATE TABLE  `mfino`.`tax_pricing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Percentage` double DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TaxPricing_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_TaxPricing_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TaxPricing_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TaxPricing_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`settlement_scheduler_logs`;
CREATE TABLE  `mfino`.`settlement_scheduler_logs` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `PartnerServicesID` bigint(20) NOT NULL,
  `ServiceSettlementConfigID` bigint(20) DEFAULT NULL,
  `QrtzJobId` varchar(255) DEFAULT NULL,
  `IsScheduled` tinyint(4) DEFAULT NULL,
  `ReasonText` varchar(512) DEFAULT NULL,
  `LastSettled` datetime DEFAULT NULL,
  `NextSettle` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`settlement_transaction_logs`;
CREATE TABLE  `mfino`.`settlement_transaction_logs` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `PartnerServicesID` bigint(20) NOT NULL,
  `CommodityTransferID` bigint(20) DEFAULT NULL,
  `ServiceSettlementConfigID` bigint(20) DEFAULT NULL,
  `TransferStatus` int(11) DEFAULT NULL,
  `Response` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;