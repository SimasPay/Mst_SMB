Delete from system_parameters where ParameterName = 'min.registration.age';
Delete from system_parameters where ParameterName =  'mdnlength.with.countrycode';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','min.registration.age','18','minimum age for registration through api'); 
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','mdnlength.with.countrycode','13','mdn lenth with country code');

commit;