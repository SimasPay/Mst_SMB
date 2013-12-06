


-- Create Columns BillerType and IntegrationCode to mfsbiller_partner_map
ALTER TABLE `mfsbiller_partner_map` ADD COLUMN `BillerPartnerType` INT(10) NULL  AFTER `PartnerBillerCode` , ADD COLUMN `IntegrationCode` VARCHAR(255) NULL  AFTER `BillerPartnerType` ;

-- Create Table mfs_denominations
DROP TABLE IF EXISTS `mfs_denominations`;
CREATE TABLE `mfs_denominations` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MFSID` bigint(20) NOT NULL,
  `Denomination` bigint(20) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `ProductCode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_MfsDenominations_ID` (`MFSID`),
  CONSTRAINT `FK_MfsDenominations_ID` FOREIGN KEY (`MFSID`) REFERENCES `mfsbiller_partner_map` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


LOCK TABLES `enum_text` WRITE;
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
(1,now(),'system',now(),'system',0,'BillerPartnerType',7040,'0','Payment_Partial','Payment_Partial'),
(1,now(),'system',now(),'system',0,'BillerPartnerType',7040,'1','Payment_Full','Payment_Full'),
(1,now(),'system',now(),'system',0,'BillerPartnerType',7040,'2','Topup_Free','Topup_Free'),
(1,now(),'system',now(),'system',0,'BillerPartnerType',7040,'3','Topup_Denomination','Topup_Denomination');
UNLOCK TABLES;