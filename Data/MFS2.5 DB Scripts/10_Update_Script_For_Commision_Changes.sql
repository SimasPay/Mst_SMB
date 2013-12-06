use mfino;

ALTER TABLE `transaction_charge` ADD COLUMN `RegisteringPartnerCommision` DECIMAL(25,4);

ALTER TABLE `charge_definition` ADD COLUMN `IsChargeFromCustomer` TINYINT(4) UNSIGNED NOT NULL;	

ALTER TABLE `charge_definition` ADD COLUMN `FundingPartnerID` BIGINT(20), ADD COLUMN `PocketID` BIGINT(20),
 ADD CONSTRAINT `FK_ChargeDefinition_Partner` FOREIGN KEY `FK_ChargeDefinition_Partner` (`FundingPartnerID`)
    REFERENCES `partner` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT,
 ADD CONSTRAINT `FK_ChargeDefinition_Pocket` FOREIGN KEY `FK_ChargeDefinition_Pocket` (`PocketID`)
    REFERENCES `pocket` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;
	
ALTER TABLE `charge_definition` ADD COLUMN `IsTaxable` TINYINT(4) UNSIGNED NOT NULL;			
	
ALTER TABLE `transaction_amount_distribution_log` ADD COLUMN `TaxAmount` DECIMAL(25,4) AFTER `ShareAmount`;	
ALTER TABLE `transaction_amount_distribution_log` ADD CONSTRAINT `FK_TransactionAmountDistributionLog_TransactionCharge` FOREIGN KEY `FK_TransactionAmountDistributionLog_TransactionCharge` (`TransactionChargeID`)
    REFERENCES `transaction_charge` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

ALTER TABLE `commodity_transfer` ADD COLUMN `TaxAmount` DECIMAL(25,4) AFTER `Charges`;
ALTER TABLE `pending_commodity_transfer` ADD COLUMN `TaxAmount` DECIMAL(25,4) AFTER `Charges`;	

ALTER TABLE `subscriber` ADD COLUMN `RegisteringPartnerID` BIGINT(20) DEFAULT NULL;

