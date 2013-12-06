use mfino;

ALTER TABLE `pocket_template` ADD COLUMN `IsSuspencePocket` TINYINT(4) default 0;
ALTER TABLE `bulk_upload_entry` ADD COLUMN `FailureReason` VARCHAR(255), ADD COLUMN `ServiceChargeTransactionLogID` BIGINT(20);

ALTER TABLE `bulk_upload` MODIFY COLUMN `SubscriberID` BIGINT(20) DEFAULT NULL,
 MODIFY COLUMN `MDNID` BIGINT(20) DEFAULT NULL,
 MODIFY COLUMN `SuccessAmount` DECIMAL(25,4) DEFAULT NULL,
 MODIFY COLUMN `VerificationChecksum` BIGINT(20) DEFAULT NULL,
 MODIFY COLUMN `DigitalSignature` VARCHAR(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL,
 MODIFY COLUMN `BankUploadTryCounter` INT(11) DEFAULT 0,
 ADD COLUMN `Pin` VARCHAR(255) ,
 ADD COLUMN `PaymentDate` DATETIME ,
 ADD COLUMN `SourcePocket` BIGINT(20) ,
 ADD COLUMN `ApproverComments` VARCHAR(255) ,
 ADD COLUMN `ServiceChargeTransactionLogID` BIGINT(20) ,
 ADD COLUMN `QrtzJobId` VARCHAR(255) ,
 ADD CONSTRAINT `FK_bulk_upload_pocket` FOREIGN KEY `FK_bulk_upload_pocket` (`SourcePocket`)
    REFERENCES `pocket` (`ID`)
    ON DELETE RESTRICT
    ON UPDATE RESTRICT;

update pocket_template set IsSuspencePocket = 1 where id = (select pockettemplateid from pocket where id = (select ParameterValue from system_parameters where ParameterName='suspense.pocket.id'));

insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values(1, now(), 'System', now(), 'system', 'suspence.pocket.template.id', (select id from pocket_template where IsSuspencePocket = 1), 'Suspence Pocket Template Id');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BusinessPartnerType','6079','9','CorporateUser','CorporateUser');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BusinessPartnerTypePartner','6415','9','CorporateUser','CorporateUser');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BulkUploadDeliveryStatus','5475','7','Approved','Approved');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BulkUploadDeliveryStatus','5475','8','Rejected','Rejected');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BulkUploadDeliveryStatus','5475','9','Scheduled','Scheduled');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','BulkUploadDeliveryStatus','5475','10','Failed','Failed');
delete from enum_text where tagid='5475' and enumcode in ('3','4','5');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5636','45','Bulk_Transfer','Bulk Transfer');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','TransactionUICategory','5636','46','Sub_Bulk_Transfer','Sub Bulk Transfer');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',now(),'system',now(),'system','0','Role','5352','26','Corporate_User','Corporate User');

INSERT INTO transaction_type (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,TransactionName,DisplayName) VALUES
(1,now(),'system',now(),'system',1,'BulkTransfer','Bulk Transfer');
INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='BulkTransfer'));
 INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Bank'),(select id from transaction_type where TransactionName='BulkTransfer'));

INSERT INTO transaction_type (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,TransactionName,DisplayName) VALUES
(1,now(),'system',now(),'system',1,'SubBulkTransfer','Sub Bulk Transfer');
INSERT INTO service_transaction (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,MSPID,ServiceID, TransactionTypeID) VALUES
(1,now(),'system',now(),'system',1,(select id from service where ServiceName='Wallet'),(select id from transaction_type where TransactionName='SubBulkTransfer'));

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 13601,1,'bulktransfer','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 13602,1,'bulktransfer.grid.view','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 13603,1,'bulktransfer.upload','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 13604,1,'bulktransfer.approve','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10226');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10210');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10211');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10235');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10219');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10228');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10614');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','10801');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','12101');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','12110');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','12301');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','12303');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','13601');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '25','13601');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','13601');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','13602');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','13602');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '26','13603');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '25','13604');

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,691,'BulkTransferCompletedToPartner',1,'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request Processed on $(TransactionDateTime).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,691,'BulkTransferCompletedToPartner',2,'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request Processed on $(TransactionDateTime).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,691,'BulkTransferCompletedToPartner',4,'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request Processed on $(TransactionDateTime).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,691,'BulkTransferCompletedToPartner',8,'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request Processed on $(TransactionDateTime).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,691,'BulkTransferCompletedToPartner',16,'Transaction ID: $(TransferID). Dear Customer, Your Bulk Transfer Request Processed on $(TransactionDateTime).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,692,'BulkTransferCompletedToSubscriber_Dummy',1,'Transaction ID: $(TransferID). Dear Customer, Transfer Completed',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,692,'BulkTransferCompletedToSubscriber_Dummy',2,'Transaction ID: $(TransferID). Dear Customer, Transfer Completed',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,692,'BulkTransferCompletedToSubscriber_Dummy',4,'Transaction ID: $(TransferID). Dear Customer, Transfer Completed',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,692,'BulkTransferCompletedToSubscriber_Dummy',8,'Transaction ID: $(TransferID). Dear Customer, Transfer Completed',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,692,'BulkTransferCompletedToSubscriber_Dummy',16,'Transaction ID: $(TransferID). Dear Customer, Transfer Completed',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,693,'BulkTransferCompletedToSubscriber',1,'Transaction ID: $(TransferID). Dear Customer, you have received $(Currency) $(Amount) as part of Bulk Transfer from $(SenderMDN). Your balance as on $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,693,'BulkTransferCompletedToSubscriber',2,'Transaction ID: $(TransferID). Dear Customer, you have received $(Currency) $(Amount) as part of Bulk Transfer from $(SenderMDN). Your balance as on $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,693,'BulkTransferCompletedToSubscriber',4,'Transaction ID: $(TransferID). Dear Customer, you have received $(Currency) $(Amount) as part of Bulk Transfer from $(SenderMDN). Your balance as on $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,693,'BulkTransferCompletedToSubscriber',8,'Transaction ID: $(TransferID). Dear Customer, you have received $(Currency) $(Amount) as part of Bulk Transfer from $(SenderMDN). Your balance as on $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).',null,0,0,now(),null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (now(),'System',now(),'System',0,1,693,'BulkTransferCompletedToSubscriber',16,'Transaction ID: $(TransferID). Dear Customer, you have received $(Currency) $(Amount) as part of Bulk Transfer from $(SenderMDN). Your balance as on $(TransactionDateTime) is $(Currency) $(DestinationMDNBalance).',null,0,0,now(),null,null,1);
	