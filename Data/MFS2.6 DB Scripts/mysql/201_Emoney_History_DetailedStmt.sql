

INSERT INTO transaction_type(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (1,now(),'System',now(),'System',1,'DetailedStatement','Detailed Statement');
INSERT INTO service_transaction(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, ServiceId, TransactionTypeId) VALUES (1,now(),'System',now(),'System',1, (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'DetailedStatement'));

Delete from notification where code = 2104;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',1,'Invalid SctlId',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',2,'Invalid SctlId',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',4,'Invalid SctlId',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',8,'Invalid SctlId',null,0,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',16,'Invalid SctlId',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',1,'Invalid SctlId',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',2,'Invalid SctlId',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',4,'Invalid SctlId',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',8,'Invalid SctlId',null,1,0,now(),null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,2104,'InvalidSctlId',16,'Invalid SctlId',null,1,0,now(),null,null,1);





