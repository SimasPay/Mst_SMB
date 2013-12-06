

Delete from enum_text where tagid = 5184 and  enumcode = '40';

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BankAccountType','5184','40','NFCCard','NFC Card');


Delete from transaction_type where transactionname = 'NFCCardBalance';

Insert into transaction_type(version, lastupdatetime, updatedby, createtime,createdby,mspid,transactionname,displayname) values (1,now(),'system',now(),'system',1,'NFCCardBalance','NFC Card Balance');

Insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,now(),'system',now(),'system',1, (select id from service where servicename = 'NFCService'), (select id from transaction_type where transactionname = 'NFCCardBalance'));


