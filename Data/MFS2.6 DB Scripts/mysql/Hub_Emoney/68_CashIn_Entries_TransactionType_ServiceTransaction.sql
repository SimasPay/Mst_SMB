
INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`)
VALUES (1,now(),'System',now(),'System',1,'TellerCashin','Teller Cashin');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`,`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`)
VALUES (1,now(),'System',now(),'System',1,'POSCashin','POS Cashin');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`,`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`)
VALUES (1,now(),'System',now(),'System',1,'AutoDebitCashIn','Auto Debit CashIn');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`,`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`)
VALUES (1,now(),'System',now(),'System',1,'KioskCashin','Kiosk Cashin');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`,`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;

INSERT INTO `transaction_type` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`TransactionName`,`DisplayName`)
VALUES (1,now(),'System',now(),'System',1,'PhoneBankingCashin','Phone Banking Cashin');
INSERT INTO `service_transaction` (`Version`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`MSPID`,`ServiceID`,`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;