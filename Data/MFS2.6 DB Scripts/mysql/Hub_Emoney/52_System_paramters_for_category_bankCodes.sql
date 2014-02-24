Delete from system_parameters where ParameterName = 'category.bankCodes';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.bankCodes',1.0,'category.bankCodes');
