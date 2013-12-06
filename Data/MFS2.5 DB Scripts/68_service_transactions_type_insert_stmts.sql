INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='AgentServices'),(select id from transaction_type where TransactionName='BillPay')),
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='AgentServices'),(select id from transaction_type where TransactionName='AirtimePurchase'));
