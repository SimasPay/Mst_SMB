DELETE FROM  notification WHERE Code=2033;

INSERT INTO  notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2033,'NewSubscriberActivation',1,'Dear Customer, You have been Successfully Activated',null,0,0,sysdate,null,null,1);

INSERT INTO  notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2033,'NewSubscriberActivation',2,'Dear Customer, You have been Successfully Activated',null,0,0,sysdate,null,null,1);

INSERT INTO  notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2033,'NewSubscriberActivation',4,'Dear Customer, You have been Successfully Activated',null,0,0,sysdate,null,null,1);

INSERT INTO  notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2033,'NewSubscriberActivation',8,'Dear Customer, You have been Successfully Activated',null,0,0,sysdate,null,null,1);

INSERT INTO  notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2033,'NewSubscriberActivation',16,'Dear Customer, You have been Successfully Activated',null,0,0,sysdate,null,null,1);

commit;