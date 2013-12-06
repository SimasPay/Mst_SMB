 
Delete FROM permission_item where Permission = '10240';

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 10240,1,'sub.settle.closed.account','default','default');

Delete FROM permission_item where Permission = '10241';

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 10241,1,'sub.settle.closed.account.approve.reject','default','default');


Delete FROM role_permission where Permission = '10240' and Role = '1';

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','10240');

Delete FROM role_permission where Permission = '10241' and Role = '25';

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '25','10241');