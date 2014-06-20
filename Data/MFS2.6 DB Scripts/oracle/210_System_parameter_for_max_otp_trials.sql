Delete from system_parameters where ParameterName = 'max.otp.trials';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','max.otp.trials','10','Maximum Otp trials allowed');

commit;


