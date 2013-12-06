
UPDATE enum_text SET displaytext = 'Product_Team_Customer_Service' WHERE enumvalue = 'Finance_Admin';
UPDATE enum_text SET displaytext = 'Product_Team_Marketing' WHERE enumvalue = 'Operation_Support';
UPDATE enum_text SET displaytext = 'Product_Team_Operations' WHERE enumvalue = 'Omnibus_Support';
UPDATE enum_text SET displaytext = 'Product_Head' WHERE enumvalue = 'Reviewer';

UPDATE role SET displaytext = 'Product_Team_Customer_Service' WHERE enumvalue = 'Finance_Admin';
UPDATE role SET displaytext = 'Product_Team_Marketing' WHERE enumvalue = 'Operation_Support';
UPDATE role SET displaytext = 'Product_Team_Operations' WHERE enumvalue = 'Omnibus_Support';
UPDATE role SET displaytext = 'Product_Head' WHERE enumvalue = 'Reviewer';

Delete from role_permission where Permission='21001';
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '1','21001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '2','21001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '3','21001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '9','21001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '12','21001');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', NOW(), 'system', NOW(), 'system', '25','21001');

Delete from role_permission where Permission='21002';
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '2','21002');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '12','21002');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '25','21002');
