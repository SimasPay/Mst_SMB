CREATE TABLE mfs_ledger (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  SctlId number(19,0) NOT NULL,
  CommodityTransferID number(19,0) NOT NULL,
  PocketID number(19,0) NOT NULL,
  Amount number(25,4) NOT NULL,
  LedgerType varchar2(25) NOT NULL,
  LedgerStatus varchar2(25) NOT NULL
 );
 
CREATE SEQUENCE mfs_ledger_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1 NOCYCLE ; 
  
commit; 