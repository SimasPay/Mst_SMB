

DELETE FROM permission_item where Permission=13504;

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 13504,1,'chargeTransaction.resendAccessCode','default','default');

DELETE FROM role_permission where Role=9 and Permission=13504;

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '9','13504');

DELETE FROM role_permission where Role=10 and Permission=13504;

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '10','13504');

commit;