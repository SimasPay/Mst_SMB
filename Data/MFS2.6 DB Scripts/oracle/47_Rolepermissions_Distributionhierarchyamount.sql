Delete FROM permission_item where Permission = '17002';

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 17002,1,'dct.distribute.amount','default','default');

Delete FROM role_permission where Permission = '17002' and Role = '23';

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '23','17002');

commit;

