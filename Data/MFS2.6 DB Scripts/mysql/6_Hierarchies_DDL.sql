

ALTER TABLE `distribution_chain_temp` ADD COLUMN `ServiceID` INT(11) UNSIGNED NOT NULL AFTER `Description`;

ALTER TABLE `distribution_chain_lvl` ADD COLUMN `TransactionTypeID` INT(11) AFTER `DistributionLevel`;

DROP TABLE IF EXISTS `dct_restrictions`;

CREATE TABLE `dct_restrictions` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `DCTID` int(11) NOT NULL,
  `TransactionTypeID` int(11),
  `RelationShipType` int(11),
  `DistributionLevel` int(11),
  `IsAllowed` tinyint(4),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `partner_restrictions`;

CREATE TABLE `partner_restrictions` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `DCTID` int(11) NOT NULL,
  `PartnerID` int(11) NOT NULL,
  `TransactionTypeID` int(11),
  `RelationShipType` int(11),
  `IsAllowed` tinyint(4),
  `IsValid` tinyint(4) DEFAULT 1,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table dct_restrictions add unique index(DCTID, TransactionTypeID, RelationShipType, DistributionLevel);

alter table partner_restrictions add unique index(DCTID, PartnerID, TransactionTypeID, RelationShipType);