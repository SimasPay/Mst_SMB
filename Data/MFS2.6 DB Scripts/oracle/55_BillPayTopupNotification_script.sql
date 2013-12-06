INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2026,'BillPayTopupCompletedToReceiver',1,'REF ID: $(TransferID). Your account recieved topup of $(Currency) $(Amount) from $(SenderMDN).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2026,'BillPayTopupCompletedToReceiver',2,'REF ID: $(TransferID). Your account recieved topup of $(Currency) $(Amount) from $(SenderMDN).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2026,'BillPayTopupCompletedToReceiver',4,'REF ID: $(TransferID). Your account recieved topup of $(Currency) $(Amount) from $(SenderMDN).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2026,'BillPayTopupCompletedToReceiver',8,'REF ID: $(TransferID). Your account recieved topup of $(Currency) $(Amount) from $(SenderMDN).',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2026,'BillPayTopupCompletedToReceiver',16,'REF ID: $(TransferID). Your account recieved topup of $(Currency) $(Amount) from $(SenderMDN).',null,0,0,sysdate,null,null,1);


commit;