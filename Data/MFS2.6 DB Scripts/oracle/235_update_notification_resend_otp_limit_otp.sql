Delete from notification where code = 655;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',1,'Dear Customer, your new otp is $(OneTimePin)',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',1,'Dear Customer, your new otp is $(OneTimePin)',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',2,'Dear Customer, your new otp is $(OneTimePin)',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',2,'Dear Customer, your new otp is $(OneTimePin)',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',4,'Pastikan nomor handphone anda sudah benar. Kami telah mengirimkan OTP kembali',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',4,'Please make sure the phone number is correct. The OTP code has been resend to your number',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',8,'Pastikan nomor handphone anda sudah benar. Kami telah mengirimkan OTP kembali',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',8,'Please make sure the phone number is correct. The OTP code has been resend to your number',null,0,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',16,'Pastikan nomor handphone anda sudah benar. Kami telah mengirimkan OTP kembali',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,655,'New_OTP_Success',16,'Please make sure the phone number is correct. The OTP code has been resend to your number',null,0,0,sysdate,null,null,1);

Delete from notification where code = 2186;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2186,'ActivationBlocked',4,'Nomor handphone anda telah diblokir. Silahkan hubungi Bank Sinarmas untuk melakukan registrasi ulang',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2186,'ActivationBlocked',4,'Your number has been blocked. Please contact Bank Sinarmas and register again',null,0,0,sysdate,null,null,1);

Delete from notification where code = 2187;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2187,'ExceedMaxResendOTP',4,'Anda telah 3 kali meminta Resend OTP. Silahkan hubungi Bank Sinarmas untuk melakukan registrasi ulang',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2187,'ExceedMaxResendOTP',4,'You have reached the max 3 attempts for OTP request. Please contact Bank Sinarmas and register again',null,0,0,sysdate,null,null,1);


Delete from notification where code = 2188;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2188,'InvalidPhoneNumberOrOTP',4,'Nomor handphone atau OTP yang anda masukkan salah. Silahkan pastikan dan coba kembali',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2188,'InvalidPhoneNumberOrOTP',4,'Invalid phone number or OTP code. Please try again.',null,0,0,sysdate,null,null,1);


Delete from system_parameters where ParameterName = 'max.otp.trials';
insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values (1, sysdate, 'System', sysdate, 'system', 'max.otp.trials', '3', 'Max otp trials');
Delete from system_parameters where ParameterName = 'show.migrate.to.simobiplus.event';
insert into system_parameters(Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) values (1, sysdate, 'System', sysdate, 'system', 'show.migrate.to.simobiplus.event', 'false', 'Show Migrate to Simobi Plus Dialog');
commit;