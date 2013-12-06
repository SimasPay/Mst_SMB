DROP TABLE IF EXISTS `partner_default_services`;
CREATE TABLE  `partner_default_services` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `BusinessPartnerType` int(11) NOT NULL,
  `ServiceDefaultConfigurationID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_PartnerDefaultServices_ServiceDefaultConfiguration` (`ServiceDefaultConfigurationID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;