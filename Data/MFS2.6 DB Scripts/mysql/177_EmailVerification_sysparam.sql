
delete from system_parameters where ParameterName = 'email.verification.needed';
insert into system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,NOW(),'System',NOW(),'system','email.verification.needed','false','Email Verification needed');