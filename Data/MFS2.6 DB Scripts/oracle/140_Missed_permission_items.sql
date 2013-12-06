
Delete from permission_item where Permission in (10240, 10241, 12005, 13603);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 10240,1,'sub.settle.closed.account','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 10241,1,'sub.settle.closed.account.approve.reject','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 12005,1,'denomination.add','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 13603,1,'bulktransfer.upload','default','default');

UPDATE permission_item SET permissiongroupid  = 1,  description = 'Closed Account Settlement Details' WHERE permission = 10240;
UPDATE permission_item SET permissiongroupid  = 1,  description = 'Approve/Reject Settlement' WHERE permission = 10241;
UPDATE permission_item SET permissiongroupid  = 15,  description = 'Add Partner in Biller Tab' WHERE permission = 12005;
UPDATE permission_item SET permissiongroupid  = 21,  description = 'Upload Bulk Transfer File' WHERE permission = 13603;

commit;
