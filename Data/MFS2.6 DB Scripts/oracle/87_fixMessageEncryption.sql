Delete from system_parameters where ParameterName = 'mfino.encrypt.fixMessage';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','mfino.encrypt.fixMessage','false','set value as true if fix message needs to be encrypted');

commit;