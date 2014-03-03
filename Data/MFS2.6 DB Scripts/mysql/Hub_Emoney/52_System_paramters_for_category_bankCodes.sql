Delete from system_parameters where ParameterName = 'category.bankCodes';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.bankCodes',1.0,'category.bankCodes');

Delete from system_parameters where ParameterName = 'category.postpaid';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.postpaid',1.0,'category.postpaid');

Delete from system_parameters where ParameterName = 'category.prepaid';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.prepaid',1.0,'category.prepaid');

Delete from system_parameters where ParameterName = 'category.postpaidPLN';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.postpaidPLN',1.0,'category.postpaidPLN');

Delete from system_parameters where ParameterName = 'category.prepaidPLN';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.prepaidPLN',1.0,'category.prepaidPLN');

Delete from system_parameters where ParameterName = 'category.postpaidPhone';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.postpaidPhone',1.0,'category.postpaidPhone');

Delete from system_parameters where ParameterName = 'category.prepaidPhone';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','category.prepaidPhone',1.0,'category.prepaidPhone');