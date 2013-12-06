USE mfino;
DELETE FROM `service_transaction`;
DELETE FROM `transaction_type`;
DELETE FROM `service`;

INSERT INTO `service` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceName`,`DisplayName`) VALUES
(1,1,now(),'system',now(),'system',1,'Account','Account'),
(2,1,now(),'system',now(),'system',1,'Wallet','Wallet'),
(3,1,now(),'system',now(),'system',1,'Bank','Bank'),
(4,1,now(),'system',now(),'system',1,'AgentServices','AgentServices'),
(5,1,now(),'system',now(),'system',1,'Shopping','Shopping');

INSERT INTO `transaction_type` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(1,1,now(),'system',now(),'system',1,'Activation','Activation'),
(2,1,now(),'system',now(),'system',1,'TransactionStatus','TransactionStatus'),
(3,1,now(),'system',now(),'system',1,'ChangePIN','ChangePIN'),
(4,1,now(),'system',now(),'system',1,'CheckBalance','CheckBalance'),
(5,1,now(),'system',now(),'system',1,'History','History'),
(6,1,now(),'system',now(),'system',1,'Transfer','Transfer'),
(7,1,now(),'system',now(),'system',1,'CashIn','CashIn'),
(8,1,now(),'system',now(),'system',1,'CashOut','CashOut'),
(9,1,now(),'system',now(),'system',1,'SubscriberRegistration','SubscriberRegistration'),
(10,1,now(),'system',now(),'system',1,'AgentActivation','AgentActivation'),
(11,1,now(),'system',now(),'system',1,'Purchase','Buy');

 
INSERT INTO `service_transaction` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(1,1,now(),'system',now(),'system',1,1,1),
(2,1,now(),'system',now(),'system',1,1,2),
(3,1,now(),'system',now(),'system',1,1,3),
(4,1,now(),'system',now(),'system',1,1,10),
(5,1,now(),'system',now(),'system',1,2,4),
(6,1,now(),'system',now(),'system',1,2,5),
(7,1,now(),'system',now(),'system',1,2,6),
(8,1,now(),'system',now(),'system',1,2,8),
(9,1,now(),'system',now(),'system',1,3,4),
(10,1,now(),'system',now(),'system',1,3,5),
(11,1,now(),'system',now(),'system',1,3,6),
(12,1,now(),'system',now(),'system',1,3,3),               
(13,1,now(),'system',now(),'system',1,4,9),
(14,1,now(),'system',now(),'system',1,4,7),
(15,1,now(),'system',now(),'system',1,5,11);