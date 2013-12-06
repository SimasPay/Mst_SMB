USE mfino;

INSERT INTO `service` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceName`,`DisplayName`) VALUES
(6,1,now(),'system',now(),'system',1,'Payment','Payment'),
(7,1,now(),'system',now(),'system',1,'Buy','Buy');


INSERT INTO `transaction_type` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(13,1,now(),'system',now(),'system',1,'BillPay','Bill Pay'),
(14,1,now(),'system',now(),'system',1,'AirtimePurchase','Airtime Purchase'),
(15,1,now(),'system',now(),'system',1,'SendReceipt','Send Receipt');

 
INSERT INTO `service_transaction` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(17,1,now(),'system',now(),'system',1,6,13),
(18,1,now(),'system',now(),'system',1,7,14),
(19,1,now(),'system',now(),'system',1,4,13),
(20,1,now(),'system',now(),'system',1,4,15);