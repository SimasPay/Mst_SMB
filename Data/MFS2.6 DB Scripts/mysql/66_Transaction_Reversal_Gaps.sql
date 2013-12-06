
Delete from service_transaction where SERVICEID = (select id from service where SERVICENAME = 'Bank') and TRANSACTIONTYPEID = (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction');

INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Bank'), (select id from transaction_type where TRANSACTIONNAME = 'ReverseTransaction'));

