Delete from role_permission where permission=12117;

Delete from permission_item where permission =12117;

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) VALUES('1', sysdate, 'system', sysdate, 'system', 12117,1,'partner.transfer','default','default',(SELECT ID FROM permission_group WHERE PermissionGroupName='Partner'),'PartnerSelfTransfer Permission');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '22','12117');


commit;
