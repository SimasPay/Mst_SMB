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



insert into company (ID, Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,CompanyName,CompanyCode) values (1,1,now(),'user',now(),'user','SMART','1001');
insert into company (ID, Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,CompanyName,CompanyCode) values (2,1,now(),'user',now(),'user','MOBILE8','1002');


alter table pocket add (CompanyID bigint, index FK_Pocket_Company (CompanyID), constraint FK_Pocket_Company foreign key (CompanyID) references company (ID));
alter table activities_log add (CompanyID bigint, ActivityCategory int, index FK_ActivitiesLog_Company (CompanyID),  constraint FK_ActivitiesLog_Company foreign key (CompanyID) references company (ID));



alter table bulk_upload add (CompanyID bigint, index FK_BulkUpload_Company (CompanyID), constraint FK_BulkUpload_Company foreign key (CompanyID) references company (ID));




alter table bulk_upload_file add (Description varchar(255), RecordCount int, FileProcessedDate datetime);

alter table commodity_transfer add (ProductIndicatorCode varchar(255) DEFAULT NULL,CompanyID bigint, index FK_CommodityTransfer_Company (CompanyID), constraint FK_CommodityTransfer_Company foreign key (CompanyID) references company (ID));

alter table company add column CustomerServiceNumber varchar(255);

alter table letter_of_purchase add (IsCommissionChanged tinyint(4) DEFAULT NULL, CompanyID bigint, index FK_LOP_Company (CompanyID), constraint FK_LOP_Company foreign key (CompanyID) references company (ID));

alter table merchant add column RangeCheck int;


alter table pending_commodity_transfer add (ProductIndicatorCode varchar(255) DEFAULT NULL, CompanyID bigint, index FK_PendingCommodityTransfer_Company (CompanyID), constraint FK_PendingCommodityTransfer_Company foreign key (CompanyID) references company (ID));


alter table pocket add column LowBalNotifQueryTime datetime;
alter table product_indicator add column CompanyID bigint;

alter table region add (RegionCode varchar(255), CompanyID bigint, index FK_Region_Company (CompanyID), constraint FK_Region_Company foreign key (CompanyID) references company (ID));
update region set companyid= coalesce((select c.id from company c where c.companycode=region.institutional),1);
alter table region DROP COLUMN Institutional;


alter table subscriber add (CompanyID bigint, MDNBrand varchar(255), index FK_Subscriber_Company (CompanyID), constraint FK_Subscriber_Company foreign key (CompanyID) references company (ID));

alter table user add (CompanyID bigint, index FK_User_Company (CompanyID), constraint FK_User_Company foreign key (CompanyID) references company (ID));


  
alter table notification add (AccessCode varchar(255), SMSNotificationCode varchar(255), CompanyID bigint, index FK_Notification_Company (CompanyID), constraint FK_Notification_Company foreign key (CompanyID) references company (ID));
 ALTER TABLE `mfino`.`notification` DROP INDEX `Index_notification_Code`,  
ADD UNIQUE INDEX `Index_notification_Code` USING BTREE(`MSPID`, `Code`, `NotificationMethod`, `Language`, `CompanyID`);

alter table product_indicator add index FK_ProductIndicator_Company (CompanyID), add constraint FK_ProductIndicator_Company foreign key (CompanyID) references company (ID);
update product_indicator set companyid= coalesce((select c.id from company c where c.companycode=product_indicator.companycode),1);
alter table product_indicator DROP COLUMN companycode;

update notification set companyid =1;
update activities_log set companyid =1;
update bulk_upload set companyid =1;
update commodity_transfer set companyid =1;
update letter_of_purchase set companyid =1;  
update user set companyid =1;
update subscriber set companyid=1;
update pending_commodity_transfer set CompanyID=1;
update pocket set CompanyID=1;

INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 1 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, 2 from offline_report where name!='OpenAPI');
INSERT IGNORE INTO `mfino`.`offline_report_company` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ReportID, CompanyID)  (select '1',NOW(),'system',NOW(),'system', id, null from offline_report where name ='OpenAPI');


update company set customerservicenumber='881' where id=1;
update company set customerservicenumber='888' where id=2;