

DELETE FROM role_permission where Role=20 and Permission in (12112, 12113);  
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '20','12112');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '20','12113');
