use mfino;

DROP TABLE IF EXISTS `mfino`.`airtime_purchase`;
CREATE TABLE  `mfino`.`airtime_purchase` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `Version` int(11) unsigned NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL,
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SctlId` bigint(20) unsigned NOT NULL,
  `PartnerCode` varchar(255) DEFAULT NULL,
  `INCode` varchar(255) DEFAULT NULL,
  `RechargeMDN` varchar(255) DEFAULT NULL,
  `SourceMDN` varchar(255) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  `Charges` decimal(25,4) DEFAULT NULL,
  `ResponseCode` int(11) DEFAULT NULL,
  `INTxnId` varchar(255) DEFAULT NULL,
  `INAccountType` varchar(255) DEFAULT NULL,
  `AirtimePurchaseStatus` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `mfino`.`visafone_txn_generator`;
CREATE TABLE  `mfino`.`visafone_txn_generator` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Version` int(11) unsigned NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL,
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `TxnTimestamp` datetime DEFAULT NULL,
  `TxnCount` int(11) unsigned DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;