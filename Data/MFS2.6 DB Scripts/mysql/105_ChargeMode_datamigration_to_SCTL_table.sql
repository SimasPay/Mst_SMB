
--
-- Migrating "charge mode" data from "transaction_rule" table to "service_charge_txn_log" table.
--
Update service_charge_txn_log s set s.chargemode = (select chargemode from transaction_rule t where t.id = s.transactionruleid);
