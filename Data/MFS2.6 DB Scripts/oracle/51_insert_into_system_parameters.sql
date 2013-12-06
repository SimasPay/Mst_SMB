DELETE FROM system_parameters WHERE parametername='automatic.resend.otp';
INSERT INTO system_parameters (version, lastupdatetime, updatedby, createtime, createdby, parametername, parametervalue, description) VALUES (1,sysdate,'System',sysdate,'system','automatic.resend.otp','1','enables or disables to send automatic OTP');

commit;