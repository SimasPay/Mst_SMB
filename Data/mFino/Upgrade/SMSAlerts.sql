use mfino;

DROP TABLE IF EXISTS `mfino`.`sms_partner`;
CREATE TABLE  `mfino`.`sms_partner` (
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
  `APIKey` varchar(255) DEFAULT NULL,
  `SendReport` tinyint(4) DEFAULT 0,
  PRIMARY KEY (`ID`),
  KEY `FK_SMSPartner_User` (`UserID`),
  CONSTRAINT `FK_SMSPartner_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `mfino`.`smsc_configuration`;
CREATE TABLE  `mfino`.`smsc_configuration` (
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
  `Charging` DOUBLE DEFAULT NULL,
  `Header` varchar(255) DEFAULT NULL,
  `Footer` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `ShortCode_UQ` (`ShortCode`) USING BTREE,
  KEY `FK_SMSC_SMSPartnerByPartnerID` (`PartnerID`),
  CONSTRAINT `FK_SMSC_SMSPartnerByPartnerID` FOREIGN KEY (`PartnerID`) REFERENCES `sms_partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `mfino`.`sms_transaction_log`;
CREATE TABLE  `mfino`.`sms_transaction_log` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `FieldID` varchar(255) DEFAULT NULL,
  `TransactionTime` datetime NOT NULL,
  `PartnerID` bigint(20) NOT NULL,
  `Source` int(11) NOT NULL,
  `DestMDN` varchar(255) NOT NULL,
  `SmscID` varchar(255) NOT NULL,
  `TransactionStatus` varchar(255) DEFAULT NULL,
  `DeliveryStatus` varchar(255) DEFAULT NULL,
  `MessageData` longtext NOT NULL,
  `MessageCode` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_SMSTransactionsLog_SMSPartnerByPartnerID` (`PartnerID`),
  CONSTRAINT `FK_SMSTransactionsLog_SMSPartnerByPartnerID` FOREIGN KEY (`PartnerID`) REFERENCES `sms_partner` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `notification` 
(`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES


(now(),"System",now(),"System",0,1,559,"InvalidSMSAlertRequest_ParameterMissing",1,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,559,"InvalidSMSAlertRequest_ParameterMissing",2,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,559,"InvalidSMSAlertRequest_ParameterMissing",4,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,559,"InvalidSMSAlertRequest_ParameterMissing",8,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,559,"InvalidSMSAlertRequest_ParameterMissing",16,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),


(now(),"System",now(),"System",0,1,560,"SMSMessageLength_Exceeded",1,"Sorry, transaction on $(TransactionDateTime) failed. SMS Message Length Exceeded. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,560,"SMSMessageLength_Exceeded",2,"Sorry, transaction on $(TransactionDateTime) failed. SMS Message Length Exceeded. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,560,"SMSMessageLength_Exceeded",4,"Sorry, transaction on $(TransactionDateTime) failed. SMS Message Length Exceeded. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,560,"SMSMessageLength_Exceeded",8,"Sorry, transaction on $(TransactionDateTime) failed. SMS Message Length Exceeded. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,560,"SMSMessageLength_Exceeded",16,"Sorry, transaction on $(TransactionDateTime) failed. SMS Message Length Exceeded. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),


(now(),"System",now(),"System",0,1,561,"InvalidSMSAlertRequest_ServerIP",1,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request ServerIP. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,561,"InvalidSMSAlertRequest_ServerIP",2,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request ServerIP. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,561,"InvalidSMSAlertRequest_ServerIP",4,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request ServerIP. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,561,"InvalidSMSAlertRequest_ServerIP",8,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request ServerIP. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,561,"InvalidSMSAlertRequest_ServerIP",16,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request ServerIP. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),


(now(),"System",now(),"System",0,1,562,"PartnerID_ShortCode_NotFound",1,"Sorry, transaction on $(TransactionDateTime) failed. PartnerID or ShortCode NotFound. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,562,"PartnerID_ShortCode_NotFound",2,"Sorry, transaction on $(TransactionDateTime) failed. PartnerID or ShortCode NotFound. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,562,"PartnerID_ShortCode_NotFound",4,"Sorry, transaction on $(TransactionDateTime) failed. PartnerID or ShortCode NotFound. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,562,"PartnerID_ShortCode_NotFound",8,"Sorry, transaction on $(TransactionDateTime) failed. PartnerID or ShortCode NotFound. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,562,"PartnerID_ShortCode_NotFound",16,"Sorry, transaction on $(TransactionDateTime) failed. PartnerID or ShortCode NotFound. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),


(now(),"System",now(),"System",0,1,563,"InvalidSMSAlertRequest_Token",1,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request Token. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,563,"InvalidSMSAlertRequest_Token",2,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request Token. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,563,"InvalidSMSAlertRequest_Token",4,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request Token. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,563,"InvalidSMSAlertRequest_Token",8,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request Token. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,563,"InvalidSMSAlertRequest_Token",16,"Sorry, transaction on $(TransactionDateTime) failed. Invalid SMSAlert Request Token. Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),


(now(),"System",now(),"System",0,1,564,"SMSAlertRequest_Success",1,"Your SMSAlert Request submitted successfully on $(TransactionDateTime)  Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,564,"SMSAlertRequest_Success",2,"Your SMSAlert Request submitted successfully on $(TransactionDateTime)  Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,564,"SMSAlertRequest_Success",4,"Your SMSAlert Request submitted successfully on $(TransactionDateTime)  Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,564,"SMSAlertRequest_Success",8,"Your SMSAlert Request submitted successfully on $(TransactionDateTime)  Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1),

(now(),"System",now(),"System",0,1,564,"SMSAlertRequest_Success",16,"Your SMSAlert Request submitted successfully on $(TransactionDateTime)  Info, call $(CustomerServiceShortCode).",null,0,0,now(),null,null,1);


ALTER TABLE `mfino`.`smsc_configuration` DROP INDEX `ShortCode_UQ`;

