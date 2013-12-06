CREATE TABLE transaction_identifier (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  TransactionIdentifier Varchar2(255) NOT NULL,
  ServiceChargeTransactionLogID NUMBER(19,0)
);

CREATE SEQUENCE  transaction_identifier_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

commit;