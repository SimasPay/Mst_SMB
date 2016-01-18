INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'Buy'), (select id from transaction_type where TransactionName = 'AirtimePurchase'), 'BillerCode', 'Additional', 50, 'Equal');

commit;