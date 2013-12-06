
ALTER TABLE rule_key DROP FOREIGN KEY FK_rulekey_serviceId;
ALTER TABLE rule_key DROP FOREIGN KEY FK_rulekey_txnTypeId;

DROP TABLE rule_key;