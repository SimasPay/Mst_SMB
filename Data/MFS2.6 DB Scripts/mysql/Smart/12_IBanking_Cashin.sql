

INSERT INTO `transaction_type`
(`Version`,
`LastUpdateTime`,
`UpdatedBy`,
`CreateTime`,
`CreatedBy`,
`MSPID`,
`TransactionName`,
`DisplayName`)
VALUES
(1,now(),'System',now(),'System',1,'IBCashIn','IB CashIn');
INSERT INTO `service_transaction`
(`Version`,
`LastUpdateTime`,
`UpdatedBy`,
`CreateTime`,
`CreatedBy`,
`MSPID`,
`ServiceID`,
`TransactionTypeID`)
select 1,now(),'System',now(),'System',1,4,MAX(transaction_type.id) from transaction_type;