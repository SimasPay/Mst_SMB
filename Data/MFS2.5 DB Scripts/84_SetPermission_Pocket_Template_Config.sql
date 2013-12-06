INSERT INTO `mfino`.`permission_item` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) 
										VALUES('1', NOW(), 'system', NOW(), 'system', 14002,1,'pocketTemplateConfig','default','default');

INSERT INTO `mfino`.`role_permission` (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) 
										VALUES('1', NOW(), 'system', NOW(), 'system', '1','14002');