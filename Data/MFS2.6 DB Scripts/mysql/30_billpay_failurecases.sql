

alter table `service_charge_txn_log` add column `IntegrationCode` varchar(255);

drop table if exists `auto_reversals`;
create table `auto_reversals` (
  `ID` bigint(20) unsigned not null auto_increment,
  `Version` int(11) unsigned not null,
  `LastUpdateTime` datetime not null,
  `UpdatedBy` varchar(255) not null,
  `CreateTime` datetime not null,
  `CreatedBy` varchar(255) not null,
  `SctlID` bigint(20) not null,
  `SourcePocketID` bigint(20) not null,
  `DestPocketID` bigint(20) not null,
  `AutoRevStatus` int(11) unsigned not null,
  `Amount` decimal(25,4) default null,
  `Charges` decimal(25,4) default null,
  primary key (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

update notification set text='Your request to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount) failed, Your amount will be reverted in 24 hours, REF: $(TransferID)' where code = 714;