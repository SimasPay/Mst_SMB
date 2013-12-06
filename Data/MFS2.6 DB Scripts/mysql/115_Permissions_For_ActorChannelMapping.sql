Delete from role_permission where permission in (21701);
Delete from role_permission where permission in (21702);
Delete from role_permission where permission in (21703);

Delete from permission_item where permission in (21701);
Delete from permission_item where permission in (21702);
Delete from permission_item where permission in (21703);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21701,1,'actorChannelMapping','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21702,1,'actorChannelMapping.add','default','default');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', now(), 'system', now(), 'system', 21703,1,'actorChannelMapping.edit','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21701');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21702');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','21703');