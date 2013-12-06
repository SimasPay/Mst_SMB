use mfino;

DELETE from  `enum_text` where tagid=5352 and enumcode=24;
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','Role','5352','24','BankTeller','BankTeller');
Update enum_text set DisplayText ="BankTeller" where tagid=6079 and enumcode=3;
Update enum_text set DisplayText ="BankTeller" where tagid=6415 and enumcode=3;

DELETE from  `enum_text` where tagid=5636 and enumcode in (36,37);
INSERT IGNORE INTO `enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES 
('1',NOW(),'system',NOW(),'system','0','TransactionUICategory','5636','36','Teller_Cashin_SelfTransfer','Teller_Cashin_SelfTransfer'),
('1',NOW(),'system',NOW(),'system','0','TransactionUICategory','5636','37','Teller_Cashin_Subscriber','Teller_Cashin_Subscriber');

DELETE from  `service_transaction` where ServiceID = (SELECT ID FROM `service` WHERE ServiceName='TellerService') ;
DELETE from  `service` where ServiceName='TellerService';

INSERT INTO `service` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'TellerService','TellerService');

INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`)
(SELECT '1',now(),'system',now(),'system','1',s.id ,t.id FROM `service` s , `transaction_type` t WHERE s.ServiceName='TellerService' AND t.TransactionName='CashIn');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`)
(SELECT '1',now(),'system',now(),'system','1',s.id ,t.id FROM `service` s , `transaction_type` t WHERE s.ServiceName='TellerService' AND t.TransactionName='CashOut');


INSERT INTO `partner_services` (`Version`, `LastUpdateTime`, `UpdatedBy`, `CreateTime`, `CreatedBy`, `MSPID`, `PartnerID`, `ServiceProviderID`, `ServiceID`,`Status`, `CollectorPocket`, `SourcePocket`, `IsServiceChargeShare`, `DestPocketID`)
(SELECT '1',NOW(),'mfino(System)',NOW(),'user','1','1','1',id,'1','2','1','2','1' FROM `service`  WHERE ServiceName='TellerService');


Update channel_code set ChannelSourceApplication =0 where ChannelCode=1;
Update channel_code set ChannelSourceApplication =1 where ChannelCode=2;
Update channel_code set ChannelSourceApplication =2,ChannelName='WebService',Description='WebService' where ChannelCode=8;
