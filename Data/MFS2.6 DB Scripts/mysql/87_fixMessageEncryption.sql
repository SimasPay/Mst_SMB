

delete from system_parameters where parameterName like 'mfino.encrypt.fixMessage';

insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,now(),'System',now(),'system','mfino.encrypt.fixMessage','false','set value as true if fix message needs to be encrypted');