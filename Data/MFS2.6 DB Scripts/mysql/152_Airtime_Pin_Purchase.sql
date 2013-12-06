DELETE FROM system_parameters WHERE parametername='airtime.pin.purchase.terminal.id';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','airtime.pin.purchase.terminal.id','3TSM0001','institution/terminal id for airtime vending platform');

DELETE FROM system_parameters WHERE parametername='airtime.pin.purchase.biller.code';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','airtime.pin.purchase.biller.code','-1','biller code of the biller associated with airtime vending platform');

DELETE FROM service_transaction where serviceid=(select id from service where servicename='AgentServices') and transactiontypeid=(select id from transaction_type where transactionname='AirtimePinPurchase');
DELETE FROM transaction_type where transactionname = 'AirtimePinPurchase'; 
INSERT INTO transaction_type(VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME,CREATEDBY,MSPID,TRANSACTIONNAME,DISPLAYNAME) VALUES (1,now(),'System',now(),'System',1,'AirtimePinPurchase','AirtimePinPurchase');
INSERT INTO service_transaction(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID) VALUES (1,now(),'System',now(),'System',1, (select id from service where SERVICENAME = 'AgentServices'), (select id from transaction_type where TRANSACTIONNAME = 'AirtimePinPurchase'));
