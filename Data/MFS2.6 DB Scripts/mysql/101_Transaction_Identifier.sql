CREATE TABLE `transaction_identifier` (
  `ID` bigint(20) not null auto_increment,
  `Version` int(11) not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `TransactionIdentifier` varchar(255) not null,
  `ServiceChargeTransactionLogID` bigint(20),
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;