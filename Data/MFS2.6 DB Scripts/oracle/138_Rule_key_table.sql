 
CREATE TABLE rule_key (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  ServiceID number(19,0) NOT NULL,
  TransactionTypeID number(19,0) NOT NULL,
  TxnRuleKey varchar2(255) NOT NULL,
  TxnRuleKeyType varchar2(255) NOT NULL,
  TxnRuleKeyPriority number(10,0) NOT NULL,
  TxnRuleKeyComparision varchar2(255) NOT NULL,
  CONSTRAINT FK_rulekey_serviceId FOREIGN KEY (ServiceID) REFERENCES service(ID),
  CONSTRAINT FK_rulekey_txnTypeId FOREIGN KEY (TransactionTypeID) REFERENCES transaction_type(ID)
 );
 
CREATE SEQUENCE  rule_key_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit; 