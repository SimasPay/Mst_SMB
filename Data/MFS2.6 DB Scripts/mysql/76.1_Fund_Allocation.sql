 
CREATE TABLE expiration_type (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `MSPID` bigint(20) not null,
  `ExpiryType` int(11),
  `ExpiryMode` int(11),
  `ExpiryValue` bigint(20),
  `ExpiryDescription` varchar(255),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE purpose (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `MSPID` bigint(20) not null,
  `Category` int(11),
  `Code` varchar(255),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE fund_events (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `MSPID` bigint(20) not null,
  `FundEventType` int(11),
  `FundEventDescription` varchar(255),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE fund_definition (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `MSPID` bigint(20) not null,
  `PurposeID` bigint(20),
  `FACLength` int(11),
  `FACPrefix` varchar(255),
  `ExpiryID` bigint(20),
  `MaxFailAttemptsAllowed` int(11),
  `OnFundAllocationTimeExpiry` bigint(20),
  `OnFailedAttemptsExceeded` bigint(20),
  `GenerationOfOTPOnFailure` bigint(20),
  `IsMultipleWithdrawalAllowed` tinyint(4),
  PRIMARY KEY (`ID`),
  KEY `FK_fundDef_fundEvent_expiry` (`OnFundAllocationTimeExpiry`),
  KEY `FK_fundDef_fundEvent_failExd` (`OnFailedAttemptsExceeded`),
  KEY `FK_fundDef_fundEvent_otpGen` (`GenerationOfOTPOnFailure`),
  KEY `FK_fundDef_expType_expID` (`ExpiryID`),
  CONSTRAINT `FK_fundDef_fundEvent_expiry` FOREIGN KEY (`OnFundAllocationTimeExpiry`) REFERENCES `fund_events` (`ID`),
  CONSTRAINT `FK_fundDef_fundEvent_failExd` FOREIGN KEY (`OnFailedAttemptsExceeded`) REFERENCES `fund_events` (`ID`),
  CONSTRAINT `FK_fundDef_fundEvent_otpGen` FOREIGN KEY (`GenerationOfOTPOnFailure`) REFERENCES `fund_events` (`ID`),
  CONSTRAINT `FK_fundDef_expType_expID` FOREIGN KEY (`ExpiryID`) REFERENCES `expiration_type` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE fund_distribution_info (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `MSPID` bigint(20) not null,
  `FundAllocationId` bigint(20),
  `DistributedAmount` decimal(25,4),
  `DistributionStatus` int(11),
  `FailureReason` varchar(255),
  `FailureReasonCode` int(11),
  `TransferSCTLId` bigint(20),
  `TransferCTId` bigint(20),
  `DistributionType` int(11),
  PRIMARY KEY (`ID`),
  KEY `FK_fundDist_UnRegTrxn_fAlloc` (`FundAllocationId`),
  CONSTRAINT `FK_fundDist_UnRegTrxn_fAlloc` FOREIGN KEY (`FundAllocationId`) REFERENCES `unregistered_txn_info` (`ID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE `unregistered_txn_info` ADD `FailureReasonCode` int(11) ;
ALTER TABLE `unregistered_txn_info` ADD `ReversalReason` varchar(255);
ALTER TABLE `unregistered_txn_info` ADD `ExpiryTime` datetime ;
ALTER TABLE `unregistered_txn_info` ADD `FundDefinitionID` bigint(20) ;
ALTER TABLE `unregistered_txn_info` ADD `AvailableAmount` decimal(25,4);
ALTER TABLE `unregistered_txn_info` ADD `WithdrawalMDN` varchar(255);
ALTER TABLE `unregistered_txn_info` ADD `WithdrawalFailureAttempt` int(11) ;
ALTER TABLE `unregistered_txn_info` ADD `PartnerCode` varchar(255);
ALTER TABLE `unregistered_txn_info` ADD CONSTRAINT FK_UnRegTrxn_fundDef_fDef FOREIGN KEY (FundDefinitionID) REFERENCES fund_definition(`ID`);






