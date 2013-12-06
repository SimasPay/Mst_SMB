
--
-- Add new column "ChargeMode" to "service_charge_txn_log" table.
--
Alter table service_charge_txn_log add ChargeMode NUMBER(10,0) DEFAULT NULL;

commit;