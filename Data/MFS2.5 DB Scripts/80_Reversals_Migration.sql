use mfino;

update service_charge_txn_log set AmtRevStatus = status where status IN (7, 8, 9, 10, 11, 12, 13, 14, 15);

update service_charge_txn_log set status = IF(IsChargeDistributed=0,2, 4) where status IN (7, 8, 9, 10, 11, 12, 13, 14, 15) and TransactionTypeID not in (select id from transaction_type where TransactionName='ReverseCharge' or TransactionName = 'ReverseTransaction');
