delete from role_permission where Role='1' and Permission='10324';

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', now(), 'system', now(), 'system', '1','10324');