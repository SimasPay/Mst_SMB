ALTER TABLE "SUBSCRIBER_MDN" ADD "ACTIVATIONWRONGOTPCOUNT" NUMERIC(3,0);

Delete from notification where code = 2186;
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2186,'ActivationBlocked',4,'OTP anda terblokir, silahkan hubungi kantor cabang Bank Sinarmas terdekat untuk mendapatkan OTP baru',null,1,0,sysdate,null,null,1);
INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,2186,'ActivationBlocked',4,'Your OTP is blocked, please visit nearest Bank Sinarmas branch to get new OTP',null,0,0,sysdate,null,null,1);


update subscriber_mdn set ISMIGRATEABLETOSIMOBIPLUS = 0 where mdn in ('628812341456', '628158811429', '628811119696', '628812340707', '628812344291', '628815003999', '6287878521401R0', '628812352007R0', '62816900389', '6287878287168', '628159171722', '6281280992162', '628159118988', '628812340001', '628811122888R0', '6289686683322', '628116136613', '62811992200', '6281808119801', '628118835500', '6287882121822', '628179007711', '628812342495', '6285716246446');

delete from system_parameters where parametername = 'android.subapp.minvalidversion';
delete from system_parameters where parametername = '.subapp.minvalidversion';
delete from system_parameters where parametername = 'ios.subapp.minvalidversion';
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','android.subapp.minvalidversion','4','Min Android version');
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','.subapp.minvalidversion','2.0','Min old IOS app version');
INSERT INTO system_parameters (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, ParameterName, ParameterValue, Description) VALUES (1,sysdate,'System',sysdate,'system','ios.subapp.minvalidversion','2.0','Min IOS version');
