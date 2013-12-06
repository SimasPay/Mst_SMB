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
