Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10260', '1', 'create.sub.suspend.emoneypocket', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Create Emoney pocket Suspend Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Master_Admin'),'10260');

Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10261', '1', 'approve.sub.suspend.emoneypocket', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Approve/Reject Emoney pocket Suspend Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Master_Admin'),'10261');

ALTER TABLE SUBSCRIBER_UPGRADE_DATA ADD Comments VARCHAR2(255);
ALTER TABLE SUBSCRIBER_UPGRADE_DATA ADD AdminAction Number(10,0);

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','SubscriberActivityStatus','8258','2','Failed','Failed');

commit;