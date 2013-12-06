CREATE TABLE audit_log (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MessageName varchar2(255) NOT NULL,
  FixMessage CLOB NOT NULL,
  JSaction varchar2(255) NOT NULL
);

CREATE SEQUENCE  audit_log_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
  
commit;