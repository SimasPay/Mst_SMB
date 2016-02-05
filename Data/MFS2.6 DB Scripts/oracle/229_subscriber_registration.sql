CREATE TABLE "KTP_DETAILS" 
   ("ID" NUMBER(19,0) NOT NULL ENABLE, 
	"VERSION" NUMBER(10,0) NOT NULL ENABLE, 
	"LASTUPDATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
	"UPDATEDBY" VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL ENABLE, 
	"CREATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
	"CREATEDBY" VARCHAR2(255 CHAR) NOT NULL ENABLE, 
	"MDN" VARCHAR2(255 CHAR) NOT NULL ENABLE, 
	"KTPID" VARCHAR2(255 CHAR), 
	"FULLNAME" VARCHAR2(255 CHAR),
	"DATEOFBIRTH" TIMESTAMP (0),
	"AGENTMDN" VARCHAR2(255 CHAR),
	"BANKRESPONSESTATUS" VARCHAR2(255),
	"BANKRESPONSE" VARCHAR2(1000 CHAR),
	 CONSTRAINT "PRIMARY_KTP1" PRIMARY KEY ("ID")
   );

DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE ktp_details_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM KTP_DETAILS;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/

ALTER TABLE SUBSCRIBER_MDN ADD  (ISIDLIFETIME VARCHAR2(2)) ;

ALTER TABLE SUBSCRIBER_ADDI_INFO ADD (WORK VARCHAR2(100), INCOME NUMBER(19,0), GOALOFACCTOPENING VARCHAR2(100), SOURCEOFFUND VARCHAR2(100));

ALTER TABLE SUBSCRIBER ADD (SUBSCRIBERADDRESSKTPID NUMBER (19,0), MOTHERSMAIDENNAME VARCHAR2(255 CHAR) DEFAULT NULL);

ALTER TABLE SUBSCRIBER ADD CONSTRAINT FK_SUB_ADDR_KTP_BY_SUB FOREIGN KEY( SUBSCRIBERADDRESSKTPID) REFERENCES ADDRESS (ID) ENABLE;

ALTER TABLE SUBSCRIBER_MDN ADD  (DOMADDRIDENTITY VARCHAR2(2)) ;

ALTER TABLE SUBSCRIBER_MDN ADD  (KTPDOCUMENTPATH VARCHAR2(255), SUBSCRIBERFORMPATH VARCHAR2(255), SUPPORTINGDOCUMENTPATH VARCHAR2(255)) ;

INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) 
VALUES (1,SYSDATE,'system',SYSDATE,'system',0,'PocketType',5049,'6','LakuPandai','Laku Pandai');

INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) 
VALUES (1,SYSDATE,'system',SYSDATE,'system',0,'LakuPandaiAccountType',8194,'1','BSA','Basic Saving Account');

INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) 
VALUES (1,SYSDATE,'system',SYSDATE,'system',0,'LakuPandaiAccountType',8194,'2','ASA','Advanced Saving Account');

INSERT INTO POCKET_TEMPLATE (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,TYPE,BANKACCOUNTCARDTYPE,DESCRIPTION,COMMODITY,CARDPANSUFFIXLENGTH,UNITS,ALLOWANCE,MAXIMUMSTOREDVALUE,MINIMUMSTOREDVALUE,MAXAMOUNTPERTRANSACTION,MINAMOUNTPERTRANSACTION,MAXAMOUNTPERDAY,MAXAMOUNTPERWEEK,MAXAMOUNTPERMONTH,MAXTRANSACTIONSPERDAY,MAXTRANSACTIONSPERWEEK,MAXTRANSACTIONSPERMONTH,MINTIMEBETWEENTRANSACTIONS,BANKCODE,OPERATORCODE,BILLINGTYPE,WEBTIMEINTERVAL,WEBSERVICETIMEINTERVAL,UTKTIMEINTERVAL,BANKCHANNELTIMEINTERVAL,DENOMINATION,MAXUNITS,POCKETCODE,TYPEOFCHECK,REGULAREXPRESSION,ISCOLLECTORPOCKET,NUMBEROFPOCKETSALLOWEDFORMDN) VALUES 
(1,SYSDATE,'system',SYSDATE,'system',1,6,1,'LakuPandaiBasicTemplate',4,6,NULL,0,'1000000.0000','0.0000','1000000.0000','0.0000','1000000.0000','100000000.0000','1000000000.0000',1000,100000,10000000,0,9998,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'8',0,'',0,1);

INSERT INTO POCKET_TEMPLATE_CONFIG (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SUBSCRIBERTYPE,BUSINESSPARTNERTYPE,KYCLEVEL,COMMODITY,POCKETTYPE,ISSUSPENCEPOCKET,ISCOLLECTORPOCKET,POCKETTEMPLATEID,ISDEFAULT) VALUES (1,SYSDATE,'system',SYSDATE,'system',0,NULL,1,4,6,0,0,(SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiBasicTemplate'),1);

INSERT INTO PTC_GROUP_MAPPING (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, GROUPID, PTCID ) VALUES (1,SYSDATE,'System',SYSDATE,'System',1,(SELECT ID FROM POCKET_TEMPLATE_CONFIG WHERE POCKETTEMPLATEID = (SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiBasicTemplate')));

INSERT INTO POCKET_TEMPLATE (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,MSPID,TYPE,BANKACCOUNTCARDTYPE,DESCRIPTION,COMMODITY,CARDPANSUFFIXLENGTH,UNITS,ALLOWANCE,MAXIMUMSTOREDVALUE,MINIMUMSTOREDVALUE,MAXAMOUNTPERTRANSACTION,MINAMOUNTPERTRANSACTION,MAXAMOUNTPERDAY,MAXAMOUNTPERWEEK,MAXAMOUNTPERMONTH,MAXTRANSACTIONSPERDAY,MAXTRANSACTIONSPERWEEK,MAXTRANSACTIONSPERMONTH,MINTIMEBETWEENTRANSACTIONS,BANKCODE,OPERATORCODE,BILLINGTYPE,WEBTIMEINTERVAL,WEBSERVICETIMEINTERVAL,UTKTIMEINTERVAL,BANKCHANNELTIMEINTERVAL,DENOMINATION,MAXUNITS,POCKETCODE,TYPEOFCHECK,REGULAREXPRESSION,ISCOLLECTORPOCKET,NUMBEROFPOCKETSALLOWEDFORMDN) VALUES 
(1,SYSDATE,'system',SYSDATE,'system',1,6,1,'LakuPandaiAdvancedTemplate',4,6,NULL,0,'1000000.0000','0.0000','1000000.0000','0.0000','1000000.0000','100000000.0000','1000000000.0000',1000,100000,10000000,0,9998,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'8',0,'',0,1);

INSERT INTO POCKET_TEMPLATE_CONFIG (VERSION,LASTUPDATETIME,UPDATEDBY,CREATETIME,CREATEDBY,SUBSCRIBERTYPE,BUSINESSPARTNERTYPE,KYCLEVEL,COMMODITY,POCKETTYPE,ISSUSPENCEPOCKET,ISCOLLECTORPOCKET,POCKETTEMPLATEID,ISDEFAULT) VALUES (1,SYSDATE,'system',SYSDATE,'system',0,NULL,3,4,6,0,0,(SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiAdvancedTemplate'),1);

INSERT INTO PTC_GROUP_MAPPING (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, GROUPID, PTCID ) VALUES (1,SYSDATE,'System',SYSDATE,'System',1,(SELECT ID FROM POCKET_TEMPLATE_CONFIG WHERE POCKETTEMPLATEID = (SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiAdvancedTemplate')));

ALTER TABLE ADDRESS ADD  (RW VARCHAR2(255), RT VARCHAR2(255), SUBSTATE VARCHAR2(255));

DELETE FROM notification WHERE CODE = 2126;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '1', 'Subscriber KTP Validation Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '2', 'Subscriber KTP Validation Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '4', 'Subscriber KTP Validation Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '8', 'Subscriber KTP Validation Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '16', 'Subscriber KTP Validation Success', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '1', 'Subscriber KTP Validation Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '2', 'Subscriber KTP Validation Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '4', 'Subscriber KTP Validation Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '8', 'Subscriber KTP Validation Success', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2126', 'SubscriberKtpValdiationSuccess', '16', 'Subscriber KTP Validation Success', '1', '0', sysdate, '1', '1');


DELETE FROM notification WHERE CODE = 2127;

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '1', 'Subscriber KTP Validation Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '2', 'Subscriber KTP Validation Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '4', 'Subscriber KTP Validation Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '8', 'Subscriber KTP Validation Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '16', 'Subscriber KTP Validation Failed', '0', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '1', 'Subscriber KTP Validation Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '2', 'Subscriber KTP Validation Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '4', 'Subscriber KTP Validation Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '8', 'Subscriber KTP Validation Failed', '1', '0', sysdate, '1', '1');

INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2127', 'SubscriberKtpValdiationFailed', '16', 'Subscriber KTP Validation Failed', '1', '0', sysdate, '1', '1');

UPDATE POCKET_TEMPLATE SET BANKCODE = 9999 WHERE ID = (SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiBasicTemplate');

UPDATE POCKET_TEMPLATE SET BANKCODE = 9999 WHERE ID = (SELECT ID FROM POCKET_TEMPLATE WHERE DESCRIPTION='LakuPandaiAdvancedTemplate');

COMMIT;
