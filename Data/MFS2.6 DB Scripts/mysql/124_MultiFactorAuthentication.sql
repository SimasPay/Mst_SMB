-- Create Table mfa_transactions_info
DROP TABLE IF EXISTS `mfa_transactions_info`;
CREATE TABLE `mfa_transactions_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MSPID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `TransactionTypeID` bigint(20) NOT NULL,
  `ChannelCodeID` bigint(20) NOT NULL,
  `MFAMode` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MFATransactionInfo_Service` (`ServiceID`),
  KEY `FK_MFATransactionInfo_TransactionType` (`TransactionTypeID`),
  KEY `FK_MFATransactionInfo_ChannelCode` (`ChannelCodeID`),
  CONSTRAINT `FK_MFATransactionInfo_Service` FOREIGN KEY (`ServiceID`) REFERENCES `service` (`ID`),
  CONSTRAINT `FK_MFATransactionInfo_TransactionType` FOREIGN KEY (`TransactionTypeID`) REFERENCES `transaction_type` (`ID`),
  CONSTRAINT `FK_MFATransactionInfo_ChannelCode` FOREIGN KEY (`ChannelCodeID`) REFERENCES `channel_code` (`ID`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;


LOCK TABLES `enum_text` WRITE;
Delete From `enum_text` where TagID =8105;
INSERT INTO `enum_text`
(
`Version`,
`LastUpdateTime`,
`UpdatedBy`,
`CreateTime`,
`CreatedBy`,
`Language`,
`TagName`,
`TagID`,
`EnumCode`,
`EnumValue`,
`DisplayText`) values
(1,now(),'system',now(),'system',0,'MFAMode',8105,'0','None','None'),
(1,now(),'system',now(),'system',0,'MFAMode',8105,'1','OTP','OTP'),
(1,now(),'system',now(),'system',0,'MFAMode',8105,'2','SecurityQuestion','SecurityQuestion');
UNLOCK TABLES;


-- Create Table mfa_authentication
DROP TABLE IF EXISTS `mfa_authentication`;
CREATE TABLE `mfa_authentication` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SctlId` bigint(20) NOT NULL,
  `MFAMode` int(11) NOT NULL,
  `MFAValue` varchar(255) NOT NULL,  
  PRIMARY KEY (`ID`)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
