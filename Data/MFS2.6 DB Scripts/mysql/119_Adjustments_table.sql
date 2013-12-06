
DROP TABLE IF EXISTS `adjustments`;
CREATE TABLE `adjustments` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  SourcePocketID bigint(20) NOT NULL,
  DestPocketID bigint(20) NOT NULL,
  Amount decimal(25,4) NOT NULL,
  SctlId bigint(20) NOT NULL,
  AdjustmentStatus int(11) NOT NULL,
  ApproveOrRejectTime datetime DEFAULT NULL,
  ApprovedOrRejectedBy varchar(255) DEFAULT NULL,
  ApproveOrRejectComment varchar(255) DEFAULT NULL,
  AppliedBy varchar(255) DEFAULT NULL,
  AppliedTime datetime DEFAULT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT `FK_Adjustments_SourcePocketID` FOREIGN KEY (`SourcePocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_Adjustments_DestPocketID` FOREIGN KEY (`DestPocketID`) REFERENCES `pocket` (`ID`),
  CONSTRAINT `FK_Adjustments_SctlId` FOREIGN KEY (`SctlId`) REFERENCES `service_charge_txn_log` (`ID`)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;