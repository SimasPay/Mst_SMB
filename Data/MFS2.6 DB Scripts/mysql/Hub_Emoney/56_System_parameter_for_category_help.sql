Delete from system_parameters where ParameterName = 'category.help';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.help',0.1,'category.help');

Delete from system_parameters where ParameterName = 'category.addressList';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.addressList',0.1,'category.addressList');