use mfino;

DROP TABLE IF EXISTS `mfino`.`biller`;
CREATE TABLE  `mfino`.`biller` (
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
  `TransactionFee` DOUBLE DEFAULT NULL,
  `BillRefOffSet` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `BankCode` (`BankCode`,`BillerCode`),
  UNIQUE KEY `BillerName` (`BankCode`,`BillerName`),
  KEY `FK_Biller_Company` (`CompanyID`),
  CONSTRAINT `FK_Biller_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `mfino`.`denomination`;
CREATE TABLE  `mfino`.`denomination` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `DenominationAmount` bigint(20) NOT NULL,
  `BillerID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_Denomination_Biller` (`BillerID`),
  CONSTRAINT `FK_Denomination_Biller` FOREIGN KEY (`BillerID`) REFERENCES `biller` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `mfino`.`bill_payment_transaction`;
CREATE TABLE  `mfino`.`bill_payment_transaction` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` bigint(20) NOT NULL,
  `TransactionID` bigint(20) NOT NULL,
  `BankCode` int(11) NOT NULL,
  `BillerName` varchar(255) DEFAULT NULL,
  `CompanyID` bigint(20) NOT NULL,
  `BillPaymentReferenceID` varchar(255) DEFAULT NULL,
  `Amount` bigint(20) DEFAULT NULL,
  `TransactionDate` varchar(255) DEFAULT NULL,
  `BillerCode` varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BillPaymentTransaction_Subscriber` (`SubscriberID`),
  KEY `FK_BillPaymentTransaction_Company` (`CompanyID`),
  CONSTRAINT `FK_BillPaymentTransaction_Company` FOREIGN KEY (`CompanyID`) REFERENCES `company` (`ID`),
  CONSTRAINT `FK_BillPaymentTransaction_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;