DELETE FROM rule_key where serviceid=(select id from service where ServiceName = 'AgentServices') and transactiontypeid=(select id from transaction_type where TransactionName = 'BillPay');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'BillPay'), 'BillerCode', 'Additional', 50, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'BillPay'), 'SourceGroup', 'Standard', 30, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'BillPay'), 'DestGroup', 'Standard', 20, 'Equal');

INSERT INTO rule_key VALUES(rule_key_ID_SEQ.nextval,'1', sysdate, 'System', sysdate, 'System', (select id from service where ServiceName = 'AgentServices'), (select id from transaction_type where TransactionName = 'BillPay'), 'Channel', 'Standard', 10, 'Equal');

commit;
