Delete from system_parameters where ParameterName = 'category.payments';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.payments',2.6,'category.payments');

