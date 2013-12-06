Delete from system_parameters where ParameterName = 'restrict.bankpocket.tobuy.airtime';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','restrict.bankpocket.tobuy.airtime','true','minimum age for registration through api'); 
