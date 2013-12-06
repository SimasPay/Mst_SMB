-- Create Reverse Suspence pocket for Service Partner

INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+YoijoWwvVDjNxw==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1);

-- Define the Reverse pockey system Parameter
Delete from system_parameters where ParameterName = 'reverse.pocket.id';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','reverse.pocket.id',pocket_id_seq.currval,'Reverse Pocket Id');

-- Define the Cashout expiry time system parameter
Delete from system_parameters where ParameterName = 'cashout.at.atm.expiry.time';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','cashout.at.atm.expiry.time','-1','Cashout At ATM Expiry Time (Hrs)');

-- Inserting Enum_text data
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionUICategory','5636','52','Auto_Reverse','Auto Reverse');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','SCTLStatus','6089','19','Expired','Expired');

-- Inserting Notification messages

Delete from notification where Code = 716;
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,716,'AutoReverseSuccess',1,'Reversed the amount success.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,716,'AutoReverseSuccess',2,'Reversed the amount success.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,716,'AutoReverseSuccess',4,'Reversed the amount success.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,716,'AutoReverseSuccess',8,'Reversed the amount success.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,716,'AutoReverseSuccess',16,'Reversed the amount success.',null,0,0,sysdate,null,null,1);

Delete from notification where Code = 717;
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,717,'AutoReverseFailed',1,'Reversed the amount Failed.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,717,'AutoReverseFailed',2,'Reversed the amount Failed.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,717,'AutoReverseFailed',4,'Reversed the amount Failed.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,717,'AutoReverseFailed',8,'Reversed the amount Failed.',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,717,'AutoReverseFailed',16,'Reversed the amount Failed.',null,0,0,sysdate,null,null,1);

Delete from notification where Code = 718;
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,718,'AutoReverseSuccessToSource',1,'Transaction ID $(TransferID) is Reversed and the amount $(Currency) $(Amount) is credited to your account. ',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,718,'AutoReverseSuccessToSource',2,'Transaction ID $(TransferID) is Reversed and the amount $(Currency) $(Amount) is credited to your account. ',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,718,'AutoReverseSuccessToSource',4,'Transaction ID $(TransferID) is Reversed and the amount $(Currency) $(Amount) is credited to your account. ',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,718,'AutoReverseSuccessToSource',8,'Transaction ID $(TransferID) is Reversed and the amount $(Currency) $(Amount) is credited to your account. ',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Version, MSPID, Code, CodeName ,NotificationMethod, Text, STKML, Language, Status, StatusTime, AccessCode, SMSNotificationCode, CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,718,'AutoReverseSuccessToSource',16,'Transaction ID $(TransferID) is Reversed and the amount $(Currency) $(Amount) is credited to your account. ',null,0,0,sysdate,null,null,1);

commit;