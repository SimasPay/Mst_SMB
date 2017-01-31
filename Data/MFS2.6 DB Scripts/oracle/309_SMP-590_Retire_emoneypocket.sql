INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','SubscriberActivity','8257','7','Retire_Subscriber_Emoney_Pocket','Retire Subscriber Emoney Pocket');

Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10265', '1', 'create.sub.retire.emoneypocket', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Create Emoney pocket/Subscriber Retire Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Master_Admin'),'10265');

Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10266', '1', 'approve.sub.retire.emoneypocket', 'default', 'default', (select id from PERMISSION_GROUP where PERMISSIONGROUPNAME = 'Subscriber'), 'Approve/Reject Emoney pocket/Subscriber Retire Request');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', (select id from Role where ENUMVALUE = 'Approver'),'10266');

commit;