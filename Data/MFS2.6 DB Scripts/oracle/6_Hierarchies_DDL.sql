ALTER TABLE distribution_chain_temp ADD (ServiceID Number(10,0) NOT NULL);

ALTER TABLE distribution_chain_lvl ADD (TransactionTypeID Number(10,0));

CREATE TABLE  dct_restrictions (
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  DCTID Number(10,0) NOT NULL,
  TransactionTypeID Number(10,0),
  RelationShipType Number(10,0),
  DistributionLevel Number(10,0),
  IsAllowed Number(3,0)
);

CREATE TABLE  partner_restrictions (
  ID Number(19,0) NOT NULL PRIMARY KEY,
  Version Number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  DCTID Number(10,0) NOT NULL,
  PartnerID Number(10,0) NOT NULL,
  TransactionTypeID Number(10,0),
  RelationShipType Number(10,0),
  IsAllowed Number(3,0),
  IsValid Number(3,0) DEFAULT 1
);

alter table dct_restrictions add constraint dct_rest_uq unique(DCTID, TransactionTypeID, RelationShipType, DistributionLevel);

alter table partner_restrictions add constraint partner_rest_uq unique(DCTID, PartnerID, TransactionTypeID, RelationShipType);

CREATE SEQUENCE  dct_restrictions_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;
  
CREATE SEQUENCE  partner_restrictions_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ;  
 
 commit;