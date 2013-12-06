
DROP TABLE IF EXISTS `rule_key`;
CREATE TABLE `rule_key` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  ServiceID bigint(20) NOT NULL,
  TransactionTypeID bigint(20) NOT NULL,
  TxnRuleKey varchar(255) NOT NULL,
  TxnRuleKeyType varchar(255) NOT NULL,
  TxnRuleKeyPriority int(11) NOT NULL,
  TxnRuleKeyComparision varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT FK_rulekey_serviceId FOREIGN KEY (ServiceID) REFERENCES service(ID),
  CONSTRAINT FK_rulekey_txnTypeId FOREIGN KEY (TransactionTypeID) REFERENCES transaction_type(ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;