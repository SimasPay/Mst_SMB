Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10262', '1', 'sub.emoneypocket.release.suspension.request', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Create Emoney pocket Suspension Release Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Master_Admin'),'10262');

Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10263', '1', 'sub.emoneypocket.release.suspension.approve', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Approve/Reject Emoney pocket Suspension Release Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Approver'),'10263');

commit;