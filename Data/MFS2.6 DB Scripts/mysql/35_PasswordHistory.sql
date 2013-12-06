

ALTER TABLE `mfino_user` ADD COLUMN `PasswordHistory` VARCHAR(255) DEFAULT NULL; 

-- replace count 
Delete from system_parameters where ParameterName = 'password.history.count';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','password.history.count','3','password history count');
