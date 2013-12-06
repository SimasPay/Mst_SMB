Delete from enum_text where tagid = 5049 and  enumcode = '4';
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','PocketType','5049','4','NFC','NFC Offline');


insert into service(version, lastupdatetime, updatedby, createtime,createdby,mspid,servicename,displayname) values (1,sysdate,'system',sysdate,'system',1,'NFCService','NFC Service');

insert into transaction_type(version, lastupdatetime, updatedby, createtime,createdby,mspid,transactionname,displayname) values (1,sysdate,'system',sysdate,'system',1,'NFCCardUnlink','NFC Card Unlink');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'NFCService'), (select id from transaction_type where transactionname = 'NFCCardUnlink'));

insert into transaction_type(version, lastupdatetime, updatedby, createtime,createdby,mspid,transactionname,displayname) values (1,sysdate,'system',sysdate,'system',1,'NFCPocketBalance','NFC Pocket Balance');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'NFCService'), (select id from transaction_type where transactionname = 'NFCPocketBalance'));

insert into transaction_type(version, lastupdatetime, updatedby, createtime,createdby,mspid,transactionname,displayname) values (1,sysdate,'system',sysdate,'system',1,'NFCCardTopup','NFC Card Topup');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'NFCService'), (select id from transaction_type where transactionname = 'NFCCardTopup'));

insert into transaction_type(version, lastupdatetime, updatedby, createtime,createdby,mspid,transactionname,displayname) values (1,sysdate,'system',sysdate,'system',1,'NFCCardTopupReversal','NFC Card Topup Reversal');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'NFCService'), (select id from transaction_type where transactionname = 'NFCCardTopupReversal'));


Delete from notification where code = 834;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,834,'NFC_Pocket_Balance',1,'Transaction ID: $(TransactionID). Your NFC Pocket balance as on $(CurrentDateTime) is $(Currency) $(CommodityBalanceValue).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,834,'NFC_Pocket_Balance',2,'Transaction ID: $(TransactionID). Your NFC Pocket balance as on $(CurrentDateTime) is $(Currency) $(CommodityBalanceValue).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,834,'NFC_Pocket_Balance',4,'Transaction ID: $(TransactionID). Your NFC Pocket balance as on $(CurrentDateTime) is $(Currency) $(CommodityBalanceValue).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,834,'NFC_Pocket_Balance',8,'Transaction ID: $(TransactionID). Your NFC Pocket balance as on $(CurrentDateTime) is $(Currency) $(CommodityBalanceValue).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,834,'NFC_Pocket_Balance',16,'Transaction ID: $(TransactionID). Your NFC Pocket balance as on $(CurrentDateTime) is $(Currency) $(CommodityBalanceValue).',null,0,0,sysdate,null,null,1);

Delete from notification where code = 835;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,835,'Invalid_CardPan',1,'ERROR: Inavlid Card Pan.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,835,'Invalid_CardPan',2,'ERROR: Inavlid Card Pan.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,835,'Invalid_CardPan',4,'ERROR: Inavlid Card Pan.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,835,'Invalid_CardPan',8,'ERROR: Inavlid Card Pan.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,835,'Invalid_CardPan',16,'ERROR: Inavlid Card Pan.',null,0,0,sysdate,null,null,1);

commit;