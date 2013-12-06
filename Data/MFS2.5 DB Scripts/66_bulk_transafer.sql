use mfino;

ALTER TABLE bulk_upload ADD COLUMN FailureReason VARCHAR(255);

update notification set Text = 'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request $(BulkTransferID) Processed on $(TransactionDateTime).' where Code = 691;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,696,'BulkTransferRequestFailedToPartner',1,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is failed.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,696,'BulkTransferRequestFailedToPartner',2,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is failed.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,696,'BulkTransferRequestFailedToPartner',4,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is failed.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,696,'BulkTransferRequestFailedToPartner',8,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is failed.',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,696,'BulkTransferRequestFailedToPartner',16,'Dear Customer, Your Bulk Transfer Request $(BulkTransferID) is failed.',null,0,0,now(),null,null,1);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 13606,1,'bulktransfer.verify','default','default');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','13606');