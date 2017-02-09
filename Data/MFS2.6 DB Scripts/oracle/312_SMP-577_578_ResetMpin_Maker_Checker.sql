INSERT INTO PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10253', '1', 'sub.approve.resetpin', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Checker for reset mPin');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Approver'),'10253');

commit;
