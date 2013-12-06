drop table if exists `unregistered_txn_info`;

CREATE TABLE `unregistered_txn_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MDNID` bigint(20) NOT NULL,
  `TransferSCTLId` bigint(20) NOT NULL,
  `TransferCTId` bigint(20) DEFAULT NULL,
  `CashoutSCTLId` bigint(20) DEFAULT NULL,
  `CashoutCTId` bigint(20) DEFAULT NULL,
  `DigestedPIN` varchar(255) DEFAULT NULL,
  `UnRegisteredTxnStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_UnRegisteredTxnInfo_SCTL` (`TransferSCTLId`),
  KEY `FK_UnRegisteredTxnInfo_TransferID` (`TransferCTId`),
  KEY `FK_UnRegisteredTxnInfo_SubscriberMDNByMDNID` (`MDNID`),
  CONSTRAINT `FK_UnRegisteredTxnInfo_SubscriberMDNByMDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`),
  CONSTRAINT `FK_UnRegisteredTxnInfo_SCTL` FOREIGN KEY (`TransferSCTLId`) REFERENCES `service_charge_txn_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;