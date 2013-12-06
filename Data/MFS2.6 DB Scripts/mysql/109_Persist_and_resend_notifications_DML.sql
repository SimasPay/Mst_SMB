
DELETE FROM `permission_item` WHERE Permission = 21401;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21401,1,'notificationLog','default','default');

DELETE FROM `role_permission` WHERE Permission = 21401;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21401');


DELETE FROM `permission_item` WHERE Permission = 21402;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21402,1,'notificationLog.view','default','default');

DELETE FROM `role_permission` WHERE Permission = 21402;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21402');


DELETE FROM `permission_item` WHERE Permission = 21403;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21403,1,'notificationLog.delete','default','default');

DELETE FROM `role_permission` WHERE Permission = 21403;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21403');



DELETE FROM `permission_item` WHERE Permission = 21501;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21501,1,'notificationLogDetails','default','default');

DELETE FROM `role_permission` WHERE Permission = 21501;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21501');


DELETE FROM `permission_item` WHERE Permission = 21502;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21502,1,'notificationLogDetails.view','default','default');

DELETE FROM `role_permission` WHERE Permission = 21502;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21502');


DELETE FROM `permission_item` WHERE Permission = 21503;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21503,1,'notificationLogDetails.delete','default','default');

DELETE FROM `role_permission` WHERE Permission = 21503;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21503');


DELETE FROM `permission_item` WHERE Permission = 21601;

INSERT INTO `permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', NOW(), 'system', NOW(), 'system', 21601,1,'notification.resend','default','default');

DELETE FROM `role_permission` WHERE Permission = 21601;

INSERT INTO `role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21601');

