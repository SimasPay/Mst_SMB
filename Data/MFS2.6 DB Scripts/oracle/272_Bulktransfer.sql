INSERT INTO permission_group (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,PermissionGroupName) VALUES(21, '1', sysdate, 'system', sysdate, 'system', 'Bulk Transfer');

insert into permission_item (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) values (1, sysdate, 'System', sysdate, 'System', 13601, 1, 'bulktransfer','default','default', (select id from permission_group where PermissionGroupName='Bulk Transfer'), 'View Tab');

insert into permission_item (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) values (1, sysdate, 'System', sysdate, 'System', 13603,1,'bulktransfer.upload','default','default', (select id from permission_group where PermissionGroupName='Bulk Transfer'), 'Upload New File');

insert into permission_item (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) values (1, sysdate, 'System', sysdate, 'System', 13604, 1, 'bulktransfer.approve','default','default', (select id from permission_group where PermissionGroupName='Bulk Transfer'), 'Approve Bulk Transfer');

insert into permission_item (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) values (1, sysdate, 'System', sysdate, 'System', 13605,1,'bulktransfer.cancel','default','default', (select id from permission_group where PermissionGroupName='Bulk Transfer'), 'Cancel Bulk Transfer');

insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'Syetem', (select id from role where enumvalue='Master_Admin'), 13601);

insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'Syetem', (select id from role where enumvalue='Master_Admin'), 13603);

insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'Syetem', (select id from role where enumvalue='Master_Admin'), 13605);

insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'Syetem', (select id from role where enumvalue='Approver'), 13601);

insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'Syetem', (select id from role where enumvalue='Approver'), 13604);

-- Script to insert new transaction types for Bulktransfer
delete from transaction_type where TransactionName in('BulkTransfer');

insert into transaction_type (Id, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (22, 1, sysdate, 'System', sysdate, 'System', 1, 'BulkTransfer', 'Bulk Transfer');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'Wallet' and mspid=1), 22);

delete from transaction_type where TransactionName in('SubBulkTransfer');

insert into transaction_type (Id, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (23, 1, sysdate, 'System', sysdate, 'System', 1, 'SubBulkTransfer', 'Individual Transfer');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'Wallet' and mspid=1), 23);

delete from transaction_type where TransactionName in('SettleBulkTransfer');

insert into transaction_type (Id, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (24, 1, sysdate, 'System', sysdate, 'System', 1, 'SettleBulkTransfer', 'settle Bulk Transfer');

insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'Wallet' and mspid=1), 24);


alter table bulk_upload add Name varchar2(50);
alter table bulk_upload_entry add IsTrfToSuspense number(3,0);

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionsTransferStatus','5415','0','Initialized','Initialized');

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TransactionsTransferStatus','5415','3','Pending','Pending');

delete from enum_text where TagID = 5475;

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','0','Initialized','Initialized');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','1','Uploaded','Uploaded');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','2','Processing','Processing');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','3','Processed','Processed');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','4','Terminated','Terminated');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','7','Complete','Complete');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','8','Approved','Approved');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','9','Rejected','Rejected');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','10','Scheduled','Scheduled');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','11','Failed','Failed');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BulkUploadDeliveryStatus','5475','13','Settlement_Pending','Settlement_Pending');

INSERT INTO pocket VALUES (pocket_id_seq.nextval,1,sysdate,'System',sysdate,'System',7,1,NULL,'/YST2/P0lVQ=',0.0000,0.0000,0.0000,0,0,0,NULL,NULL,NULL,'wl1Hb4D+Yojm6MOu7FCeUQ==',0,1,1,sysdate,sysdate,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1, NULL, 0.0000,0.0000,0.0000,0,0,0);

-- Define the Reverse pockey system Parameter
Delete from system_parameters where ParameterName = 'interest.commission.funding.pocket.id';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','interest.commission.funding.pocket.id',pocket_id_seq.currval,'Interest / Commission funding pocket id');


commit;

