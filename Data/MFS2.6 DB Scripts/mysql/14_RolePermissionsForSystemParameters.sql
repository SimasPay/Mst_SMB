
Delete FROM permission_item where Permission = '18001';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 18001,1,'systemParameters','default','default');

Delete FROM role_permission where Permission = '18001' and Role = '1';
INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','18001');

Delete FROM permission_item where Permission = '18002';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 18002,1,'systemParameters.view','default','default');

Delete FROM role_permission where Permission = '18002' and Role = '1';
INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','18002');

Delete FROM permission_item where Permission = '18003';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 18003,1,'systemParameters.add','default','default');

Delete FROM role_permission where Permission = '18003' and Role = '1';
INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','18003');

Delete FROM permission_item where Permission = '18004';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 18004,1,'systemParameters.edit','default','default');

Delete FROM role_permission where Permission = '18004' and Role = '1';
INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','18004');

Delete FROM permission_item where Permission = '18005';
INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 18005,1,'systemParameters.delete','default','default');

