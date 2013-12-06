use mfino;

INSERT IGNORE INTO `pocket_template` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`Type`,`BankAccountCardType`,`Description`,`Commodity`,`CardPANSuffixLength`,`Units`,`Allowance`,`MaximumStoredValue`,`MinimumStoredValue`,`MaxAmountPerTransaction`,`MinAmountPerTransaction`,`MaxAmountPerDay`,`MaxAmountPerWeek`,`MaxAmountPerMonth`,`MaxTransactionsPerDay`,`MaxTransactionsPerWeek`,`MaxTransactionsPerMonth`,`MinTimeBetweenTransactions`,`BankCode`,`OperatorCode`,`BillingType`,`LowBalanceNotificationThresholdAmount`,`LowBalanceNotificationEnabled`,`WebTimeInterval`,`WebServiceTimeInterval`,`UTKTimeInterval`,`BankChannelTimeInterval`,`Denomination`,`MaxUnits`,`PocketCode`,`TypeOfCheck`,`RegularExpression`,`IsCollectorPocket`) VALUES 
 (0,now(),'System',now(),'System',1,1,NULL,'Emoney-UnRegistered',4,NULL,NULL,0,'10000.0000','0.0000','1000.0000','10.0000','1000.0000','10000.0000','100000.0000',10,50,100,0,9999,NULL,NULL,'0.0000',0,0,0,0,0,NULL,NULL,'1',0,'',0);
 
INSERT INTO system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values
(1, now(), 'System', now(), 'system', 'pocket_template_unregistered', (select id from pocket_template where Description='Emoney-UnRegistered'), 'Pocket Template for UnRegistered Subscribers');

INSERT IGNORE INTO `mfino`.`enum_text` (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES 
('1',NOW(),'system',NOW(),'system','0','SubscriberStatus','5024','7','NotRegistered','NotRegistered');

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'TransferToUnRegistered','TransferToUnRegistered');

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'CashOutToUnRegistered','CashOutToUnRegistered');

INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='TransferToUnRegistered')),
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='AgentServices'),(select id from transaction_type where TransactionName='CashOutToUnRegistered'));

