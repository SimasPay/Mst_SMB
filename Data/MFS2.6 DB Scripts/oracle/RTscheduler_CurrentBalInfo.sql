Create Table current_balance_info 
(
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  CurrentBalance VARCHAR(255),
  SubscriberID BIGINT(20),
  KYCLevel BIGINT(20)
);
CREATE SEQUENCE  current_balance_info_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
