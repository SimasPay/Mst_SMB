
ALTER TABLE `notification_log` ADD COLUMN `NotificationMethod` INTEGER;

ALTER TABLE `notification_log` ADD COLUMN `SourceAddress` VARCHAR(255);
 
ALTER TABLE `notification_log` ADD COLUMN `NotificationReceiverType` INTEGER;

ALTER TABLE `notification_log` ADD COLUMN `EmailSubject` VARCHAR(255);

DROP TABLE IF EXISTS `notification_log_details`;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `notification_log_details` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `NotificationLogID` BIGINT(20) unsigned NOT NULL,
  `Status` int(11) NOT NULL,
  PRIMARY KEY (`ID`),
  constraint FK_notification_log_details_Notification_Log_ID foreign key (`NotificationLogID`) references notification_log (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

