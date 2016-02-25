
insert into transaction_type (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (1, sysdate, 'System', sysdate, 'System', 1, 'CashOut', 'Cash Out');
insert into transaction_type (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (1, sysdate, 'System', sysdate, 'System', 1, 'CashOutInquiry', 'Cash Out Inquiry');
insert into transaction_type (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, MSPID, TransactionName, DisplayName) VALUES (1, sysdate, 'System', sysdate, 'System', 1, 'CashInInquiry', 'Cash In Inquiry');
insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid,IsReverseAllowed) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'AgentServices' and mspid=1), (select id from transaction_type where transactionname = 'CashInInquiry' and mspid=1),0);
insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid,IsReverseAllowed) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'Wallet' and mspid=1), (select id from transaction_type where transactionname = 'CashOut' and mspid=1),0);
insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid,IsReverseAllowed) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'Wallet' and mspid=1), (select id from transaction_type where transactionname = 'CashOutInquiry' and mspid=1),0);
insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'AgentServices' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'CashIn' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);
insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'AgentServices' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'CashInInquiry' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);
insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Wallet' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'CashOut' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);
insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'Wallet' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'CashOutInquiry' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','1','10228');

update ENUM_TEXT set DISPLAYTEXT='Perorangan' where TAGID=6079 and  ENUMCODE=4;
update ENUM_TEXT set DISPLAYTEXT='Badan Usaha' where TAGID=6079 and  ENUMCODE=5;

commit;
