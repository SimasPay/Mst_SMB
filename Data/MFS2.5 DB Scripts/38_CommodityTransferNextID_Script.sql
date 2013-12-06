use mfino;

DROP TABLE IF EXISTS `commodity_transfer_next_id`;
CREATE TABLE  `commodity_transfer_next_id` (
  `ID` bigint(20) NOT NULL,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `NextRecordID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `pending_commodity_transfer` MODIFY COLUMN `ID` BIGINT(20) NOT NULL;

 INSERT IGNORE INTO `commodity_transfer_next_id` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`NextRecordID`)
(select '1', '1',NOW(),'system',NOW(),'system', max(id)+1 from(select max(id) as id from pending_commodity_transfer union select max(id) as id from commodity_transfer) as t);