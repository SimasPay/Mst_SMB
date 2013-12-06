DELETE FROM system_parameters WHERE ParameterName='sms.interval.inactive.bulkupload';
DELETE FROM notification WHERE Code=2034;

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'System','sms.interval.inactive.bulkupload',2,'Time interval between succesive sms');

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2034,'ActivationSMSTypeBulkUpload',1,'Dear Subscriber, You are registered for mfino easymoney kindly activate your account.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2034,'ActivationSMSTypeBulkUpload',2,'Dear Subscriber, You are registered for mfino easymoney kindly activate your account.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2034,'ActivationSMSTypeBulkUpload',4,'Dear Subscriber, You are registered for mfino easymoney kindly activate your account.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2034,'ActivationSMSTypeBulkUpload',8,'Dear Subscriber, You are registered for mfino easymoney kindly activate your account.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2034,'ActivationSMSTypeBulkUpload',16,'Dear Subscriber, You are registered for mfino easymoney kindly activate your account.',null,0,0,sysdate,null,null,1);

commit;