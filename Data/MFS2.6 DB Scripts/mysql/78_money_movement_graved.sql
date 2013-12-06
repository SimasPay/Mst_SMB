
-- Create Table closed_account_settlement_mdn
DROP TABLE IF EXISTS `close_acct_setl_mdn`;
CREATE TABLE `close_acct_setl_mdn` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `MDNID` bigint(20) NOT NULL,  
  `ToBankAccount` tinyint(4) DEFAULT NULL,
  `SettlementMDN` varchar(255) DEFAULT NULL,
  `SettlementAccountNumber` varchar(255) DEFAULT NULL,
  `ApprovalState` int(11) DEFAULT NULL,
  `ApproveOrRejectTime` datetime DEFAULT NULL,
  `ApprovedOrRejectedBy` varchar(255) DEFAULT NULL,
  `ApproveOrRejectComment` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_close_acct_setl_mdn_MDNID` (`MDNID`),
  CONSTRAINT `FK_close_acct_setl_mdn_MDNID` FOREIGN KEY (`MDNID`) REFERENCES `subscriber_mdn` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `money_clearance_graved`;
CREATE TABLE money_clearance_graved (
         ID BIGINT(20) unsigned NOT NULL AUTO_INCREMENT,
         Version INT(11) NOT NULL,
		 LastUpdateTime DATETIME NOT NULL,
         UpdatedBy VARCHAR(255) NOT NULL,
		 CreateTime DATETIME NOT NULL,
         CreatedBy VARCHAR(255) NOT NULL,
		 MDNID BIGINT(20) NOT NULL,
		 PocketID BIGINT(20) NOT NULL,
		 SctlId BIGINT(20) NOT NULL,
		 Amount DECIMAL(25,4) NOT NULL,
		 RefundMDNID BIGINT(20),
		 RefundAccountNumber VARCHAR(16),
		 RefundPocketID BIGINT(20),
		 RefundSctlID BIGINT(20),
		 MCStatus INT(11) NOT NULL,
		 PRIMARY KEY (`ID`),
		 Foreign Key (`MDNID`) references subscriber_mdn(`ID`),
		 Foreign Key (`PocketID`) references pocket(`ID`),
		 Foreign Key (`SctlId`) references service_charge_txn_log(`ID`),
		 Foreign Key (`RefundMDNID`) references subscriber_mdn(`ID`),
		 Foreign Key (`RefundPocketID`) references pocket(`ID`),
		 Foreign Key (`RefundSctlID`) references service_charge_txn_log(`ID`),
		 UNIQUE KEY (`MDNID`,`PocketID`) 
       )ENGINE=InnoDB DEFAULT CHARSET=latin1;

DELETE FROM enum_text where TagID=8019 and  EnumCode='0';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','MCStatus','8019','0','INITIALIZED','INITIALIZED');

DELETE FROM enum_text where TagID=8019 and  EnumCode='1';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','MCStatus','8019','1','REFUNDED','REFUNDED');

DELETE FROM enum_text where TagID=8019 and  EnumCode='2';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','MCStatus','8019','2','MOVED_TO_NATIONAL_TREASURY','MOVED TO NATIONAL TREASURY');



DELETE FROM system_parameters WHERE parametername='retired.subscriber.system.collector.pocket';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','retired.subscriber.system.collector.pocket','-1','retired subscriber balance amount will be moved to this pocket');

DELETE FROM system_parameters WHERE parametername='national.treasury.partner.code';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','national.treasury.partner.code','-1','Partner Code of Partner associated with National Treasury');

DELETE FROM system_parameters WHERE parametername='days.to.national.treasury.of.graved';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','days.to.national.treasury.of.graved','180','Days to National Treasury after graved');

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='Refund');
DELETE FROM transaction_type where transactionname = 'Refund'; 
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'Refund','Refund Retired Subscriber Money');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'Refund'));

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToSystem');
DELETE FROM transaction_type where transactionname = 'TransferToSystem';
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'TransferToSystem','MoveRetiredSubscriberBalanceMoney');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'TransferToSystem'));

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToTreasury');
DELETE FROM transaction_type where transactionname = 'TransferToTreasury';
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'TransferToTreasury','MoveMoneyToNationalTreasury');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Wallet'), (select id from transaction_type where TRANSACTIONNAME = 'TransferToTreasury'));
