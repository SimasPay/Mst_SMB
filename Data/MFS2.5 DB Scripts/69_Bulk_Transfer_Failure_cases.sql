use mfino;

Alter table bulk_upload ADD COLUMN ReverseSCTLID BIGINT(20);

INSERT INTO transaction_type (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,TransactionName,DisplayName) VALUES
(1,now(),'system',now(),'system',1,'SettleBulkTransfer','Settle Bulk Transfer');
INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='SettleBulkTransfer'));

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5636','47','Settle_Bulk_Transfer','Settle Bulk Transfer');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5475','11','Pending','Pending');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5475','12','Settlement_Pending','Settlement Pending');

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,700,'BulkTransferRequestRejectedToPartner',1,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is Rejected.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,700,'BulkTransferRequestRejectedToPartner',2,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is Rejected.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,700,'BulkTransferRequestRejectedToPartner',4,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is Rejected.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,700,'BulkTransferRequestRejectedToPartner',8,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is Rejected.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,700,'BulkTransferRequestRejectedToPartner',16,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is Rejected.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,701,'BulkTransferReverseCompletedToPartner',1,'Dear Customer, Failed amount for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,701,'BulkTransferReverseCompletedToPartner',2,'Dear Customer, Failed amount for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,701,'BulkTransferReverseCompletedToPartner',4,'Dear Customer, Failed amount for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,701,'BulkTransferReverseCompletedToPartner',8,'Dear Customer, Failed amount for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,701,'BulkTransferReverseCompletedToPartner',16,'Dear Customer, Failed amount for Your Bulk Transfer Request $(BulkTransferID) is credited to souce pocket.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,702,'BulkTransferRequestPendingToPartner',1,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is in Pending state. Please call Customer Care: $(CustomerServiceShortCode) to resolve ',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,702,'BulkTransferRequestPendingToPartner',2,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is in Pending state. Please call Customer Care: $(CustomerServiceShortCode) to resolve ',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,702,'BulkTransferRequestPendingToPartner',4,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is in Pending state. Please call Customer Care: $(CustomerServiceShortCode) to resolve ',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,702,'BulkTransferRequestPendingToPartner',8,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is in Pending state. Please call Customer Care: $(CustomerServiceShortCode) to resolve ',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,702,'BulkTransferRequestPendingToPartner',16,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is in Pending state. Please call Customer Care: $(CustomerServiceShortCode) to resolve ',null,0,0,now(),null,null,1);
