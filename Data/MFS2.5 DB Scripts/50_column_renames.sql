use mfino;

ALTER TABLE `activities_log` CHANGE COLUMN `ISO8583_RetrievalReferenceNumber` `ISO8583_RetrievalReferenceNum` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 CHANGE COLUMN `ISO8583_AcquiringInstitutionIdentificationCode` `ISO8583_AcquiringInstIdCode` INT(11) DEFAULT NULL,
 CHANGE COLUMN `ISO8583_CardAcceptorIdentificationCode` `ISO8583_CardAcceptorIdCode` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `commodity_transfer` CHANGE COLUMN `ISO8583_LocalTransactionTimeHhmmss` `ISO8583_LocalTxnTimeHhmmss` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 CHANGE COLUMN `ISO8583_AcquiringInstitutionIdentificationCode` `ISO8583_AcquiringInstIdCode` INT(11) DEFAULT NULL,
 CHANGE COLUMN `ISO8583_RetrievalReferenceNumber` `ISO8583_RetrievalReferenceNum` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 CHANGE COLUMN `ISO8583_CardAcceptorIdentificationCode` `ISO8583_CardAcceptorIdCode` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `pending_commodity_transfer` CHANGE COLUMN `ISO8583_LocalTransactionTimeHhmmss` `ISO8583_LocalTxnTimeHhmmss` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 CHANGE COLUMN `ISO8583_AcquiringInstitutionIdentificationCode` `ISO8583_AcquiringInstIdCode` INT(11) DEFAULT NULL,
 CHANGE COLUMN `ISO8583_RetrievalReferenceNumber` `ISO8583_RetrievalReferenceNum` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 CHANGE COLUMN `ISO8583_CardAcceptorIdentificationCode` `ISO8583_CardAcceptorIdCode` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `pocket` CHANGE COLUMN `CurrentDailyTransactionsCount` `CurrentDailyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentWeeklyTransactionsCount` `CurrentWeeklyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentMonthlyTransactionsCount` `CurrentMonthlyTxnsCount` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `pocket_template` CHANGE COLUMN `LowBalanceNotificationThresholdAmount` `LowBalanceNtfcThresholdAmt` DECIMAL(25,4) DEFAULT NULL;

ALTER TABLE `partner_transactions` CHANGE COLUMN `CurrentDailyTransactionsCount` `CurrentDailyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentWeeklyTransactionsCount` `CurrentWeeklyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentMonthlyTransactionsCount` `CurrentMonthlyTxnsCount` INT(11) NOT NULL DEFAULT 0;

ALTER TABLE `service_audit` CHANGE COLUMN `CurrentDailyTransactionsCount` `CurrentDailyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentWeeklyTransactionsCount` `CurrentWeeklyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `CurrentMonthlyTransactionsCount` `CurrentMonthlyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `PreviousDailyTransactionsCount` `PreviousDailyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `PreviousWeeklyTransactionsCount` `PreviousWeeklyTxnsCount` INT(11) NOT NULL DEFAULT 0,
 CHANGE COLUMN `PreviousMonthlyTransactionsCount` `PreviousMonthlyTxnsCount` INT(11) NOT NULL DEFAULT 0; 
 
ALTER TABLE `user` RENAME TO `mfino_user`; 




