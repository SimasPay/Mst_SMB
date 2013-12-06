
update `mfino`.`commodity_transfer` set creditcardtransactionid=null;
update `mfino`.`pending_commodity_transfer` set creditcardtransactionid=null;

ALTER TABLE `mfino`.`commodity_transfer` DROP FOREIGN KEY  `FK_CommodityTransfer_CreditCardTransaction`;
ALTER TABLE `mfino`.`pending_commodity_transfer`  DROP FOREIGN KEY  `FK_PendingCommodityTransfer_CreditCardTransaction`;

DROP TABLE IF EXISTS `mfino`.`credit_card_destinations`;
CREATE TABLE  `mfino`.`credit_card_destinations` (
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


DROP TABLE IF EXISTS `mfino`.`card_info`;
CREATE TABLE  `mfino`.`card_info` (
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


DROP TABLE IF EXISTS `mfino`.`credit_card_transaction`;
CREATE TABLE  `mfino`.`credit_card_transaction` (
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
  `NSIATransCompletionTime` datetime DEFAULT NULL,
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
  `CCFailureReason` int(11) DEFAULT NULL,
  `SessionID` varchar(255) DEFAULT NULL,
  `CCBucketType` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CreditCardTransaction_Pocket` (`PocketID`),
  KEY `FK_CreditCardTransaction_Subscriber` (`SubscriberID`),
  KEY `FK_CreditCardTransaction_Company` (`CompanyID`),
  CONSTRAINT `FK_CreditCardTransaction_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Pocket` FOREIGN KEY (`PocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_CreditCardTransaction_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table commodity_transfer add CONSTRAINT `FK_CommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`);
alter table pending_commodity_transfer add CONSTRAINT `FK_PendingCommodityTransfer_CreditCardTransaction` FOREIGN KEY (`CreditCardTransactionID`) REFERENCES `credit_card_transaction` (`ID`);



ALTER TABLE  `mfino`.`user` ADD (
  `ForgotPasswordCode` varchar(255) DEFAULT NULL,
  `HomePhone` varchar(255) DEFAULT NULL,
  `WorkPhone` varchar(255) DEFAULT NULL,
  `OldHomePhone` varchar(255) DEFAULT NULL,
  `OldWorkPhone` varchar(255) DEFAULT NULL,
  `OldSecurityQuestion` varchar(255) DEFAULT NULL,
  `OldSecurityAnswer` varchar(255) DEFAULT NULL,
  `OldFirstName` varchar(255) DEFAULT NULL,
  `OldLastName` varchar(255) DEFAULT NULL
) ;

ALTER TABLE  `mfino`.`subscriber_mdn` ADD(`ScrambleCode` varchar(255) DEFAULT NULL);
