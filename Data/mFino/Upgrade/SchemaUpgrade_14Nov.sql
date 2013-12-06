use mfino;
  
INSERT INTO `offline_report` (`ID`, `Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `Name`, `ReportSql`, `ReportClass`) values('18','1',NOW(),'system',NOW(),'system','LOPDiscountChange',NULL,'com.mfino.report.LOPDiscountChangeReport');
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',18,1);
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',18,2);
update pocket set companyid=(select s.companyid from subscriber s inner join subscriber_mdn m  on m.subscriberid=s.id where m.id=pocket.mdnid limit 1);


--
-- Definition of table `bulk_lop`
--

DROP TABLE IF EXISTS `bulk_lop`;
CREATE TABLE `bulk_lop` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `GiroRefID` varchar(255) DEFAULT NULL,
  `FileData` longtext NOT NULL,
  `MDNID` bigint(20) DEFAULT NULL,
  `TransferDate` varchar(255) DEFAULT NULL,
  `Comment` varchar(255) DEFAULT NULL,
  `ActualAmountPaid` bigint(20) DEFAULT NULL,
  `Status` varchar(255) DEFAULT NULL,
  `MerchantID` bigint(20) DEFAULT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  `DCTLevelID` bigint(20) DEFAULT NULL,
  `DCTID` bigint(20) DEFAULT NULL,
  `LevelPermissions` int(11) DEFAULT NULL,
  `AmountDistributed` bigint(20) DEFAULT NULL,
  `DistributedBy` varchar(255) DEFAULT NULL,
  `DistributeTime` datetime DEFAULT NULL,
  `ApprovedBy` varchar(255) DEFAULT NULL,
  `ApprovalTime` datetime DEFAULT NULL,
  `RejectedBy` varchar(255) DEFAULT NULL,
  `RejectTime` datetime DEFAULT NULL,
  `SourceApplication` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`ID`),
  KEY `FK_BulkLOP_SubscriberMDNByMDNID` (`MDNID`),
  KEY `FK_BulkLOP_Merchant` (`MerchantID`),
  KEY `FK_BulkLOP_Company` (`CompanyID`),
  KEY `FK_BulkLOP_DistributionChainTemplateByDCTID` (`DCTID`),
  KEY `FK_BulkLOP_DistributionChainLevelByDCTLevelID` (`DCTLevelID`),
  CONSTRAINT `FK_BulkLOP_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_BulkLOP_DistributionChainLevelByDCTLevelID` FOREIGN KEY (`DCTLevelID`) REFERENCES `distribution_chain_level` (`ID`),
  CONSTRAINT `FK_BulkLOP_DistributionChainTemplateByDCTID` FOREIGN KEY (`DCTID`) REFERENCES `distribution_chain_template` (`ID`),
  CONSTRAINT `FK_BulkLOP_Merchant` FOREIGN KEY (`MerchantID`) REFERENCES `merchant` (`ID`),
  CONSTRAINT `FK_BulkLOP_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
 
--
-- Definition of table `pending_transactions_entry`
--

DROP TABLE IF EXISTS `pending_transactions_entry`;
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
  `Amount` bigint(20) NOT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `DestMDN` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `TransactionsFileID` (`TransactionsFileID`,`LineNumber`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `pending_transactions_file`
--

DROP TABLE IF EXISTS `pending_transactions_file`;
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
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ResolveAs` int(11) DEFAULT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PendingTransactionsFile_Company` (`CompanyID`),
  CONSTRAINT `FK_PendingTransactionsFile_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `lop_history`
--

DROP TABLE IF EXISTS `lop_history`;
CREATE TABLE `lop_history` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `LOPID` bigint(20) NOT NULL,
  `OldDiscount` double DEFAULT NULL,
  `NewDiscount` double DEFAULT NULL,
  `DiscountChangedBy` varchar(255) DEFAULT NULL,
  `DiscountChangeTime` datetime DEFAULT NULL,
  `Comments` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_LOPHistory_LOP` (`LOPID`),
  CONSTRAINT `FK_LOPHistory_LOP` FOREIGN KEY (`LOPID`) REFERENCES `letter_of_purchase` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


alter table pocket_template add(WebTimeInterval int, WebServiceTimeInterval int,UTKTimeInterval int,BankChannelTimeInterval int, Denomination bigint, MaxUnits bigint);



alter table bulk_upload_file add (CompanyID bigint, index FK_BulkUploadFile_Company (CompanyID), constraint FK_BulkUploadFile_Company foreign key (CompanyID) references company (ID));

alter table company add column smsc varchar(255);
update company set smsc='m8_877' where id = 1;
update company set smsc='m8_808' where id = 2;

alter table distribution_chain_level add (MaxCommission double, MinCommission double);
alter table letter_of_purchase add (Commission double, Units bigint, BulkLOPID bigint);

alter table pending_commodity_transfer add (Units bigint, Denomination bigint);
alter table commodity_transfer add (Units bigint, Denomination bigint);

alter table letter_of_purchase add (index FK_LOP_BulkLOP (BulkLOPID), constraint FK_LOP_BulkLOP foreign key (BulkLOPID) references Bulk_LOP (ID));

   
