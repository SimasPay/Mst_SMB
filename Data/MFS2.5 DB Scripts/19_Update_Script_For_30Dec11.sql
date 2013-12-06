Use mfino;

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','BusinessPartnerType','6079','8','Biller','Biller');
INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','BusinessPartnerTypePartner','6415','8','Biller','Biller');

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SchedulerStatus','6501','4','SimilarConfigScheduled','SimilarConfigScheduled');
--
-- Table structure for table `bill_payments`
--

DROP TABLE IF EXISTS `bill_payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bill_payments` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SctlID` bigint(20) NOT NULL,
  `BillerCode` varchar(255) DEFAULT NULL,
  `InvoiceNumber` varchar(255) DEFAULT NULL,
  `Amount` decimal(25,4) DEFAULT NULL,
  `Charges` decimal(25,4) DEFAULT NULL,
  `BillPayStatus` int(11) DEFAULT NULL,
  `ResponseCode` int(11) DEFAULT NULL,
  `NoOfRetries` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BillPaymentTransaction_SCTL` (`SctlID`),
  CONSTRAINT `FK_BillPaymentTransaction_SCTL` FOREIGN KEY (`SctlID`) REFERENCES `service_charge_transaction_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

ALTER TABLE service_settlement_config
ADD COLUMN CollectorPocket bigint(20) DEFAULT NULL,
ADD COLUMN SimilarConfigID bigint(20) DEFAULT NULL,
ADD KEY `FK_ServiceSettlementConfig_PocketByCollectorPocket` (`CollectorPocket`),
ADD CONSTRAINT `FK_ServiceSettlementConfig_PocketByCollectorPocket` FOREIGN KEY (`CollectorPocket`) REFERENCES `pocket` (`ID`);

TRUNCATE TABLE settlement_scheduler_logs;