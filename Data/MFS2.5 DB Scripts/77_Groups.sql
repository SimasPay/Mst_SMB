ALTER TABLE `mfino`.`transaction_rule`
 ADD COLUMN `SourceGroup` INT(11) UNSIGNED AFTER `DestKYC`,
 ADD COLUMN `DestinationGroup` INT(11) UNSIGNED AFTER `SourceGroup`;

DROP TABLE IF EXISTS `mfino`.`groups`;
CREATE TABLE  `mfino`.`groups` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Version` int(11) unsigned NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL,
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `GroupName` varchar(255) NOT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `SystemGroup` tinyint(4) unsigned DEFAULT '0',
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT IGNORE INTO `mfino`.`groups` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, GroupName, Description, SystemGroup) VALUES ('1',NOW(),'system',NOW(),'system', 'ANY', 'System Group ANY', 1);

DROP TABLE IF EXISTS `mfino`.`subscriber_groups`;
CREATE TABLE  `mfino`.`subscriber_groups` (
  `ID` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `Version` int(11) unsigned NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL,
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `SubscriberID` int(10) unsigned DEFAULT NULL,
  `GroupID` int(10) unsigned DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

update transaction_rule set SourceGroup=1, DestinationGroup=1;

ALTER TABLE `mfino`.`transaction_charge` DROP INDEX `TransactionRuleID`;

update transaction_charge tc set tc.TransactionRuleID =
(
select trg1.id from
(
  select max(tr1.id) id,tr1.TransactionTypeId from transaction_rule tr1, transaction_rule tr2 where
  tr1.MSPID = tr2.MSPID and
  tr1.ServiceProviderID = tr2.ServiceProviderID and
  tr1.ServiceId = tr2.ServiceId and
  tr1.TransactionTypeID = tr2.TransactionTypeID and
  tr1.ChannelCodeID = tr2.ChannelCodeID group by tr1.TransactionTypeId
) trg1,
(select id,transactiontypeid from transaction_rule) trg2
where trg1.TransactionTypeId = trg2.TransactionTypeId and
trg2.id = tc.TransactionRuleID
) where tc.TransactionRuleID is not null;

delete from transaction_rule where id not in (select transactionruleid from transaction_charge);