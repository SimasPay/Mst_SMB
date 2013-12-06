use mfino;

ALTER TABLE bulk_upload_entry ADD COLUMN FirstName VARCHAR(255),
 ADD COLUMN LastName VARCHAR(255),
 ADD COLUMN IsUnRegistered TINYINT(4);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,695,'SendFundAccessCodeForNonRegisteredBulkTransfer',1,'Your Fund access code is $(OneTimePin)',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,695,'SendFundAccessCodeForNonRegisteredBulkTransfer',2,'Your Fund access code is $(OneTimePin)',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,695,'SendFundAccessCodeForNonRegisteredBulkTransfer',4,'Your Fund access code is $(OneTimePin)',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,695,'SendFundAccessCodeForNonRegisteredBulkTransfer',8,'Your Fund access code is $(OneTimePin)',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,695,'SendFundAccessCodeForNonRegisteredBulkTransfer',16,'Your Fund access code is $(OneTimePin)',null,0,0,now(),null,null,1);