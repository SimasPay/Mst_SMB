ALTER TABLE `mfino`.`merchant_code` MODIFY COLUMN `MerchantCode` VARCHAR(255) NOT NULL;

ALTER TABLE `mfino`.`denomination` MODIFY COLUMN `DenominationAmount` BIGINT(20)  NOT NULL;

ALTER TABLE `mfino`.`biller` MODIFY COLUMN `TransactionFee` double NULL;

ALTER TABLE `mfino`.`smsc_configuration` MODIFY COLUMN `Charging` double NULL;

ALTER TABLE `mfino`.`sms_transaction_log` MODIFY COLUMN `Source` VARCHAR(255) NOT NULL;

ALTER TABLE `mfino`.`pocket_template` MODIFY COLUMN `PocketCode` VARCHAR(255) DEFAULT NULL;