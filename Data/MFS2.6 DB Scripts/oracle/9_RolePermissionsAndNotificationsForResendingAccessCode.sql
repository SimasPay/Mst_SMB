INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 13504,1,'chargeTransaction.resendAccessCode','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','13504');

delete from notification where code=707;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,707,'ResendAccessCodeNotificationToSenderOfUnregisteredTransfer',1,'Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,707,'ResendAccessCodeNotificationToSenderOfUnregisteredTransfer',2,'Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,707,'ResendAccessCodeNotificationToSenderOfUnregisteredTransfer',4,'Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,707,'ResendAccessCodeNotificationToSenderOfUnregisteredTransfer',8,'Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,707,'ResendAccessCodeNotificationToSenderOfUnregisteredTransfer',16,'Fund access code for the Transaction ID $(TransferID) is reset. You initiated a transaction of amount $(Currency) $(Amount) to $(ReceiverMDN). New Fund access code for the same is $(OneTimePin)',null,0,0,sysdate,null,null,1);

commit;