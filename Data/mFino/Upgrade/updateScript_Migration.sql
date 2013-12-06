use mfino;

--
-- Definition of table `brand`
--

DROP TABLE IF EXISTS `brand`;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



--
-- Definition of table `mdn_range`
--

DROP TABLE IF EXISTS `mdn_range`;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Definition of table `offline_report_company`
--

DROP TABLE IF EXISTS `offline_report_company`;
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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


--
-- Definition of table `sms_code`
--

DROP TABLE IF EXISTS `sms_code`;
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
  PRIMARY KEY (`ID`),
  UNIQUE KEY `SMSCodeText` (`SMSCodeText`),
  KEY `FK_SMSCode_Brand` (`BrandID`),
  CONSTRAINT `FK_SMSCode_Brand` FOREIGN KEY (`BrandID`) REFERENCES `brand` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `merchant_code`;
CREATE TABLE  `merchant_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantCode` int(11) NOT NULL,
  `MDN` varchar(255) NOT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MerchantCode` (`MerchantCode`),
  KEY `FK_MerchantCode_Company` (`CompanyID`),
  CONSTRAINT `FK_MerchantCode_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;



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
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
 
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

alter table company add (smsc varchar(255),CustomerServiceNumber varchar(255));

insert into company (ID, Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,CompanyName,CompanyCode,smsc,customerservicenumber) values (1,1,now(),'user',now(),'user','SMART','1001','m8_877','881');
insert into company (ID, Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,CompanyName,CompanyCode,smsc,customerservicenumber) values (2,1,now(),'user',now(),'user','MOBILE8','1002','m8_808','888');

INSERT INTO `offline_report` (`ID`, `Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `Name`, `ReportSql`, `ReportClass`) values('18','1',NOW(),'system',NOW(),'system','LOPDiscountChange',NULL,'com.mfino.report.LOPDiscountChangeReport');
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',18,1);
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',18,2);

alter table subscriber add (CompanyID bigint, MDNBrand varchar(255), index FK_Subscriber_Company (CompanyID), constraint FK_Subscriber_Company foreign key (CompanyID) references company (ID));


alter table pocket add (LowBalNotifQueryTime datetime, CompanyID bigint, index FK_Pocket_Company (CompanyID), constraint FK_Pocket_Company foreign key (CompanyID) references company (ID));

update pocket set companyid=(select s.companyid from subscriber s inner join subscriber_mdn m  on m.subscriberid=s.id where m.id=pocket.mdnid limit 1);


alter table activities_log add (CompanyID bigint, ActivityCategory int, index FK_ActivitiesLog_Company (CompanyID),  constraint FK_ActivitiesLog_Company foreign key (CompanyID) references company (ID));



alter table bulk_upload add (CompanyID bigint, index FK_BulkUpload_Company (CompanyID), constraint FK_BulkUpload_Company foreign key (CompanyID) references company (ID));




alter table bulk_upload_file add (Description varchar(255), RecordCount int, FileProcessedDate datetime,CompanyID bigint, index FK_BulkUploadFile_Company (CompanyID), constraint FK_BulkUploadFile_Company foreign key (CompanyID) references company (ID));

alter table commodity_transfer add (Units bigint, Denomination bigint,ProductIndicatorCode varchar(255) DEFAULT NULL,CompanyID bigint, index FK_CommodityTransfer_Company (CompanyID), constraint FK_CommodityTransfer_Company foreign key (CompanyID) references company (ID));


alter table letter_of_purchase add (Commission double, Units bigint, BulkLOPID bigint, IsCommissionChanged tinyint(4) DEFAULT NULL, CompanyID bigint, index FK_LOP_BulkLOP (BulkLOPID), constraint FK_LOP_BulkLOP foreign key (BulkLOPID) references Bulk_LOP (ID), index FK_LOP_Company (CompanyID), constraint FK_LOP_Company foreign key (CompanyID) references company (ID));

alter table merchant add column RangeCheck int;


alter table pending_commodity_transfer add (Units bigint, Denomination bigint, ProductIndicatorCode varchar(255) DEFAULT NULL, CompanyID bigint, index FK_PendingCommodityTransfer_Company (CompanyID), constraint FK_PendingCommodityTransfer_Company foreign key (CompanyID) references company (ID));


alter table product_indicator add (CompanyID bigint, index FK_ProductIndicator_Company (CompanyID), constraint FK_ProductIndicator_Company foreign key (CompanyID) references company (ID));

alter table region add (RegionCode varchar(255), CompanyID bigint, index FK_Region_Company (CompanyID), constraint FK_Region_Company foreign key (CompanyID) references company (ID));
update region set companyid= coalesce((select c.id from company c where c.companycode=region.institutional),1);
alter table region DROP COLUMN Institutional;


alter table user add (CompanyID bigint, index FK_User_Company (CompanyID), constraint FK_User_Company foreign key (CompanyID) references company (ID));

alter table pocket_template add(WebTimeInterval int, WebServiceTimeInterval int,UTKTimeInterval int,BankChannelTimeInterval int, Denomination bigint, MaxUnits bigint);
  
alter table notification add (AccessCode varchar(255), SMSNotificationCode varchar(255), CompanyID bigint, index FK_Notification_Company (CompanyID), constraint FK_Notification_Company foreign key (CompanyID) references company (ID));
 ALTER TABLE `mfino`.`notification` DROP INDEX `Index_notification_Code`,  
ADD UNIQUE INDEX `Index_notification_Code` USING BTREE(`MSPID`, `Code`, `NotificationMethod`, `Language`, `CompanyID`);

update product_indicator set companyid= coalesce((select c.id from company c where c.companycode=product_indicator.companycode),1);
alter table product_indicator DROP COLUMN companycode;






alter table distribution_chain_level add (MaxCommission double, MinCommission double);




update notification set companyid =1;
update activities_log set companyid =1;
update bulk_upload set companyid =1;
update commodity_transfer set companyid =1;
update letter_of_purchase set companyid =1;  
update user set companyid =1;
update subscriber set companyid=1;
update pending_commodity_transfer set CompanyID=1;
update pocket set CompanyID=1;

INSERT IGNORE INTO `offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 2 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, null from offline_report where name ='OpenAPI');

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES

(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",1,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,0,0,"2010-09-24 11:08:28",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",2,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,0,0,"2010-09-24 11:08:28",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",4,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,0,0,"2010-09-24 11:08:28",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",8,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,0,0,"2010-09-24 11:08:28",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",16,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,0,0,"2010-09-24 11:08:29",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",1,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,1,0,"2010-09-24 11:08:29",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",2,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,1,0,"2010-09-24 11:08:29",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",4,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,1,0,"2010-09-24 11:08:29",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",8,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,1,0,"2010-09-24 11:08:30",null,null,1),
(now(),"System",now(),"System",0,1,537,"InvalidSMSCommand",16,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command specified. Info, call $(CustomerServiceShortCode).",null,1,0,"2010-09-24 11:08:30",null,null,1)
;

insert into notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, version, MSPID, Code, CodeName, NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID)
select LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, version, MSPID, Code, CodeName, NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode,2 from Notification where CompanyID=1;