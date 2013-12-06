DELETE FROM enum_text WHERE TagID=5055 AND EnumCode=2018;

INSERT INTO enum_text (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Language,TagName,TagID,EnumCode,EnumValue,DisplayText) VALUES (1,sysdate,'system',sysdate,'system',0,'NotificationCode',5055,'2018','CashOutAlreadyRequested','CashOutAlreadyRequested');

DELETE FROM notification WHERE Code=2018;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2018,'CashOutAlreadyRequested',1,'ERROR: Previous CashOut Request is still under processing please try after some time.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2018,'CashOutAlreadyRequested',2,'ERROR: Previous CashOut Request is still under processing please try after some time.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2018,'CashOutAlreadyRequested',4,'ERROR: Previous CashOut Request is still under processing please try after some time.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2018,'CashOutAlreadyRequested',8,'ERROR: Previous CashOut Request is still under processing please try after some time.',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2018,'CashOutAlreadyRequested',16,'ERROR: Previous CashOut Request is still under processing please try after some time.',null,0,0,sysdate,null,null,1);

commit;