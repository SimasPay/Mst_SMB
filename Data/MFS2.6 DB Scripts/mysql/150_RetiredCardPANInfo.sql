DROP TABLE IF EXISTS `retired_cardpan_info`;
CREATE TABLE `retired_cardpan_info` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `CardPAN` varchar(255) NOT NULL,
  `RetireCount` int(11) NOT NULL,  
  PRIMARY KEY (`ID`),
  CONSTRAINT UNIQUE_retired_cardpan_info UNIQUE (CardPAN)
  ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
