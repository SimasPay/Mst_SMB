use mfino;
ALTER TABLE unregistered_txn_info DROP COLUMN FailureReasonCode;
ALTER TABLE unregistered_txn_info DROP COLUMN ReversalReason;
ALTER TABLE unregistered_txn_info DROP COLUMN ExpiryTime;
ALTER TABLE `unregistered_txn_info` DROP FOREIGN KEY `FK_UnRegTrxn_fundDef_fDef`;
ALTER TABLE unregistered_txn_info DROP COLUMN FundDefinitionID;
ALTER TABLE unregistered_txn_info DROP COLUMN AvailableAmount;
ALTER TABLE unregistered_txn_info DROP COLUMN WithdrawalMDN;
ALTER TABLE unregistered_txn_info DROP COLUMN WithdrawalFailureAttempt;
ALTER TABLE unregistered_txn_info DROP COLUMN PartnerCode;

DROP TABLE fund_distribution_info;
DROP TABLE fund_definition;
DROP TABLE expiration_type;
DROP TABLE purpose;
DROP TABLE fund_events;










