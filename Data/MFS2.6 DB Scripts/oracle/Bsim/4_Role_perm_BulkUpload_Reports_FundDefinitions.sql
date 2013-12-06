Delete from role_permission where role=1 and permission = 10802;
Delete from role_permission where role=1 and permission = 14001;
Delete from role_permission where role=1 and permission = 21201;
Delete from role_permission where role=1 and permission = 11001;
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','11001');
Delete from role_permission where role=1 and permission = 11004;
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','11004');

commit;