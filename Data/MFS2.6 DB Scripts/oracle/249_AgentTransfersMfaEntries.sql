
insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid,IsReverseAllowed) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'AgentServices' and mspid=1), (select id from transaction_type where transactionname = 'B2ETransfer' and mspid=1),0);
insert into service_transaction(version,lastupdatetime,updatedby,createtime,createdby,mspid,serviceid,transactiontypeid,IsReverseAllowed) values (1,sysdate,'system',sysdate,'system',1, (select id from service where servicename = 'AgentServices' and mspid=1), (select id from transaction_type where transactionname = 'E2BTransfer' and mspid=1),0);

insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'AgentServices' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'B2ETransfer' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);
insert into mfa_transactions_info VALUES (mfa_transactions_info_ID_SEQ.nextval,1,sysdate,'System',sysdate,'System',1,(select id from service where SERVICENAME = 'AgentServices' and mspid=1),(select id from transaction_type where TRANSACTIONNAME = 'E2BTransfer' and mspid=1),(select id from channel_code where CHANNELNAME = 'WebAPI'),1);

commit;
