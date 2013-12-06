Delete from system_parameters where ParameterName = 'reset.pin.mode';

INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','reset.pin.mode','otp','Reset Pin by OTP mode');

commit;