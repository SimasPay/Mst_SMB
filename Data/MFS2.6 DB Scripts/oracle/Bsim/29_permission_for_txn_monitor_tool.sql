insert into permission_group(id, version, lastupdatetime, updatedby, createtime, createdby, permissiongroupname) values (31, 0, sysdate, 'system', sysdate, 'system', 'Transaction Monitor');

insert into permission_item(version, lastupdatetime, updatedby, createtime, createdby, permission, itemtype, itemid, fieldid,action, permissiongroupid, description) values (0, sysdate, 'system', sysdate, 'system', 21301, 1, 'transactionMonitor', 'default', 'default', 31, 'View Transaction Monitor');

insert into permission_item (version, lastupdatetime, updatedby, createtime, createdby, permission, itemtype, itemid, fieldid,action, permissiongroupid, description) values (0, sysdate, 'system', sysdate, 'system', 21302, 1, 'transactionMonitor.viewFloatBalance', 'default', 'default', '31', 'View Float Balance');