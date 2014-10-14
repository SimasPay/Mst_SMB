DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ResetPinByOTP');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ResetPinByOTP';
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'ResetPinByOTP','Reset Pin By OTP');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'Account'), (select id from transaction_type where TRANSACTIONNAME = 'ResetPinByOTP'));
