
delete from notification where code = 806;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,806,'PendingSettlementDetails',1,'TradeName:$(TradeName), Pending Settlement Amount:$(Amount), Service:$(Service), Status:$(SettlementStatus)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,806,'PendingSettlementDetails',2,'TradeName:$(TradeName), Pending Settlement Amount:$(Amount), Service:$(Service), Status:$(SettlementStatus)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,806,'PendingSettlementDetails',4,'TradeName:$(TradeName), Pending Settlement Amount:$(Amount), Service:$(Service), Status:$(SettlementStatus)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,806,'PendingSettlementDetails',8,'TradeName:$(TradeName), Pending Settlement Amount:$(Amount), Service:$(Service), Status:$(SettlementStatus)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,806,'PendingSettlementDetails',16,'TradeName:$(TradeName), Pending Settlement Amount:$(Amount), Service:$(Service), Status:$(SettlementStatus)',null,0,0,sysdate,null,null,1);


delete from notification where code = 807;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,807,'NoPendingsettlementsWereFound',1,'Dear Customer, there are no pending settlements for you',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,807,'NoPendingsettlementsWereFound',2,'Dear Customer, there are no pending settlements for you',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,807,'NoPendingsettlementsWereFound',4,'Dear Customer, there are no pending settlements for you',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,807,'NoPendingsettlementsWereFound',8,'Dear Customer, there are no pending settlements for you',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,807,'NoPendingsettlementsWereFound',16,'Dear Customer, there are no pending settlements for you',null,0,0,sysdate,null,null,1);


delete from notification where code = 808;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,808,'InvalidPartner',1,'Dear Customer, you are not registered as partner. Please contact our customer care services at: $(CustomerServiceShortCode)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,808,'InvalidPartner',2,'Dear Customer, you are not registered as partner. Please contact our customer care services at: $(CustomerServiceShortCode)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,808,'InvalidPartner',4,'Dear Customer, you are not registered as partner. Please contact our customer care services at: $(CustomerServiceShortCode)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,808,'InvalidPartner',8,'Dear Customer, you are not registered as partner. Please contact our customer care services at: $(CustomerServiceShortCode)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,808,'InvalidPartner',16,'Dear Customer, you are not registered as partner. Please contact our customer care services at: $(CustomerServiceShortCode)',null,0,0,sysdate,null,null,1);


DELETE FROM enum_text where TagID=8037;

INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (1,sysdate,'system',sysdate,'system',0,'SettlementStatus',8037,0,'Initiated','Initiated');

INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (1,sysdate,'system',sysdate,'system',0,'SettlementStatus',8037,1,'Completed','Completed');

INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (1,sysdate,'system',sysdate,'system',0,'SettlementStatus',8037,2,'Failed','Failed');

INSERT INTO enum_text (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Language, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (1,sysdate,'system',sysdate,'system',0,'SettlementStatus',8037,3,'Failed','Failed');


commit;
