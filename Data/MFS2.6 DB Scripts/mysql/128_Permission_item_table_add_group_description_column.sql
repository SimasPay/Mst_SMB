
ALTER TABLE permission_item ADD COLUMN `PermissionGroupID` bigint(20);
ALTER TABLE permission_item ADD COLUMN `Description` varchar(255);
ALTER TABLE permission_item ADD CONSTRAINT fk_permission_item_groupid FOREIGN KEY(PermissionGroupID) REFERENCES permission_group(ID);