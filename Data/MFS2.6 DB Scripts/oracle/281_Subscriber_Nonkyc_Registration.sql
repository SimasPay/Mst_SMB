
INSERT INTO KYC_LEVEL VALUES (4,1,SYSDATE,'System',SYSDATE,'System',0,'NoKyc',NULL,NULL);

INSERT INTO POCKET_TEMPLATE (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,TYPE,BANKACCOUNTCARDTYPE,DESCRIPTION,COMMODITY,CARDPANSUFFIXLENGTH,UNITS,ALLOWANCE,MAXIMUMSTOREDVALUE,MINIMUMSTOREDVALUE,MAXAMOUNTPERTRANSACTION,MINAMOUNTPERTRANSACTION,MAXAMOUNTPERDAY,MAXAMOUNTPERWEEK,MAXAMOUNTPERMONTH,MAXTRANSACTIONSPERDAY,MAXTRANSACTIONSPERWEEK,MAXTRANSACTIONSPERMONTH,MINTIMEBETWEENTRANSACTIONS,BANKCODE,OPERATORCODE,BILLINGTYPE,WEBTIMEINTERVAL,WEBSERVICETIMEINTERVAL,UTKTIMEINTERVAL,BANKCHANNELTIMEINTERVAL,DENOMINATION,MAXUNITS,POCKETCODE,TYPEOFCHECK,REGULAREXPRESSION,ISCOLLECTORPOCKET,NUMBEROFPOCKETSALLOWEDFORMDN) VALUES 
(1,SYSDATE,'system',SYSDATE,'system',1,1,1,'Emoney-NoKyc',4,1,NULL,0,'1000000.0000','0.0000','1000000.0000','0.0000','1000000.0000','100000000.0000','1000000000.0000',1000,100000,10000000,0,9999,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'14',0,'',0,1);

INSERT INTO POCKET_TEMPLATE_CONFIG (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SUBSCRIBERTYPE,BUSINESSPARTNERTYPE,KYCLEVEL,COMMODITY,POCKETTYPE,ISSUSPENCEPOCKET,ISCOLLECTORPOCKET,POCKETTEMPLATEID,ISDEFAULT) VALUES (1,SYSDATE,'system',SYSDATE,'system',0,NULL,4,4,1,0,0,(SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='Emoney-NoKyc'),1);

INSERT INTO PTC_GROUP_MAPPING (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, GROUPID, PTCID ) VALUES (1,SYSDATE,'System',SYSDATE,'System',1,(SELECT ID FROM POCKET_TEMPLATE_CONFIG WHERE POCKETTEMPLATEID = (SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='Emoney-NoKyc')));

INSERT INTO TRANSACTION_TYPE VALUES (TRANSACTION_TYPE_ID_SEQ.NEXTVAL,1,SYSDATE,'System',SYSDATE,'System',1,'SubscriberRegistration','Subscriber Registration');

DELETE FROM notification WHERE CODE = 2180;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '1', 'Congratulations, Registration successful.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '2', 'Congratulations, Registration successful.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '4', 'Congratulations, Registration successful.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '8', 'Congratulations, Registration successful.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '16', 'Congratulations, Registration successful.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '1', 'Congratulations, Registration successful.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '2', 'Congratulations, Registration successful.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '4', 'Congratulations, Registration successful.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '8', 'Congratulations, Registration successful.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2180', 'NonKycSubscriberSuccessfullyRegistered', '16', 'Congratulations, Registration successful.', '1', '0', sysdate, '1', '1');

DELETE FROM notification WHERE CODE = 2181;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '1', 'Registration Failed.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '2', 'Registration Failed.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '4', 'Registration Failed.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '8', 'Registration Failed.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '16', 'Registration Failed.', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '1', 'Registration Failed.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '2', 'Registration Failed.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '4', 'Registration Failed.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '8', 'Registration Failed.', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2181', 'NonKycSubscriberRegisteredFailed', '16', 'Registration Failed.', '1', '0', sysdate, '1', '1');

COMMIT;