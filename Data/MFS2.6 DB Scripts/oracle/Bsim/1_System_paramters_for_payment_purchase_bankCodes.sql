Delete from system_parameters where ParameterName = 'category.bankCodes';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','category.bankCodes',2.6,'category.bankCodes');
Delete from system_parameters where ParameterName = 'category.purchase';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','category.purchase',2.6,'category.purchase');
Delete from system_parameters where ParameterName = 'category.payments';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','category.payments',2.6,'category.payments');

commit;
