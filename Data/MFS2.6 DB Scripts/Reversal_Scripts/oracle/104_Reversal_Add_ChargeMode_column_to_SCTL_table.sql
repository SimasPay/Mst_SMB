
--
-- Drop column "ChargeMode" from "service_charge_txn_log" table.
--
Alter table service_charge_txn_log drop column ChargeMode;

commit;


