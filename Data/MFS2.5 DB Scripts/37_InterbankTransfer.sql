use mfino;

INSERT INTO system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES 
(1, now(), 'system', now(), 'system', 'interbank.partner.mdn', '', 'Partner MDN for Interbank transfers');

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'InterBankTransfer','Inter Bank Transfer');

INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Bank'),(select id from transaction_type where TransactionName='InterBankTransfer'));

INSERT INTO `pocket_template` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`Type`,`BankAccountCardType`,`Description`,`Commodity`,`CardPANSuffixLength`,`Units`,`Allowance`,`MaximumStoredValue`,`MinimumStoredValue`,`MaxAmountPerTransaction`,`MinAmountPerTransaction`,`MaxAmountPerDay`,`MaxAmountPerWeek`,`MaxAmountPerMonth`,`MaxTransactionsPerDay`,`MaxTransactionsPerWeek`,`MaxTransactionsPerMonth`,`MinTimeBetweenTransactions`,`BankCode`,`OperatorCode`,`BillingType`,`LowBalanceNotificationThresholdAmount`,`LowBalanceNotificationEnabled`,`WebTimeInterval`,`WebServiceTimeInterval`,`UTKTimeInterval`,`BankChannelTimeInterval`,`Denomination`,`MaxUnits`,`PocketCode`,`TypeOfCheck`,`RegularExpression`,`IsCollectorPocket`,`NumberOfPocketsAllowedForMDN`) VALUES 
(1,now(),'system',now(),'system',1,3,20,'InterBankTransferTemplate',4,6,NULL,0,'1000000.0000','0.0000','1000000.0000','0.0000','1000000.0000','100000000.0000','1000000000.0000',1000,100000,10000000,0,9998,NULL,NULL,'0.0000',0,NULL,NULL,NULL,NULL,NULL,NULL,'8',0,'',0,1);

DROP TABLE IF EXISTS `mfino`.`interbank_transfers`;

CREATE TABLE `mfino`.`interbank_transfers` (
  `ID` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Version` INTEGER(11) UNSIGNED NOT NULL,
  `LastUpdateTime` DATETIME NOT NULL,
  `UpdatedBy` VARCHAR(255) NOT NULL,
  `CreateTime` DATETIME NOT NULL,
  `CreatedBy` VARCHAR(255) NOT NULL,
  `TerminalID` VARCHAR(255) NOT NULL,
  `DestBankCode` VARCHAR(255) NOT NULL,
  `SourceAccountName` varchar(255),
  `DestAccountName` varchar(255),
  `SourceAccountNumber` varchar(255),
  `DestAccountNumber` varchar(255),
  `Amount` DECIMAL(25,4) NOT NULL,
  `Charges` DECIMAL(25,4) NOT NULL,
  `TransferID` BIGINT(20) UNSIGNED,
  `SctlId` BIGINT(20) UNSIGNED,
  `SessionID` varchar(255),
  `Narration` varchar(255),
  `PaymentReference` VARCHAR(45),
  `NIBResponseCode` VARCHAR(45),
  `IBTStatus` INTEGER(11) UNSIGNED,
  PRIMARY KEY (`ID`)
)
ENGINE = InnoDB;

DROP TABLE IF EXISTS `mfino`.`interbank_codes`;

CREATE TABLE `mfino`.`interbank_codes` (
  `ID` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
  `Version` INT(11) UNSIGNED NOT NULL,
  `LastUpdateTime` DATETIME NOT NULL,
  `UpdatedBy` VARCHAR(255) NOT NULL,
  `CreateTime` DATETIME NOT NULL,
  `CreatedBy` VARCHAR(255) NOT NULL,
  `BankCode` VARCHAR(45) NOT NULL,
  `BankName` VARCHAR(512) NOT NULL,
  `ibAllowed` TINYINT UNSIGNED NOT NULL,
  PRIMARY KEY (`ID`)
)
ENGINE = InnoDB;

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '044', 'ACCESS BA0K 0IGERIA PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '014', 'AFRIBA0K 0IG PLC', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '082', 'KE1STO0E BA0K', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '063', 'DIAMO0D BA0K LTD', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '050', 'ECOBA0K PLC', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '040', 'EQUITORIAL TRUST BA0K', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '070', 'FIDELIT1 BA0K PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '011', 'FIRST BA0K OF 0IGERIA PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '214', 'FIRST CIT1 MO0UME0T BA0K', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '085', 'FIRST I0LA0D BA0K', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '058', 'GUARA0T1 TRUST BA0K PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '069', 'I0TERCO0TI0E0TAL BA0K LTD', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '023', '0IGERIA I0TL BA0K LTD (CITIBA0K)', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '056', 'OCEA0IC BA0K I0TL LTD', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '076', 'SK1E BA0K PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '084', 'SPRI0G BA0K PLC', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '221', 'STA0BIC-IBTC BA0K PLC', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '068', 'STA0DARD CHARTERED BA0K', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '232', 'STERLI0G BA0K', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '033', 'UBA PLC.', 1);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '032', 'U0IO0 BA0K', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '215', 'U0IT1 BA0K PLC', 0);

INSERT INTO interbank_codes (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, BankCode, BankName, ibAllowed) VALUES (1, NOW(), 'system', NOW(), 'system', '035', 'WEMA BA0K PLC', 0);