Delete from role_permission where permission in (22001);
Delete from role_permission where permission in (22002);

Delete from permission_item where permission in (22001);
Delete from permission_item where permission in (22002);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) VALUES('1', now(), 'system', now(), 'system', 22001,1,'permissions','default','default',36,'View Permissions Tab');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) VALUES('1', now(), 'system', now(), 'system', 22002,1,'permissions.save','default','default',36,'Save Permissions');


INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '2','22001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '2','22002');