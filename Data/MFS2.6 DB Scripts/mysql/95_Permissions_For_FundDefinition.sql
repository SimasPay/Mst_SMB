Delete from role_permission where permission in ('21201','21202','21203','21204','21205');
Delete from permission_item where permission in (21201,21202,21203,21204,21205);
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21201,1,'fundDefinitions','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21201');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21202,1,'fundDefinitions.add','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21202');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21203,1,'fundDefinitions.edit','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21203');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21204,1,'purpose.add','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21204');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21205,1,'expiry.add','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21205');

