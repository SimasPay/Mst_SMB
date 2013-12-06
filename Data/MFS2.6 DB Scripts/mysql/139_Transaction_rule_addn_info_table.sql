
DROP TABLE IF EXISTS `txn_rule_addn_info`;
CREATE TABLE `txn_rule_addn_info` (
  ID bigint(20) NOT NULL AUTO_INCREMENT,
  Version int(11) NOT NULL,
  LastUpdateTime datetime NOT NULL,
  UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
  CreateTime datetime NOT NULL,
  CreatedBy varchar(255) NOT NULL,
  TransactionRuleID bigint(20) NOT NULL,
  TxnRuleKey varchar(255) NOT NULL,
  TxnRuleValue varchar(255) NOT NULL,
  TxnRuleComparator varchar(255) NOT NULL,
  PRIMARY KEY (`ID`),
  CONSTRAINT FK_txn_rule_transactionId FOREIGN KEY (TransactionRuleID) REFERENCES transaction_rule(ID)
 )ENGINE=InnoDB DEFAULT CHARSET=utf8;