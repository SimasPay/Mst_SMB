
CREATE TABLE adjustments (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  SourcePocketID number(19,0) NOT NULL,
  DestPocketID number(19,0) NOT NULL,
  Amount number(25,4) NOT NULL,
  SctlId number(19,0) NOT NULL,
  AdjustmentStatus number(10,0) NOT NULL,
  ApproveOrRejectTime TIMESTAMP(0),
  ApprovedOrRejectedBy VARCHAR2(255 CHAR),
  ApproveOrRejectComment VARCHAR2(255 CHAR),
  AppliedBy VARCHAR2(255 CHAR),
  AppliedTime TIMESTAMP(0),
  CONSTRAINT FK_Adjustments_SourcePocketID FOREIGN KEY (SourcePocketID) REFERENCES pocket (ID),
  CONSTRAINT FK_Adjustments_DestPocketID FOREIGN KEY (DestPocketID) REFERENCES pocket (ID),
  CONSTRAINT FK_Adjustments_SctlId FOREIGN KEY (SctlId) REFERENCES service_charge_txn_log (ID)
 );
 
CREATE SEQUENCE  adjustments_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit; 