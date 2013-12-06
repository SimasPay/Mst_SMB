
Delete from notification where code in (2042, 2043, 2044, 2045, 2046, 2047);


-- Delete Txn type, service for Change email, change Nickname, Forgot Pin
DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ChangeEmail');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ChangeEmail';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ChangeNickname');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ChangeNickname';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='ForgotPin');
DELETE FROM transaction_type where TRANSACTIONNAME = 'ForgotPin';

commit;