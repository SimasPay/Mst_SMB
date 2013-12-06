USE mfino;

INSERT INTO `service` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'System','System');


INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,now(),'system',now(),'system',1,'ChargeSettlement','Charge Settlement'),
(1,now(),'system',now(),'system',1,'FundReimburse','Fund Reimburse');


INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='System'),(select id from transaction_type where TransactionName='ChargeSettlement')),
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='System'),(select id from transaction_type where TransactionName='FundReimburse'));