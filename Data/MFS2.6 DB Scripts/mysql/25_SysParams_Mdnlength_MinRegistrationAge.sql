
Delete from system_parameters where ParameterName = 'min.registration.age' or 'mdnlength.with.countrycode';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','min.registration.age','18','minimum age for registration through api'); 
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','mdnlength.with.countrycode','13','mdn lenth with country code');