DELETE FROM permission_item WHERE Permission IN (21401, 21402, 21403, 21501, 21502, 21503, 21601);

delete from role_permission where Permission IN (21401, 21402, 21403, 21501, 21502, 21503, 21601);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system1', 21401,1,'notificationLog','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21401);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21402,1,'notificationLog.view','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21402);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21403,1,'notificationLog.delete','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21403);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21501,1,'notificationLogDetails','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21501);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21502,1,'notificationLogDetails.view','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21502);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21503,1,'notificationLogDetails.delete','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21503);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES(1, sysdate, 'system', sysdate, 'system', 21601,1,'notification.resend','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES(1, sysdate, 'system', sysdate, 'system', 1,21601);

commit;