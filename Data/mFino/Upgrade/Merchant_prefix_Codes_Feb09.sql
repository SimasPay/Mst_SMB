use mfino;

DROP TABLE IF EXISTS `mfino`.`merchant_prefix_code`;
CREATE TABLE  `mfino`.`merchant_prefix_code` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MerchantPrefixCode` int(11) NOT NULL,
  `BillerName` varchar(255) NOT NULL,
  `CompanyID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `MerchantPrefixCode` (`MerchantPrefixCode`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES
(now(),"System",now(),"System",0,1,554,"EMoneyTransferRequestCompleted",4,"Thank you for eMoney topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID)",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,554,"EMoneyTransferRequestCompleted",4,"Thank you for eMoney topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID)",null,0,0,now(),null,null,2),
(now(),"System",now(),"System",0,1,554,"EMoneyTransferRequestCompleted",4,"Thank you for eMoney topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID)",null,1,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,554,"EMoneyTransferRequestCompleted",4,"Thank you for eMoney topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID)",null,1,0,now(),null,null,2);

INSERT INTO `notification` (`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Version`,`MSPID`,`Code`,`CodeName`,`NotificationMethod`,`Text`,`STKML`,`Language`,`Status`,`StatusTime`,`AccessCode`,`SMSNotificationCode`,`CompanyID`) VALUES
(now(),"System",now(),"System",0,1,555,"DestinationMoneySVAPocketNotActive",4,"Sorry, transaction on $(TransactionDateTime) failed.  Merchant Account service on destination number is not active. Info, call $(CustomerServiceShortCode). REF: $(TransactionID).",null,0,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,555,"DestinationMoneySVAPocketNotActive",4,"Sorry, transaction on $(TransactionDateTime) failed.  Merchant Account service on destination number is not active. Info, call $(CustomerServiceShortCode). REF: $(TransactionID).",null,0,0,now(),null,null,2),
(now(),"System",now(),"System",0,1,555,"DestinationMoneySVAPocketNotActive",4,"Sorry, transaction on $(TransactionDateTime) failed.  Merchant Account service on destination number is not active. Info, call $(CustomerServiceShortCode). REF: $(TransactionID).",null,1,0,now(),null,null,1),
(now(),"System",now(),"System",0,1,555,"DestinationMoneySVAPocketNotActive",4,"Sorry, transaction on $(TransactionDateTime) failed.  Merchant Account service on destination number is not active. Info, call $(CustomerServiceShortCode). REF: $(TransactionID).",null,1,0,now(),null,null,2);