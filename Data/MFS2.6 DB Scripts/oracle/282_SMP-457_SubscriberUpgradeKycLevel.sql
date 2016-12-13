ALTER TABLE SUBSCRIBER_MDN ADD UPGRADEACCTREQUESTBY VARCHAR2(255);

DELETE FROM PERMISSION_ITEM WHERE PERMISSION = 10248;
DELETE FROM PERMISSION_ITEM WHERE PERMISSION = 10249;
Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10248', '1', 'sub.details.upgrade.kyc', 'default', 'default', '1', 'Upgrade eMoney Kyc');
Insert into PERMISSION_ITEM (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE, ITEMID, FIELDID, ACTION, PERMISSIONGROUPID, DESCRIPTION) values ('1', sysdate, 'system', sysdate, 'system', '10249', '1', 'sub.details.upgrade.kyc.checker', 'default', 'default', '1', 'Upgrade eMoney Kyc For Checker');

DELETE FROM notification WHERE ENUMCODE = 8460;
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'UpgradeKycStatusSearch', '8460', '0', 'All', 'All');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'UpgradeKycStatusSearch', '8460', '0', 'Initialized', 'Initialized');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'UpgradeKycStatusSearch', '8460', '1', 'Approved', 'Approved');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'UpgradeKycStatusSearch', '8460', '2', 'Rejected', 'Rejected');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'UpgradeKycStatusSearch', '8460', '3', 'Revision', 'Revision');

DELETE FROM notification WHERE ENUMCODE = 8459;
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'IDTypeForKycUpgrade', '8459', '1', 'Passport', 'Passport');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'IDTypeForKycUpgrade', '8459', '2', 'KTP', 'KTP');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'IDTypeForKycUpgrade', '8459', '3', 'SIM', 'SIM');

DELETE FROM notification WHERE ENUMCODE = 8461;
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'SubscriberUpgradeKycStatus', '8461', '0', 'Initialized', 'Initialized');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'SubscriberUpgradeKycStatus', '8461', '1', 'Approved', 'Approved');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'SubscriberUpgradeKycStatus', '8461', '2', 'Rejected', 'Rejected');
INSERT INTO ENUM_TEXT (VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, LANGUAGE, TAGNAME, TAGID, ENUMCODE, ENUMVALUE, DISPLAYTEXT) VALUES ('1', sysdate, 'system', sysdate, 'system', '0', 'SubscriberUpgradeKycStatus', '8461', '3', 'Revision', 'Revision');

DELETE FROM notification WHERE CODE = 2183;
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '1',  'Your Subscriber Upgrade Request Need Revision.', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '2',  'Your Subscriber Upgrade Request Need Revision.', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '4',  'Your Subscriber Upgrade Request Need Revision.', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '8',  'Your Subscriber Upgrade Request Need Revision.', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '16', 'Your Subscriber Upgrade Request Need Revision.', '0', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '1',  'Your Subscriber Upgrade Request Need Revision.', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '2',  'Your Subscriber Upgrade Request Need Revision.', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '4',  'Your Subscriber Upgrade Request Need Revision.', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '8',  'Your Subscriber Upgrade Request Need Revision.', '1', '0', sysdate, '1', '1');
INSERT INTO notification (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, Code, CodeName, NotificationMethod, Text, Language, Status, StatusTime, CompanyID, IsActive) VALUES ('0', sysdate, 'System', sysdate, 'System', '1', '2183', 'SubscriberUpgradeRequestRevision', '16', 'Your Subscriber Upgrade Request Need Revision.', '1', '0', sysdate, '1', '1');


DROP TABLE SUBSCRIBER_UPGRADE_DATA;

CREATE TABLE "SUBSCRIBER_UPGRADE_DATA" (   
	"ID" NUMBER(19,0) NOT NULL, 
	"FULLNAME" VARCHAR2(255BYTE) NOT NULL, 
	"EMAIL" VARCHAR2(255BYTE), 
	"IDTYPE" VARCHAR2(255BYTE), 
	"IDNUMBER" VARCHAR2(255BYTE), 
	"BIRTH_PLACE" VARCHAR2(255BYTE), 
	"BIRTH_DATE" TIMESTAMP NULL, 
	"MOTHER_MAIDEN_NAME" VARCHAR2(255BYTE), 
	"IDCARD_SCAN_PATH" VARCHAR2(255BYTE), 
	"ADDRESS_ID" NUMBER(19,0), 
	"MDNID" NUMBER(19,0) NOT NULL, 
	"VERSION" NUMBER(10,0) NOT NULL, 
	"LASTUPDATETIME" TIMESTAMP NOT NULL, 
	"UPDATEDBY" VARCHAR2(255BYTE) NOT NULL, 
	"CREATETIME" TIMESTAMP NOT NULL, 
	"CREATEDBY" VARCHAR2(255BYTE) NOT NULL
	);

ALTER TABLE SUBSCRIBER_UPGRADE_DATA ADD CONSTRAINT "PK_SUBSCRIBER_UPGRADE_DATA" PRIMARY KEY("ID");

CREATE SEQUENCE SUBSCRIBER_UPGRADE_DATA_ID_SEQ  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;

CREATE OR REPLACE TRIGGER SUBSCRIBER_UPGRADE_DATA_ID_TRG BEFORE INSERT ON SUBSCRIBER_UPGRADE_DATA
FOR EACH ROW
DECLARE
v_newVal NUMBER(12) := 0;
v_incval NUMBER(12) := 0;
BEGIN
  IF INSERTING AND :new.ID IS NULL THEN
    SELECT  SUBSCRIBER_UPGRADE_DATA_ID_SEQ.NEXTVAL INTO v_newVal FROM DUAL;
    IF v_newVal = 1 THEN
      SELECT NVL(max(ID),0) INTO v_newVal FROM subscriber;
      v_newVal := v_newVal + 1;
      LOOP
           EXIT WHEN v_incval>=v_newVal;
           SELECT SUBSCRIBER_UPGRADE_DATA_ID_SEQ.nextval INTO v_incval FROM dual;
      END LOOP;
    END IF;
   :new.ID := v_newVal;
  END IF;
END;

commit;
