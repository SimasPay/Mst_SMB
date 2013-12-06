use mfino;

DROP TABLE IF EXISTS `credit_card_transaction`;
CREATE TABLE  `credit_card_transaction` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `PocketID` bigint(20) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Amount` bigint(20) DEFAULT NULL,
  `PaymentMethod` varchar(255) DEFAULT NULL,
  `ErrCode` varchar(255) DEFAULT NULL,
  `UserCode` varchar(255) DEFAULT NULL,
  `TransStatus` varchar(255) DEFAULT NULL,
  `CurrCode` varchar(255) DEFAULT NULL,
  `EUI` varchar(255) DEFAULT NULL,
  `TransactionDate` varchar(255) DEFAULT NULL,
  `TransType` varchar(255) DEFAULT NULL,
  `IsBlackListed` varchar(255) DEFAULT NULL,
  `FraudRiskLevel` int(11) DEFAULT NULL,
  `FraudRiskScore` double DEFAULT NULL,
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
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CreditCardTransaction_Pocket` (`PocketID`),
  KEY `FK_CreditCardTransaction_Subscriber` (`SubscriberID`),
  KEY `FK_CreditCardTransaction_Company` (`CompanyID`),
  CONSTRAINT `FK_CreditCardTransaction_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `card_info`;
CREATE TABLE  `card_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `IssuerName` varchar(255) DEFAULT NULL,
  `NameOnCard` varchar(255) DEFAULT NULL,
  `AddressID` bigint(20) DEFAULT NULL,
  `PocketID` bigint(20) DEFAULT NULL,
  `CardF6` varchar(255) DEFAULT NULL,
  `CardL3` varchar(255) DEFAULT NULL,
  `CardStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CardInfo_Pocket` (`PocketID`),
  KEY `FK_CardInfo_Subscriber` (`SubscriberID`),
  KEY `FK_CardInfo_Address` (`AddressID`),
  CONSTRAINT `FK_CardInfo_Address` FOREIGN KEY (`AddressID`) REFERENCES `address` (`ID`),
  CONSTRAINT `FK_CardInfo_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CardInfo_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table commodity_transfer add column CreditCardTransactionID bigint(20) default null, add CONSTRAINT `FK_CommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`);
alter table pending_commodity_transfer add column CreditCardTransactionID bigint(20) default null, add CONSTRAINT `FK_PendingCommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`);

INSERT INTO `offline_report` (`ID`, `Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `Name`, `ReportSql`, `ReportClass`) values('19','1',NOW(),'system',NOW(),'system','CreditCardRegistration',NULL,'com.mfino.report.CreditCardRegistrationReport');


insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',19,1);
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',19,2);

alter table `user` add 
( SecurityQuestion varchar(255) NULL, 
  SecurityAnswer varchar(255) NULL, 
  ConfirmationTime datetime NULL,
  UserActivationTime datetime NULL, 
  RejectionTime datetime NULL, 
  ExpirationTime datetime NULL,
  ConfirmationCode varchar(255) NULL,
  DateOfBirth datetime NULL
);

alter table `subscriber` add
(
  `SubscriberUserID` bigint(20) DEFAULT NULL,
  KEY `FK_Subscriber_UserBySubscriberUserID` (`SubscriberUserID`),
  CONSTRAINT `FK_Subscriber_UserBySubscriberUserID` FOREIGN KEY (`SubscriberUserID`) REFERENCES `user` (`ID`)
);

alter table address add column RegionName varchar(255) default null;

insert into `notification`(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) values (now(),'System',now(),'System',0,1,549,'InvalidSMSCommandToCode',1,'Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command to this Code. Info, call $(CustomerServiceShortCode).',null,0,0,now(),837,837,1);
insert into `notification`(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) values (now(),'System',now(),'System',0,1,549,'InvalidSMSCommandToCode',1,'Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command to this Code. Info, call $(CustomerServiceShortCode).',null,1,0,now(),837,837,1);
insert into `notification`(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) values (now(),'System',now(),'System',0,1,549,'InvalidSMSCommandToCode',1,'Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command to this Code. Info, call $(CustomerServiceShortCode).',null,0,0,now(),808,808,2);
insert into `notification`(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) values (now(),'System',now(),'System',0,1,549,'InvalidSMSCommandToCode',1,'Sorry, transaction on $(TransactionDateTime) failed. Invalid SMS Command to this Code. Info, call $(CustomerServiceShortCode).',null,1,0,now(),808,808,2);

ALTER TABLE sms_code add column ShortCodes varchar(255) null;

INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Your account has been activated. Now you can use your credit card to top up or pay SmartFren postpaid bills in SmartFren website. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Rekening anda sudah diaktifkan. Anda dapat menggunakan kartu kredit anda untuk top up dan melakukan pembayaran tagihan SmartFren melalui SmartFren website. Info hubungi $(CustomerServiceShortCode).", null, 
1, 0,now(),null,837,1);



INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Your account has been activated. Now you can use your credit card to top up or pay SmartFren postpaid bills in SmartFren website. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 547, "CCActivated", 1, "Rekening anda sudah diaktifkan. Anda dapat menggunakan kartu kredit anda untuk top up dan melakukan pembayaran tagihan SmartFren melalui SmartFren website. Info hubungi $(CustomerServiceShortCode).", null, 1, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, " Sorry, your credit card registration can’t be processed. The registered information does not match with bank information. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,837,1);

INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Maaf, registrasi kartu kredit Anda tidak dapat diproses. Informasi yang diregistrasikan berbeda dengan informasi bank. Info hub $(CustomerServiceShortCode).", null, 
1, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Sorry, your credit card registration can’t be processed. The registered information does not match with bank information. Info call $(CustomerServiceShortCode).", null, 
0, 0,now(),null,808,2);
INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 548, "CCRejected", 1, "Maaf, registrasi kartu kredit Anda tidak dapat diproses. Informasi yang diregistrasikan berbeda dengan informasi bank. Info hub $(CustomerServiceShortCode).", null, 
1, 0,now(),null,808,2);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Dest_MDN` USING BTREE(`MDN`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Auth_ID` USING BTREE(`AuthID`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Transaction_ID` USING BTREE(`TransactionID`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Bank_Ref_Num` USING BTREE(`BankReference`, `CompanyID`);

ALTER TABLE `credit_card_transaction` ADD INDEX `Index_Create_Time` USING BTREE(`CreateTime`, `CompanyID`);

INSERT INTO `offline_report` (`ID`, `Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `Name`, `ReportSql`, `ReportClass`) values('20','1',NOW(),'system',NOW(),'system','CreditCardTransaction',NULL,'com.mfino.report.CreditCardTransactionReport');
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',20,1);
insert into    offline_report_company (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `reportid`,`companyid`) values ('1',NOW(),'system',NOW(),'system',20,2);

INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Your transaction is having a problem. To check the status of transaction, contact $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
0, 0,now(),null,837,1);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Transaksi yang anda lakukan mengalami masalah. Untuk memastikan status transaksi, hub $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
1, 0,now(),null,837,1);



INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Your transaction is having a problem. To check the status of transaction, contact $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 
0, 0,now(),null,808,2);


INSERT INTO `notification`
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,
`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,
`Language`,`Status`,`StatusTime`,`accesscode`,`smsnotificationcode`,`companyID`)
VALUES
(now(), "System", now(), "System",0,
1, 550, "MultixGenericCCResponse", 4, "Transaksi yang anda lakukan mengalami masalah. Untuk memastikan status transaksi, hub $(CustomerServiceShortCode). CCTransID: $(CCTransID)", null, 1, 0,now(),null,808,2);

