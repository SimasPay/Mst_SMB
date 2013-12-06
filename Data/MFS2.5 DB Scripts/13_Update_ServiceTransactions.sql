USE mfino;

INSERT INTO `transaction_type` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`) VALUES
(12,1,now(),'system',now(),'system',1,'AgentToAgentTransfer','Agent Transfer');

 
INSERT INTO `service_transaction` (`ID`,`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`, `TransactionTypeID`) VALUES
(16,1,now(),'system',now(),'system',1,2,12);