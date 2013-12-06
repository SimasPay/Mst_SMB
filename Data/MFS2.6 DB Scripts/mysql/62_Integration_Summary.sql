
CREATE TABLE `integration_summary` (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null default ' ',
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `SctlId` bigint(20) unsigned NOT NULL,
  `IntegrationType` varchar(255) default null,
  `ReconcilationID1` varchar(255) default null,
  `ReconcilationID2` varchar(255) default null,
  `ReconcilationID3` varchar(255) default null,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB default CHARSET=utf8;