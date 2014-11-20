

Delete from notification where code=2083;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2083,'NotNFCAccount',1,'Requested $(PocketDescription) account is not of type NFC account.Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2083,'NotNFCAccount',2,'Requested $(PocketDescription) account is not of type NFC account.Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2083,'NotNFCAccount',4,'Requested $(PocketDescription) account is not of type NFC account.Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2083,'NotNFCAccount',8,'Requested $(PocketDescription) account is not of type NFC account.Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2083,'NotNFCAccount',16,'Requested $(PocketDescription) account is not of type NFC account.Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);


Delete from notification where code=2084;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2084,'NoPocketWithGivenCardPAN',1,'Pocket with the given Card PAN did not exist. Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2084,'NoPocketWithGivenCardPAN',2,'Pocket with the given Card PAN did not exist. Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2084,'NoPocketWithGivenCardPAN',4,'Pocket with the given Card PAN did not exist. Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2084,'NoPocketWithGivenCardPAN',8,'Pocket with the given Card PAN did not exist. Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2084,'NoPocketWithGivenCardPAN',16,'Pocket with the given Card PAN did not exist. Info, call $(CustomerServiceShortCode).',null,0,0,sysdate,null,null,1);


ALTER TABLE pocket ADD CardAlias VARCHAR(255);

commit;