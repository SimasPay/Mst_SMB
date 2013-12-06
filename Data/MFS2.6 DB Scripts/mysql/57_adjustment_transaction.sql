

INSERT INTO transaction_type
(Version,
LastUpdateTime,
UpdatedBy,
CreateTime,
CreatedBy,
MSPID,
TransactionName,
DisplayName)
VALUES
(1,now(),'System',now(),'System',1,'Adjustments','Adjustments');
INSERT INTO service_transaction
(Version,
LastUpdateTime,
UpdatedBy,
CreateTime,
CreatedBy,
MSPID,
ServiceID,
TransactionTypeID)
VALUES
(1,now(),'System',now(),'System',1,(select id from service where SERVICENAME = 'System'),(select id from transaction_type where TRANSACTIONNAME = 'Adjustments'));
