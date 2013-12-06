CREATE TABLE expiration_type (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  ExpiryType NUMBER(10,0),
  ExpiryMode NUMBER(10,0),
  ExpiryValue NUMBER(19,0),
  ExpiryDescription varchar2(255)
);
CREATE SEQUENCE  expiration_type_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
CREATE TABLE purpose (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  Category NUMBER(10,0),
  Code varchar2(255)
);
CREATE SEQUENCE  purpose_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
CREATE TABLE fund_events (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  FundEventType NUMBER(10,0),
  FundEventDescription varchar2(255)
);
CREATE SEQUENCE  fund_events_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
CREATE TABLE fund_definition (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  PurposeID NUMBER(19,0),
  FACLength NUMBER(10,0),
  FACPrefix varchar2(255),
  ExpiryID NUMBER(19,0),
  MaxFailAttemptsAllowed NUMBER(10,0),
  OnFundAllocationTimeExpiry NUMBER(19,0),
  OnFailedAttemptsExceeded NUMBER(19,0),
  GenerationOfOTPOnFailure NUMBER(19,0),
  IsMultipleWithdrawalAllowed NUMBER(3,0)
);
CREATE SEQUENCE  fund_definition_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
ALTER TABLE fund_definition
ADD CONSTRAINT FK_fundDef_fundEvent_expiry FOREIGN KEY
(
  OnFundAllocationTimeExpiry
)
REFERENCES fund_events
(
  ID
)
ENABLE
;
ALTER TABLE fund_definition
ADD CONSTRAINT FK_fundDef_fundEvent_failExd FOREIGN KEY
(
  OnFailedAttemptsExceeded
)
REFERENCES fund_events
(
  ID
)
ENABLE
;
ALTER TABLE fund_definition
ADD CONSTRAINT FK_fundDef_fundEvent_otpGen FOREIGN KEY
(
  GenerationOfOTPOnFailure
)
REFERENCES fund_events
(
  ID
)
ENABLE
;
ALTER TABLE fund_definition
ADD CONSTRAINT FK_fundDef_expType_expID FOREIGN KEY
(
  ExpiryID
)
REFERENCES expiration_type
(
  ID
)
ENABLE
;
CREATE TABLE fund_distribution_info (
  ID NUMBER(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  MSPID NUMBER(19,0) NOT NULL,
  FundAllocationId NUMBER(19,0),
  DistributedAmount NUMBER(25,4),
  DistributionStatus Number(10,0),
  FailureReason varchar2(255),
  FailureReasonCode NUMBER(10,0),
  TransferSCTLId NUMBER(19,0),
  TransferCTId NUMBER(19,0),
  DistributionType NUMBER(10,0)
);
ALTER TABLE fund_distribution_info
ADD CONSTRAINT FK_fundDist_UnRegTrxn_fAlloc FOREIGN KEY
(
  FundAllocationId
)
REFERENCES unregistered_txn_info
(
  ID
)
ENABLE
;
CREATE SEQUENCE  fund_distribution_info_ID_SEQ MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;

ALTER TABLE unregistered_txn_info ADD FailureReasonCode NUMBER(10,0) ;
ALTER TABLE unregistered_txn_info ADD ReversalReason varchar2(255);
ALTER TABLE unregistered_txn_info ADD ExpiryTime TIMESTAMP ;
ALTER TABLE unregistered_txn_info ADD FundDefinitionID NUMBER(19,0) ;
ALTER TABLE unregistered_txn_info ADD AvailableAmount NUMBER(25,4);
ALTER TABLE unregistered_txn_info ADD WithdrawalMDN varchar2(255);
ALTER TABLE unregistered_txn_info ADD WithdrawalFailureAttempt NUMBER(10,0) ;
ALTER TABLE unregistered_txn_info ADD PartnerCode varchar2(255);
ALTER TABLE unregistered_txn_info
ADD CONSTRAINT FK_UnRegTrxn_fundDef_fDef FOREIGN KEY
(
  FundDefinitionID
)
REFERENCES fund_definition
(
  ID
)
ENABLE
;

commit;





