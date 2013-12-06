
DELETE FROM system_parameters WHERE ParameterName='otp.timeout.duration';

INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','otp.timeout.duration','24','otp timeout duration in hours');

COMMIT;

