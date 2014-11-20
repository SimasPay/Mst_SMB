CREATE TABLE cashin_first_time (
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MDNID Number(19,0),
  MDN varchar2(255),
  SCTLID Number(19,0),
  TransactionAmount Number(25,4) DEFAULT 0
 );
 
CREATE SEQUENCE  cashin_first_time_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
 
ALTER TABLE subscriber_mdn ADD CashinFirstTimeID Number(19,0) DEFAULT NULL;



