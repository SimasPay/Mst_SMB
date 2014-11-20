ALTER TABLE service_charge_txn_log ADD parentIntegrationTransID number(19) DEFAULT NULL;

commit;