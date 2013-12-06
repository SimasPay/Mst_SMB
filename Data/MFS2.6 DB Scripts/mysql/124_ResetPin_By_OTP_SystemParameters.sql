Delete from system_parameters where ParameterName = 'reset.pin.mode';

INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,now(),'System',now(),'system','reset.pin.mode','direct','Reset Pin by OTP mode');