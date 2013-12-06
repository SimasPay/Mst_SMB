use mfino;

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToTreasury');
DELETE FROM transaction_type where transactionname = 'TransferToTreasury';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='TransferToSystem');
DELETE FROM transaction_type where transactionname = 'TransferToSystem';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Wallet') and transactiontypeid=(select id from transaction_type where transactionname='Refund');
DELETE FROM transaction_type where transactionname = 'Refund';

DELETE FROM system_parameters WHERE parametername='days.to.national.treasury.of.graved';

DELETE FROM system_parameters WHERE parametername='national.treasury.partner.code';

DELETE FROM system_parameters WHERE parametername='retired.subscriber.system.collector.pocket';

DELETE FROM enum_text where TagID=8019 and  EnumCode='2';
DELETE FROM enum_text where TagID=8019 and  EnumCode='1';
DELETE FROM enum_text where TagID=8019 and  EnumCode='0';

-- Drop Table mfino.money_clearance_graved
DROP TABLE IF EXISTS `money_clearance_graved`;

-- Drop Table closed_account_settlement_mdn
DROP TABLE IF EXISTS `close_acct_setl_mdn`;
