 
 CREATE TABLE actor_channel_mapping (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  IsAllowed number(3,0) NOT NULL,
  SubscriberType number(10,0) NOT NULL,
  BusinessPartnerType number(10,0) DEFAULT NULL,
  KYCLevel number(19,0) DEFAULT NULL,
  ServiceID number(19,0) NOT NULL,  
  TransactionTypeID number(19,0) NOT NULL,
  ChannelCodeID number(19,0) NOT NULL,
  GroupID number(10,0) DEFAULT NULL,  
  CONSTRAINT UNIQUE_acmapping_subscriber UNIQUE (SubscriberType, KYCLevel, ServiceID, TransactionTypeID, ChannelCodeID, GroupID),
  CONSTRAINT UNIQUE_acmapping_partner UNIQUE (BusinessPartnerType, ServiceID, TransactionTypeID, ChannelCodeID, GroupID),
  CONSTRAINT FK_acmapping_serviceid FOREIGN KEY (ServiceID) REFERENCES service(ID),
  CONSTRAINT FK_acmapping_transaction_type FOREIGN KEY (TransactionTypeID) REFERENCES transaction_type(ID),
  CONSTRAINT FK_acmapping_kyc_level FOREIGN KEY (KYCLevel) REFERENCES kyc_level(ID),
  CONSTRAINT FK_acmapping_channelcode FOREIGN KEY (ChannelCodeID) REFERENCES channel_code(ID),
  CONSTRAINT FK_acmapping_groupid FOREIGN KEY (GroupID) REFERENCES Groups(ID)
 );
 
CREATE SEQUENCE  actor_channel_mapping_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit; 
  
  
  