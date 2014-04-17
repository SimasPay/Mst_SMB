

INSERT INTO service_transaction(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, ServiceId, TransactionTypeId) VALUES (1,now(),'System',now(),'System',1, (select id from service where ServiceName = 'NFCService'), (select id from transaction_type where TransactionName = 'DetailedStatement'));

Delete from notification where code = 2105;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',1,'No of records exceeded the limit given by ISO Provider',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',2,'No of records exceeded the limit given by ISO Provider',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',4,'No of records exceeded the limit given by ISO Provider',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',8,'No of records exceeded the limit given by ISO Provider',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',16,'No of records exceeded the limit given by ISO Provider',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',1,'No of records exceeded the limit given by ISO Provider',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',2,'No of records exceeded the limit given by ISO Provider',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',4,'No of records exceeded the limit given by ISO Provider',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',8,'No of records exceeded the limit given by ISO Provider',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2105,'NofRecordsExceededMaxLimit',16,'No of records exceeded the limit given by ISO Provider',null,1,0,now(),null,null,1);






