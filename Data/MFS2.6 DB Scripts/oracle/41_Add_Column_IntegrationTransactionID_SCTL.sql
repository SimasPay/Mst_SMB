ALTER TABLE service_charge_txn_log ADD IntegrationTransactionID NUMBER(19,0) DEFAULT NULL ;
alter table service_charge_txn_log add constraint ITID_unique unique(IntegrationTransactionID);

commit;