ALTER TABLE "SUBSCRIBER_MDN"   ADD "MIGRATETOSIMOBIPLUS" NUMBER(3,0)  ADD "MIGRATETOKEN" VARCHAR2(255)  ADD "MIGRATEDATE" TIMESTAMP NULL;
ALTER TABLE "SUBSCRIBER_MDN"   ADD "ISMIGRATEABLETOSIMOBIPLUS" NUMBER(3,0);

-- insert statement for notification
-- InvalidMigrateSimobiPlusToken(2180)
-- TimeoutMigrateSimobiPlusToken(2181)
-- SubscriberMigratedToSimobiPlus(2182)
-- MDNIsNotActive(7)
Delete from notification where code = 2180;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2180,'InvalidMigrateSimobiPlusToken',16,'Token Salah',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2180,'InvalidMigrateSimobiPlusToken',16,'Invalid Token',null,0,0,sysdate,null,null,1);
Delete from notification where code = 2181;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2181,'TimeoutMigrateSimobiPlusToken',16,'Token Sudah tidak Berlaku',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2181,'TimeoutMigrateSimobiPlusToken',16,'Token Timeout',null,0,0,sysdate,null,null,1);
Delete from notification where code = 2182;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2182,'SubscriberMigratedToSimobiPlus',16,'Subscriber Sudah Migrasi ke SimobiPlus',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2182,'SubscriberMigratedToSimobiPlus',16,'Subscriber Account has Migrated to SimobiPlus',null,0,0,sysdate,null,null,1);
Delete from notification where code = 7 and NotificationMethod = 16;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,7,'MDNIsNotActive',16,'Subscriber tidak Aktif',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,7,'MDNIsNotActive',16,'Subscriber is Not Active',null,0,0,sysdate,null,null,1);


-- insert statement for system parameter
-- show.migrate.to.simobiplus.event
-- force.migrate.to.simobiplus.date
insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values (1, sysdate, 'System', sysdate, 'system', 'show.migrate.to.simobiplus.event', 'true', 'Show Migrate to Simobi Plus Dialog');
insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values (1, sysdate, 'System', sysdate, 'system', 'force.migrate.to.simobiplus.date', '30/10/2017', 'Force Subscriber to migrate from simobi to SimobiPlus');