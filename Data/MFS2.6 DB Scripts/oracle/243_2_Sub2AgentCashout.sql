
delete from mfa_transactions_info where serviceid=(select id from service where servicename = 'AgentServices' and mspid=1) and transactiontypeid=(select id from transaction_type where transactionname = 'CashInInquiry' and mspid=1);
delete from mfa_transactions_info where serviceid=(select id from service where servicename = 'Wallet' and mspid=1) and transactiontypeid=(select id from transaction_type where transactionname = 'CashOutInquiry' and mspid=1);
delete from service_transaction where serviceid=(select id from service where servicename = 'AgentServices' and mspid=1) and transactiontypeid=(select id from transaction_type where transactionname = 'CashInInquiry' and mspid=1);
delete from service_transaction where serviceid=(select id from service where servicename = 'Wallet' and mspid=1) and transactiontypeid=(select id from transaction_type where transactionname = 'CashOutInquiry' and mspid=1);
delete from transaction_type where TransactionName='CashOutInquiry';
delete from transaction_type where TransactionName='CashInInquiry';

commit;