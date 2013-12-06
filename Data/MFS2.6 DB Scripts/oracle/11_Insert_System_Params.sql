
DELETE FROM system_parameters WHERE ParameterName='pin.length';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','pin.length','4','Pin Length');

DELETE FROM system_parameters WHERE ParameterName='country.code';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','country.code','234','Country Prefix code');

commit;