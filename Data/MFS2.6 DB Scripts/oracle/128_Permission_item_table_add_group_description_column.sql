
ALTER TABLE permission_item ADD PermissionGroupID NUMBER(19,0);
ALTER TABLE permission_item ADD Description VARCHAR2(255 CHAR);
ALTER TABLE permission_item ADD CONSTRAINT fk_permission_item_groupid FOREIGN KEY(PermissionGroupID) REFERENCES permission_group(ID);

commit;