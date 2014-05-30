Delete from system_parameters where ParameterName = 'category.help';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.help',0.1,'category.help');

Delete from system_parameters where ParameterName = 'category.upgradeAddressList';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.upgradeAddressList',0.1,'category.upgradeAddressList');

Delete from system_parameters where ParameterName = 'category.cashoutAddressList';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.cashoutAddressList',0.1,'category.cashoutAddressList');