update service_transaction set serviceid=(select id from service where servicename='AgentServices') where transactiontypeid=(select id from transaction_type where transactionname='CashInToAgent');

update transaction_rule set serviceid=(select id from service where servicename='AgentServices') where transactiontypeid=(select id from transaction_type where transactionname='CashInToAgent');

update service_charge_txn_log set serviceid=(select id from service where servicename='AgentServices') where transactiontypeid=(select id from transaction_type where transactionname='CashInToAgent');