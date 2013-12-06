

ALTER TABLE `bill_payments` ADD COLUMN `PartnerBillerCode` VARCHAR(255) AFTER `BillerCode`;

ALTER TABLE `bill_payments` ADD COLUMN `IntegrationCode` VARCHAR(255) AFTER `PartnerBillerCode`;