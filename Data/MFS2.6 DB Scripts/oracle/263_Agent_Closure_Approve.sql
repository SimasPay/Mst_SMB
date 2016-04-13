

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10246,1,'agent.approveClose','default','default',1,'Approve Agent Closing');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '25','10246');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,permissiongroupid,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 10247,1,'close.account','default','default',1,'Closing Account');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','10247');


ALTER TABLE PARTNER  ADD CLOSEACCTSTATUS NUMBER;
ALTER TABLE PARTNER  ADD CLOSEACCTAPPROVEDBY VARCHAR2(255);
ALTER TABLE PARTNER  ADD CLOSEACCTTIME TIMESTAMP(0);
ALTER TABLE PARTNER  ADD CLOSEAPPROVERCOMMENTS VARCHAR2(255);

COMMIT;
