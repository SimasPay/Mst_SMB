
INSERT INTO permission_item (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) values (1, sysdate, 'System', sysdate, 'System', 10228, 1, 'sub.details.checkBalance','default','default', (select id from permission_group where PermissionGroupName='Pocket'), 'Check Pocket Balance');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'System',sysdate,'System',(select id from role where ENUMVALUE='Master_Admin'),'10228');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'System',sysdate,'System',(select id from role where ENUMVALUE='System_Admin'),'10228');

commit;