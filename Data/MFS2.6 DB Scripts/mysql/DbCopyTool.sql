CREATE DATABASE /*!32312 IF NOT EXISTS*/ `report` /*!40100 DEFAULT CHARACTER SET utf8 */;
use report;

DROP TABLE IF EXISTS `copy_info`;
CREATE TABLE  `copy_info` (
  `Id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ClassName` varchar(255) DEFAULT NULL,
  `LastCopyTime` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

