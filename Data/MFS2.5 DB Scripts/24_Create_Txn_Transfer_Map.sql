USE mfino;

-- Table structure for table `chargetxn_transfer_map`
--

DROP TABLE IF EXISTS `chargetxn_transfer_map`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chargetxn_transfer_map` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SctlID` bigint(20) NOT NULL,
  `CommodityTransferID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_TxnTransferMap_SCTL` (`SctlID`),
  CONSTRAINT `FK_TxnTransferMap_SCTL` FOREIGN KEY (`SctlID`) REFERENCES `service_charge_transaction_log` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;