CREATE DATABASE /*!32312 IF NOT EXISTS*/ `report` /*!40100 DEFAULT CHARACTER SET utf8 */;
use report;

DROP TABLE IF EXISTS `copy_info`;
CREATE TABLE  `copy_info` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ClassName` varchar(255) DEFAULT NULL,
  `LastCopyTime` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('SubscriberClassification','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('AgentClassification','2000-01-01 00:00:00.0');  
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('BillerClassification','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('UserRoles','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('SubscriberPocket','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('AgentPocket','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('BillerPocket','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('KinInformationMissingAccount','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('CommodityTransfer','2000-01-01 00:00:00.0'); 
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('PendingCommodityTransfer','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('Sctl','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('SctlCt','2000-01-01 00:00:00.0');    
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('SctlTxn','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('Users','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('Subscribers','2000-01-01 00:00:00.0');
INSERT INTO `copy_info` (`ClassName`,`LastCopyTime`) VALUES ('Authorities','2000-01-01 00:00:00.0');