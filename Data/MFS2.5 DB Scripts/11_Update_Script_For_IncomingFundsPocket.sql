use mfino;

ALTER TABLE `partner_services` ADD COLUMN `DestPocketID` BIGINT(20);

ALTER TABLE `partner_services` ADD CONSTRAINT `FK_PartnerServices_PocketByDestPocketID` FOREIGN KEY `FK_PartnerServices_PocketByDestPocketID` (`DestPocketID`)
    REFERENCES `pocket` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

ALTER TABLE `pocket_template` ADD COLUMN `NumberOfPocketsAllowedForMDN` INT(11) DEFAULT '1'; 
