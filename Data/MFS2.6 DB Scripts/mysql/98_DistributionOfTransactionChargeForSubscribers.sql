ALTER TABLE `txn_amount_dstrb_log` MODIFY COLUMN `PartnerID` BIGINT(20);

ALTER TABLE txn_amount_dstrb_log ADD COLUMN `SubscriberID` BIGINT(20),
ADD KEY `FK_TransactionAmountDistributionLog_Subscriber` (`subscriberID`),
ADD CONSTRAINT `FK_TransactionAmountDistributionLog_Subscriber` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`ID`);

ALTER TABLE `sctl_settlement_map` MODIFY COLUMN `PartnerID` BIGINT(20);

ALTER TABLE `transaction_charge` ADD IsChrgDstrbApplicableToSrcSub tinyint(4) DEFAULT '0';

ALTER TABLE `transaction_charge` ADD IsChrgDstrbApplicableToDestSub tinyint(4) DEFAULT '0';

