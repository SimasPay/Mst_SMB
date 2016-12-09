INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'UpdateProfile','Update Profile');

INSERT INTO SERVICE_TRANSACTION(VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,SERVICEID,TRANSACTIONTYPEID)  VALUES (1,SYSDATE,'System',SYSDATE,'System',1, (SELECT ID FROM SERVICE WHERE SERVICENAME = 'Account'), (SELECT ID FROM TRANSACTION_TYPE WHERE TRANSACTIONNAME = 'UpdateProfile'));

Alter table subscriber_mdn add  ProfileImagePath varchar2(255 char) DEFAULT NULL;

DELETE FROM NOTIFICATION WHERE CODE = 2143;

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '1', 'Your profile image has been successfully updated.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '2', 'Your profile image has been successfully updated.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '4', 'Your profile image has been successfully updated.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '8', 'Your profile image has been successfully updated.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '16', 'Your profile image has been successfully updated.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '1', 'Your profile image has been successfully updated.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '2', 'Your profile image has been successfully updated.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '4', 'Your profile image has been successfully updated.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '8', 'Your profile image has been successfully updated.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2143', 'UpdateProfileCompleted', '16', 'Your profile image has been successfully updated.', '1', '0', SYSDATE, '1', '1');


DELETE FROM NOTIFICATION WHERE CODE = 2144;

INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '1', 'Your profile update has failed.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '2', 'Your profile update has failed.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '4', 'Your profile update has failed.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '8', 'Your profile update has failed.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '16', 'Your profile update has failed.', '0', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '1', 'Your profile update has failed.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '2', 'Your profile update has failed.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '4', 'Your profile update has failed.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '8', 'Your profile update has failed.', '1', '0', SYSDATE, '1', '1');
INSERT INTO NOTIFICATION (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, MSPID, CODE, CODENAME, NOTIFICATIONMETHOD, TEXT, LANGUAGE, STATUS, STATUSTIME, COMPANYID, ISACTIVE) VALUES ('0', SYSDATE, 'System', SYSDATE, 'System', '1', '2144', 'UpdateProfileFailed', '16', 'Your profile update has failed.', '1', '0', SYSDATE, '1', '1');

commit;