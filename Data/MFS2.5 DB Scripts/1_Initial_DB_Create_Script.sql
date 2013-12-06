-- MySQL dump 10.13  Distrib 5.1.36, for Win32 (ia32)
--
-- Host: localhost    Database: mfino
-- ------------------------------------------------------
-- Server version	5.1.36-community-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Create schema mfino
--

DROP DATABASE IF EXISTS mfino;
CREATE DATABASE IF NOT EXISTS mfino;
USE mfino;

--
-- Table structure for table `activities_log`
--

DROP TABLE IF EXISTS `activities_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activities_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `ParentTransactionID` bigint(20) NOT NULL,
  `TransferID` bigint(20) DEFAULT NULL,
  `IsSuccessful` tinyint(4) DEFAULT NULL,
  `ErrorCode` int(11) DEFAULT NULL,
  `LOPID` bigint(20) DEFAULT NULL,
  `BulkUploadEntryID` bigint(20) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `MsgType` int(11) DEFAULT NULL,
  `SourceSubscriberID` bigint(20) DEFAULT NULL,
  `SourceSubscriberName` varchar(255) DEFAULT NULL,
  `SourceMDNID` bigint(20) DEFAULT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `SourcePocketID` bigint(20) DEFAULT NULL,
  `SourcePocketType` int(11) DEFAULT NULL,
  `SourceApplication` int(11) DEFAULT NULL,
  `ServletPath` varchar(255) DEFAULT NULL,
  `ISO8583_ProcessingCode` varchar(255) DEFAULT NULL,
  `ISO8583_PrimaryAccountNumber` varchar(255) DEFAULT NULL,
  `ISO8583_SystemTraceAuditNumber` varchar(255) DEFAULT NULL,
  `ISO8583_RetrievalReferenceNumber` varchar(255) DEFAULT NULL,
  `ISO8583_MerchantType` varchar(255) DEFAULT NULL,
  `ISO8583_AcquiringInstitutionIdentificationCode` int(11) DEFAULT NULL,
  `ISO8583_CardAcceptorIdentificationCode` varchar(255) DEFAULT NULL,
  `ISO8583_Variant` varchar(255) DEFAULT NULL,
  `ISO8583_ResponseCode` varchar(255) DEFAULT NULL,
  `WebClientIP` varchar(255) DEFAULT NULL,
  `Commodity` int(11) DEFAULT NULL,
  `ActivityCategory` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ActivitiesLog_Company` (`CompanyID`),
  CONSTRAINT `FK_ActivitiesLog_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activities_log`
--

LOCK TABLES `activities_log` WRITE;
/*!40000 ALTER TABLE `activities_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `activities_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `address`
--

DROP TABLE IF EXISTS `address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `address` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Line1` varchar(255) DEFAULT NULL,
  `Line2` varchar(255) DEFAULT NULL,
  `City` varchar(255) NOT NULL DEFAULT '',
  `State` varchar(255) NOT NULL DEFAULT '',
  `ZipCode` varchar(255) NOT NULL DEFAULT '',
  `Country` varchar(255) NOT NULL DEFAULT '',
  `RegionName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `address`
--

LOCK TABLES `address` WRITE;
/*!40000 ALTER TABLE `address` DISABLE KEYS */;
/*!40000 ALTER TABLE `address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `agent_cashin_transaction_log`
--

DROP TABLE IF EXISTS `agent_cashin_transaction_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `agent_cashin_transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `DestPartnerID` bigint(20) DEFAULT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `SourcePocketID` bigint(20) DEFAULT NULL,
  `DestPocketID` bigint(20) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `agent_cashin_transaction_log`
--

LOCK TABLES `agent_cashin_transaction_log` WRITE;
/*!40000 ALTER TABLE `agent_cashin_transaction_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `agent_cashin_transaction_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `authorizing_person_details`
--

DROP TABLE IF EXISTS `authorizing_person_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `authorizing_person_details` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `FirstName` varchar(255) DEFAULT NULL,
  `LastName` varchar(255) DEFAULT NULL,
  `AddressID` bigint(20) DEFAULT NULL,
  `IDDesc` varchar(255) DEFAULT NULL,
  `IDNumber` varchar(255) DEFAULT NULL,
  `DateOfBirth` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_AuthorizingPerson_Address` (`AddressID`),
  KEY `FK_AuthorizingPerson_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_AuthorizingPerson_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_AuthorizingPerson_Address` FOREIGN KEY (`AddressID`) REFERENCES `address` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authorizing_person_details`
--

LOCK TABLES `authorizing_person_details` WRITE;
/*!40000 ALTER TABLE `authorizing_person_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `authorizing_person_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank`
--

DROP TABLE IF EXISTS `bank`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `BankCode` int(11) DEFAULT NULL,
  `Header` varchar(255) DEFAULT NULL,
  `ContactNumber` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `StatusTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BankCode` (`BankCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank`
--

LOCK TABLES `bank` WRITE;
/*!40000 ALTER TABLE `bank` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bank_admin`
--

DROP TABLE IF EXISTS `bank_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bank_admin` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `BankID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BankAdmin_Bank` (`BankID`),
  KEY `FK_BankAdmin_User` (`UserID`),
  CONSTRAINT `FK_BankAdmin_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FK_BankAdmin_Bank` FOREIGN KEY (`BankID`) REFERENCES `bank` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bank_admin`
--

LOCK TABLES `bank_admin` WRITE;
/*!40000 ALTER TABLE `bank_admin` DISABLE KEYS */;
/*!40000 ALTER TABLE `bank_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_payment_transaction`
--

DROP TABLE IF EXISTS `bill_payment_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bill_payment_transaction` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `ParentTransactionID` bigint(20) NOT NULL,
  `TransactionID` bigint(20) DEFAULT NULL,
  `BankCode` int(11) NOT NULL,
  `BillerID` bigint(20) DEFAULT NULL,
  `BillerName` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `BillPaymentReferenceID` varchar(255) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  `CustomerID` varchar(255) NOT NULL,
  `BillPaymentTransactionType` int(11) NOT NULL,
  `TransactionFee` decimal(25,4) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `TransactionDate` varchar(255) DEFAULT NULL,
  `BillerCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BillPaymentTransaction_Subscriber` (`SubscriberID`),
  KEY `FK_BillPaymentTransaction_Company` (`CompanyID`),
  CONSTRAINT `FK_BillPaymentTransaction_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_BillPaymentTransaction_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_payment_transaction`
--

LOCK TABLES `bill_payment_transaction` WRITE;
/*!40000 ALTER TABLE `bill_payment_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `bill_payment_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `biller`
--

DROP TABLE IF EXISTS `biller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `biller` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `BankCode` int(11) NOT NULL,
  `BillerCode` varchar(255) NOT NULL,
  `BillerName` varchar(255) DEFAULT NULL,
  `BillerType` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  `TransactionFee` decimal(25,4) DEFAULT NULL,
  `BillRefOffSet` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BankCode` (`BankCode`,`BillerCode`),
  UNIQUE KEY `BillerName` (`BillerName`),
  KEY `FK_Biller_Company` (`CompanyID`),
  CONSTRAINT `FK_Biller_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `biller`
--

LOCK TABLES `biller` WRITE;
/*!40000 ALTER TABLE `biller` DISABLE KEYS */;
/*!40000 ALTER TABLE `biller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `brand`
--

DROP TABLE IF EXISTS `brand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `brand` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `InternationalCountryCode` varchar(255) NOT NULL,
  `PrefixCode` varchar(255) NOT NULL,
  `BrandName` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BrandName` (`BrandName`),
  KEY `FK_Brand_Company` (`CompanyID`),
  KEY `FK_Brand_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_Brand_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_Brand_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `brand`
--

LOCK TABLES `brand` WRITE;
/*!40000 ALTER TABLE `brand` DISABLE KEYS */;
/*!40000 ALTER TABLE `brand` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulk_bank_account`
--

DROP TABLE IF EXISTS `bulk_bank_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bulk_bank_account` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `FileName` varchar(255) NOT NULL,
  `FileData` longtext NOT NULL,
  `TotalLineCount` int(11) DEFAULT NULL,
  `ErrorLineCount` int(11) DEFAULT NULL,
  `UploadFileStatus` int(11) NOT NULL,
  `UploadReport` longtext,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulk_bank_account`
--

LOCK TABLES `bulk_bank_account` WRITE;
/*!40000 ALTER TABLE `bulk_bank_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulk_bank_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulk_lop`
--

DROP TABLE IF EXISTS `bulk_lop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bulk_lop` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantID` bigint(20) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `DCTLevelID` bigint(20) DEFAULT NULL,
  `DCTID` bigint(20) DEFAULT NULL,
  `LevelPermissions` int(11) DEFAULT NULL,
  `GiroRefID` varchar(255) DEFAULT NULL,
  `TransferDate` varchar(255) DEFAULT NULL,
  `ActualAmountPaid` decimal(25,4) DEFAULT NULL,
  `AmountDistributed` decimal(25,4) DEFAULT NULL,
  `Status` varchar(255) DEFAULT NULL,
  `DistributedBy` varchar(255) DEFAULT NULL,
  `DistributeTime` datetime DEFAULT NULL,
  `ApprovedBy` varchar(255) DEFAULT NULL,
  `ApprovalTime` datetime DEFAULT NULL,
  `RejectedBy` varchar(255) DEFAULT NULL,
  `RejectTime` datetime DEFAULT NULL,
  `Comment` varchar(255) DEFAULT NULL,
  `FileData` longtext,
  `SourceApplication` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_BulkLOP_Merchant` (`MerchantID`),
  KEY `FK_BulkLOP_Company` (`CompanyID`),
  KEY `FK_BulkLOP_DistributionChainTemplateByDCTID` (`DCTID`),
  KEY `FK_BulkLOP_DistributionChainLevelByDCTLevelID` (`DCTLevelID`),
  KEY `FK_BulkLOP_SubscriberMDNByMDNID` (`MDNID`),
  CONSTRAINT `FK_BulkLOP_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_BulkLOP_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_BulkLOP_DistributionChainLevelByDCTLevelID` FOREIGN KEY (`DCTLevelID`) REFERENCES `distribution_chain_level` (`ID`),
  CONSTRAINT `FK_BulkLOP_DistributionChainTemplateByDCTID` FOREIGN KEY (`DCTID`) REFERENCES `distribution_chain_template` (`ID`),
  CONSTRAINT `FK_BulkLOP_Merchant` FOREIGN KEY (`MerchantID`) REFERENCES `merchant` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulk_lop`
--

LOCK TABLES `bulk_lop` WRITE;
/*!40000 ALTER TABLE `bulk_lop` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulk_lop` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulk_upload`
--

DROP TABLE IF EXISTS `bulk_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bulk_upload` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `UserName` varchar(255) NOT NULL,
  `MDN` varchar(255) NOT NULL,
  `InFileName` varchar(255) NOT NULL,
  `InFileData` longtext NOT NULL,
  `InFileCreateDate` varchar(255) NOT NULL,
  `OutFileName` varchar(255) DEFAULT NULL,
  `OutFileData` longtext,
  `ReportFileName` varchar(255) DEFAULT NULL,
  `ReportFileData` longtext,
  `FileType` int(11) NOT NULL,
  `DeliveryStatus` int(11) NOT NULL,
  `DeliveryDate` datetime DEFAULT NULL,
  `FailedTransactionsCount` int(11) DEFAULT NULL,
  `TransactionsCount` int(11) NOT NULL,
  `TotalAmount` decimal(25,4) NOT NULL,
  `SuccessAmount` decimal(25,4) NOT NULL,
  `VerificationChecksum` bigint(20) NOT NULL,
  `DigitalSignature` varchar(255) NOT NULL,
  `FileError` int(11) DEFAULT NULL,
  `ProcessID` int(11) DEFAULT NULL,
  `WebClientIP` varchar(255) DEFAULT NULL,
  `BankUploadTryCounter` int(11) NOT NULL DEFAULT '0',
  `BankUploadLastTryDate` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SubscriberID` (`SubscriberID`,`InFileCreateDate`,`FileType`,`TransactionsCount`,`TotalAmount`,`VerificationChecksum`),
  KEY `FK_BulkUpload_Subscriber` (`SubscriberID`),
  KEY `FK_BulkUpload_Company` (`CompanyID`),
  KEY `FK_BulkUpload_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_BulkUpload_User` (`UserID`),
  CONSTRAINT `FK_BulkUpload_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FK_BulkUpload_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_BulkUpload_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_BulkUpload_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulk_upload`
--

LOCK TABLES `bulk_upload` WRITE;
/*!40000 ALTER TABLE `bulk_upload` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulk_upload` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulk_upload_entry`
--

DROP TABLE IF EXISTS `bulk_upload_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bulk_upload_entry` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `UploadID` bigint(20) NOT NULL,
  `LineNumber` int(11) NOT NULL,
  `Status` int(11) NOT NULL,
  `TransferFailureReason` int(11) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `TransferID` bigint(20) DEFAULT NULL,
  `Amount` decimal(25,4) NOT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `UploadID` (`UploadID`,`LineNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulk_upload_entry`
--

LOCK TABLES `bulk_upload_entry` WRITE;
/*!40000 ALTER TABLE `bulk_upload_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulk_upload_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bulk_upload_file`
--

DROP TABLE IF EXISTS `bulk_upload_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bulk_upload_file` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `FileName` varchar(255) NOT NULL,
  `FileData` longtext NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `RecordCount` int(11) DEFAULT NULL,
  `TotalLineCount` int(11) DEFAULT NULL,
  `ErrorLineCount` int(11) DEFAULT NULL,
  `UploadFileStatus` int(11) NOT NULL,
  `UploadReport` longtext,
  `RecordType` int(11) DEFAULT NULL,
  `FileProcessedDate` datetime DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BulkUploadFile_Company` (`CompanyID`),
  CONSTRAINT `FK_BulkUploadFile_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bulk_upload_file`
--

LOCK TABLES `bulk_upload_file` WRITE;
/*!40000 ALTER TABLE `bulk_upload_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `bulk_upload_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `business_partner`
--

DROP TABLE IF EXISTS `business_partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `business_partner` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `business_partner`
--

LOCK TABLES `business_partner` WRITE;
/*!40000 ALTER TABLE `business_partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `business_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `card_info`
--

DROP TABLE IF EXISTS `card_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `card_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `CardF6` varchar(255) DEFAULT NULL,
  `CardL4` varchar(255) DEFAULT NULL,
  `IssuerName` varchar(255) DEFAULT NULL,
  `NameOnCard` varchar(255) DEFAULT NULL,
  `AddressID` bigint(20) DEFAULT NULL,
  `BillingAddressID` bigint(20) DEFAULT NULL,
  `OldCardF6` varchar(255) DEFAULT NULL,
  `OldCardL4` varchar(255) DEFAULT NULL,
  `OldIssuerName` varchar(255) DEFAULT NULL,
  `OldNameOnCard` varchar(255) DEFAULT NULL,
  `OldAddressID` bigint(20) DEFAULT NULL,
  `OldBillingAddressID` bigint(20) DEFAULT NULL,
  `PocketID` bigint(20) DEFAULT NULL,
  `CardStatus` int(11) NOT NULL,
  `isConformationRequired` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CardInfo_Pocket` (`PocketID`),
  KEY `FK_CardInfo_Subscriber` (`SubscriberID`),
  KEY `FK_CardInfo_Address` (`AddressID`),
  KEY `FK_CardInfo_AddressByOldBillingAddressID` (`OldBillingAddressID`),
  KEY `FK_CardInfo_AddressByOldAddressID` (`OldAddressID`),
  KEY `FK_CardInfo_AddressByBillingAddressID` (`BillingAddressID`),
  CONSTRAINT `FK_CardInfo_AddressByBillingAddressID` FOREIGN KEY (`BillingAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_CardInfo_Address` FOREIGN KEY (`AddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_CardInfo_AddressByOldAddressID` FOREIGN KEY (`OldAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_CardInfo_AddressByOldBillingAddressID` FOREIGN KEY (`OldBillingAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_CardInfo_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CardInfo_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `card_info`
--

LOCK TABLES `card_info` WRITE;
/*!40000 ALTER TABLE `card_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `card_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channel_code`
--

DROP TABLE IF EXISTS `channel_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `channel_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ChannelCode` varchar(255) NOT NULL,
  `ChannelName` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `ChannelSourceApplication` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ChannelCode` (`ChannelCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `channel_code`
--

LOCK TABLES `channel_code` WRITE;
/*!40000 ALTER TABLE `channel_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `channel_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `channel_session_management`
--

DROP TABLE IF EXISTS `channel_session_management`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `channel_session_management` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `SessionKey` varchar(512) DEFAULT NULL,
  `RequestCountAfterLogin` int(11) DEFAULT NULL,
  `LastRequestTime` datetime DEFAULT NULL,
  `LastLoginTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ChannelSessionManagement_SubscriberMDNByMDNID` (`MDNID`),
  CONSTRAINT `FK_ChannelSessionManagement_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `channel_session_management`
--

LOCK TABLES `channel_session_management` WRITE;
/*!40000 ALTER TABLE `channel_session_management` DISABLE KEYS */;
/*!40000 ALTER TABLE `channel_session_management` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge_definition`
--

DROP TABLE IF EXISTS `charge_definition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charge_definition` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `ChargeTypeID` bigint(20) NOT NULL,
  `DependantChargeTypeID` bigint(20) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `CalculationType` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name` (`Name`),
  KEY `FK_ChargeDefinition_ChargeTypeByDependantChargeTypeID` (`DependantChargeTypeID`),
  KEY `FK_ChargeDefinition_ChargeType` (`ChargeTypeID`),
  KEY `FK_ChargeDefinition_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ChargeDefinition_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ChargeDefinition_ChargeType` FOREIGN KEY (`ChargeTypeID`) REFERENCES `charge_type` (`ID`),
  CONSTRAINT `FK_ChargeDefinition_ChargeTypeByDependantChargeTypeID` FOREIGN KEY (`DependantChargeTypeID`) REFERENCES `charge_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge_definition`
--

LOCK TABLES `charge_definition` WRITE;
/*!40000 ALTER TABLE `charge_definition` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge_definition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge_pricing`
--

DROP TABLE IF EXISTS `charge_pricing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charge_pricing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ChargeDefinitionID` bigint(20) NOT NULL,
  `MinAmount` decimal(25,4) DEFAULT NULL,
  `MaxAmount` decimal(25,4) DEFAULT NULL,
  `ChargeInFixed` decimal(25,4) DEFAULT NULL,
  `ChargeInPercentage` decimal(25,4) DEFAULT NULL,
  `IsDefault` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ChargePricing_ChargeDefinition` (`ChargeDefinitionID`),
  KEY `FK_ChargePricing_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ChargePricing_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ChargePricing_ChargeDefinition` FOREIGN KEY (`ChargeDefinitionID`) REFERENCES `charge_definition` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge_pricing`
--

LOCK TABLES `charge_pricing` WRITE;
/*!40000 ALTER TABLE `charge_pricing` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge_pricing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `charge_type`
--

DROP TABLE IF EXISTS `charge_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `charge_type` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name` (`Name`),
  KEY `FK_ChargeType_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ChargeType_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `charge_type`
--

LOCK TABLES `charge_type` WRITE;
/*!40000 ALTER TABLE `charge_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `charge_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `commodity_transfer`
--

DROP TABLE IF EXISTS `commodity_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `commodity_transfer` (
  `ID` bigint(20) NOT NULL,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `TransactionID` bigint(20) NOT NULL,
  `MsgType` int(11) NOT NULL,
  `UICategory` int(11) DEFAULT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `TransferStatus` int(11) NOT NULL,
  `TransferFailureReason` int(11) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `StartTime` datetime NOT NULL,
  `EndTime` datetime DEFAULT NULL,
  `ExpirationTimeout` int(11) NOT NULL DEFAULT '60000',
  `SourceIP` varchar(255) DEFAULT NULL,
  `SourceReferenceID` varchar(255) DEFAULT NULL,
  `SourceTerminalID` varchar(255) DEFAULT NULL,
  `SourceMDN` varchar(255) NOT NULL,
  `SourceMDNID` bigint(20) NOT NULL,
  `LOPID` bigint(20) DEFAULT NULL,
  `DCTLevelID` bigint(20) DEFAULT NULL,
  `LevelPermissions` int(11) DEFAULT NULL,
  `SourceSubscriberID` bigint(20) NOT NULL,
  `SourceSubscriberName` varchar(255) DEFAULT NULL,
  `SourcePocketAllowance` int(11) DEFAULT NULL,
  `SourcePocketType` int(11) NOT NULL,
  `SourcePocketID` bigint(20) NOT NULL,
  `SourcePocketBalance` varchar(255) DEFAULT NULL,
  `SourceCardPAN` varchar(512) DEFAULT NULL,
  `SourceMessage` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `DestMDNID` bigint(20) DEFAULT NULL,
  `DestSubscriberID` bigint(20) DEFAULT NULL,
  `DestSubscriberName` varchar(255) DEFAULT NULL,
  `DestPocketAllowance` int(11) DEFAULT NULL,
  `DestPocketType` int(11) DEFAULT NULL,
  `DestPocketID` bigint(20) DEFAULT NULL,
  `DestPocketBalance` varchar(255) DEFAULT NULL,
  `DestBankAccountName` varchar(255) DEFAULT NULL,
  `DestCardPAN` varchar(512) DEFAULT NULL,
  `BillingType` int(11) DEFAULT NULL,
  `Amount` decimal(25,4) NOT NULL,
  `Charges` decimal(25,4) NOT NULL,
  `Commodity` int(11) NOT NULL,
  `BucketType` varchar(255) DEFAULT NULL,
  `SourceApplication` int(11) NOT NULL,
  `ServletPath` varchar(255) DEFAULT NULL,
  `Currency` varchar(255) NOT NULL,
  `BankCode` int(11) DEFAULT NULL,
  `OperatorCode` int(11) DEFAULT NULL,
  `OperatorResponseTime` datetime DEFAULT NULL,
  `OperatorResponseCode` int(11) DEFAULT NULL,
  `OperatorRejectReason` varchar(255) DEFAULT NULL,
  `OperatorErrorText` varchar(1000) DEFAULT NULL,
  `OperatorAuthorizationCode` varchar(255) DEFAULT NULL,
  `OperatorReversalResponseTime` datetime DEFAULT NULL,
  `OperatorReversalResponseCode` int(11) DEFAULT NULL,
  `OperatorReversalRejectReason` varchar(255) DEFAULT NULL,
  `OperatorReversalErrorText` varchar(255) DEFAULT NULL,
  `OperatorReversalCount` int(11) DEFAULT NULL,
  `OperatorLastReversalTime` datetime DEFAULT NULL,
  `TopupPeriod` bigint(20) DEFAULT NULL,
  `BankRetrievalReferenceNumber` varchar(255) DEFAULT NULL,
  `BankSystemTraceAuditNumber` varchar(255) DEFAULT NULL,
  `BankResponseTime` datetime DEFAULT NULL,
  `BankResponseCode` int(11) DEFAULT NULL,
  `BankRejectReason` varchar(255) DEFAULT NULL,
  `BankErrorText` varchar(3000) DEFAULT NULL,
  `BankAuthorizationCode` varchar(255) DEFAULT NULL,
  `BankReversalResponseTime` datetime DEFAULT NULL,
  `BankReversalResponseCode` int(11) DEFAULT NULL,
  `BankReversalRejectReason` varchar(255) DEFAULT NULL,
  `BankReversalErrorText` varchar(3000) DEFAULT NULL,
  `BankReversalAuthorizationCode` varchar(255) DEFAULT NULL,
  `ReversalCount` int(11) DEFAULT NULL,
  `LastReversalTime` datetime DEFAULT NULL,
  `CSRAction` int(11) DEFAULT NULL,
  `CSRActionTime` datetime DEFAULT NULL,
  `CSRUserID` bigint(20) DEFAULT NULL,
  `CSRUserName` varchar(255) DEFAULT NULL,
  `CSRComment` varchar(255) DEFAULT NULL,
  `ISO8583_ProcessingCode` varchar(255) DEFAULT NULL,
  `ISO8583_PrimaryAccountNumber` varchar(255) DEFAULT NULL,
  `ISO8583_SystemTraceAuditNumber` varchar(255) DEFAULT NULL,
  `ISO8583_LocalTransactionTimeHhmmss` varchar(255) DEFAULT NULL,
  `ISO8583_MerchantType` varchar(255) DEFAULT NULL,
  `ISO8583_AcquiringInstitutionIdentificationCode` int(11) DEFAULT NULL,
  `ISO8583_RetrievalReferenceNumber` varchar(255) DEFAULT NULL,
  `ISO8583_CardAcceptorIdentificationCode` varchar(255) DEFAULT NULL,
  `ISO8583_Variant` varchar(255) DEFAULT NULL,
  `ISO8583_ResponseCode` varchar(255) DEFAULT NULL,
  `BulkUploadID` bigint(20) DEFAULT NULL,
  `BulkUploadLineNumber` int(11) DEFAULT NULL,
  `CopyToPermanentError` varchar(255) DEFAULT NULL,
  `WebClientIP` varchar(255) DEFAULT NULL,
  `OperatorRRN` varchar(255) DEFAULT NULL,
  `OperatorSTAN` varchar(255) DEFAULT NULL,
  `ProductIndicatorCode` varchar(255) DEFAULT NULL,
  `Units` bigint(20) DEFAULT NULL,
  `Denomination` bigint(20) DEFAULT NULL,
  `CreditCardTransactionID` bigint(20) DEFAULT NULL,
  `TransactionChargeID` bigint(20) DEFAULT NULL,
  `IsPartOfSharedUpChain` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BulkUploadID` (`BulkUploadID`,`BulkUploadLineNumber`),
  KEY `FK_CommodityTransfer_PocketBySourcePocketID` (`SourcePocketID`),
  KEY `FK_CommodityTransfer_CreditCardTransaction` (`CreditCardTransactionID`),
  KEY `FK_CommodityTransfer_Company` (`CompanyID`),
  KEY `FK_CommodityTransfer_TransactionsLogByTransactionID` (`TransactionID`),
  KEY `FK_CommodityTransfer_LOP` (`LOPID`),
  KEY `FK_CommodityTransfer_SubscriberMDNBySourceMDNID` (`SourceMDNID`),
  KEY `FK_CommodityTransfer_SubscriberBySourceSubscriberID` (`SourceSubscriberID`),
  KEY `FK_CommodityTransfer_DistributionChainLevelByDCTLevelID` (`DCTLevelID`),
  KEY `FK_CommodityTransfer_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_CommodityTransfer_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_DistributionChainLevelByDCTLevelID` FOREIGN KEY (`DCTLevelID`) REFERENCES `distribution_chain_level` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_LOP` FOREIGN KEY (`LOPID`) REFERENCES `letter_of_purchase` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_PocketBySourcePocketID` FOREIGN KEY (`SourcePocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_SubscriberBySourceSubscriberID` FOREIGN KEY (`SourceSubscriberID`) REFERENCES `subscriber` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_SubscriberMDNBySourceMDNID` FOREIGN KEY (`SourceMDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_CommodityTransfer_TransactionsLogByTransactionID` FOREIGN KEY (`TransactionID`) REFERENCES `transaction_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `commodity_transfer`
--

LOCK TABLES `commodity_transfer` WRITE;
/*!40000 ALTER TABLE `commodity_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `commodity_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `company`
--

DROP TABLE IF EXISTS `company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `company` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `CompanyName` varchar(255) NOT NULL,
  `CompanyCode` int(11) NOT NULL,
  `CustomerServiceNumber` varchar(255) DEFAULT NULL,
  `smsc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `CompanyCode` (`CompanyCode`),
  UNIQUE KEY `CompanyName` (`CompanyName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `company`
--

LOCK TABLES `company` WRITE;
/*!40000 ALTER TABLE `company` DISABLE KEYS */;
/*!40000 ALTER TABLE `company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card_destinations`
--

DROP TABLE IF EXISTS `credit_card_destinations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credit_card_destinations` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `OldDestMDN` varchar(255) DEFAULT NULL,
  `CCMDNStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CreditCardDestinations_Subscriber` (`SubscriberID`),
  CONSTRAINT `FK_CreditCardDestinations_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card_destinations`
--

LOCK TABLES `credit_card_destinations` WRITE;
/*!40000 ALTER TABLE `credit_card_destinations` DISABLE KEYS */;
/*!40000 ALTER TABLE `credit_card_destinations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credit_card_transaction`
--

DROP TABLE IF EXISTS `credit_card_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `credit_card_transaction` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `PocketID` bigint(20) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  `PaymentMethod` varchar(255) DEFAULT NULL,
  `ErrCode` varchar(255) DEFAULT NULL,
  `UserCode` varchar(255) DEFAULT NULL,
  `TransStatus` varchar(255) DEFAULT NULL,
  `CurrCode` varchar(255) DEFAULT NULL,
  `EUI` varchar(255) DEFAULT NULL,
  `TransactionDate` varchar(255) DEFAULT NULL,
  `NSIATransCompletionTime` datetime DEFAULT NULL,
  `TransType` varchar(255) DEFAULT NULL,
  `IsBlackListed` varchar(255) DEFAULT NULL,
  `FraudRiskLevel` int(11) DEFAULT NULL,
  `FraudRiskScore` decimal(25,4) DEFAULT NULL,
  `ExceedHighRisk` varchar(255) DEFAULT NULL,
  `CardType` varchar(255) DEFAULT NULL,
  `CardNoPartial` varchar(255) DEFAULT NULL,
  `CardName` varchar(255) DEFAULT NULL,
  `AcquirerBank` varchar(255) DEFAULT NULL,
  `BankResCode` varchar(255) DEFAULT NULL,
  `BankResMsg` varchar(255) DEFAULT NULL,
  `AuthID` varchar(255) DEFAULT NULL,
  `BankReference` varchar(255) DEFAULT NULL,
  `WhiteListCard` varchar(255) DEFAULT NULL,
  `Operation` varchar(255) DEFAULT NULL,
  `MDN` varchar(255) DEFAULT NULL,
  `TransactionID` bigint(20) DEFAULT NULL,
  `BillReferenceNumber` bigint(20) DEFAULT NULL,
  `CCFailureReason` int(11) DEFAULT NULL,
  `SessionID` varchar(255) DEFAULT NULL,
  `CCBucketType` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `SourceIP` varchar(255) DEFAULT NULL,
  `PaymentGatewayEDU` tinyint(4) DEFAULT NULL,
  `IsVoid` tinyint(4) DEFAULT NULL,
  `VoidBy` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CreditCardTransaction_Pocket` (`PocketID`),
  KEY `FK_CreditCardTransaction_Subscriber` (`SubscriberID`),
  KEY `FK_CreditCardTransaction_Company` (`CompanyID`),
  CONSTRAINT `FK_CreditCardTransaction_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credit_card_transaction`
--

LOCK TABLES `credit_card_transaction` WRITE;
/*!40000 ALTER TABLE `credit_card_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `credit_card_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `db_param`
--

DROP TABLE IF EXISTS `db_param`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `db_param` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `db_param`
--

LOCK TABLES `db_param` WRITE;
/*!40000 ALTER TABLE `db_param` DISABLE KEYS */;
/*!40000 ALTER TABLE `db_param` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `denomination`
--

DROP TABLE IF EXISTS `denomination`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `denomination` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `DenominationAmount` decimal(25,4) NOT NULL,
  `BillerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Denomination_Biller` (`BillerID`),
  CONSTRAINT `FK_Denomination_Biller` FOREIGN KEY (`BillerID`) REFERENCES `biller` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `denomination`
--

LOCK TABLES `denomination` WRITE;
/*!40000 ALTER TABLE `denomination` DISABLE KEYS */;
/*!40000 ALTER TABLE `denomination` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribution_chain_level`
--

DROP TABLE IF EXISTS `distribution_chain_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribution_chain_level` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `TemplateID` bigint(20) NOT NULL,
  `Level` int(11) NOT NULL,
  `Permissions` int(11) NOT NULL,
  `Commission` decimal(25,4) DEFAULT NULL,
  `MaxCommission` decimal(25,4) DEFAULT NULL,
  `MinCommission` decimal(25,4) DEFAULT NULL,
  `MaxWeeklyLOPAmount` decimal(25,4) DEFAULT NULL,
  `MaxLOPAmount` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TemplateID` (`TemplateID`,`Level`),
  KEY `FK_DistributionChainLevel_DistributionChainTemplateByTemplateID` (`TemplateID`),
  CONSTRAINT `FK_DistributionChainLevel_DistributionChainTemplateByTemplateID` FOREIGN KEY (`TemplateID`) REFERENCES `distribution_chain_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribution_chain_level`
--

LOCK TABLES `distribution_chain_level` WRITE;
/*!40000 ALTER TABLE `distribution_chain_level` DISABLE KEYS */;
/*!40000 ALTER TABLE `distribution_chain_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribution_chain_template`
--

DROP TABLE IF EXISTS `distribution_chain_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribution_chain_template` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_DistributionChainTemplate_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_DistributionChainTemplate_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribution_chain_template`
--

LOCK TABLES `distribution_chain_template` WRITE;
/*!40000 ALTER TABLE `distribution_chain_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `distribution_chain_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enum_text`
--

DROP TABLE IF EXISTS `enum_text`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enum_text` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Language` int(11) NOT NULL DEFAULT '0',
  `TagName` varchar(255) NOT NULL,
  `TagID` int(11) NOT NULL,
  `EnumCode` varchar(255) NOT NULL,
  `EnumValue` varchar(255) NOT NULL,
  `DisplayText` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Language` (`Language`,`TagID`,`EnumCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enum_text`
--

LOCK TABLES `enum_text` WRITE;
/*!40000 ALTER TABLE `enum_text` DISABLE KEYS */;
/*!40000 ALTER TABLE `enum_text` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `integration_partner`
--

DROP TABLE IF EXISTS `integration_partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `integration_partner` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `integration_partner`
--

LOCK TABLES `integration_partner` WRITE;
/*!40000 ALTER TABLE `integration_partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `integration_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kyc_fields`
--

DROP TABLE IF EXISTS `kyc_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kyc_fields` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `KYCFieldsLevelID` bigint(20) NOT NULL,
  `KYCFieldsName` varchar(255) NOT NULL,
  `KYCFieldsDescription` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_KYCFields_KYCLevelByKYCFieldsLevelID` (`KYCFieldsLevelID`),
  CONSTRAINT `FK_KYCFields_KYCLevelByKYCFieldsLevelID` FOREIGN KEY (`KYCFieldsLevelID`) REFERENCES `kyc_level` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_fields`
--

LOCK TABLES `kyc_fields` WRITE;
/*!40000 ALTER TABLE `kyc_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `kyc_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `kyc_level`
--

DROP TABLE IF EXISTS `kyc_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `kyc_level` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `KYCLevel` bigint(20) NOT NULL,
  `KYCLevelName` varchar(255) NOT NULL,
  `PocketTemplateID` bigint(20) DEFAULT NULL,
  `KYCLevelDescription` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `KYCLevel` (`KYCLevel`),
  KEY `FK_KYCLevel_PocketTemplate` (`PocketTemplateID`),
  CONSTRAINT `FK_KYCLevel_PocketTemplate` FOREIGN KEY (`PocketTemplateID`) REFERENCES `pocket_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `kyc_level`
--

LOCK TABLES `kyc_level` WRITE;
/*!40000 ALTER TABLE `kyc_level` DISABLE KEYS */;
/*!40000 ALTER TABLE `kyc_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ledger`
--

DROP TABLE IF EXISTS `ledger`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ledger` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `CommodityTransferID` bigint(20) NOT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `SourcePocketID` bigint(20) DEFAULT NULL,
  `SourcePocketBalance` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `DestPocketID` bigint(20) DEFAULT NULL,
  `DestPocketBalance` varchar(255) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ledger`
--

LOCK TABLES `ledger` WRITE;
/*!40000 ALTER TABLE `ledger` DISABLE KEYS */;
/*!40000 ALTER TABLE `ledger` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `letter_of_purchase`
--

DROP TABLE IF EXISTS `letter_of_purchase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `letter_of_purchase` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `TransactionID` bigint(20) NOT NULL,
  `DCTLevelID` bigint(20) DEFAULT NULL,
  `DCTID` bigint(20) DEFAULT NULL,
  `LevelPermissions` int(11) DEFAULT NULL,
  `GiroRefID` varchar(255) DEFAULT NULL,
  `TransferDate` varchar(255) DEFAULT NULL,
  `ActualAmountPaid` decimal(25,4) DEFAULT NULL,
  `AmountDistributed` decimal(25,4) DEFAULT NULL,
  `Status` varchar(255) DEFAULT NULL,
  `Commission` decimal(25,4) DEFAULT NULL,
  `DistributedBy` varchar(255) DEFAULT NULL,
  `DistributeTime` datetime DEFAULT NULL,
  `ApprovedBy` varchar(255) DEFAULT NULL,
  `ApprovalTime` datetime DEFAULT NULL,
  `RejectedBy` varchar(255) DEFAULT NULL,
  `RejectTime` datetime DEFAULT NULL,
  `Comment` varchar(255) DEFAULT NULL,
  `SourceApplication` int(11) NOT NULL DEFAULT '2',
  `SourceIP` varchar(255) DEFAULT NULL,
  `WebClientIP` varchar(255) DEFAULT NULL,
  `Units` bigint(20) DEFAULT NULL,
  `BulkLOPID` bigint(20) DEFAULT NULL,
  `IsCommissionChanged` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_LOP_MerchantBySubscriberID` (`SubscriberID`),
  KEY `FK_LOP_Company` (`CompanyID`),
  KEY `FK_LOP_BulkLOP` (`BulkLOPID`),
  KEY `FK_LOP_TransactionsLogByTransactionID` (`TransactionID`),
  KEY `FK_LOP_DistributionChainTemplateByDCTID` (`DCTID`),
  KEY `FK_LOP_DistributionChainLevelByDCTLevelID` (`DCTLevelID`),
  KEY `FK_LOP_SubscriberMDNByMDNID` (`MDNID`),
  CONSTRAINT `FK_LOP_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_LOP_BulkLOP` FOREIGN KEY (`BulkLOPID`) REFERENCES `bulk_lop` (`ID`),
  CONSTRAINT `FK_LOP_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_LOP_DistributionChainLevelByDCTLevelID` FOREIGN KEY (`DCTLevelID`) REFERENCES `distribution_chain_level` (`ID`),
  CONSTRAINT `FK_LOP_DistributionChainTemplateByDCTID` FOREIGN KEY (`DCTID`) REFERENCES `distribution_chain_template` (`ID`),
  CONSTRAINT `FK_LOP_MerchantBySubscriberID` FOREIGN KEY (`SubscriberID`) REFERENCES `merchant` (`ID`),
  CONSTRAINT `FK_LOP_TransactionsLogByTransactionID` FOREIGN KEY (`TransactionID`) REFERENCES `transaction_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `letter_of_purchase`
--

LOCK TABLES `letter_of_purchase` WRITE;
/*!40000 ALTER TABLE `letter_of_purchase` DISABLE KEYS */;
/*!40000 ALTER TABLE `letter_of_purchase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lop_history`
--

DROP TABLE IF EXISTS `lop_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lop_history` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `LOPID` bigint(20) NOT NULL,
  `OldDiscount` decimal(25,4) DEFAULT NULL,
  `NewDiscount` decimal(25,4) DEFAULT NULL,
  `DiscountChangedBy` varchar(255) DEFAULT NULL,
  `DiscountChangeTime` datetime DEFAULT NULL,
  `Comments` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_LOPHistory_LOP` (`LOPID`),
  CONSTRAINT `FK_LOPHistory_LOP` FOREIGN KEY (`LOPID`) REFERENCES `letter_of_purchase` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lop_history`
--

LOCK TABLES `lop_history` WRITE;
/*!40000 ALTER TABLE `lop_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `lop_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mdn_range`
--

DROP TABLE IF EXISTS `mdn_range`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mdn_range` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantID` bigint(20) NOT NULL,
  `StartPrefix` varchar(255) NOT NULL,
  `EndPrefix` varchar(255) NOT NULL,
  `BrandID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MDNRange_Merchant` (`MerchantID`),
  KEY `FK_MDNRange_Brand` (`BrandID`),
  CONSTRAINT `FK_MDNRange_Brand` FOREIGN KEY (`BrandID`) REFERENCES `brand` (`ID`),
  CONSTRAINT `FK_MDNRange_Merchant` FOREIGN KEY (`MerchantID`) REFERENCES `merchant` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mdn_range`
--

LOCK TABLES `mdn_range` WRITE;
/*!40000 ALTER TABLE `mdn_range` DISABLE KEYS */;
/*!40000 ALTER TABLE `mdn_range` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant`
--

DROP TABLE IF EXISTS `merchant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant` (
  `ID` bigint(20) NOT NULL,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ParentID` bigint(20) DEFAULT NULL,
  `GroupID` varchar(255) DEFAULT NULL,
  `TradeName` varchar(255) NOT NULL,
  `TypeOfOrganization` varchar(255) DEFAULT NULL,
  `DistributionChainTemplateID` bigint(20) DEFAULT NULL,
  `FaxNumber` varchar(255) DEFAULT NULL,
  `WebSite` varchar(255) DEFAULT NULL,
  `CurrentWeeklyPurchaseAmount` decimal(25,4) DEFAULT NULL,
  `LastLOPTime` datetime DEFAULT NULL,
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
  `AdminComment` varchar(255) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `StatusTime` datetime NOT NULL,
  `RegionID` bigint(20) DEFAULT NULL,
  `RangeCheck` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Merchant_AddressByMerchantAddressID` (`MerchantAddressID`),
  KEY `FKE1E1C9C8238986A3` (`ID`),
  KEY `FK_Merchant_Region` (`RegionID`),
  KEY `FK_Merchant_MerchantByParentID` (`ParentID`),
  KEY `FK_Merchant_AddressByFranchiseOutletAddressID` (`FranchiseOutletAddressID`),
  CONSTRAINT `FK_Merchant_AddressByFranchiseOutletAddressID` FOREIGN KEY (`FranchiseOutletAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FKE1E1C9C8238986A3` FOREIGN KEY (`ID`) REFERENCES `subscriber` (`ID`),
  CONSTRAINT `FK_Merchant_AddressByMerchantAddressID` FOREIGN KEY (`MerchantAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_Merchant_MerchantByParentID` FOREIGN KEY (`ParentID`) REFERENCES `merchant` (`ID`),
  CONSTRAINT `FK_Merchant_Region` FOREIGN KEY (`RegionID`) REFERENCES `region` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant`
--

LOCK TABLES `merchant` WRITE;
/*!40000 ALTER TABLE `merchant` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_code`
--

DROP TABLE IF EXISTS `merchant_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantCode` varchar(255) NOT NULL,
  `MDN` varchar(255) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MerchantCode` (`MerchantCode`),
  UNIQUE KEY `MDN` (`MDN`),
  KEY `FK_MerchantCode_Company` (`CompanyID`),
  CONSTRAINT `FK_MerchantCode_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_code`
--

LOCK TABLES `merchant_code` WRITE;
/*!40000 ALTER TABLE `merchant_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `merchant_prefix_code`
--

DROP TABLE IF EXISTS `merchant_prefix_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `merchant_prefix_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantPrefixCode` int(11) NOT NULL,
  `BillerName` varchar(255) NOT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  `VAServiceName` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MerchantPrefixCode` (`MerchantPrefixCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `merchant_prefix_code`
--

LOCK TABLES `merchant_prefix_code` WRITE;
/*!40000 ALTER TABLE `merchant_prefix_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `merchant_prefix_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mfino_service_provider`
--

DROP TABLE IF EXISTS `mfino_service_provider`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mfino_service_provider` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `StatusTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mfino_service_provider`
--

LOCK TABLES `mfino_service_provider` WRITE;
/*!40000 ALTER TABLE `mfino_service_provider` DISABLE KEYS */;
/*!40000 ALTER TABLE `mfino_service_provider` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mfs_biller`
--

DROP TABLE IF EXISTS `mfs_biller`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mfs_biller` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mfs_biller`
--

LOCK TABLES `mfs_biller` WRITE;
/*!40000 ALTER TABLE `mfs_biller` DISABLE KEYS */;
/*!40000 ALTER TABLE `mfs_biller` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mfsbiller_partner_mapping`
--

DROP TABLE IF EXISTS `mfsbiller_partner_mapping`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mfsbiller_partner_mapping` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mfsbiller_partner_mapping`
--

LOCK TABLES `mfsbiller_partner_mapping` WRITE;
/*!40000 ALTER TABLE `mfsbiller_partner_mapping` DISABLE KEYS */;
/*!40000 ALTER TABLE `mfsbiller_partner_mapping` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `mobile_network_operator`
--

DROP TABLE IF EXISTS `mobile_network_operator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `mobile_network_operator` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `StatusTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `mobile_network_operator`
--

LOCK TABLES `mobile_network_operator` WRITE;
/*!40000 ALTER TABLE `mobile_network_operator` DISABLE KEYS */;
/*!40000 ALTER TABLE `mobile_network_operator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Code` int(11) NOT NULL,
  `CodeName` varchar(255) NOT NULL,
  `NotificationMethod` int(11) NOT NULL,
  `Text` longtext NOT NULL,
  `STKML` longtext,
  `Language` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '1',
  `StatusTime` datetime NOT NULL,
  `AccessCode` varchar(255) DEFAULT NULL,
  `SMSNotificationCode` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MSPID` (`MSPID`,`Code`,`NotificationMethod`,`Language`,`CompanyID`),
  KEY `FK_Notification_Company` (`CompanyID`),
  KEY `FK_Notification_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_Notification_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_Notification_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offline_report`
--

DROP TABLE IF EXISTS `offline_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_report` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `ReportSql` longtext,
  `ReportClass` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offline_report`
--

LOCK TABLES `offline_report` WRITE;
/*!40000 ALTER TABLE `offline_report` DISABLE KEYS */;
/*!40000 ALTER TABLE `offline_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offline_report_company`
--

DROP TABLE IF EXISTS `offline_report_company`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_report_company` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ReportID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_OfflineReportForCompany_Company` (`CompanyID`),
  KEY `FK_OfflineReportForCompany_OfflineReportByReportID` (`ReportID`),
  CONSTRAINT `FK_OfflineReportForCompany_OfflineReportByReportID` FOREIGN KEY (`ReportID`) REFERENCES `offline_report` (`ID`),
  CONSTRAINT `FK_OfflineReportForCompany_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offline_report_company`
--

LOCK TABLES `offline_report_company` WRITE;
/*!40000 ALTER TABLE `offline_report_company` DISABLE KEYS */;
/*!40000 ALTER TABLE `offline_report_company` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offline_report_receiver`
--

DROP TABLE IF EXISTS `offline_report_receiver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `offline_report_receiver` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ReportID` bigint(20) NOT NULL,
  `Email` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_OfflineReportReceiver_OfflineReportByReportID` (`ReportID`),
  CONSTRAINT `FK_OfflineReportReceiver_OfflineReportByReportID` FOREIGN KEY (`ReportID`) REFERENCES `offline_report` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offline_report_receiver`
--

LOCK TABLES `offline_report_receiver` WRITE;
/*!40000 ALTER TABLE `offline_report_receiver` DISABLE KEYS */;
/*!40000 ALTER TABLE `offline_report_receiver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partner`
--

DROP TABLE IF EXISTS `partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `PartnerCode` varchar(255) DEFAULT NULL,
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
  UNIQUE KEY `TradeName` (`TradeName`),
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partner`
--

LOCK TABLES `partner` WRITE;
/*!40000 ALTER TABLE `partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partner_services`
--

DROP TABLE IF EXISTS `partner_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner_services` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceProviderServicesID` bigint(20) DEFAULT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `ServiceProviderID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `DistributionChainTemplateID` bigint(20) DEFAULT NULL,
  `ParentID` bigint(20) DEFAULT NULL,
  `Level` int(11) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `CollectorPocket` bigint(20) NOT NULL,
  `SourcePocket` bigint(20) DEFAULT NULL,
  `IsServiceChargeShare` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PartnerServices_Partner` (`PartnerID`),
  KEY `FK_PartnerServices_PartnerByServiceProviderID` (`ServiceProviderID`),
  KEY `FK_PartnerServices_ServiceProviderServices` (`ServiceProviderServicesID`),
  KEY `FK_PartnerServices_PocketByCollectorPocket` (`CollectorPocket`),
  KEY `FK_PartnerServices_DistributionChainTemplate` (`DistributionChainTemplateID`),
  KEY `FK_PartnerServices_PartnerByParentID` (`ParentID`),
  KEY `FK_PartnerServices_PocketBySourcePocket` (`SourcePocket`),
  KEY `FK_PartnerServices_Service` (`ServiceID`),
  KEY `FK_PartnerServices_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_PartnerServices_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_PartnerServices_DistributionChainTemplate` FOREIGN KEY (`DistributionChainTemplateID`) REFERENCES `distribution_chain_template` (`ID`),
  CONSTRAINT `FK_PartnerServices_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_PartnerServices_PartnerByParentID` FOREIGN KEY (`ParentID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_PartnerServices_PartnerByServiceProviderID` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_PartnerServices_PocketByCollectorPocket` FOREIGN KEY (`CollectorPocket`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_PartnerServices_PocketBySourcePocket` FOREIGN KEY (`SourcePocket`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_PartnerServices_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_PartnerServices_ServiceProviderServices` FOREIGN KEY (`ServiceProviderServicesID`) REFERENCES `service_provider_services` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partner_services`
--

LOCK TABLES `partner_services` WRITE;
/*!40000 ALTER TABLE `partner_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `partner_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `partner_transactions`
--

DROP TABLE IF EXISTS `partner_transactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `partner_transactions` (
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
  `CurrentBalance` decimal(25,4) DEFAULT NULL,
  `CurrentDailyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentWeeklyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentMonthlyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `partner_transactions`
--

LOCK TABLES `partner_transactions` WRITE;
/*!40000 ALTER TABLE `partner_transactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `partner_transactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_commodity_transfer`
--

DROP TABLE IF EXISTS `pending_commodity_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending_commodity_transfer` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `TransactionID` bigint(20) NOT NULL,
  `MsgType` int(11) NOT NULL,
  `UICategory` int(11) DEFAULT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `TransferStatus` int(11) NOT NULL,
  `TransferFailureReason` int(11) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `StartTime` datetime NOT NULL,
  `EndTime` datetime DEFAULT NULL,
  `ExpirationTimeout` int(11) NOT NULL DEFAULT '60000',
  `SourceIP` varchar(255) DEFAULT NULL,
  `SourceReferenceID` varchar(255) DEFAULT NULL,
  `SourceTerminalID` varchar(255) DEFAULT NULL,
  `SourceMDN` varchar(255) NOT NULL,
  `SourceMDNID` bigint(20) NOT NULL,
  `LOPID` bigint(20) DEFAULT NULL,
  `DCTLevelID` bigint(20) DEFAULT NULL,
  `LevelPermissions` int(11) DEFAULT NULL,
  `SourceSubscriberID` bigint(20) NOT NULL,
  `SourceSubscriberName` varchar(255) DEFAULT NULL,
  `SourcePocketAllowance` int(11) DEFAULT NULL,
  `SourcePocketType` int(11) NOT NULL,
  `SourcePocketID` bigint(20) NOT NULL,
  `SourcePocketBalance` varchar(255) DEFAULT NULL,
  `SourceCardPAN` varchar(512) DEFAULT NULL,
  `SourceMessage` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `DestMDNID` bigint(20) DEFAULT NULL,
  `DestSubscriberID` bigint(20) DEFAULT NULL,
  `DestSubscriberName` varchar(255) DEFAULT NULL,
  `DestPocketAllowance` int(11) DEFAULT NULL,
  `DestPocketType` int(11) DEFAULT NULL,
  `DestPocketID` bigint(20) DEFAULT NULL,
  `DestPocketBalance` varchar(255) DEFAULT NULL,
  `DestBankAccountName` varchar(255) DEFAULT NULL,
  `DestCardPAN` varchar(512) DEFAULT NULL,
  `BillingType` int(11) DEFAULT NULL,
  `Amount` decimal(25,4) NOT NULL,
  `Charges` decimal(25,4) NOT NULL,
  `Commodity` int(11) NOT NULL,
  `BucketType` varchar(255) DEFAULT NULL,
  `SourceApplication` int(11) NOT NULL,
  `ServletPath` varchar(255) DEFAULT NULL,
  `Currency` varchar(255) NOT NULL,
  `BankCode` int(11) DEFAULT NULL,
  `OperatorCode` int(11) DEFAULT NULL,
  `OperatorResponseTime` datetime DEFAULT NULL,
  `OperatorResponseCode` int(11) DEFAULT NULL,
  `OperatorRejectReason` varchar(255) DEFAULT NULL,
  `OperatorErrorText` varchar(1000) DEFAULT NULL,
  `OperatorAuthorizationCode` varchar(255) DEFAULT NULL,
  `OperatorReversalResponseTime` datetime DEFAULT NULL,
  `OperatorReversalResponseCode` int(11) DEFAULT NULL,
  `OperatorReversalRejectReason` varchar(255) DEFAULT NULL,
  `OperatorReversalErrorText` varchar(255) DEFAULT NULL,
  `OperatorReversalCount` int(11) DEFAULT NULL,
  `OperatorLastReversalTime` datetime DEFAULT NULL,
  `TopupPeriod` bigint(20) DEFAULT NULL,
  `BankRetrievalReferenceNumber` varchar(255) DEFAULT NULL,
  `BankSystemTraceAuditNumber` varchar(255) DEFAULT NULL,
  `BankResponseTime` datetime DEFAULT NULL,
  `BankResponseCode` int(11) DEFAULT NULL,
  `BankRejectReason` varchar(255) DEFAULT NULL,
  `BankErrorText` varchar(3000) DEFAULT NULL,
  `BankAuthorizationCode` varchar(255) DEFAULT NULL,
  `BankReversalResponseTime` datetime DEFAULT NULL,
  `BankReversalResponseCode` int(11) DEFAULT NULL,
  `BankReversalRejectReason` varchar(255) DEFAULT NULL,
  `BankReversalErrorText` varchar(3000) DEFAULT NULL,
  `BankReversalAuthorizationCode` varchar(255) DEFAULT NULL,
  `ReversalCount` int(11) DEFAULT NULL,
  `LastReversalTime` datetime DEFAULT NULL,
  `CSRAction` int(11) DEFAULT NULL,
  `CSRActionTime` datetime DEFAULT NULL,
  `CSRUserID` bigint(20) DEFAULT NULL,
  `CSRUserName` varchar(255) DEFAULT NULL,
  `CSRComment` varchar(255) DEFAULT NULL,
  `ISO8583_ProcessingCode` varchar(255) DEFAULT NULL,
  `ISO8583_PrimaryAccountNumber` varchar(255) DEFAULT NULL,
  `ISO8583_SystemTraceAuditNumber` varchar(255) DEFAULT NULL,
  `ISO8583_LocalTransactionTimeHhmmss` varchar(255) DEFAULT NULL,
  `ISO8583_MerchantType` varchar(255) DEFAULT NULL,
  `ISO8583_AcquiringInstitutionIdentificationCode` int(11) DEFAULT NULL,
  `ISO8583_RetrievalReferenceNumber` varchar(255) DEFAULT NULL,
  `ISO8583_CardAcceptorIdentificationCode` varchar(255) DEFAULT NULL,
  `ISO8583_Variant` varchar(255) DEFAULT NULL,
  `ISO8583_ResponseCode` varchar(255) DEFAULT NULL,
  `BulkUploadID` bigint(20) DEFAULT NULL,
  `BulkUploadLineNumber` int(11) DEFAULT NULL,
  `CopyToPermanentError` varchar(255) DEFAULT NULL,
  `WebClientIP` varchar(255) DEFAULT NULL,
  `OperatorRRN` varchar(255) DEFAULT NULL,
  `OperatorSTAN` varchar(255) DEFAULT NULL,
  `ProductIndicatorCode` varchar(255) DEFAULT NULL,
  `Units` bigint(20) DEFAULT NULL,
  `Denomination` bigint(20) DEFAULT NULL,
  `CreditCardTransactionID` bigint(20) DEFAULT NULL,
  `TransactionChargeID` bigint(20) DEFAULT NULL,
  `IsPartOfSharedUpChain` tinyint(4) DEFAULT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `LocalBalanceRevertRequired` tinyint(4) NOT NULL DEFAULT '0',
  `LocalRevertRequired` tinyint(4) NOT NULL DEFAULT '0',
  `BankReversalRequired` tinyint(4) NOT NULL DEFAULT '0',
  `OperatorActionRequired` tinyint(4) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BulkUploadID` (`BulkUploadID`,`BulkUploadLineNumber`),
  KEY `FK_PendingCommodityTransfer_PocketBySourcePocketID` (`SourcePocketID`),
  KEY `FK_PendingCommodityTransfer_CreditCardTransaction` (`CreditCardTransactionID`),
  KEY `FK_PendingCommodityTransfer_Company` (`CompanyID`),
  KEY `FK_PendingCommodityTransfer_TransactionsLogByTransactionID` (`TransactionID`),
  KEY `FK_PendingCommodityTransfer_LOP` (`LOPID`),
  KEY `FK_PendingCommodityTransfer_SubscriberMDNBySourceMDNID` (`SourceMDNID`),
  KEY `FK_PendingCommodityTransfer_SubscriberBySourceSubscriberID` (`SourceSubscriberID`),
  KEY `FK_PendingCommodityTransfer_DistributionChainLevelByDCTLevelID` (`DCTLevelID`),
  KEY `FK_PendingCommodityTransfer_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_PendingCommodityTransfer_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_DistributionChainLevelByDCTLevelID` FOREIGN KEY (`DCTLevelID`) REFERENCES `distribution_chain_level` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_LOP` FOREIGN KEY (`LOPID`) REFERENCES `letter_of_purchase` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_PocketBySourcePocketID` FOREIGN KEY (`SourcePocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_SubscriberBySourceSubscriberID` FOREIGN KEY (`SourceSubscriberID`) REFERENCES `subscriber` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_SubscriberMDNBySourceMDNID` FOREIGN KEY (`SourceMDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_PendingCommodityTransfer_TransactionsLogByTransactionID` FOREIGN KEY (`TransactionID`) REFERENCES `transaction_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_commodity_transfer`
--

LOCK TABLES `pending_commodity_transfer` WRITE;
/*!40000 ALTER TABLE `pending_commodity_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_commodity_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_transactions_entry`
--

DROP TABLE IF EXISTS `pending_transactions_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending_transactions_entry` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `TransactionsFileID` bigint(20) NOT NULL,
  `LineNumber` int(11) NOT NULL,
  `Status` int(11) NOT NULL,
  `ResolveFailureReason` varchar(255) DEFAULT NULL,
  `NotificationCode` int(11) DEFAULT NULL,
  `TransferID` bigint(20) DEFAULT NULL,
  `Amount` decimal(25,4) NOT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TransactionsFileID` (`TransactionsFileID`,`LineNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_transactions_entry`
--

LOCK TABLES `pending_transactions_entry` WRITE;
/*!40000 ALTER TABLE `pending_transactions_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_transactions_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pending_transactions_file`
--

DROP TABLE IF EXISTS `pending_transactions_file`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pending_transactions_file` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `FileName` varchar(255) NOT NULL,
  `FileData` longtext NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `RecordCount` int(11) DEFAULT NULL,
  `TotalLineCount` int(11) DEFAULT NULL,
  `ErrorLineCount` int(11) DEFAULT NULL,
  `UploadFileStatus` int(11) NOT NULL,
  `UploadReport` longtext,
  `RecordType` int(11) DEFAULT NULL,
  `FileProcessedDate` datetime DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ResolveAs` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PendingTransactionsFile_Company` (`CompanyID`),
  CONSTRAINT `FK_PendingTransactionsFile_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pending_transactions_file`
--

LOCK TABLES `pending_transactions_file` WRITE;
/*!40000 ALTER TABLE `pending_transactions_file` DISABLE KEYS */;
/*!40000 ALTER TABLE `pending_transactions_file` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permission_item`
--

DROP TABLE IF EXISTS `permission_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `permission_item` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Permission` int(11) NOT NULL,
  `ItemType` int(11) NOT NULL,
  `ItemID` varchar(255) NOT NULL,
  `FieldID` varchar(255) NOT NULL,
  `Action` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Permission` (`Permission`,`ItemType`,`ItemID`,`FieldID`,`Action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permission_item`
--

LOCK TABLES `permission_item` WRITE;
/*!40000 ALTER TABLE `permission_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `permission_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `person_2_person`
--

DROP TABLE IF EXISTS `person_2_person`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `person_2_person` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `MDN` varchar(255) NOT NULL,
  `PeerName` varchar(255) DEFAULT NULL,
  `ActivationTime` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MSPID` (`MSPID`,`SubscriberID`,`MDN`),
  KEY `FK_Person2Person_Subscriber` (`SubscriberID`),
  KEY `FK_Person2Person_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_Person2Person_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_Person2Person_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `person_2_person`
--

LOCK TABLES `person_2_person` WRITE;
/*!40000 ALTER TABLE `person_2_person` DISABLE KEYS */;
/*!40000 ALTER TABLE `person_2_person` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pocket`
--

DROP TABLE IF EXISTS `pocket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pocket` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PocketTemplateID` bigint(20) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `LastTransactionTime` datetime DEFAULT NULL,
  `CurrentBalance` varchar(255) DEFAULT NULL,
  `CurrentDailyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentWeeklyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentMonthlyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentDailyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentWeeklyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentMonthlyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `LastBankResponseCode` int(11) DEFAULT NULL,
  `LastBankAuthorizationCode` varchar(255) DEFAULT NULL,
  `LastBankRequestCode` int(11) DEFAULT NULL,
  `CardPAN` varchar(512) DEFAULT NULL,
  `Restrictions` int(11) NOT NULL DEFAULT '0',
  `IsDefault` tinyint(4) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `StatusTime` datetime NOT NULL,
  `ActivationTime` datetime DEFAULT NULL,
  `OldPocketTemplateID` bigint(20) DEFAULT NULL,
  `PocketTemplateChangeTime` datetime DEFAULT NULL,
  `PocketTemplateChangedBy` varchar(255) DEFAULT NULL,
  `LowBalNotifType` int(11) DEFAULT NULL,
  `LowBalNotifTriggerTime` datetime DEFAULT NULL,
  `LowBalNotifRegistered` int(11) DEFAULT NULL,
  `LowBalNotifQueryTime` datetime DEFAULT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Pocket_PocketTemplate` (`PocketTemplateID`),
  KEY `FK_Pocket_Company` (`CompanyID`),
  KEY `FK_Pocket_PocketTemplateByOldPocketTemplateID` (`OldPocketTemplateID`),
  KEY `FK_Pocket_SubscriberMDNByMDNID` (`MDNID`),
  CONSTRAINT `FK_Pocket_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_Pocket_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_Pocket_PocketTemplate` FOREIGN KEY (`PocketTemplateID`) REFERENCES `pocket_template` (`ID`),
  CONSTRAINT `FK_Pocket_PocketTemplateByOldPocketTemplateID` FOREIGN KEY (`OldPocketTemplateID`) REFERENCES `pocket_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pocket`
--

LOCK TABLES `pocket` WRITE;
/*!40000 ALTER TABLE `pocket` DISABLE KEYS */;
/*!40000 ALTER TABLE `pocket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pocket_template`
--

DROP TABLE IF EXISTS `pocket_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pocket_template` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `Type` int(11) NOT NULL,
  `BankAccountCardType` int(11) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Commodity` int(11) NOT NULL,
  `CardPANSuffixLength` int(11) DEFAULT NULL,
  `Units` varchar(255) DEFAULT NULL,
  `Allowance` int(11) NOT NULL,
  `MaximumStoredValue` decimal(25,4) DEFAULT NULL,
  `MinimumStoredValue` decimal(25,4) DEFAULT NULL,
  `MaxAmountPerTransaction` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MinAmountPerTransaction` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerDay` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerWeek` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerMonth` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxTransactionsPerDay` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerWeek` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerMonth` int(11) NOT NULL DEFAULT '0',
  `MinTimeBetweenTransactions` int(11) NOT NULL DEFAULT '0',
  `BankCode` int(11) DEFAULT NULL,
  `OperatorCode` int(11) DEFAULT NULL,
  `BillingType` int(11) DEFAULT NULL,
  `LowBalanceNotificationThresholdAmount` decimal(25,4) DEFAULT NULL,
  `LowBalanceNotificationEnabled` tinyint(4) DEFAULT NULL,
  `WebTimeInterval` int(11) DEFAULT NULL,
  `WebServiceTimeInterval` int(11) DEFAULT NULL,
  `UTKTimeInterval` int(11) DEFAULT NULL,
  `BankChannelTimeInterval` int(11) DEFAULT NULL,
  `Denomination` bigint(20) DEFAULT NULL,
  `MaxUnits` bigint(20) DEFAULT NULL,
  `PocketCode` varchar(255) DEFAULT NULL,
  `TypeOfCheck` int(11) NOT NULL DEFAULT '0',
  `RegularExpression` varchar(255) DEFAULT NULL,
  `IsCollectorPocket` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PocketTemplate_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_PocketTemplate_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pocket_template`
--

LOCK TABLES `pocket_template` WRITE;
/*!40000 ALTER TABLE `pocket_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `pocket_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_indicator`
--

DROP TABLE IF EXISTS `product_indicator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `product_indicator` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `TransactionUICategory` int(11) NOT NULL,
  `ChannelSourceApplication` int(11) NOT NULL,
  `RequestorID` varchar(255) DEFAULT NULL,
  `ProductDescription` varchar(255) DEFAULT NULL,
  `ChannelText` varchar(255) DEFAULT NULL,
  `ProductIndicatorCode` varchar(255) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ProductIndicator_Company` (`CompanyID`),
  CONSTRAINT `FK_ProductIndicator_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_indicator`
--

LOCK TABLES `product_indicator` WRITE;
/*!40000 ALTER TABLE `product_indicator` DISABLE KEYS */;
/*!40000 ALTER TABLE `product_indicator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `region`
--

DROP TABLE IF EXISTS `region`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `region` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `RegionName` varchar(255) NOT NULL,
  `RegionCode` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `RegionCode` (`RegionCode`),
  KEY `FK_Region_Company` (`CompanyID`),
  CONSTRAINT `FK_Region_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `region`
--

LOCK TABLES `region` WRITE;
/*!40000 ALTER TABLE `region` DISABLE KEYS */;
/*!40000 ALTER TABLE `region` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `role_permission` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Role` int(11) NOT NULL,
  `Permission` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Role` (`Role`,`Permission`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role_permission`
--

LOCK TABLES `role_permission` WRITE;
/*!40000 ALTER TABLE `role_permission` DISABLE KEYS */;
/*!40000 ALTER TABLE `role_permission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sap_groupid`
--

DROP TABLE IF EXISTS `sap_groupid`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sap_groupid` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `GroupID` varchar(255) NOT NULL,
  `GroupIDName` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sap_groupid`
--

LOCK TABLES `sap_groupid` WRITE;
/*!40000 ALTER TABLE `sap_groupid` DISABLE KEYS */;
/*!40000 ALTER TABLE `sap_groupid` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service`
--

DROP TABLE IF EXISTS `service`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceName` varchar(255) DEFAULT NULL,
  `DisplayName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Service_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_Service_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service`
--

LOCK TABLES `service` WRITE;
/*!40000 ALTER TABLE `service` DISABLE KEYS */;
/*!40000 ALTER TABLE `service` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_audit`
--

DROP TABLE IF EXISTS `service_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_audit` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ServiceProviderID` bigint(20) DEFAULT NULL,
  `ServiceID` bigint(20) DEFAULT NULL,
  `SourceType` int(11) DEFAULT NULL,
  `SourceID` bigint(20) DEFAULT NULL,
  `KYCLevelId` bigint(20) DEFAULT NULL,
  `LastTransactionTime` datetime DEFAULT NULL,
  `CurrentDailyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentWeeklyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentMonthlyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `CurrentDailyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentWeeklyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `CurrentMonthlyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `PreviousDailyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `PreviousWeeklyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `PreviousMonthlyExpenditure` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `PreviousDailyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `PreviousWeeklyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  `PreviousMonthlyTransactionsCount` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_audit`
--

LOCK TABLES `service_audit` WRITE;
/*!40000 ALTER TABLE `service_audit` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_charge_pricing`
--

DROP TABLE IF EXISTS `service_charge_pricing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_charge_pricing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `MinAmount` decimal(25,4) DEFAULT NULL,
  `MaxAmount` decimal(25,4) DEFAULT NULL,
  `ChargeInFixed` decimal(25,4) DEFAULT NULL,
  `ChargeInPercentage` decimal(25,4) DEFAULT NULL,
  `IsDefault` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargePricing_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_ServiceChargePricing_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargePricing_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceChargePricing_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_charge_pricing`
--

LOCK TABLES `service_charge_pricing` WRITE;
/*!40000 ALTER TABLE `service_charge_pricing` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_charge_pricing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_charge_template`
--

DROP TABLE IF EXISTS `service_charge_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_charge_template` (
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
  `MaxAmountPerTransaction` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MinAmountPerTransaction` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerDay` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerWeek` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxAmountPerMonth` decimal(25,4) NOT NULL DEFAULT '0.0000',
  `MaxTransactionsPerDay` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerWeek` int(11) NOT NULL DEFAULT '0',
  `MaxTransactionsPerMonth` int(11) NOT NULL DEFAULT '0',
  `MinTimeBetweenTransactions` int(11) NOT NULL DEFAULT '0',
  `BusinessPartnerSharePercentage` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargeTemplate_PartnerByServiceProviderID` (`ServiceProviderID`),
  KEY `FK_ServiceChargeTemplate_ChannelCode` (`ChannelCodeID`),
  KEY `FK_ServiceChargeTemplate_ServiceTransaction` (`ServiceTransactionID`),
  KEY `FK_ServiceChargeTemplate_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargeTemplate_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_PartnerByServiceProviderID` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_ServiceChargeTemplate_ServiceTransaction` FOREIGN KEY (`ServiceTransactionID`) REFERENCES `service_transaction` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_charge_template`
--

LOCK TABLES `service_charge_template` WRITE;
/*!40000 ALTER TABLE `service_charge_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_charge_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_charge_transaction_log`
--

DROP TABLE IF EXISTS `service_charge_transaction_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_charge_transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) DEFAULT NULL,
  `TransactionID` bigint(20) DEFAULT NULL,
  `MFSBillerCode` varchar(255) DEFAULT NULL,
  `SourcePartnerID` bigint(20) DEFAULT NULL,
  `DestPartnerID` bigint(20) DEFAULT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  `OnBeHalfOfMDN` varchar(255) DEFAULT NULL,
  `ServiceID` bigint(20) DEFAULT NULL,
  `TransactionTypeID` bigint(20) DEFAULT NULL,
  `ServiceProviderID` bigint(20) DEFAULT NULL,
  `ChannelCodeID` bigint(20) DEFAULT NULL,
  `TransactionAmount` decimal(25,4) NOT NULL,
  `Mode` varchar(255) DEFAULT NULL,
  `InvoiceNo` varchar(255) DEFAULT NULL,
  `TransactionRuleID` bigint(20) DEFAULT NULL,
  `CalculatedCharge` decimal(25,4) DEFAULT NULL,
  `CommodityTransferID` bigint(20) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `FailureReason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceChargeTransactionLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceChargeTransactionLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_charge_transaction_log`
--

LOCK TABLES `service_charge_transaction_log` WRITE;
/*!40000 ALTER TABLE `service_charge_transaction_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_charge_transaction_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_partner`
--

DROP TABLE IF EXISTS `service_partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_partner` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_partner`
--

LOCK TABLES `service_partner` WRITE;
/*!40000 ALTER TABLE `service_partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_provider_services`
--

DROP TABLE IF EXISTS `service_provider_services`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_provider_services` (
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
  KEY `FK_ServiceProviderServices_PartnerByServiceProviderID` (`ServiceProviderID`),
  KEY `FK_ServiceProviderServices_Service` (`ServiceID`),
  KEY `FK_ServiceProviderServices_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_ServiceProviderServices_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceProviderServices_PartnerByServiceProviderID` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_ServiceProviderServices_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_provider_services`
--

LOCK TABLES `service_provider_services` WRITE;
/*!40000 ALTER TABLE `service_provider_services` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_provider_services` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_settlement_config`
--

DROP TABLE IF EXISTS `service_settlement_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_settlement_config` (
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
  `SchedulerStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceSettlementConfig_SettlementTemplate` (`SettlementTemplateID`),
  KEY `FK_ServiceSettlementConfig_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_ServiceSettlementConfig_PartnerServicesByPartnerServiceID` (`PartnerServiceID`),
  CONSTRAINT `FK_ServiceSettlementConfig_PartnerServicesByPartnerServiceID` FOREIGN KEY (`PartnerServiceID`) REFERENCES `partner_services` (`ID`),
  CONSTRAINT `FK_ServiceSettlementConfig_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_ServiceSettlementConfig_SettlementTemplate` FOREIGN KEY (`SettlementTemplateID`) REFERENCES `settlement_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_settlement_config`
--

LOCK TABLES `service_settlement_config` WRITE;
/*!40000 ALTER TABLE `service_settlement_config` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_settlement_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `service_transaction`
--

DROP TABLE IF EXISTS `service_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_transaction` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_transaction`
--

LOCK TABLES `service_transaction` WRITE;
/*!40000 ALTER TABLE `service_transaction` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlement_configuration`
--

DROP TABLE IF EXISTS `settlement_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_configuration` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `SettlementName` varchar(255) DEFAULT NULL,
  `SettlementPocket` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SettlementConfiguration_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_SettlementConfiguration_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlement_configuration`
--

LOCK TABLES `settlement_configuration` WRITE;
/*!40000 ALTER TABLE `settlement_configuration` DISABLE KEYS */;
/*!40000 ALTER TABLE `settlement_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlement_scheduler_logs`
--

DROP TABLE IF EXISTS `settlement_scheduler_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_scheduler_logs` (
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
  `ReasonText` varchar(255) DEFAULT NULL,
  `LastSettled` datetime DEFAULT NULL,
  `NextSettle` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlement_scheduler_logs`
--

LOCK TABLES `settlement_scheduler_logs` WRITE;
/*!40000 ALTER TABLE `settlement_scheduler_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `settlement_scheduler_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlement_template`
--

DROP TABLE IF EXISTS `settlement_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_template` (
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlement_template`
--

LOCK TABLES `settlement_template` WRITE;
/*!40000 ALTER TABLE `settlement_template` DISABLE KEYS */;
/*!40000 ALTER TABLE `settlement_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `settlement_transaction_logs`
--

DROP TABLE IF EXISTS `settlement_transaction_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `settlement_transaction_logs` (
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
  `Response` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `settlement_transaction_logs`
--

LOCK TABLES `settlement_transaction_logs` WRITE;
/*!40000 ALTER TABLE `settlement_transaction_logs` DISABLE KEYS */;
/*!40000 ALTER TABLE `settlement_transaction_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `share_partner`
--

DROP TABLE IF EXISTS `share_partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `share_partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `TransactionChargeID` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `SharePercentage` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SharePartner_Partner` (`PartnerID`),
  KEY `FK_SharePartner_TransactionCharge` (`TransactionChargeID`),
  KEY `FK_SharePartner_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_SharePartner_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_SharePartner_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_SharePartner_TransactionCharge` FOREIGN KEY (`TransactionChargeID`) REFERENCES `transaction_charge` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `share_partner`
--

LOCK TABLES `share_partner` WRITE;
/*!40000 ALTER TABLE `share_partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `share_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `share_partners`
--

DROP TABLE IF EXISTS `share_partners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `share_partners` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `SharePercentage` decimal(25,4) DEFAULT NULL,
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `share_partners`
--

LOCK TABLES `share_partners` WRITE;
/*!40000 ALTER TABLE `share_partners` DISABLE KEYS */;
/*!40000 ALTER TABLE `share_partners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_code`
--

DROP TABLE IF EXISTS `sms_code`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SMSCodeText` varchar(255) NOT NULL,
  `ServiceName` varchar(255) NOT NULL,
  `Description` varchar(255) NOT NULL,
  `SMSCodeStatus` int(11) NOT NULL,
  `BrandID` bigint(20) NOT NULL,
  `ShortCodes` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SMSCodeText` (`SMSCodeText`),
  KEY `FK_SMSCode_Brand` (`BrandID`),
  CONSTRAINT `FK_SMSCode_Brand` FOREIGN KEY (`BrandID`) REFERENCES `brand` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_code`
--

LOCK TABLES `sms_code` WRITE;
/*!40000 ALTER TABLE `sms_code` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_code` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_partner`
--

DROP TABLE IF EXISTS `sms_partner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms_partner` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PartnerName` varchar(255) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `ContactName` varchar(255) NOT NULL,
  `ContactPhone` varchar(255) NOT NULL,
  `ContactEmail` varchar(255) NOT NULL,
  `ServerIP` varchar(255) NOT NULL,
  `APIKey` varchar(255) NOT NULL,
  `SendReport` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SMSPartner_User` (`UserID`),
  CONSTRAINT `FK_SMSPartner_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_partner`
--

LOCK TABLES `sms_partner` WRITE;
/*!40000 ALTER TABLE `sms_partner` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_partner` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sms_transaction_log`
--

DROP TABLE IF EXISTS `sms_transaction_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sms_transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `FieldID` varchar(255) DEFAULT NULL,
  `TransactionTime` datetime NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `Source` varchar(255) NOT NULL,
  `DestMDN` varchar(255) NOT NULL,
  `SmscID` varchar(255) NOT NULL,
  `TransactionStatus` varchar(255) DEFAULT NULL,
  `DeliveryStatus` varchar(255) DEFAULT NULL,
  `MessageData` longtext NOT NULL,
  `MessageCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SMSTransactionsLog_SMSPartnerByPartnerID` (`PartnerID`),
  CONSTRAINT `FK_SMSTransactionsLog_SMSPartnerByPartnerID` FOREIGN KEY (`PartnerID`) REFERENCES `sms_partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sms_transaction_log`
--

LOCK TABLES `sms_transaction_log` WRITE;
/*!40000 ALTER TABLE `sms_transaction_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sms_transaction_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `smsc_configuration`
--

DROP TABLE IF EXISTS `smsc_configuration`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `smsc_configuration` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `ShortCode` varchar(255) DEFAULT NULL,
  `LongNumber` varchar(255) DEFAULT NULL,
  `SmartfrenSMSCID` varchar(255) DEFAULT NULL,
  `OtherLocalOperatorSMSCID` varchar(255) DEFAULT NULL,
  `Charging` decimal(25,4) DEFAULT NULL,
  `Header` varchar(255) DEFAULT NULL,
  `Footer` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ShortCode` (`ShortCode`),
  KEY `FK_SMSC_SMSPartnerByPartnerID` (`PartnerID`),
  CONSTRAINT `FK_SMSC_SMSPartnerByPartnerID` FOREIGN KEY (`PartnerID`) REFERENCES `sms_partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `smsc_configuration`
--

LOCK TABLES `smsc_configuration` WRITE;
/*!40000 ALTER TABLE `smsc_configuration` DISABLE KEYS */;
/*!40000 ALTER TABLE `smsc_configuration` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriber` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `ParentID` bigint(20) NOT NULL DEFAULT '0',
  `FirstName` varchar(255) DEFAULT NULL,
  `LastName` varchar(255) DEFAULT NULL,
  `Gender` varchar(255) DEFAULT NULL,
  `BirthPlace` varchar(255) DEFAULT NULL,
  `DateOfBirth` datetime DEFAULT NULL,
  `MDNBrand` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `NotificationMethod` int(11) DEFAULT NULL,
  `Language` int(11) NOT NULL DEFAULT '0',
  `Currency` varchar(255) NOT NULL,
  `Timezone` varchar(255) DEFAULT NULL,
  `Restrictions` int(11) NOT NULL DEFAULT '0',
  `Type` int(11) NOT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `StatusTime` datetime NOT NULL,
  `ActivationTime` datetime DEFAULT NULL,
  `ApproveOrRejectTime` datetime DEFAULT NULL,
  `ApprovedOrRejectedBy` varchar(255) DEFAULT NULL,
  `ApproveOrRejectComment` varchar(255) DEFAULT NULL,
  `AppliedBy` varchar(255) DEFAULT NULL,
  `AppliedTime` datetime DEFAULT NULL,
  `DompetMerchant` tinyint(4) NOT NULL DEFAULT '0',
  `UserID` bigint(20) DEFAULT NULL,
  `SubscriberAddressID` bigint(20) DEFAULT NULL,
  `SecurityQuestion` varchar(255) DEFAULT NULL,
  `SecurityAnswer` varchar(255) DEFAULT NULL,
  `PartnerType` int(11) DEFAULT NULL,
  `KYCLevel` bigint(20) DEFAULT NULL,
  `UpgradableKYCLevel` bigint(20) DEFAULT NULL,
  `UpgradeState` int(11) DEFAULT NULL,
  `IDExiparetionTime` datetime DEFAULT NULL,
  `SubscriberUserID` bigint(20) DEFAULT NULL,
  `ReferenceAccount` bigint(20) DEFAULT NULL,
  `AuthorizingPersonID` bigint(20) DEFAULT NULL,
  `AliasName` varchar(255) DEFAULT NULL,
  `DetailsRequired` tinyint(4) DEFAULT NULL,
  `RegistrationMedium` int(11) DEFAULT '0',
  PRIMARY KEY (`ID`),
  KEY `FK_Subscriber_AuthorizingPerson` (`AuthorizingPersonID`),
  KEY `FK_Subscriber_Company` (`CompanyID`),
  KEY `FK_Subscriber_UserBySubscriberUserID` (`SubscriberUserID`),
  KEY `FK_Subscriber_AddressBySubscriberAddressID` (`SubscriberAddressID`),
  KEY `FK_Subscriber_KYCLevelByKYCLevel` (`KYCLevel`),
  KEY `FK_Subscriber_mFinoServiceProviderByMSPID` (`MSPID`),
  KEY `FK_Subscriber_User` (`UserID`),
  CONSTRAINT `FK_Subscriber_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`),
  CONSTRAINT `FK_Subscriber_AddressBySubscriberAddressID` FOREIGN KEY (`SubscriberAddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_Subscriber_AuthorizingPerson` FOREIGN KEY (`AuthorizingPersonID`) REFERENCES `authorizing_person_details` (`ID`),
  CONSTRAINT `FK_Subscriber_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_Subscriber_KYCLevelByKYCLevel` FOREIGN KEY (`KYCLevel`) REFERENCES `kyc_level` (`ID`),
  CONSTRAINT `FK_Subscriber_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_Subscriber_UserBySubscriberUserID` FOREIGN KEY (`SubscriberUserID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber_addi_info`
--

DROP TABLE IF EXISTS `subscriber_addi_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriber_addi_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `ProofofAddress` varchar(255) DEFAULT NULL,
  `Reference1` varchar(255) DEFAULT NULL,
  `Reference2` varchar(255) DEFAULT NULL,
  `CreditCheck` varchar(255) DEFAULT NULL,
  `SubsCompanyName` varchar(255) DEFAULT NULL,
  `CertofIncorporation` varchar(255) DEFAULT NULL,
  `Misc1` varchar(255) DEFAULT NULL,
  `Misc2` varchar(255) DEFAULT NULL,
  `Nationality` varchar(255) DEFAULT NULL,
  `KinName` varchar(255) DEFAULT NULL,
  `KinMDN` varchar(255) DEFAULT NULL,
  `ControllReference` int(11) DEFAULT NULL,
  `SubscriberMobileCompany` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SubscribersAdditionalFields_Subscriber` (`SubscriberID`),
  CONSTRAINT `FK_SubscribersAdditionalFields_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber_addi_info`
--

LOCK TABLES `subscriber_addi_info` WRITE;
/*!40000 ALTER TABLE `subscriber_addi_info` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriber_addi_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber_mdn`
--

DROP TABLE IF EXISTS `subscriber_mdn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscriber_mdn` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `MDN` varchar(255) NOT NULL,
  `IMSI` varchar(255) DEFAULT NULL,
  `MarketingCategory` varchar(255) DEFAULT NULL,
  `IDType` varchar(255) DEFAULT NULL,
  `IDNumber` varchar(255) DEFAULT NULL,
  `AuthenticationPhoneNumber` varchar(255) DEFAULT NULL,
  `Status` int(11) NOT NULL DEFAULT '0',
  `AuthenticationPhrase` varchar(255) DEFAULT NULL,
  `Restrictions` int(11) NOT NULL DEFAULT '0',
  `WrongPINCount` int(11) NOT NULL DEFAULT '0',
  `DigestedPIN` varchar(255) DEFAULT NULL,
  `MerchantDigestedPIN` varchar(255) DEFAULT NULL,
  `MDNBrand` varchar(255) DEFAULT NULL,
  `StatusTime` datetime NOT NULL,
  `ActivationTime` datetime DEFAULT NULL,
  `LastTransactionTime` datetime DEFAULT NULL,
  `LastTransactionID` bigint(20) DEFAULT NULL,
  `H2HAllowedIP` varchar(255) DEFAULT NULL,
  `IsMDNRecycled` tinyint(4) DEFAULT '0',
  `ScrambleCode` varchar(255) DEFAULT NULL,
  `OTP` varchar(255) DEFAULT NULL,
  `OTPExpirationTime` datetime DEFAULT NULL,
  `ApplicationID` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MDN` (`MDN`),
  KEY `FK_SubscriberMDN_Subscriber` (`SubscriberID`),
  CONSTRAINT `FK_SubscriberMDN_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber_mdn`
--

LOCK TABLES `subscriber_mdn` WRITE;
/*!40000 ALTER TABLE `subscriber_mdn` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscriber_mdn` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_parameters`
--

DROP TABLE IF EXISTS `system_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_parameters` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ParameterName` varchar(255) DEFAULT NULL,
  `ParameterValue` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_parameters`
--

LOCK TABLES `system_parameters` WRITE;
/*!40000 ALTER TABLE `system_parameters` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_parameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tax_pricing`
--

DROP TABLE IF EXISTS `tax_pricing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tax_pricing` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceChargeTemplateID` bigint(20) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Percentage` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TaxPricing_ServiceChargeTemplate` (`ServiceChargeTemplateID`),
  KEY `FK_TaxPricing_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TaxPricing_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TaxPricing_ServiceChargeTemplate` FOREIGN KEY (`ServiceChargeTemplateID`) REFERENCES `service_charge_template` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tax_pricing`
--

LOCK TABLES `tax_pricing` WRITE;
/*!40000 ALTER TABLE `tax_pricing` DISABLE KEYS */;
/*!40000 ALTER TABLE `tax_pricing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_amount_distribution_log`
--

DROP TABLE IF EXISTS `transaction_amount_distribution_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_amount_distribution_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) DEFAULT NULL,
  `ServiceChargeTransactionLogID` bigint(20) NOT NULL,
  `TransactionID` bigint(20) NOT NULL,
  `TransactionChargeID` bigint(20) DEFAULT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `PocketID` bigint(20) NOT NULL,
  `ShareAmount` decimal(25,4) NOT NULL,
  `IsPartOfCharge` tinyint(4) DEFAULT NULL,
  `IsActualAmount` tinyint(4) DEFAULT NULL,
  `IsPartOfSharedUpChain` tinyint(4) DEFAULT NULL,
  `Status` int(11) NOT NULL,
  `FailureReason` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionAmountDistributionLog_Pocket` (`PocketID`),
  KEY `FK_TransactionAmountDistributionLog_Partner` (`PartnerID`),
  KEY `FK_TransactionAmountDistributionLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_Partner` FOREIGN KEY (`PartnerID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_TransactionAmountDistributionLog_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_amount_distribution_log`
--

LOCK TABLES `transaction_amount_distribution_log` WRITE;
/*!40000 ALTER TABLE `transaction_amount_distribution_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_amount_distribution_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_charge`
--

DROP TABLE IF EXISTS `transaction_charge`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_charge` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `TransactionRuleID` bigint(20) NOT NULL,
  `ChargeTypeID` bigint(20) NOT NULL,
  `ChargeDefinitionID` bigint(20) NOT NULL,
  `SourceCommision` decimal(25,4) DEFAULT NULL,
  `DestCommision` decimal(25,4) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TransactionRuleID` (`TransactionRuleID`,`ChargeTypeID`),
  KEY `FK_TransactionCharge_ChargeType` (`ChargeTypeID`),
  KEY `FK_TransactionCharge_ChargeDefinition` (`ChargeDefinitionID`),
  KEY `FK_TransactionCharge_TransactionRule` (`TransactionRuleID`),
  KEY `FK_TransactionCharge_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionCharge_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TransactionCharge_ChargeDefinition` FOREIGN KEY (`ChargeDefinitionID`) REFERENCES `charge_definition` (`ID`),
  CONSTRAINT `FK_TransactionCharge_ChargeType` FOREIGN KEY (`ChargeTypeID`) REFERENCES `charge_type` (`ID`),
  CONSTRAINT `FK_TransactionCharge_TransactionRule` FOREIGN KEY (`TransactionRuleID`) REFERENCES `transaction_rule` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_charge`
--

LOCK TABLES `transaction_charge` WRITE;
/*!40000 ALTER TABLE `transaction_charge` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_charge` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_charge_log`
--

DROP TABLE IF EXISTS `transaction_charge_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_charge_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) DEFAULT NULL,
  `ServiceChargeTransactionLogID` bigint(20) NOT NULL,
  `TransactionChargeID` bigint(20) NOT NULL,
  `CalculatedCharge` decimal(25,4) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionChargeLog_TransactionCharge` (`TransactionChargeID`),
  KEY `FK_TransactionChargeLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionChargeLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TransactionChargeLog_TransactionCharge` FOREIGN KEY (`TransactionChargeID`) REFERENCES `transaction_charge` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_charge_log`
--

LOCK TABLES `transaction_charge_log` WRITE;
/*!40000 ALTER TABLE `transaction_charge_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_charge_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_log`
--

DROP TABLE IF EXISTS `transaction_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ParentTransactionID` bigint(20) DEFAULT NULL,
  `MultiXID` int(11) DEFAULT NULL,
  `TransactionTime` datetime NOT NULL,
  `MessageCode` int(11) NOT NULL,
  `MessageData` longtext NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionsLog_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionsLog_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_log`
--

LOCK TABLES `transaction_log` WRITE;
/*!40000 ALTER TABLE `transaction_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_rule`
--

DROP TABLE IF EXISTS `transaction_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_rule` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceProviderID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `TransactionTypeID` bigint(20) NOT NULL,
  `ChannelCodeID` bigint(20) NOT NULL,
  `ChargeMode` int(11) NOT NULL,
  `SourceType` int(11) DEFAULT NULL,
  `SourceKYC` bigint(20) DEFAULT NULL,
  `DestType` int(11) DEFAULT NULL,
  `DestKYC` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Name` (`Name`),
  KEY `FK_TransactionRule_TransactionType` (`TransactionTypeID`),
  KEY `FK_TransactionRule_PartnerByServiceProviderID` (`ServiceProviderID`),
  KEY `FK_TransactionRule_KYCLevelBySourceKYC` (`SourceKYC`),
  KEY `FK_TransactionRule_ChannelCode` (`ChannelCodeID`),
  KEY `FK_TransactionRule_KYCLevelByDestKYC` (`DestKYC`),
  KEY `FK_TransactionRule_Service` (`ServiceID`),
  KEY `FK_TransactionRule_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionRule_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_TransactionRule_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`),
  CONSTRAINT `FK_TransactionRule_KYCLevelByDestKYC` FOREIGN KEY (`DestKYC`) REFERENCES `kyc_level` (`ID`),
  CONSTRAINT `FK_TransactionRule_KYCLevelBySourceKYC` FOREIGN KEY (`SourceKYC`) REFERENCES `kyc_level` (`ID`),
  CONSTRAINT `FK_TransactionRule_PartnerByServiceProviderID` FOREIGN KEY (`ServiceProviderID`) REFERENCES `partner` (`ID`),
  CONSTRAINT `FK_TransactionRule_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_TransactionRule_TransactionType` FOREIGN KEY (`TransactionTypeID`) REFERENCES `transaction_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_rule`
--

LOCK TABLES `transaction_rule` WRITE;
/*!40000 ALTER TABLE `transaction_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_rule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `transaction_type`
--

DROP TABLE IF EXISTS `transaction_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `transaction_type` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `TransactionName` varchar(255) DEFAULT NULL,
  `DisplayName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TransactionType_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_TransactionType_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `transaction_type`
--

LOCK TABLES `transaction_type` WRITE;
/*!40000 ALTER TABLE `transaction_type` DISABLE KEYS */;
/*!40000 ALTER TABLE `transaction_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `Username` varchar(255) DEFAULT NULL,
  `Password` varchar(255) DEFAULT NULL,
  `FirstName` varchar(255) DEFAULT NULL,
  `LastName` varchar(255) DEFAULT NULL,
  `Email` varchar(255) DEFAULT NULL,
  `Language` int(11) NOT NULL DEFAULT '0',
  `Timezone` varchar(255) DEFAULT NULL,
  `Restrictions` int(11) NOT NULL DEFAULT '0',
  `Status` int(11) NOT NULL DEFAULT '0',
  `StatusTime` datetime NOT NULL,
  `FailedLoginCount` int(11) NOT NULL DEFAULT '0',
  `FirstTimeLogin` tinyint(4) DEFAULT NULL,
  `LastLoginTime` datetime DEFAULT NULL,
  `AdminComment` varchar(255) DEFAULT NULL,
  `Role` int(11) DEFAULT NULL,
  `SecurityQuestion` varchar(255) DEFAULT NULL,
  `SecurityAnswer` varchar(255) DEFAULT NULL,
  `ConfirmationTime` datetime DEFAULT NULL,
  `UserActivationTime` datetime DEFAULT NULL,
  `RejectionTime` datetime DEFAULT NULL,
  `ExpirationTime` datetime DEFAULT NULL,
  `ConfirmationCode` varchar(255) DEFAULT NULL,
  `DateOfBirth` datetime DEFAULT NULL,
  `ForgotPasswordCode` varchar(255) DEFAULT NULL,
  `HomePhone` varchar(255) DEFAULT NULL,
  `WorkPhone` varchar(255) DEFAULT NULL,
  `OldHomePhone` varchar(255) DEFAULT NULL,
  `OldWorkPhone` varchar(255) DEFAULT NULL,
  `OldSecurityQuestion` varchar(255) DEFAULT NULL,
  `OldSecurityAnswer` varchar(255) DEFAULT NULL,
  `OldFirstName` varchar(255) DEFAULT NULL,
  `OldLastName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MSPID` (`MSPID`,`Username`),
  KEY `FK_User_Company` (`CompanyID`),
  KEY `FK_User_mFinoServiceProviderByMSPID` (`MSPID`),
  CONSTRAINT `FK_User_mFinoServiceProviderByMSPID` FOREIGN KEY (`MSPID`) REFERENCES `mfino_service_provider` (`ID`),
  CONSTRAINT `FK_User_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2011-11-16 17:45:06
ALTER TABLE `pocket` MODIFY COLUMN `CardPAN` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 ADD UNIQUE INDEX `CardPan_unique`(`CardPAN`);