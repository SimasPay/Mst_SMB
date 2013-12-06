DROP TABLE IF EXISTS `service_defualt_config`;
CREATE TABLE  `service_defualt_config` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `ServiceProviderID` bigint(20) NOT NULL,
  `ServiceID` bigint(20) NOT NULL,
  `SourcePocketType` int(11) DEFAULT 1,
  `DestPocketType` int(11) DEFAULT 3,
  PRIMARY KEY (`ID`),
  KEY `FK_ServiceDefaultConfiguration_PartnerByServiceProviderID` (`ServiceProviderID`),
  KEY `FK_ServiceDefaultConfiguration_Service` (`ServiceID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;