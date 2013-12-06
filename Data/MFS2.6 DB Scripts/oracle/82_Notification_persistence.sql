
CREATE TABLE notification_log (
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
       SctlID Number(19,0) NOT NULL,
       Code Number(11,0) NOT NULL,
       Text varchar2(255) DEFAULT NULL
);

CREATE SEQUENCE  notification_log_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
  
commit;  