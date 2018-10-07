Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '23103', '1', 'user.details.useractivity', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'User'), 'Internal User Audit Log');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Master_Admin'),'23103');

commit;