DELETE FROM permission_item where Permission in (21001, 21002);

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) VALUES('1', now(), 'system', now(), 'system', 21001,1,'fundingForAgent','default','default',28,'View Funding For Agent Tab');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PermissionGroupID,Description) VALUES('1', now(), 'system', now(), 'system', 21002,1,'fundingForAgent.approve','default','default',28,'Approve/Reject Agent CashIn Request');
