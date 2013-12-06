DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='Reactivation');
DELETE FROM transaction_type where transactionname = 'Reactivation'; 
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,sysdate,'System',sysdate,'System',1,'Reactivation','Reactivation');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,sysdate,'System',sysdate,'System',1, (select id from service where SERVICENAME = 'Account'), (select id from transaction_type where TRANSACTIONNAME = 'Reactivation'));

commit;