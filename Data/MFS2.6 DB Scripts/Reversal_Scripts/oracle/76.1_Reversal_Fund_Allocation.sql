ALTER TABLE unregistered_txn_info DROP COLUMN FailureReasonCode;
ALTER TABLE unregistered_txn_info DROP COLUMN ReversalReason;
ALTER TABLE unregistered_txn_info DROP COLUMN ExpiryTime;
ALTER TABLE unregistered_txn_info DROP COLUMN FundDefinitionID;
ALTER TABLE unregistered_txn_info DROP COLUMN AvailableAmount;
ALTER TABLE unregistered_txn_info DROP COLUMN WithdrawalMDN;
ALTER TABLE unregistered_txn_info DROP COLUMN WithdrawalFailureAttempt;
ALTER TABLE unregistered_txn_info DROP COLUMN PartnerCode;

DROP TABLE expiration_type CASCADE CONSTRAINTS;
Drop SEQUENCE expiration_type_ID_SEQ;
DROP TABLE purpose CASCADE CONSTRAINTS;
Drop SEQUENCE purpose_ID_SEQ;
DROP TABLE fund_definition CASCADE CONSTRAINTS;
Drop SEQUENCE fund_definition_ID_SEQ;
DROP TABLE fund_distribution_info CASCADE CONSTRAINTS;
Drop SEQUENCE fund_distribution_info_ID_SEQ;
DROP TABLE fund_events CASCADE CONSTRAINTS;
Drop SEQUENCE fund_events_ID_SEQ;

commit;









