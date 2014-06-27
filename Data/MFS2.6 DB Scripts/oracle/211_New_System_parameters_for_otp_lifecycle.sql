Delete from system_parameters where ParameterName = 'resend.otp.block.duration.minutes';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','resend.otp.block.duration.minutes','30','Resend otp blocking duration in minutes');

Delete from system_parameters where ParameterName = 'absolute.lock.duration.hours';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','absolute.lock.duration.hours','3','Absolute locking duration in hours');

Delete from system_parameters where ParameterName = 'otp.timeout.duration.minutes';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','otp.timeout.duration.minutes','20','Otp timeout duration in minutes');

commit;


