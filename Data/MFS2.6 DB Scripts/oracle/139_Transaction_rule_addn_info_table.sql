  
CREATE TABLE txn_rule_addn_info (
  ID number(19,0) NOT NULL PRIMARY KEY,
  Version number(10,0) NOT NULL,
  LastUpdateTime TIMESTAMP NOT NULL,
  UpdatedBy varchar2(255) NOT NULL,
  CreateTime TIMESTAMP NOT NULL,
  CreatedBy varchar2(255) NOT NULL,
  TransactionRuleID number(19,0) NOT NULL,
  TxnRuleKey varchar2(255) NOT NULL,
  TxnRuleValue varchar2(255) NOT NULL,
  TxnRuleComparator varchar2(255) NOT NULL,
  CONSTRAINT FK_txn_rule_transactionId FOREIGN KEY (TransactionRuleID) REFERENCES transaction_rule(ID)
 );
 
CREATE SEQUENCE  txn_rule_addn_info_ID_SEQ  
  MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE ; 
  
commit; 