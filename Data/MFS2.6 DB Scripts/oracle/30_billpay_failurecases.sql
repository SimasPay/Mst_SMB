
alter table service_charge_txn_log add (IntegrationCode varchar2(255));

create table auto_reversals (
  ID Number(20) primary key,
  Version Number(11) not null,
  LastUpdateTime Timestamp not null,
  UpdatedBy varchar2(255) not null,
  CreateTime Timestamp not null,
  CreatedBy varchar2(255) not null,
  SctlID Number(20) not null,
  SourcePocketID Number(20) not null,
  DestPocketID Number(20) not null,
  AutoRevStatus Number(11) not null,
  Amount Number(25,4) ,
  Charges Number(25,4)
);

CREATE SEQUENCE  auto_reversals_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 


update notification set text='Your request to top up $(ReceiverMDN) from $(BillerCode) with $(Currency) $(Amount) failed, Your amount will be reverted in 24 hours, REF: $(TransferID)' where code = 714;

COMMIT;