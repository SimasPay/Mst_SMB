
INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) VALUES (system_parameters_ID_SEQ.NEXTVAL, '1', sysdate, 'System', sysdate, 'System', 'minimum.ios.app.version', '7.9.1', 'App Version Minimum for iOs');

INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) VALUES (system_parameters_ID_SEQ.NEXTVAL, '1', sysdate, 'System', sysdate, 'System', 'minimum.android.app.version', '7.9.1', 'App Version Minimum for Android');

INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) VALUES (system_parameters_ID_SEQ.NEXTVAL, '1', sysdate, 'System', sysdate, 'System', 'ios.app.url', 'https://itunes.apple.com/id/app/simobi/id807937634?l=id&mt=8', 'App URL for Upgrade iOs');

INSERT INTO SYSTEM_PARAMETERS (ID, VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PARAMETERNAME, PARAMETERVALUE, DESCRIPTION) VALUES (system_parameters_ID_SEQ.NEXTVAL, '1', sysdate, 'System', sysdate, 'System', 'android.app.url', 'https://play.google.com/store/apps/details?id=com.mfino.bsim&hl=in', 'App URL for Upgrade Android');

DELETE FROM notification WHERE CODE = 2309;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '1', 'The App version is updated', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '2', 'The App version is updated', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '4', 'The App version is updated', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '8', 'The App version is updated', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '16', 'The App version is updated', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '1', 'The App version is updated', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '2', 'The App version is updated', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '4', 'The App version is updated', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '8', 'The App version is updated', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2309', 'NotRequiredForceUpgradeApp', '16', 'The App version is updated', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2310;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '1', 'The App version is need to be upgrade', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '2', 'The App version is need to be upgrade', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '4', 'The App version is need to be upgrade', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '8', 'The App version is need to be upgrade', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '16', 'The App version is need to be upgrade', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '1', 'The App version is need to be upgrade', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '2', 'The App version is need to be upgrade', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '4', 'The App version is need to be upgrade', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '8', 'The App version is need to be upgrade', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2310', 'RequiredForceUpgradeApp', '16', 'The App version is need to be upgrade', '1', '0', sysdate, '1', '1');

COMMIT;