

ALTER TABLE service_transaction ADD IsReverseAllowed tinyint(4) DEFAULT '0';

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Wallet') and  transactiontypeid = (select id from transaction_type where transactionname = 'Transfer');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Wallet') and  transactiontypeid = (select id from transaction_type where transactionname = 'SubBulkTransfer');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Wallet') and  transactiontypeid = (select id from transaction_type where transactionname = 'AgentToAgentTransfer');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Wallet') and  transactiontypeid = (select id from transaction_type where transactionname = 'CashOut');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Wallet') and  transactiontypeid = (select id from transaction_type where transactionname = 'TransferToUnregistered');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Bank') and  transactiontypeid = (select id from transaction_type where transactionname = 'Transfer');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'AgentServices') and  transactiontypeid = (select id from transaction_type where transactionname = 'CashIn');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'AgentServices') and  transactiontypeid = (select id from transaction_type where transactionname = 'Transfer');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'AgentServices') and  transactiontypeid = (select id from transaction_type where transactionname = 'CashOutToUnregistered');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'AgentServices') and  transactiontypeid = (select id from transaction_type where transactionname = 'BillPay');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Shopping') and  transactiontypeid = (select id from transaction_type where transactionname = 'Purchase');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'Payment') and  transactiontypeid = (select id from transaction_type where transactionname = 'BillPay');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'TellerService') and  transactiontypeid = (select id from transaction_type where transactionname = 'CashIn');
update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'TellerService') and  transactiontypeid = (select id from transaction_type where transactionname = 'CashOut');

update service_transaction set IsReverseAllowed = 1 where serviceid = (select id from service where servicename = 'System') and  transactiontypeid = (select id from transaction_type where transactionname = 'ChargeSettlement');
