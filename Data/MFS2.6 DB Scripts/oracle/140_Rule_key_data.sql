DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'Activation');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Activation'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Activation'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Activation'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'TransactionStatus');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'TransactionStatus'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'TransactionStatus'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'TransactionStatus'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'ChangePIN');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'SubscriberRegistration');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'AgentActivation');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'AgentActivation'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'AgentActivation'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'AgentActivation'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'ChangeSettings');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangeSettings'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangeSettings'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'ChangeSettings'), 'Channel', 'Standard', 10, 'Equal');



DELETE FROM rule_key where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where TransactionName = 'Reactivation');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Reactivation'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Reactivation'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Account'), (select id from transaction_type where TransactionName = 'Reactivation'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Wallet') and transactiontypeid=(select id from transaction_type where TransactionName = 'CheckBalance');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Wallet') and transactiontypeid=(select id from transaction_type where TransactionName = 'History');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'History'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'History'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'History'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Wallet') and transactiontypeid=(select id from transaction_type where TransactionName = 'Transfer');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'Transfer'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'Transfer'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'Transfer'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Wallet') and transactiontypeid=(select id from transaction_type where TransactionName = 'CashOut');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CashOut'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CashOut'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'CashOut'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Wallet') and transactiontypeid=(select id from transaction_type where TransactionName = 'AgentToAgentTransfer');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'AgentToAgentTransfer'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'AgentToAgentTransfer'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Wallet'), (select id from transaction_type where TransactionName = 'AgentToAgentTransfer'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Bank') and transactiontypeid=(select id from transaction_type where TransactionName = 'CheckBalance');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'CheckBalance'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Bank') and transactiontypeid=(select id from transaction_type where TransactionName = 'History');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'History'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'History'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'History'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Bank') and transactiontypeid=(select id from transaction_type where TransactionName = 'Transfer');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'Transfer'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'Transfer'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'Transfer'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Bank') and transactiontypeid=(select id from transaction_type where TransactionName = 'ChangePIN');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'ChangePIN'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Bank') and transactiontypeid=(select id from transaction_type where TransactionName = 'InterBankTransfer');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'InterBankTransfer'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'InterBankTransfer'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Bank'), (select id from transaction_type where TransactionName = 'InterBankTransfer'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'AgentServices') and transactiontypeid=(select id from transaction_type where TransactionName = 'SubscriberRegistration');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'SubscriberRegistration'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'AgentServices') and transactiontypeid=(select id from transaction_type where TransactionName = 'CashIn');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'CashIn'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'CashIn'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'CashIn'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Shopping') and transactiontypeid=(select id from transaction_type where TransactionName = 'Purchase');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Shopping'), (select id from transaction_type where TransactionName = 'Purchase'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Shopping'), (select id from transaction_type where TransactionName = 'Purchase'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Shopping'), (select id from transaction_type where TransactionName = 'Purchase'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Payment') and transactiontypeid=(select id from transaction_type where TransactionName = 'BillPay');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillPay'), 'BillerCode', 'Additional', 50, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillPay'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillPay'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillPay'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Payment') and transactiontypeid=(select id from transaction_type where TransactionName = 'BillInquiry');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillInquiry'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillInquiry'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Payment'), (select id from transaction_type where TransactionName = 'BillInquiry'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'Buy') and transactiontypeid=(select id from transaction_type where TransactionName = 'AirtimePurchase');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Buy'), (select id from transaction_type where TransactionName = 'AirtimePurchase'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Buy'), (select id from transaction_type where TransactionName = 'AirtimePurchase'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Buy'), (select id from transaction_type where TransactionName = 'AirtimePurchase'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'TellerService') and transactiontypeid=(select id from transaction_type where TransactionName = 'CashIn');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashIn'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashIn'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashIn'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'TellerService') and transactiontypeid=(select id from transaction_type where TransactionName = 'CashOut');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashOut'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashOut'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'CashOut'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'TellerService') and transactiontypeid=(select id from transaction_type where TransactionName = 'TellerEMoneyClearance');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'TellerEMoneyClearance'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'TellerEMoneyClearance'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'TellerService'), (select id from transaction_type where TransactionName = 'TellerEMoneyClearance'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'System') and transactiontypeid=(select id from transaction_type where TransactionName = 'ChargeSettlement');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'ChargeSettlement'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'ChargeSettlement'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'ChargeSettlement'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'System') and transactiontypeid=(select id from transaction_type where TransactionName = 'FundReimburse');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'FundReimburse'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'FundReimburse'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'FundReimburse'), 'Channel', 'Standard', 10, 'Equal');


DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'System') and transactiontypeid=(select id from transaction_type where TransactionName = 'Adjustments');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'Adjustments'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'Adjustments'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'System'), (select id from transaction_type where TransactionName = 'Adjustments'), 'Channel', 'Standard', 10, 'Equal');

commit;