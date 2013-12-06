use mfino;

ALTER TABLE `partner_services` DROP COLUMN `ServiceProviderServicesID`,
 DROP FOREIGN KEY `FK_PartnerServices_ServiceProviderServices`;

DROP TABLE share_partners;

DROP TABLE service_charge_pricing;

DROP TABLE tax_pricing;

DROP TABLE partner_transactions;

DROP TABLE integration_partner;

DROP TABLE business_partner;

DROP TABLE service_partner;

DROP TABLE service_charge_template;

DROP TABLE service_provider_services;

DROP TABLE settlement_configuration;


ALTER TABLE `service_charge_txn_log` CHANGE COLUMN `Mode` `TransactionMode` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `agent_cashin_transaction_log` RENAME TO `agent_cashin_txn_log`; 

ALTER TABLE `authorizing_person_details` RENAME TO `auth_person_details`;

ALTER TABLE `bill_payment_transaction` RENAME TO `bill_payment_txn`;

ALTER TABLE `channel_session_management` RENAME TO `channel_session_mgmt`;

ALTER TABLE `credit_card_destinations` RENAME TO `creditcard_destinations`; 

ALTER TABLE `distribution_chain_level` RENAME TO `distribution_chain_lvl`;

ALTER TABLE `distribution_chain_template` RENAME TO `distribution_chain_temp`;

ALTER TABLE `mfsbiller_partner_mapping` RENAME TO `mfsbiller_partner_map`;

ALTER TABLE `pending_transactions_entry` RENAME TO `pending_txns_entry`;

ALTER TABLE `pending_transactions_file` RENAME TO `pending_txns_file`;

ALTER TABLE `service_settlement_config` RENAME TO `service_settlement_cfg`;

ALTER TABLE `settlement_scheduler_logs` RENAME TO `settlement_schedule_log`;

ALTER TABLE `settlement_transaction_logs` RENAME TO `settlement_txn_log`;

ALTER TABLE `txn_amount_distribution_log` RENAME TO `txn_amount_dstrb_log`;

ALTER TABLE `bulk_lop` CHANGE COLUMN `Comment` `LOPComment` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `distribution_chain_lvl` CHANGE COLUMN `Level` `DistributionLevel` INT(11) NOT NULL,
 DROP INDEX `TemplateID`,
 ADD UNIQUE INDEX `TemplateID` USING BTREE(`TemplateID`, `DistributionLevel`);
 
ALTER TABLE `letter_of_purchase` CHANGE COLUMN `Comment` `LOPComment` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL;

ALTER TABLE `partner_services` CHANGE COLUMN `Level` `PSLevel` INT(11) DEFAULT NULL; 

ALTER TABLE `address` MODIFY COLUMN `City` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci,
 MODIFY COLUMN `State` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci,
 MODIFY COLUMN `ZipCode` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci,
 MODIFY COLUMN `Country` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci;


