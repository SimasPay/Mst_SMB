
Delete from system_parameters where ParameterName = 'no.of.decimals';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','no.of.decimals','2','No of decimals');

COMMIT;