
--
-- Add new column "ChargeMode" to "service_charge_txn_log" table.
--
Alter table service_charge_txn_log add column ChargeMode int(11) DEFAULT NULL;
