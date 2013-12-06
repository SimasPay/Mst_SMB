use mfino;

CREATE TABLE `enum_text` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Language` int(11) DEFAULT NULL,
  `TagName` varchar(255) DEFAULT NULL,
  `TagID` int(11) DEFAULT NULL,
  `EnumCode` varchar(255) DEFAULT NULL,
  `EnumValue` varchar(255) DEFAULT NULL,
  `LastUpdateTime` datetime DEFAULT NULL,
  `UpdatedBy` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `Language` (`Language`,`TagID`,`EnumCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

