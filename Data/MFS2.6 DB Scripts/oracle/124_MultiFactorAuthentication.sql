DROP TABLE mfa_transactions_info CASCADE CONSTRAINTS;
CREATE TABLE mfa_transactions_info (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  ServiceID NUMBER(19,0) NOT NULL,
  TransactionTypeID NUMBER(19,0) NOT NULL,
  ChannelCodeID NUMBER(19,0) NOT NULL,
  MFAMode NUMBER(10,0) NOT NULL,  
  CONSTRAINT FK_MFATxnInfo_Service FOREIGN KEY (ServiceID) REFERENCES service(ID),
  CONSTRAINT FK_MFATxnInfo_TransactionType FOREIGN KEY (TransactionTypeID) REFERENCES transaction_type(ID),
  CONSTRAINT FK_MFATxnInfo_ChannelCode FOREIGN KEY (ChannelCodeID) REFERENCES channel_code(ID)
  );

PROMPT Creating Primary Key Constraint mfa_transactions_info_pk on table mfa_transactions_info ... 
ALTER TABLE mfa_transactions_info
ADD CONSTRAINT mfa_transactions_info_pk PRIMARY KEY
(
  ID
)
ENABLE
;

DROP SEQUENCE  mfa_transactions_info_ID_SEQ;
CREATE SEQUENCE  mfa_transactions_info_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

Delete From enum_text where TagID =8105; 
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MFAMode',8105,'0','None','None');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MFAMode',8105,'1','OTP','OTP');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','MFAMode',8105,'2','SecurityQuestion','SecurityQuestion');

DROP TABLE mfa_authentication CASCADE CONSTRAINTS;
CREATE TABLE mfa_authentication (
  ID NUMBER(19,0) NOT NULL,
  Version NUMBER(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP(0) NOT NULL,
  UpdatedBy VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL,
  CreateTime TIMESTAMP(0) NOT NULL,
  CreatedBy VARCHAR2(255 CHAR) NOT NULL,
  SctlId NUMBER(19,0) NOT NULL,
  MFAMode NUMBER(10,0) NOT NULL,
  MFAValue VARCHAR2(255 CHAR) NOT NULL
  );

PROMPT Creating Primary Key Constraint mfa_authentication_pk on table mfa_authentication ...
ALTER TABLE mfa_authentication
ADD CONSTRAINT mfa_authentication_pk PRIMARY KEY
(
  ID
)
ENABLE
;

DROP SEQUENCE  mfa_authentication_ID_SEQ;
CREATE SEQUENCE  mfa_authentication_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 

commit;
