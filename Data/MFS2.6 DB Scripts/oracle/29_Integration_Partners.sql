
ALTER TABLE integration_partner_map ADD IntegrationName VARCHAR2(255 CHAR);

ALTER TABLE integration_partner_map ADD AuthenticationKey VARCHAR2(255 CHAR);

ALTER TABLE integration_partner_map ADD IsAuthenticationKeyEnabled NUMBER(3,0) DEFAULT '0';

ALTER TABLE integration_partner_map ADD IsLoginEnabled NUMBER(3,0) DEFAULT '0';

DROP TABLE ip_mapping CASCADE CONSTRAINTS;

CREATE TABLE ip_mapping (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  IntegrationID NUMBER(10,0) NOT NULL,
  IPAddress VARCHAR2(255 CHAR) NOT NULL,
  CONSTRAINT FK_Integration_ID
  FOREIGN KEY (IntegrationID)
  REFERENCES integration_partner_map(ID)
);

PROMPT Creating Primary Key Constraint ip_mapping_pk on table ip_mapping ... 
ALTER TABLE ip_mapping
ADD CONSTRAINT ip_mapping_pk PRIMARY KEY
(
  ID
)
ENABLE
;

CREATE SEQUENCE  ip_mapping_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 19101,1,'integrations','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','19101');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 19102,1,'integrations.view','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','19102');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 19103,1,'integrations.add','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','19103');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 19104,1,'integrations.edit','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','19104');

INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action) VALUES('1', sysdate, 'system', sysdate, 'system', 19105,1,'integrations.delete','default','default');

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES('1', sysdate, 'system', sysdate, 'system', '1','19105');



delete from notification where code=751;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,751,'AuthenticationKeyForIntegration',1,'Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,751,'AuthenticationKeyForIntegration',2,'Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,751,'AuthenticationKeyForIntegration',4,'Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,751,'AuthenticationKeyForIntegration',8,'Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,751,'AuthenticationKeyForIntegration',16,'Authentication Key for Integration $(IntegrationName) is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);


delete from notification where code=752;

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,752,'ResetAuthenticationKeyForIntegration',1,'Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,752,'ResetAuthenticationKeyForIntegration',2,'Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,752,'ResetAuthenticationKeyForIntegration',4,'Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,752,'ResetAuthenticationKeyForIntegration',8,'Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

INSERT INTO notification (LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Version,MSPID,Code,CodeName,NotificationMethod,Text,STKML,Language,Status,StatusTime,AccessCode,SMSNotificationCode,CompanyID) VALUES (sysdate,'System',sysdate,'System',0,1,752,'ResetAuthenticationKeyForIntegration',16,'Authentication Key for Integration $(IntegrationName) is reset. New Authentication key is $(AuthenticationKey)',null,0,0,sysdate,null,null,1);

commit;
