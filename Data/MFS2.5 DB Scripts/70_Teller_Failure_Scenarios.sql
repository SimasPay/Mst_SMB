use mfino;

delete from enum_text where tagid=6089 and enumcode in (17,18);

delete from service_transaction where serviceid = (select id from service where ServiceName='TellerService') and transactiontypeid = (select id from transaction_type where TransactionName='TellerEMoneyClearance');

delete from transaction_type where transactionname like 'TellerEMoneyClearance';

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','17','Pending_Resolved','Pending Resolved');

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',NOW(),'system',NOW(),'system','0','SCTLStatus','6089','18','Pending_Resolved_Processing','Pending Resolved Processing');

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'TellerEMoneyClearance','TellerEMoneyClearance');

INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='TellerService'),(select id from transaction_type where TransactionName='TellerEMoneyClearance'));