ALTER TABLE mfino_user ADD (PasswordHistory VARCHAR(255) DEFAULT NULL); 

-- replace count 
Delete from system_parameters where ParameterName = 'password.history.count';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','password.history.count','3','password history count');

commit;